package gov.nysenate.openleg.common.script;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.Person;
import gov.nysenate.openleg.legislation.member.PersonName;
import gov.nysenate.openleg.legislation.member.dao.SqlMemberDao;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.csv.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A script for mass updating members for a session year change over.
 *
 * This script takes a csv input file containing new members and members which have switched districts. It does not
 * need to contain members returning to the same chamber/district and the same shortname.
 * These member updates are then merged with the previous years members and an SQL migration file is output.
 *
 * This should be run in early December before a Session Year change over.
 *
 * This script will:
 * - Add inserts into the session_member table for returning members.
 * - Add inserts into the person, member, and session_member table for members who have not served previously.
 * - Update the member table, setting incumbent = false, for members who have been replaced and are no longer serving in any district.
 *
 * Known issues/room for improvement:
 * - Incoming vacant district logic has not been added.
 * - Will not check validity of data in input_file csv.
 * - Exceptions may occur if current member data is out of date, these will have to be handled manually.
 *   i.e. Two members for the same chamber/district because one never had their incumbent field updated.
 * - Likely will not correctly handle members who switch chambers.
 *
 * <p>
 * Execution:
 * Run as an Application script through IntelliJ. The VM Options must have a profile set and the command line arguments
 * need to be set in the Program Arguments section. Below are examples.
 * <p>
 * - VM Option:
 *   "-Dspring.profiles.active=dev"
 * <p>
 * - Program Arguments:
 *   "-input_file=/home/ol/2025_member_changes.csv"
 *   "-output_file=/home/ol/2025_merged_members.sql"
 * <p>
 */
@Component
public class MemberUpdateScript extends BaseScript {

    @Autowired
    private SqlMemberDao sqlMemberDao;
    private static final SessionYear NEXT_SESSION_YEAR = SessionYear.current().nextSessionYear();

    public static void main(String[] args) throws Exception {
        SCRIPT_NAME = MemberUpdateScript.class.getName();
        AnnotationConfigApplicationContext ctx = init();
        MemberUpdateScript memberUpdateScript = ctx.getBean(MemberUpdateScript.class);
        CommandLine cmd = getCommandLine(memberUpdateScript.getOptions(), args);
        memberUpdateScript.execute(cmd);
        shutdown(ctx);
        System.exit(0);
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {
        String inputFile = opts.getOptionValue("input_file");
        String outputFile = opts.getOptionValue("output_file");
        executeMemberUpdateScript(inputFile, outputFile);
    }

    @Override
    protected Options getOptions() {
        Options options = new Options();
        options.addOption(null, "input_file", true, "Input CSV file containing new member data.");
        options.addOption(null, "output_file", true, "File which will be overwritten with the SQL migration.");
        return options;
    }

    public void executeMemberUpdateScript(String inputFile, String outputFile) throws IOException {
        // --- Collect and Organize Data ---
        Map<Integer, SessionMember> csvMembers = readCsvFile(inputFile);
        // Populate the member data for new members who have previously served.
        for (Map.Entry<Integer, SessionMember> entry : csvMembers.entrySet()) {
            var entryPersonId = entry.getValue().getMember().getPerson().personId();
            if (entryPersonId != 0) {
                Member prevMemberRecord = sqlMemberDao.getAllSessionMembers(SortOrder.ASC, LimitOffset.ALL).stream() // TODO inefficient
                        .filter(sm -> sm.getMember().getPerson().personId().equals(entryPersonId))
                        .findFirst().get().getMember();
                entry.getValue().setMember(prevMemberRecord);
            }
        }

        Map<Integer, SessionMember> newSenators = csvMembers.entrySet().stream()
                .filter(e -> e.getValue().getMember().getChamber().equals(Chamber.SENATE))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Integer, SessionMember> newAssemblymen = csvMembers.entrySet().stream()
                .filter(e -> e.getValue().getMember().getChamber().equals(Chamber.ASSEMBLY))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<SessionMember> allCurrentMembers = sqlMemberDao.getAllSessionMembers(SortOrder.ASC, LimitOffset.ALL).stream()
                .filter(sm -> sm.getSessionYear().equals(SessionYear.current()))
                .filter(sm -> sm.getMember().isIncumbent())
                .filter(sm -> !sm.isAlternate())
                .toList();
        Map<Integer, SessionMember> previousSenators = allCurrentMembers.stream()
                .filter(m -> m.getMember().getChamber().equals(Chamber.SENATE))
                .collect(Collectors.toMap(SessionMember::getDistrictCode, Function.identity()));
        Map<Integer, SessionMember> previousAssemblymen = allCurrentMembers.stream()
                .filter(m -> m.getMember().getChamber().equals(Chamber.ASSEMBLY))
                .collect(Collectors.toMap(SessionMember::getDistrictCode, Function.identity()));


        // --- Combine data ---
        Map<Integer, SessionMember> mergedSenators = new HashMap<>(previousSenators);
        Map<Integer, SessionMember> mergedAssemblymen = new HashMap<>(previousAssemblymen);

        // Replace previous members with the new members.
        mergedSenators.putAll(newSenators);
        mergedAssemblymen.putAll(newAssemblymen);

        // --- Generate SQL ---
        StringBuilder sql = new StringBuilder();
        sql.append(generateSql(mergedSenators, previousSenators, Chamber.SENATE));
        sql.append(generateSql(mergedAssemblymen, previousAssemblymen, Chamber.ASSEMBLY));
        saveSqlFile(outputFile, sql.toString());
    }

    private String generateSql(Map<Integer, SessionMember> mergedMembers, Map<Integer, SessionMember> previousMembers, Chamber chamber) {
        StringBuilder sql = new StringBuilder();
        for (Map.Entry<Integer, SessionMember> entry : mergedMembers.entrySet()) {
            sql.append(String.format("-- Updates for %s district %s.\n", chamber, entry.getKey()));

            // Check if previous serving member of this district is no longer serving.
            if (previousMembers.containsKey(entry.getKey())) {
                // District is not currently vacant.
                if (!entry.getValue().getMember().getPerson().personId().equals(previousMembers.get(entry.getKey()).getMember().getPerson().personId())) {
                    if (!mergedMembers.containsValue(previousMembers.get(entry.getKey()))) {
                        // Prev member is no longer serving in any district. Mark incumbent = false;
                        sql.append(setIncumbentSQL(previousMembers.get(entry.getKey()), false));
                        sql.append("\n");
                    }
                }
            }

            // Check if this member has previously served.
            if (entry.getValue().getMember().getPerson().personId() == 0) {
                // New Member
                sql.append(newMemberSQL(entry.getValue()));
            } else {
                // This member has previously served.
                sql.append(existingMemberSQL(entry.getValue()));
            }
            sql.append("\n");
        }
        return sql.toString();
    }

    private static final String SESSION_MEMBER_INSERT = """
            INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
            VALUES (%s, '%s', %s, %s);
            """;

    private String existingMemberSQL(SessionMember sm) {
        return String.format(SESSION_MEMBER_INSERT,
                sm.getMember().getMemberId(),
                sqlEscapeString(sm.getLbdcShortName()),
                NEXT_SESSION_YEAR.year(),
                sm.getDistrictCode());
    }

    private static final String SET_INCUMBENT = """
            UPDATE public.member
            SET incumbent = %s
            WHERE id = %s;
            """;

    private String setIncumbentSQL(SessionMember sm, boolean isIncumbent) {
        return String.format(SET_INCUMBENT, isIncumbent, sm.getMember().getMemberId());
    }

    private static final String NEW_MEMBER_INSERTS = """
            WITH p AS (
              INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
            VALUES ('${firstName}', '${middleName}', '${lastName}', '${email}', '${suffix}', 'no_image.jpg')
            RETURNING id
            ),
            m AS (
              INSERT INTO public.member(person_id, chamber, incumbent)
                VALUES ((SELECT id from p), '${chamber}', true)
                RETURNING id
            )
            INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
              VALUES ((SELECT id from m), '${shortname}', ${sessionYear}, ${districtCode});
            """;

    private String newMemberSQL(SessionMember sm) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("firstName", sqlEscapeString(sm.getMember().getPerson().name().firstName()));
        valuesMap.put("middleName", sqlEscapeString(sm.getMember().getPerson().name().middleName()));
        valuesMap.put("lastName", sqlEscapeString(sm.getMember().getPerson().name().lastName()));
        valuesMap.put("email", sm.getMember().getPerson().email());
        valuesMap.put("suffix", sm.getMember().getPerson().name().suffix());
        valuesMap.put("chamber", sm.getMember().getChamber().asSqlEnum());
        valuesMap.put("shortname", sqlEscapeString(sm.getLbdcShortName()));
        valuesMap.put("sessionYear", NEXT_SESSION_YEAR.toString());
        valuesMap.put("districtCode", String.valueOf(sm.getDistrictCode()));
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        return sub.replace(NEW_MEMBER_INSERTS);
    }

