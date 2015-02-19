package gov.nysenate.openleg.service.bill.text;

import gov.nysenate.openleg.service.scraping.BillTextScraper;
import gov.nysenate.openleg.util.StringDiffer;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

/**
 * Created by kyle on 2/19/15.
 */
@Repository
public class BillTextCheckService {
    private static final Logger logger = Logger.getLogger(BillTextCheckService.class);

    public BillTextCheckService(){
        BillTextScraper billTextScraper = new BillTextScraper();


    }

    /**
     *
     * @param text
     * @param billType
     * @throws Exception
     */
    public void scrapeAllTypes(String text, String billType) throws Exception {
        String x = "";

        if (billType.equalsIgnoreCase("S")) {
            text = text.replaceAll("§", "S");
        } else if (billType.equalsIgnoreCase("A")) {
            text = text.replaceFirst("Assembly Resolution No. \\d+", "Legislative Resolution");
            text = text.replaceFirst("BY: M. of A. \\w+", "");
            text = text.replaceAll("§", "S");
            text = text.replaceAll("\\d+(\\s\\s)*", "");
            x = x.replaceAll("\\d+(\\s\\s)*", "");

        } else if (billType.equalsIgnoreCase("R")) {
            text = text.replaceFirst("No. \\d+", "");
            text = text.replaceFirst("BY: Senator \\w+", "");
            text = text.replaceAll("§", "S");

        } else if (billType.equalsIgnoreCase("E")) {
            text = text.replaceFirst("No. \\d+", "");
            text = text.replaceFirst("BY: M. of A. \\w+", "");
            text = text.replaceAll("§", "Âº");              //assembly character????

        } else if (billType.equalsIgnoreCase("K")) {
            text = text.replaceFirst("Assembly Resolution No. \\d+", "Legislative Resolution");
            text = text.replaceFirst("BY: M. of A. \\w+", "");
            text = text.replaceAll("§", "S");              //Senate character????

        } else if (billType.equalsIgnoreCase("J")) {
            text = text.replaceFirst("Senate Resolution No. \\d+", "Legislative Resolution");
            text = text.replaceFirst("BY: Senator \\w+", "");

        } else if (billType.equalsIgnoreCase("B")) {
            text = text.replaceFirst("Senate Resolution No. \\d+", "");
            text = text.replaceFirst("BY: Senator \\w+", "");

        } else if (billType.equalsIgnoreCase("L")) {
            text = text.replaceFirst("Assembly Resolution No. \\d+", "");
            text = text.replaceFirst("BY: M. of A. \\w+", "");

        }
        text = text.replaceAll("\\s", "");
        text = text.replaceAll("\"", "\\\\\"");
        text = text.replaceAll("-", "");

        //////////Reformatting stuff from api for testing/////////////
        x = x.replaceAll("\\\\n", "");
        x = x.replaceAll("\\s", "");
        x = x.replaceAll("-", "");

        StringDiffer dif = new StringDiffer();
        LinkedList<StringDiffer.Diff> diffs = dif.diff_main(text, x);
    }

}
