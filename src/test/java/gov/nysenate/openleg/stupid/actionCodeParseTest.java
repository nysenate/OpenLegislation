package gov.nysenate.openleg.stupid;

import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.sourcefiles.SourceFileRefDao;
import gov.nysenate.openleg.util.FileIOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;

@SillyTest
public class actionCodeParseTest {
    @Autowired
    Environment env;

    @Autowired
    SourceFileRefDao sourceFileRefDao;

    @Test
    public void parseActionCodes() {

        final String fileName = "LBDC_ACTION_CODES.csv";

        final File testFileDir = FileIOUtils.getResourceFile("sourcefile/");

        File csvFile = new File(testFileDir, fileName);

        File outputCodes = FileIOUtils.getResourceFile("sourcefile/outputActionCodes.txt");
        try {
            //outputCodes.createNewFile();
            Reader in = new FileReader(csvFile);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputCodes));
            StringBuffer switchClause = new StringBuffer();


            for (CSVRecord record : records) {
                String outer = record.get(1).toUpperCase().replaceAll(",","").replaceAll(" ","_");
                String inner = "(" + record.get(1).toLowerCase() + "," + record.get(2) + "), //" + record.get(0);
                writer.write(outer + inner + "\n");

                switchClause.append( "case " + record.get(0) + ":\n\t" +
                        "requestedCode = " + record.get(1).toUpperCase().replaceAll(",","").replaceAll(" ","_") +
                        ";\n\t break;\n");

//                String columnOne = record.get(0);
//                String columnTwo = record.get(1);
//                String columnThree = record.get(2);
            }
            writer.write(switchClause.toString());
            writer.close();
        }
        catch (Exception e){

        }


    }

}