    // Escape "'" characters
    private String sqlEscapeString(String s) {
        return s.replaceAll("'", "''");
    }

    /**
     * Returns a map of district codes to the session member from the data in the input file.
     * If the district is vacant, session member is null.
     * If person_id column was set in the CSV, the SessionMember personId is set to that value.
     */
    private Map<Integer, SessionMember> readCsvFile(String inputFile) throws IOException {
        String[] HEADERS = {"chamber", "district_code", "person_id", "first_name",
                "middle_name", "last_name", "suffix", "shortname", "isvacant"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();

        Map<Integer, SessionMember> districtToMember = new HashMap<>();
        Reader file = new FileReader(inputFile);
        Iterable<CSVRecord> records = csvFormat.parse(file);
        for (CSVRecord record : records) {
            String chamber = record.get("chamber");
            Integer districtCode = Integer.valueOf(record.get("district_code"));
            Integer personId = NumberUtils.toInt(record.get("person_id"), 0);
            String firstName = record.get("first_name");
            String middleName = record.get("middle_name");
            String lastName = record.get("last_name");
            String suffix = record.get("suffix");
            String shortname = record.get("shortname");
            boolean isVacant = Boolean.valueOf(record.get("isvacant"));

            if (isVacant) {
                districtToMember.put(districtCode, null);
            } else {
                PersonName name = new PersonName("", "", firstName, middleName, lastName, suffix);
                Person p = new Person(personId, name, "", "");
                Member m = new Member(p, 0, Chamber.getValue(chamber), true);
                SessionMember sm = new SessionMember(0, m, shortname, NEXT_SESSION_YEAR, districtCode, false);
                districtToMember.put(districtCode, sm);
            }
        }
        return districtToMember;
    }

    public void saveSqlFile(String outputFile, String fileData) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(fileData.getBytes());
        fos.flush();
        fos.close();
    }
}
