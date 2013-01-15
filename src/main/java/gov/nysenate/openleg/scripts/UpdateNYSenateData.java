package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.util.Config;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.services.MemoryCachedNYSenateClient;
import gov.nysenate.services.NYSenateClient;
import gov.nysenate.services.model.Committee;
import gov.nysenate.services.model.Member;
import gov.nysenate.services.model.Senator;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

public class UpdateNYSenateData {
    public static Logger logger = Logger.getLogger(UpdateNYSenateData.class);

    public static void main(String[] args) throws Exception {
        CommandLine opts = null;
        try {
            Options options = new Options()
                .addOption("h", "help", false, "Print this message")
                .addOption("a","all",false, "Refresh all available data.")
                .addOption("c","committees",false, "Refresh committee data")
                .addOption("s","senators",false, "Refresh senator data");
            opts = new PosixParser().parse(options, args);
            if(opts.hasOption("-h")) {
                System.out.println("USAGE: UpdateNYSenateData [-h|--help] [-c|--committees] [-s|--senators] [-a|--all]");
                System.exit(0);

            }
        } catch (ParseException e) {
            logger.fatal("Error parsing arguments: ", e);
            System.exit(0);
        }

        String apiKey = Config.get("nysenate.apiKey");
        String apiDomain = Config.get("nysenate.apiDomain");
        NYSenateClient client = new MemoryCachedNYSenateClient(apiDomain, apiKey);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.INDENT_OUTPUT, true);


        File baseDir = new File("/home/graylinkim/projects/nysenate/OpenLegislation/src/main/resources/data/");
        String sessionYear = String.valueOf(SessionYear.getSessionYear());


        if (opts.hasOption("committees") || opts.hasOption("all")) {
            File committeesDir = new File(baseDir, "committees");
            File currentCommitteesDir = new File(committeesDir, sessionYear);
            FileUtils.forceMkdir(currentCommitteesDir);

            List<Committee> committees = client.getStandingCommittees();
            for (Committee committee : committees) {
                for(Member member : (List<Member>)ListUtils.union(committee.getChairs(), committee.getMembers())) {
                    member.setShortName(getSenatorKey(member.getName()));
                }
                File committeeFile = new File(currentCommitteesDir, committee.getShortName());
                mapper.writeValue(committeeFile, committee);
            }

        }

        if (opts.hasOption("senators") || opts.hasOption("all")) {
            File senatorsDir = new File(baseDir, "senators");
            File currentSenatorsDir = new File(senatorsDir, sessionYear);
            FileUtils.forceMkdir(currentSenatorsDir);

            List<Senator> senators =  client.getSenators();
            for (Senator senator : senators) {
                File senatorFile = new File(currentSenatorsDir, senator.getDistrict().getNumber()+senator.getShortName());
                System.out.println("Writing "+senator.getName()+" to "+senatorFile.getAbsolutePath());
                mapper.writeValue(senatorFile, senator);
            }
        }

        logger.info("Done!");
    }

    public static  String getSenatorKey(String memberName) {
        String senatorKey = memberName.replaceAll(
                "(?i)( (jr|sr)\\.?)", "");
        String[] tuple = senatorKey.split(" ");
        senatorKey = tuple[tuple.length - 1].toLowerCase();
        return senatorKey;
    }
}
