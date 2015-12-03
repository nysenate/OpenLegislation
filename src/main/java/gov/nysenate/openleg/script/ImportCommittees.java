package gov.nysenate.openleg.script;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.service.entity.committee.data.CommitteeDataService;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.time.LocalDate;
import java.util.*;

@Component
public class ImportCommittees extends BaseScript {

    private static final Logger logger = LoggerFactory.getLogger(ImportCommittees.class);

    private static FilenameFilter intFileNameFilter = (File dir, String name) -> StringUtils.isNumeric(name);

    private static FilenameFilter jsonFileNameFilter = (File dir, String name) -> name.endsWith(".json");

    @Autowired
    CommitteeDataService committeeDataService;

    @Autowired
    MemberService memberService;

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx = init();
        ImportCommittees importCommittees = ctx.getBean(ImportCommittees.class);
        CommandLine cmd = getCommandLine(importCommittees.getOptions(), args);
        importCommittees.execute(cmd);
        shutdown(ctx);
    }

    @Override
    protected Options getOptions() {
        Options options = super.getOptions();
        options.addOption(new Option("d", "committeeDir", true, "Path to the root committee json directory"));
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {

        File committeeDir = new File(opts.getOptionValue("committeeDir"));
        if (committeeDir.isDirectory()) {
            List<File> yearDirs = Arrays.asList(committeeDir.listFiles(intFileNameFilter));
            yearDirs.sort((File f1, File f2 ) -> ((Integer) Integer.parseInt(f1.getName())).compareTo(Integer.parseInt(f2.getName())));
            for (File yearDir : committeeDir.listFiles(intFileNameFilter)) {
                int year = Integer.parseInt(yearDir.getName());
                if (yearDir.isDirectory()) {
                    for (File jsonFile : yearDir.listFiles(jsonFileNameFilter)) {
                        try {
                            logger.info("importing " + year + "/" + jsonFile.getName());
                            Committee committee = getCommitteeFromJson(jsonFile, year, Chamber.SENATE);
                            committeeDataService.saveCommittee(committee, null);
                            logger.info("inserted committee: " + committee.getVersionId());
                        }
                        catch (DataAccessException ex) {
                            logger.warn("Error storing committee: ");
                            ex.printStackTrace();
                            System.exit(-1);
                        }
                        catch (Exception ex) {
                            logger.warn("invalid committee json file: " + jsonFile.getAbsolutePath());
                            ex.printStackTrace();
                            System.exit(-1);
                        }
                    }
                }
            }
        }
    }

    private Committee getCommitteeFromJson(File jsonFile, int year, Chamber chamber) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonFile);

        Committee committee = new Committee();
        committee.setName(root.get("name").textValue());
        committee.setChamber(chamber);

        committee.setSession(SessionYear.of(year));
        committee.setPublishedDateTime(LocalDate.of(year, 1, 1).atStartOfDay());

        int sequenceNum = 1;
        List<CommitteeMember> committeeMembers = new ArrayList<>();
        Set<SessionMember> addedMembers = new HashSet<>(); // Used to prevent the same member from being added multiple times

        Iterator<JsonNode> chairs = root.path("chairs").elements();
        while (chairs.hasNext()) {
            try {
                CommitteeMember committeeMember = getCommitteeMemberFromJson(chairs.next(), year, chamber);
                committeeMember.setTitle(sequenceNum == 1 ? CommitteeMemberTitle.CHAIR_PERSON : CommitteeMemberTitle.VICE_CHAIR);
                if (!addedMembers.contains(committeeMember.getMember())) {
                    committeeMember.setSequenceNo(sequenceNum++);
                    committeeMembers.add(committeeMember);
                    addedMembers.add(committeeMember.getMember());
                }
            }
            catch (MemberNotFoundEx ex) {
                ex.printStackTrace();
            }
        }

        Iterator<JsonNode> members = root.get("members").elements();
        while (members.hasNext()) {
            try {
                CommitteeMember committeeMember = getCommitteeMemberFromJson(members.next(), year, chamber);
                committeeMember.setTitle(CommitteeMemberTitle.MEMBER);
                if (!addedMembers.contains(committeeMember.getMember())) {
                    committeeMember.setSequenceNo(sequenceNum++);
                    committeeMembers.add(committeeMember);
                    addedMembers.add(committeeMember.getMember());
                }
            }
            catch (MemberNotFoundEx ex) {
                ex.printStackTrace();
            }
        }

        committee.setMembers(committeeMembers);

        return committee;
    }

    private CommitteeMember getCommitteeMemberFromJson(JsonNode memberNode, int year, Chamber chamber) throws MemberNotFoundEx{
        CommitteeMember committeeMember = new CommitteeMember();
        SessionMember member = memberService.getMemberByShortName(memberNode.get("shortName").textValue(), SessionYear.of(year), chamber);
        committeeMember.setMember(member);
        return committeeMember;
    }


}
