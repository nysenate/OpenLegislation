package gov.nysenate.openleg.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class SenatorJsonWriter {

    public static void main(String[] args) throws IOException {
        SenatorJsonWriter writer = null;
        if(args.length == 0) {
            writer = new SenatorJsonWriter();
        }
        else {
            writer = new SenatorJsonWriter(args[0]);
        }
        writer.generateJson();
    }

    private static Logger logger = Logger.getLogger(SenatorJsonWriter.class);

    public static final String SENATOR_JSON_URL = "http://geo.nysenate.gov/maps/json/raw/";
    public static final String DEFAULT_WRITE_DIRECTORY = "/vol/share/tomcat/webapps/legislation/WEB-INF/classes/data/districts/";

    private final String writeDirectory;

    public SenatorJsonWriter(String writeDirectory) {
        this.writeDirectory = writeDirectory;
    }

    public SenatorJsonWriter() {
        writeDirectory = DEFAULT_WRITE_DIRECTORY;
    }

    public void generateJson() throws IOException {
        File directory = new File(writeDirectory);
        if(directory.exists()) {
            for(int i = 1; i <= 62; i++) {
                String json = getSenatorJson(i);
                if(json == null) {
                    logger.warn("could not get json for district " + i);
                    continue;
                }

                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(writeDirectory + "/sd" + i + ".json")));
                bw.write(json);
                bw.close();
            }
        }
        else {
            logger.warn("specified write directory (" + writeDirectory + ")  does not exist");
        }
    }

    private String getSenatorJson(int districtNumber) throws MalformedURLException, IOException {
        String json = null;

        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(SENATOR_JSON_URL + "sd" + districtNumber + ".json").openStream()));

        String in = null;
        while((in = br.readLine()) != null) {
            if(json == null)
                json = in;
            else
                json += in;
        }
        br.close();

        return json;
    }
}
