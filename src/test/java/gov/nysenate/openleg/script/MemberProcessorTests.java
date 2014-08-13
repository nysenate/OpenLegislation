package gov.nysenate.openleg.script;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.BaseTests;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberProcessorTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(MemberProcessorTests.class);

    @Autowired
    JdbcTemplate jdbc;

    @Test
    public void testMemberInsertion() throws Exception {
        String[] extensions = {"json"};
        Resource dir = new FileSystemResource("/home/ash/Web/nysenate/OpenLegislation/src/main/resources/data/senators/");
        Collection<File> jsonFiles = FileUtils.listFiles(dir.getFile(), extensions, true);
        ObjectMapper om = new ObjectMapper();
        Set<String> lastNameSet = new HashSet<>();
        int incumbentCount = 0;
        for (File file : jsonFiles) {
            Integer sessionYear = Integer.parseInt(file.getParentFile().getName());
            JsonNode node = om.readTree(file);
            String name = node.get("name").asText();
            String lastName = node.get("lastName").asText();
            String email = node.get("email").asText();
            int district = node.get("district").get("number").asInt();
            logger.debug("{} {} {} {} {}", sessionYear, name, lastName, email, district);
            boolean incumbent = (sessionYear == 2013);

            if (incumbent) {
//                jdbc.update("update member set incumbent = true where full_name = ?", name);
//                incumbentCount++;
//                logger.info("adding {}", name);
            }

//            jdbc.update("insert into member (person_id, full_name, chamber) select id, full_name, 'senate' from person where full_name = ?" +
//                    " AND NOT EXISTS (SELECT * FROM member WHERE person_id = person.id)", name);


            //Object[] params = {name, lastName, email, name, lastName};
//            Pattern firstlast = Pattern.compile("(.*? )(.\\. )?(.*)( .*)?");
//            Matcher m = firstlast.matcher(name);
//            if (m.find()) {
//                Object[] params = {((m.group(1) != null) ? m.group(1).trim() : null),
//                        ((m.group(2) != null ? m.group(2).trim() : null)), ((m.group(4) != null ? m.group(4).trim() : null)), name};
//                jdbc.update("UPDATE person SET first_name = ?, middle_name = ?, suffix = ?, prefix = 'Senator' " +
//                        "WHERE full_name = ?", params);
//            }
            //jdbc.update("INSERT INTO person (full_name, last_name, email) SELECT ?, ?, ? WHERE NOT EXISTS " +
            //        "(SELECT 1 FROM person WHERE full_name = ? AND last_name = ?)", params);

            lastNameSet.add(lastName);


        }
        logger.debug("Incumbent count {}", incumbentCount);
//        Assert.assertEquals(jsonFiles.size(), lastNameSet.size());
    }

    @Test
    public void testInsertMember() throws Exception {
        File f = new File("/data/2013_lbdc_sn.txt");
        HashSet<String> shortNames = new HashSet<>();
        List<String> lines = FileUtils.readLines(f);
        for (String l : lines) {
            Matcher m = Pattern.compile(".*\\:(.{11})6([^0-9]+)").matcher(l);
            if (m.find() && !l.contains("DELETE")) {
                shortNames.add(m.group(2));
            }
        }
        logger.debug("size {}" , lines.size());
        //File writeFile = new File("/data/proc_2009_lbdc_sn_senator.txt");

        Resource dir = new FileSystemResource("/home/ash/Web/nysenate/OpenLegislation/src/main/resources/data/senators/2013");
        Collection<File> jsonFiles = FileUtils.listFiles(dir.getFile(), new String[] {"json"}, true);
        ObjectMapper om = new ObjectMapper();
        for (File file : jsonFiles) {
            JsonNode node = om.readTree(file);
            String lastName = node.get("lastName").asText();
            int district = node.get("district").get("number").asInt();

            Pattern firstlast = Pattern.compile("(.*? )(.\\. )?(.*)( .*)?");
            Matcher m = firstlast.matcher(node.get("name").asText());
            if (m.find()) {
                String first = ((m.group(1) != null) ? m.group(1).trim() : null);
                logger.debug("{} {} - {} - {}", first, lastName,  district, shortNames.contains(lastName.toUpperCase()));
                if (shortNames.contains(lastName.toUpperCase())) {
                    Object[] params = {lastName.toUpperCase(), district, first, lastName};
//                    jdbc.update("INSERT INTO session_member (member_id, lbdc_short_name, session_year, district_code) " +
//                            "SELECT member.id, ?, 2013, ? " +
//                          "FROM member JOIN person p ON member.person_id = p.id WHERE p.first_name = ? AND p.last_name = ?", params);
                }
            }



//



        }


        //for (String s : shortNames) {
        //    FileIOUtils.writeStringToFile(writeFile ,  s + " , 2009\n", true);
        //}
    }

    @Test
    public void testMissingSenators() throws Exception {
        File f = new File("/data/2013.cosponsors.sn.txt");
        HashSet<String> shortNames = new HashSet<>();
        List<String> lines = FileUtils.readLines(f);
        for (String l : lines) {
            Matcher m = Pattern.compile(".*\\:(.{11})7([^0-9]+)").matcher(l);
            if (m.find() && !l.contains("DELETE")) {
                String names = m.group(2);
                for (String name : names.split(",")) {
                    shortNames.add(name.trim());
                }
            }
        }
        for (String s : shortNames) {
            List<Integer> i = jdbc.query("SELECT 1 FROM session_member WHERE lbdc_short_name = ? AND session_year = 2013", new Object[]{s}, new SingleColumnRowMapper<Integer>());
            if (i == null || i.isEmpty()) {
                logger.debug(s);
            }
        }
        logger.info("{}", shortNames.size());
        //File writeFile = new File("/data/proc_2009_lbdc_sn_senator.txt");


    }

    @Test
    public void testAssemblyCSV() throws Exception {
        Resource f = new FileSystemResource("/data/2013_assembly_members.csv");
        List<String> lines = FileUtils.readLines(f.getFile());
        for (String s : lines) {
            String[] parts = s.split(",");
            int district = Integer.parseInt(parts[0]);
            String name = parts[1];

            String[] nameParts = name.split(" ");
            int nameSize = nameParts.length;
            String first = nameParts[0].trim();
            String middle = ((nameSize == 3 || nameSize == 4) && nameParts[1].length() < 5) ? nameParts[1].trim() : null;
            String last = (nameSize == 2 || (nameSize == 3 && nameParts[2].length() < 5)) ? nameParts[1].trim() : nameParts[2].trim();
            String suff = (nameSize == 4) ? nameParts[3].trim() : (nameSize == 3 && nameParts[2].trim().length() < 4) ? nameParts[2].trim() : null;

            List<Integer> res = jdbc.query("SELECT 1 FROM person WHERE first_name = ? AND last_name = ?", new Object[]{first, last}, new SingleColumnRowMapper<Integer>());
            logger.debug("{} - {} | [{}] [{}] [{}] [{}]", ((!res.isEmpty()) ? "FOUND" : "NOTFOUND"), name, first, middle, last, suff);

            if (res.isEmpty()) {
                //Integer id = jdbc.queryForObject("INSERT INTO person (prefix, full_name, first_name, middle_name, last_name, suffix) VALUES (?,?,?,?,?,?) RETURNING id",
//                        new Object[]{"Assembly Member", name, first, middle, last, suff}, new SingleColumnRowMapper<Integer>());
                //logger.info("ADDED " + id + " " + name);
//                jdbc.update("INSERT INTO member (person_id, chamber, incumbent, full_name) VALUES (?,'assembly', false, ?)", id, name);
            }
        }
    }

    @Test
    public void testAssemblyShortNames() throws Exception {
        File f = new File("/data/2013_assembly_sponsors_sn.txt");
        File cf = new File("/data/2013_assembly_cosponsors_sn.txt");
        File csvF = new File("/data/openleg_member_data/2013_assembly_members.csv");
        HashSet<String> shortNames = new HashSet<>();
        List<String> lines = FileUtils.readLines(f);
        for (String l : lines) {
            Matcher m = Pattern.compile(".*\\:(.{11})6([^0-9]+)").matcher(l);
            if (m.find() && !l.contains("DELETE")) {
                String sn = m.group(2).trim();
                if (!sn.startsWith("RULES") && !sn.startsWith("BUDGET")) {
                    shortNames.add(sn);
                }
            }
        }

        // Add co sponsors to set
        List<String> csLines = FileUtils.readLines(cf);
        for (String l : csLines) {
            Matcher m = Pattern.compile(".*\\:(.{11})7([^0-9]+)").matcher(l);
            if (m.find() && !l.contains("DELETE") && !l.contains("RULES")) {
                String names = m.group(2);
                for (String name : names.split(",")) {
                    if (name.length() > 1)
                    shortNames.add(name.trim());
                }
            }
        }

        HashMap<String, Integer> distAss = new HashMap<>();
        List<String> csvLines = FileUtils.readLines(csvF);
        for (String s : csvLines) {
            String[] parts = s.split(",");
            int district = Integer.parseInt(parts[0]);
            String name = parts[1];

            String[] nameParts = name.split(" ");
            int nameSize = nameParts.length;
            String first = nameParts[0].trim();
            String last = (nameSize == 2 || (nameSize == 3 && nameParts[2].length() < 5)) ? nameParts[1].trim() : nameParts[2].trim();
            distAss.put(last.toUpperCase(), district);
        }

        ArrayList<String> shortNameList = new ArrayList<>(shortNames);
        Collections.sort(shortNameList);

        for (String s : shortNameList) {
            List<Integer> res = jdbc.query("SELECT 1 FROM person p JOIN member m ON p.id = m.person_id WHERE p.last_name ILIKE ? AND m.chamber = 'assembly'",
                    new Object[]{s.toUpperCase()}, new SingleColumnRowMapper<Integer>());
            boolean notfound = (res.isEmpty());
           if ( notfound)
            logger.info("{} {} ({}) - Found: {}", ((notfound) ? "-" : "+"), s.toUpperCase(), distAss.get(s.toUpperCase()));

//            if (!notfound && !s.toUpperCase().startsWith("LOPEZ") && !s.toUpperCase().startsWith("MILLER") && !s.toUpperCase().startsWith("RIVERA")) {
//                Integer dist = distAss.get(s.toUpperCase());
//                Object[] params = {s.toUpperCase(), dist, s.toUpperCase()};
//                jdbc.update("INSERT INTO session_member (member_id, lbdc_short_name, session_year, district_code) " +
//                            "SELECT m.id, ?, 2013, ? " +
//                          "FROM member m JOIN person p ON m.person_id = p.id WHERE p.last_name ILIKE ? AND m.chamber = 'assembly'", params);
//
//                logger.info("Added: " + s.toUpperCase());
//            }
        }
        logger.info("Total {}", shortNames.size());
    }

    @Test
    public void testInsert2013AssemblyExtra() throws Exception {
        File f = new File("/data/openleg_member_data/2013_assembly_extra.csv");
        List<String> lines = FileUtils.readLines(f);
        for (String line : lines) {
            String[] parts = line.split(",");
            String fullName = (parts[0] + ((!parts[1].isEmpty()) ? (" " + parts[1]) : "") + " " + parts[2] + " " + parts[3]).trim();

            logger.debug("Inserting: {} -> {}|{}|{}|{}|{}|{}", fullName, parts[0], parts[1], parts[2],parts[3],parts[4], parts[5]);
            Object[] personParams = {fullName, parts[0], parts[1], parts[2], parts[3]};
            Integer personID = jdbc.queryForObject("INSERT INTO person (prefix, full_name, first_name, middle_name, last_name, suffix) " +
                                                               "VALUES ('Assemblymember',?,?,?,?,?) RETURNING id", personParams, new SingleColumnRowMapper<Integer>());
            Integer memberID = jdbc.queryForObject("INSERT INTO member (person_id, chamber, incumbent, full_name) " +
                    "VALUES (?,'assembly', false, ?) RETURNING id", new Object[]{personID, fullName}, new SingleColumnRowMapper<Integer>());
            jdbc.update("INSERT INTO session_member (member_id, lbdc_short_name, session_year, district_code) VALUES (?,?,?,?)", memberID, parts[5].trim(), 2013, Integer.parseInt(parts[4]));
            logger.info("INSERTED " + fullName);
        }
    }
}
