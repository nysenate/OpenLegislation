package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.model.base.SessionYear;
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
 * Created by kyle on 3/10/15.
 */
@Repository
public class ScrapedBillTextParser {
    private String billText = "";
    //String billType = null;

    @PostConstruct
    public void init(){
        //this.billType = billType;
        //generateReference(file);
    }
    public String getBillText(File file, String billType) throws IOException{
        Scanner sc = new Scanner(file);
        String printNo = "";
        String sessionYear = "";

        if (sc.hasNextLine()){
            printNo = sc.nextLine();
        }
        if (sc.hasNextLine()) {
            sessionYear = sc.nextLine();
        }
        while (sc.hasNextLine()) {
            billText = billText.concat(sc.nextLine());
        }
        sc.close();
        parseBillTypes(billText, billType);

        return billText;
    }

    public BillTextSpotcheckReference generateReference(File file, String billType) throws IOException {
        String printNo = "";
        String session = "";

        Scanner sc = new Scanner(file);

        //Scanner sc = new Scanner(new File(billTextScraper.getBillTextDirectory(), billTextScraper.getTextFileName()));
        //Scanner scMemo = new Scanner(new File(billTextScraper.getBillMemoDirectory(), billTextScraper.getMemoFileName()));
        //get the printNo and Session year from the first two lines of the file
        if (sc.hasNextLine()){
            printNo = sc.nextLine();
        }
        if (sc.hasNextLine()) {
            session = sc.nextLine();
        }
        SessionYear sessionYear = new SessionYear(Integer.parseInt(session));
        while (sc.hasNextLine()) {
            billText = billText.concat(sc.nextLine());
        }
        sc.close();
        parseBillTypes(billText, billType);

        return new BillTextSpotcheckReference(printNo, sessionYear, LocalDateTime.now(), billText, null, null);
    }

