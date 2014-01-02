package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.services.MemoryCachedNYSenateClient;
import gov.nysenate.services.NYSenateClient;
import gov.nysenate.services.model.Committee;
import gov.nysenate.services.model.Member;
import gov.nysenate.services.model.Senator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

public class UpdateNYSenateData extends BaseScript
{
    public static Logger logger = Logger.getLogger(UpdateNYSenateData.class);

    public static void main(String[] args) throws Exception
    {
        new UpdateNYSenateData().run(args);
    }

    public Options getOptions()
    {
        Options options = new Options();
        options.addOption("a","all",false, "Refresh all available data.");
        options.addOption("c","committees",false, "Refresh committee data");
        options.addOption("s","senators",false, "Refresh senator data");
        return options;
    }

    public void execute(CommandLine opts) throws IOException, XmlRpcException
    {
        String apiKey = Application.getConfig().getValue("nysenate.apiKey");
        String apiDomain = Application.getConfig().getValue("nysenate.apiDomain");
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
                // Fix senator shortNames.
                @SuppressWarnings("unchecked")
                List<Member> members = ListUtils.union(committee.getChairs(), committee.getMembers());
                for(Member member : members) {
                    member.setShortName(getSenatorKey(member.getName()));
                }

                // Remove all but the first listed chair, remove that chair from member listings.
                if (!committee.getChairs().isEmpty()) {
                    Member chair = committee.getChairs().get(0);
                    committee.setChairs(new ArrayList<Member>(Arrays.asList(new Member[]{chair})));
                    committee.getMembers().remove(chair);
                }

                String filename = committee.getName().replaceAll("[',.-]", "").replaceAll(" ", "_").toLowerCase()+".json";
                File committeeFile = new File(currentCommitteesDir, filename);
                mapper.writeValue(committeeFile, committee);
            }

        }

        if (opts.hasOption("senators") || opts.hasOption("all")) {
            File senatorsDir = new File(baseDir, "senators");
            File currentSenatorsDir = new File(senatorsDir, sessionYear);
            FileUtils.forceMkdir(currentSenatorsDir);

            List<Senator> senators =  client.getSenators();
            for (Senator senator : senators) {
                senator.setShortName(getSenatorKey(senator.getName()));
                File senatorFile = new File(currentSenatorsDir, senator.getDistrict().getNumber()+".json");
                System.out.println("Writing "+senator.getName()+" ["+senator.getShortName()+"] to "+senatorFile.getAbsolutePath());
                mapper.writeValue(senatorFile, senator);
            }
        }

        logger.info("Done!");
    }

    public String getSenatorKey(String memberName) {
        String senatorKey = memberName.replaceAll("(?i)( (jr|sr)\\.?)", "");
        String[] tuple = senatorKey.split(" ");
        senatorKey = tuple[tuple.length - 1];
        return senatorKey;
    }
}
