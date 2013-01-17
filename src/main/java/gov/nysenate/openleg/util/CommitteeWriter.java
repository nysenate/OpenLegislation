package gov.nysenate.openleg.util;

import gov.nysenate.openleg.qa.LBDConnect;
import gov.nysenate.services.model.Committee;
import gov.nysenate.services.model.Member;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.SimpleType;

public class CommitteeWriter {

    public static final String FILE = "file";
    public static final String API_KEY = "api-key";
    public static final String HELP = "help";

    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
        CommitteeWriter writer = new CommitteeWriter();

        CommandLineParser parser = new PosixParser();

        Options options = new Options();

        options.addOption("f", FILE, true, "The file where the committee JSON should be written");
        options.addOption("k", API_KEY, true, "The services API key");
        options.addOption("h", HELP, false, "Print this message");

        try {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption(HELP)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("posix", options );
            }
            else if(line.hasOption(FILE) && line.hasOption(API_KEY)) {
                writer.writeCommitteeJson(
                        line.getOptionValue(API_KEY),
                        line.getOptionValue(FILE),
                        getMapper());
            }
            else {
                throw new ParseException("Use -h for help");
            }
        } catch (ParseException e) {
            logger.error(e);
        }
    }

    private static Logger logger = Logger.getLogger(CommitteeWriter.class);

    /**
     *
     * @param apiKey services APIKey
     * @param filePath
     * @param mapper
     */
    public void writeCommitteeJson(String apiKey, String filePath, ObjectMapper mapper) {
        writeCommitteeJson(apiKey, new File(filePath), mapper);
    }

    public void writeCommitteeJson(String apiKey, File file, ObjectMapper mapper) {
        try {
            mapper.writeValue(file,
                    insertsectCommittees(this.getCommitteeNamesFromLbdc(), this.getCommitteesFromServices(apiKey)));
        } catch (JsonGenerationException e) {
            logger.error(e);
        } catch (JsonMappingException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public ArrayList<Committee> getCommitteesFromJson(String filePath, ObjectMapper mapper) {
        return getCommitteesFromJson(new File(filePath), mapper);
    }

    public ArrayList<Committee> getCommitteesFromJson(File file, ObjectMapper mapper) {
        CollectionType type = CollectionType.construct(ArrayList.class, SimpleType.construct(Committee.class));

        ArrayList<Committee> committees = null;

        try {
            committees = mapper.readValue(
                    file,
                    type);
        } catch (JsonParseException e) {
            logger.error(e);
        } catch (JsonMappingException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }

        return committees;
    }

    public ArrayList<Committee> getCommitteesFromServices(String apiKey) {
//        SenateServicesFactory factory = new SenateServicesFactory();
//        SenateServicesDAO dao = factory.createSenateServicesDAO(apiKey);
//        return dao.getCommittees();
        return null;
    }

    public ArrayList<String> getCommitteeNamesFromLbdc() {
        LBDConnect lbd = LBDConnect.getInstance();

        return lbd.getSenateCommittees();
    }

    public ArrayList<Committee> insertsectCommittees(ArrayList<String> committeeNames, ArrayList<Committee> committees) {
        for(int i = committees.size()-1; i >= 0; i--) {
            Committee committee = committees.get(i);

            if(!committeeNames.contains(committee.getName())) {
                committees.remove(i);
                continue;
            }

            setMemberKeys(committee.getChairs());
            setMemberKeys(committee.getMembers());
        }
        return committees;
    }

    public void setMemberKeys(ArrayList<Member> members) {
        for(Member member:members) {
            member.setShortName(getSenatorKey(member.getName()));
        }
    }

    public String getSenatorKey(String memberName) {
        String senatorKey = memberName.replaceAll(
                "(?i)( (jr|sr)\\.?)", "");
        String[] tuple = senatorKey.split(" ");
        senatorKey = tuple[tuple.length - 1].toLowerCase();
        return senatorKey;
    }

    public static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SerializationConfig cnfg = mapper.getSerializationConfig();
        cnfg.set(Feature.INDENT_OUTPUT, true);
        mapper.setSerializationConfig(cnfg);

        return mapper;
    }
}