    /**
     *
     * @param text
     * @param billType
     * @throws Exception
     */
    private void parseBillTypes(String text, String billType) throws IOException {
        String x = "\\n                           S T A T E   O F   N E W   Y O R K\\n       ________________________________________________________________________\\n\\n                                           1\\n\\n                              2015-2016 Regular Sessions\\n\\n                                   I N  S E N A T E\\n\\n                                    January 9, 2015\\n                                      ___________\\n\\n       Introduced by Sens. SAVINO, LITTLE, GOLDEN, ROBACH, HANNON -- read twice\\n         and ordered printed, and when printed to be committed to the Committee\\n         on Rules\\n\\n       AN ACT to amend the labor law, in relation to the prohibition of differ-\\n         ential pay because of sex\\n\\n         THE  PEOPLE OF THE STATE OF NEW YORK, REPRESENTED IN SENATE AND ASSEM-\\n       BLY, DO ENACT AS FOLLOWS:\\n\\n    1    Section 1. Subdivision 1 of section 194 of the labor law, as added  by\\n    2  chapter  548  of the laws of 1966, is amended and three new subdivisions\\n    3  2, 3 and 4 are added to read as follows:\\n    4    1. No employee shall be paid a wage at a rate less than  the  rate  at\\n    5  which  an employee of the opposite sex in the same establishment is paid\\n    6  for equal work on a job the performance of which requires  equal  skill,\\n    7  effort  and responsibility, and which is performed under similar working\\n    8  conditions, except where payment is  made  pursuant  to  a  differential\\n    9  based on:\\n   10    a. a seniority system;\\n   11    b. a merit system;\\n   12    c.  a  system  which  measures  earnings  by  quantity  or  quality of\\n   13  production; or\\n   14    d. [any other factor other than sex] A BONA  FIDE  FACTOR  OTHER  THAN\\n   15  SEX, SUCH AS EDUCATION, TRAINING, OR EXPERIENCE.  SUCH FACTOR: (I) SHALL\\n   16  NOT  BE  BASED  UPON OR DERIVED FROM A SEX-BASED DIFFERENTIAL IN COMPEN-\\n   17  SATION AND (II) SHALL BE JOB-RELATED WITH RESPECT  TO  THE  POSITION  IN\\n   18  QUESTION AND SHALL BE CONSISTENT WITH BUSINESS NECESSITY. SUCH EXCEPTION\\n   19  UNDER  THIS PARAGRAPH SHALL NOT APPLY WHEN THE EMPLOYEE DEMONSTRATES (A)\\n   20  THAT AN EMPLOYER USES A PARTICULAR EMPLOYMENT  PRACTICE  THAT  CAUSES  A\\n   21  DISPARATE IMPACT ON THE BASIS OF SEX, (B) THAT AN ALTERNATIVE EMPLOYMENT\\n   22  PRACTICE  EXISTS  THAT  WOULD  SERVE  THE  SAME BUSINESS PURPOSE AND NOT\\n   23  PRODUCE SUCH DIFFERENTIAL, AND (C) THAT  THE  EMPLOYER  HAS  REFUSED  TO\\n   24  ADOPT SUCH ALTERNATIVE PRACTICE.\\n\\n        EXPLANATION--Matter in ITALICS (underscored) is new; matter in brackets\\n                             [ ] is old law to be omitted.\\n                                                                  LBD07113-01-5\\n\\n       S. 1                                2\\n\\n    1    2.  FOR  THE  PURPOSE  OF  SUBDIVISION  ONE OF THIS SECTION, \\\"BUSINESS\\n    2  NECESSITY\\\" SHALL BE DEFINED AS A FACTOR THAT BEARS A MANIFEST  RELATION-\\n    3  SHIP TO THE EMPLOYMENT IN QUESTION.\\n    4    3.  FOR  THE  PURPOSES  OF  SUBDIVISION ONE OF THIS SECTION, EMPLOYEES\\n    5  SHALL BE DEEMED TO WORK IN THE SAME ESTABLISHMENT IF THE EMPLOYEES  WORK\\n    6  FOR  THE  SAME  EMPLOYER  AT WORKPLACES LOCATED IN THE SAME GEOGRAPHICAL\\n    7  REGION, NO LARGER THAN A COUNTY, TAKING INTO ACCOUNT POPULATION DISTRIB-\\n    8  UTION, ECONOMIC ACTIVITY, AND/OR THE PRESENCE OF MUNICIPALITIES.\\n    9    4. (A) NO EMPLOYER SHALL PROHIBIT AN EMPLOYEE  FROM  INQUIRING  ABOUT,\\n   10  DISCUSSING,  OR DISCLOSING THE WAGES OF SUCH EMPLOYEE OR ANOTHER EMPLOY-\\n   11  EE.\\n   12    (B) AN EMPLOYER MAY, IN A WRITTEN POLICY PROVIDED  TO  ALL  EMPLOYEES,\\n   13  ESTABLISH  REASONABLE  WORKPLACE  AND  WORKDAY  LIMITATIONS ON THE TIME,\\n   14  PLACE AND MANNER FOR INQUIRES ABOUT, DISCUSSION OF, OR THE DISCLOSURE OF\\n   15  WAGES. SUCH LIMITATIONS SHALL BE CONSISTENT WITH  STANDARDS  PROMULGATED\\n   16  BY  THE  COMMISSIONER  AND  SHALL BE CONSISTENT WITH ALL OTHER STATE AND\\n   17  FEDERAL LAWS. SUCH LIMITATIONS MAY INCLUDE PROHIBITING AN EMPLOYEE  FROM\\n   18  DISCUSSING  OR  DISCLOSING  THE  WAGES  OF ANOTHER EMPLOYEE WITHOUT SUCH\\n   19  EMPLOYEE'S PRIOR PERMISSION.\\n   20    (C) NOTHING IN THIS SUBDIVISION SHALL REQUIRE AN EMPLOYEE TO  DISCLOSE\\n   21  HIS  OR HER WAGES.  THE FAILURE OF AN EMPLOYEE TO ADHERE TO SUCH REASON-\\n   22  ABLE LIMITATIONS IN SUCH WRITTEN POLICY SHALL BE AN AFFIRMATIVE  DEFENSE\\n   23  TO  ANY CLAIMS MADE AGAINST AN EMPLOYER UNDER THIS SUBDIVISION, PROVIDED\\n   24  THAT ANY ADVERSE EMPLOYMENT ACTION TAKEN BY THE EMPLOYER WAS FOR FAILURE\\n   25  TO ADHERE TO SUCH REASONABLE  LIMITATIONS  AND  NOT  FOR  MERE  INQUIRY,\\n   26  DISCUSSION  OR  DISCLOSURE  OF  WAGES IN ACCORDANCE WITH SUCH REASONABLE\\n   27  LIMITATIONS IN SUCH WRITTEN POLICY.\\n   28    (D) THIS PROHIBITION SHALL NOT APPLY TO INSTANCES IN WHICH AN EMPLOYEE\\n   29  WHO HAS ACCESS TO THE WAGE INFORMATION OF OTHER EMPLOYEES AS A  PART  OF\\n   30  SUCH  EMPLOYEE'S  ESSENTIAL  JOB  FUNCTIONS  DISCLOSES THE WAGES OF SUCH\\n   31  OTHER EMPLOYEES TO INDIVIDUALS WHO DO NOT OTHERWISE HAVE ACCESS TO  SUCH\\n   32  INFORMATION,  UNLESS  SUCH  DISCLOSURE  IS IN RESPONSE TO A COMPLAINT OR\\n   33  CHARGE, OR IN FURTHERANCE OF AN INVESTIGATION, PROCEEDING,  HEARING,  OR\\n   34  ACTION  UNDER  THIS CHAPTER, INCLUDING AN INVESTIGATION CONDUCTED BY THE\\n   35  EMPLOYER.\\n   36    (E) NOTHING IN THIS SECTION SHALL BE CONSTRUED TO LIMIT THE RIGHTS  OF\\n   37  AN  EMPLOYEE  PROVIDED  UNDER  ANY  OTHER PROVISION OF LAW OR COLLECTIVE\\n   38  BARGAINING AGREEMENT.\\n   39    S 2. Subdivision 1-a of section 198 of the labor law,  as  amended  by\\n   40  chapter 564 of the laws of 2010, is amended to read as follows:\\n   41    1-a.  On behalf of any employee paid less than the wage to which he or\\n   42  she is entitled under the provisions of this article,  the  commissioner\\n   43  may  bring  any legal action necessary, including administrative action,\\n   44  to collect such claim and as part of such legal action, in  addition  to\\n   45  any other remedies and penalties otherwise available under this article,\\n   46  the  commissioner  shall  assess against the employer the full amount of\\n   47  any such underpayment, and an additional amount as  liquidated  damages,\\n   48  unless  the  employer  proves  a good faith basis for believing that its\\n   49  underpayment of wages was in compliance with the law. Liquidated damages\\n   50  shall be calculated by the commissioner as  no  more  than  one  hundred\\n   51  percent of the total amount of wages found to be due, EXCEPT SUCH LIQUI-\\n   52  DATED  DAMAGES MAY BE UP TO THREE HUNDRED PERCENT OF THE TOTAL AMOUNT OF\\n   53  THE WAGES FOUND TO BE DUE FOR A WILLFUL VIOLATION OF SECTION ONE HUNDRED\\n   54  NINETY-FOUR OF THIS ARTICLE. In any action instituted in the courts upon\\n   55  a wage claim by an employee or the commissioner in  which  the  employee\\n   56  prevails, the court shall allow such employee to recover the full amount\\n\\n       S. 1                                3\\n\\n    1  of  any underpayment, all reasonable attorney's fees, prejudgment inter-\\n    2  est as required under the civil practice law and rules, and, unless  the\\n    3  employer  proves  a good faith basis to believe that its underpayment of\\n    4  wages was in compliance with the law, an additional amount as liquidated\\n    5  damages  equal  to  one hundred percent of the total amount of the wages\\n    6  found to be due, EXCEPT SUCH LIQUIDATED  DAMAGES  MAY  BE  UP  TO  THREE\\n    7  HUNDRED  PERCENT  OF THE TOTAL AMOUNT OF THE WAGES FOUND TO BE DUE FOR A\\n    8  WILLFUL VIOLATION OF SECTION ONE HUNDRED NINETY-FOUR OF THIS ARTICLE.\\n    9    S 3. The department of labor and the division of  human  rights  shall\\n   10  make  training  available  to  assist  employers in developing training,\\n   11  policies and procedures to address discrimination and harassment in  the\\n   12  workplace  including,  but  not limited to issues relating to pregnancy,\\n   13  familial status, pay equity and sexual harassment.  Such training  shall\\n   14  take  into  account the needs of employers of various sizes. The depart-\\n   15  ment and division shall make such training available through,  including\\n   16  but  not  limited to, online means.  In developing such training materi-\\n   17  als, the department and division shall afford the public an  opportunity\\n   18  to submit comments on such training.\\n   19    S 4. Severability clause. If any clause, sentence, paragraph, subdivi-\\n   20  sion, section or part of this act shall be adjudged by a court of compe-\\n   21  tent  jurisdiction to be invalid, such judgment shall not affect, impair\\n   22  or invalidate the remainder thereof, but shall be confined in its opera-\\n   23  tion to the clause, sentence, paragraph, subdivision,  section  or  part\\n   24  thereof  directly  involved  in  the  controversy in which such judgment\\n   25  shall have been rendered. It is hereby declared to be the intent of  the\\n   26  legislature  that  this act would have been enacted even if such invalid\\n   27  provisions had not been included herein.\\n   28    S 5. This act shall take effect on the ninetieth day  after  it  shall\\n   29  have  become  a  law;  provided, however, that the commissioner of labor\\n   30  shall take actions necessary to provide for the promulgation  of  stand-\\n   31  ards pursuant to subdivision 4 of section 194 of the labor law, as added\\n   32  by  section  one  of  this  act,  prior  to  this act taking effect; and\\n   33  provided further, however, that the department of labor and division  of\\n   34  human rights shall take actions necessary to establish training pursuant\\n   35  to section three of this act prior to this act taking effect.\\n";

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
