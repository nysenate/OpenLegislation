package gov.nysenate.openleg;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.Person;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Test {

    public static Pattern sobiHeaderPattern = Pattern.compile("^((\\d{4})([A-Z]\\d{5}[ A-Z])([0-9A-Z]))(.*)");
    public static SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMDD'.T'HHmmss'.TXT'");
    public static Pattern billDataPattern = Pattern.compile("(.{20})([0-9]{5}[ A-Z])(.{33})([ A-Z][0-9]{5}[ A-Z])(.{8}).*");
    public static Pattern sameAsPattern = Pattern.compile("Same as( Uni\\.)? ([A-Z] [0-9]{1,5}-?[A-Z]?)");

    public void applyBillData(String data, Bill bill) {
        if (data.contains("\r\n")) {
            logger.error("Bill Data (1) blocks should only ever be 1 line long!");

        } else if (data.trim().startsWith("DELETE")) {
            //Need to somehow remove the whole bill here..

        } else {
            data = data.replace('\0', ' ');
            Matcher billData = billDataPattern.matcher(data);
            if (billData.find()) {
                String sponsor = billData.group(1);
                String reprint = billData.group(2);
                String blurb = billData.group(3);
                String oldbill = billData.group(4);
                String lbdnum = billData.group(5);
            } else {
                logger.error("billDataPattern not matched: "+data);
            }
        }
    }

    public void applyLaw(String data, Bill bill) {
        if (data.trim().startsWith("DELETE")) {
            // The Law delete code should also remove the summary information
            bill.setLaw("");
            bill.setSummary("");

        } else {
            // We'll definitely need to clean this data up more than a little bit..
            bill.setLaw(data.replace("\r\n", " ").replaceAll("\\xBD", ""));
        }
    }

    public void applyCosponsors(String data, Bill bill) {
        // No DELETE code for coSponsors, sent through sponsor
        // instead which deletes the whole package

        // New values are always replacements of existing sets...
        ArrayList<Person> coSponsors = new ArrayList<Person>();
        for(String coSponsor : data.replace("\r\n", " ").split(",")) {
            coSponsors.add(new Person(coSponsor.trim()));
        }
        bill.setCoSponsors(coSponsors);
    }

    public void applyMultisponsors(String data, Bill bill) {
        // No DELETE code for multisponsors, sent through sponsor
        // instead which deletes the whole package

        // New values are always replacements of existing sets...
        ArrayList<Person> multiSponsors = new ArrayList<Person>();
        for(String multiSponsor : data.replace("\r\n", " ").split(",")) {
            multiSponsors.add(new Person(multiSponsor.trim()));
        }
        bill.setMultiSponsors(multiSponsors);
    }

    public void applySponsor(String data, Bill bill) {
        if (data.contains("\r\n")) {
            logger.error("Sponsor blocks should only ever be 1 line long!");

        } else if (data.startsWith("DELETE")) {
            // When we receive a delete for sponsor, remove ALL sponsor information
            bill.setSponsor(null);
            bill.setCoSponsors(new ArrayList<Person>());
            bill.setMultiSponsors(new ArrayList<Person>());

        } else {
            bill.setSponsor(new Person(data.trim()));
        }

    }

    public void applySummary(String data, Bill bill) {
        // The DELETE code for the summary goes through the law block (B)
        // Combine the lines with a space and handle special character issues..
        // Again, I don't have any examples of these special characters right now
        // data = data.replace("›","S").replaceAll("\\x27(\\W|\\s)", "&apos;$1");
        bill.setSummary(data.replace("\r\n", " ").trim());
    }

    public void applyTitle(String data, Bill bill) {
        // No DELETE code for titles
        // Combine the lines with a space and handle special character issues..
        // Again, I don't have any examples of these special characters right now
        // data = data.replace("›","S").replaceAll("\\x27(\\W|\\s)", "&apos;$1");
        bill.setTitle(data.replace("\r\n", " ").trim());
    }

    public void applySameAs(String data, Bill bill) {
        if (data.contains("\r\n")) {
            logger.error("sameAs blocks should only ever be 1 line long!");

        } else if (data.trim().equals("No same as") || data.trim().startsWith("DELETE")) {
            bill.setSameAs("");

        } else {
            // Why do we do this, don't have an example of this issue..
            // data = data.replace("/", ",").replaceAll("[ \\-.;]", "");
            Matcher sameAs = sameAsPattern.matcher(data);
            if (sameAs.find()) {
                bill.setSameAs(sameAs.group(2).replace("-","").replace(" ",""));
            } else {
                logger.error("sameAsPattern not matched: "+data);
            }
        }
    }

    public Logger logger;
    public File sobiDirectory;
    public File jsonDirectory;
    public HashMap<String, Method> methodMap;

    public Test(String sobiDirectory, String jsonDirectory) {
        this.logger = Logger.getLogger(this.getClass());
        this.sobiDirectory = new File(sobiDirectory);
        this.jsonDirectory = new File(jsonDirectory);
        this.methodMap = new HashMap<String, Method>();

        try {
            methodMap.put("1", this.getClass().getDeclaredMethod("applyBillData", String.class, Bill.class));
            methodMap.put("3", this.getClass().getDeclaredMethod("applyTitle", String.class, Bill.class));
            methodMap.put("5", this.getClass().getDeclaredMethod("applySameAs", String.class, Bill.class));
            methodMap.put("6", this.getClass().getDeclaredMethod("applySponsor", String.class, Bill.class));
            methodMap.put("7", this.getClass().getDeclaredMethod("applyCosponsors", String.class, Bill.class));
            methodMap.put("8", this.getClass().getDeclaredMethod("applyMultisponsors", String.class, Bill.class));
            methodMap.put("B", this.getClass().getDeclaredMethod("applyLaw", String.class, Bill.class));
            methodMap.put("C", this.getClass().getDeclaredMethod("applySummary", String.class, Bill.class));
            /*

            methodMap.put("2", new LawSectionLineParser());
            methodMap.put("4", new BillEventLineParser());
            methodMap.put("A", new ActClauseLineParser());


            methodMap.put("M", new MemoLineParser());
            methodMap.put("R", new TextLineParser());
            methodMap.put("T", new TextLineParser());
            methodMap.put("V", new VoteLineParser());
            */
        } catch (NoSuchMethodException e) {
            logger.error("Failure building methodMap: "+e.getMessage());
        }
    }

    public void ingest() throws Exception {
        // Loop through an ordered list of files in the sobiDirectory
        Collection<File> files = FileUtils.listFiles(sobiDirectory, new String[] {"TXT"}, false);
        Collections.sort((List<File>) files);
        for(File sobiFile : files) {

            // Initialize file variables
            String oldBlock = "";
            String newBlock = "";
            String blockData = "";
            String billYear = "";
            String billId = "";
            String lineCode = "";
            String fileName = sobiFile.getName();
            //Date date = Test.sobiDateFormat.parse(fileName);


            // Loop through the lines in the file
            logger.info("Reading file: "+fileName);
            List<String> lines = FileUtils.readLines(sobiFile);
            lines.add(""); // Add a line to remove last line edge case
            for(String line: lines) {

                // Check to see if the current line is in the SOBI format
                Matcher sobiHeader = sobiHeaderPattern.matcher(line);

                // Supply a default newBlock identifier for non-matching lines
                newBlock = sobiHeader.find() ? sobiHeader.group(1) : "";

                // If we previously had a block and the new block is different
                // commit the old block before starting a new one.
                if (!oldBlock.equals("") && !newBlock.equals(oldBlock) ) {

                    // Load the referenced bill object (may create new one instead)
                    Bill bill = loadBill(billId, billYear);

                    // commit block
                    if (methodMap.containsKey(lineCode)) {
                        // Invoke the update procedure and apply data to the loaded bill
                        methodMap.get(lineCode).invoke(this, blockData,  bill);

                        // Save the bill object.. (not necessarily to file)
                        //logger.debug("Committing block: "+oldBlock);

                    } else {
                        //logger.info("Skipped block: "+oldBlock);
                    }

                    // cleanup
                    blockData = "";
                }

                // Move our identifier forward
                oldBlock = newBlock;

                // Skip lines that did not match the SOBI format
                if (newBlock.equals("")) continue;


                if (blockData.equals("")) {

                    // If we're starting from a blank slate, initialize block values
                    billYear = sobiHeader.group(2);
                    billId = sobiHeader.group(3).trim(); // Can have trailing space in amendment slot
                    lineCode = sobiHeader.group(4);
                    blockData = sobiHeader.group(5);

                } else {
                    // Otherwise, build the data string, carry the new lines
                    blockData += sobiHeader.group(5)+"\r\n";
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Test test = new Test("/home/test/sobi/","/home/test/json");
        test.ingest();
        System.out.println("Done");
    }

    public static Bill loadBill(String id, String year) {
        return new Bill();
    }
}
