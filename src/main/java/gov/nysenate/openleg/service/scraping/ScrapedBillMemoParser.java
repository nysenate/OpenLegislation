package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.util.StringDiffer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by kyle on 3/12/15.
 */
@Repository
public class ScrapedBillMemoParser {
    private String memoText = "";
    private String amendment = "";

    @PostConstruct
    public void init(){

    }

    /**
     *
     * @param file
     * @return the text contained in the memo
     * @throws IOException
     */
    public String getBillMemoText(File file) throws IOException{
        Scanner scMemo = new Scanner(file);

        //check the first line for an amendment at the end. Regardless, it adds it to the memotext
        if (scMemo.hasNextLine()){
            String amend = scMemo.nextLine();
            memoText = memoText.concat(amend);
            amend = amend.substring(amend.length()-1);

            if (amend.matches("\\p{Upper}")){
                this.amendment = amend;
            }

        }
        while (scMemo.hasNextLine()) {
            memoText = memoText.concat(scMemo.nextLine());
        }
        scMemo.close();
        parseBillMemo(memoText);
        return memoText;
    }


    public String getAmendment() throws IOException{
        return this.amendment;
    }

    /**
     *
     * @param text
     * @throws Exception
     */
    public void parseBillMemo(String text) throws IOException{

        text = text.replaceAll("\\s", "");
        text = text.replaceAll("\"", "\\\\\"");
        text = text.replaceAll("-", "");
        text = text.replaceAll("\u00A0", "");
        text = text.replaceFirst("SPONSOR: \\S+", "");
        text = text.replaceAll("§", "Â§");                             //strange memo character change

    }
}
