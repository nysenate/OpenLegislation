package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.model.spotcheck.billtext.BillTextSpotcheckReference;
import gov.nysenate.openleg.util.StringDiffer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
        String x = "BILL NUMBER:S1\\n\\nTITLE OF BILL:  An act to amend the labor law, in relation to the\\nprohibition of differential pay because of sex\\n\\nSUMMARY OF BILLS:\\n\\n1) Pay Equity\\n\\nThis bill would amend Labor L Â§ 194, which prohibits a differential in\\nrate of pay because of sex, to replace the current \\\"any other factor\\nother than sex\\\" exception with an exception that requires that the\\ndifferential in rate of pay he based on a bona fide factor other than\\nsex such as education, training or experience. Such a factor could not\\nbe based on a sex-based differential, and must be job-related arid\\nconsistent with business necessity. This standard would mirror the\\ncurrent defense afforded to employers in disparate impact cases under\\nTitle VII of the Civil Rights Act. The exception would not apply if\\nthe employee demonstrated that an employer uses a \\\"particular\\nemployment practice that causes a disparate impact on the basis of sex\\nand that there was an alternative employment practice that would\\naccomplish the same-business purpose and the employer has refused to\\nadopt such a practice\\\". \\\"Business necessity\\\" would be defined as a\\nfactor that bears a manifest relationship to the employment in\\nquestion, the definition enunciated by the Supreme Court in Griggs v.\\nDuke Power. Co., 401 U S. 424 (1971) and subsequent cases\\nAdditionally, Â§ 194 would be amended to clarify that a differential in\\npay may not exist even if the two employees whose rate of pay is being\\ncompared work in different physical locations, provided that those\\nlocations were in the same geographic region Section 194 would also be\\namended to forbid employers from prohibiting employees from sharing\\nwage information This protection helps guarantee the individual state\\nright of equal pay, without interfering with current federal\\nprovisions regarding collective protected activity aimed at \\\"mutual\\naid and protection\\\".\\n\\nThis bill would also:\\n\\n*amend Labor L Â§ 198 to increase the willful violations of Â§ 194 to up\\nto and\\n\\n*require the Department of Labor and make training available to\\nemployers discrimination and harassment in the liquidated damages\\nallowed for 300% of the wages found to be due; the Division of Human\\nRights to to assist them in preventing workplace.\\n\\nEXISTING LAW:\\n\\nThis bill would impact existing protections that are available under\\nthe Labor Law.\\n\\nSTATEMENT IN SUPPORT:\\n\\n* Achieving Pay Equity\\n\\n\\nWage disparities have a detrimental effect on Society Individuals are\\nput at an economic disadvantage because of characteristics that bear\\nno relationship to their job performance. Such disparities prevent\\nmaximum utilization of labor in the state economy. Additionally,\\npolicies adopted by employers that discourage or prohibit employees\\nfrom sharing information about their earnings can contribute to unjust\\nwage disparities going undetected. Despite existing protections under\\nthe law, women in New York earn 84 percent of what men earn and jobs\\ntraditionally held by women pay significantly less than jobs\\npredominately employing men. In New York, on average, a woman working\\nfull time is paid $42,113 per year, while a man working full time is\\npaid $50,388 per year. This creates a wage gap of $8,275 between\\nfull-time working men and women in the state.\\n\\nThis bill would amend existing law to ensure that women receive the\\nwages they were always entitled to, as well as provide for increased\\nliquidated damages. Individuals who were paid unequal wages would be\\nentitled to liquidated damages of up to 300% of the amount of unpaid\\nwages Existing exemptions in the law would be tightened so that pay\\ndifferentials are excused where the employer can show that the\\ndifferential is truly caused by something other than sex and is\\nrelated to job performance and consistent with business necessity\\nEmployers would also be prohibited from forbidding employees from\\nsharing wage information that would otherwise deny women workers the\\nability to discover whether their wages are unequal to their male\\ncounterparts.\\n\\nBUDGET IMPLICATIONS:\\n\\nThis bill has no budget implications for the State.\\n\\nEFFECTIVE DATE:\\n\\nThis bill would take effect 90 days after enactment.\\n\\n";

        text = text.replaceAll("\\s", "");
        text = text.replaceAll("\"", "\\\\\"");
        text = text.replaceAll("-", "");
        text = text.replaceAll("\u00A0", "");
        text = text.replaceFirst("SPONSOR: \\S+", "");
        text = text.replaceAll("§", "Â§");                             //strange memo character change

        //////////Reformatting stuff from api for testing/////////////
        x = x.replaceAll("\\\\n", "");
        x = x.replaceAll("\\s", "");
        x = x.replaceAll("-", "");

        StringDiffer dif = new StringDiffer();
        LinkedList<StringDiffer.Diff> diffs = dif.diff_main(text, x);
    }
}
