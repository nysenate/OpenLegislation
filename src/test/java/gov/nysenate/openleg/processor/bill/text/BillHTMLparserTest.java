package gov.nysenate.openleg.processor.bill.text;

import gov.nysenate.openleg.util.BillTextUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Created by uros on 3/30/17. Tested for XML BILLTEXT AND SENMEMO FILES
 */
@Transactional
public class BillHTMLparserTest extends BillTextUtils {

    private final File testFileDir = new File(
           getClass().getClassLoader().getResource("sourcefile/").getFile());

            /**
             * Text XML BillTEXT files
             */
    @Test
    public void billTextHTMLparse() {
        String hText = "\n" +
                "<STYLE><!--U  {color: Green}S  {color: RED} I  {color: DARKBLUE; background-color:yellow}\n" +
                "P.brk {page-break-before:always}--></STYLE>\n" +
                "<BASEFONT SIZE=3>\n" +
                "<PRE WIDTH=\"94\">\n" +
                " \n" +
                "<FONT SIZE=5><B>                STATE OF NEW YORK</B></FONT>\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "                                          5457\n" +
                " \n" +
                "                               2017-2018 Regular Sessions\n" +
                " \n" +
                "<FONT SIZE=5><B>                   IN ASSEMBLY</B></FONT>\n" +
                " \n" +
                "                                    February 9, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        Introduced by M. of A. WEPRIN -- read once and referred to the Committee\n" +
                "          on Governmental Operations\n" +
                " \n" +
                "        AN  ACT to amend the state finance law, in relation to products manufac-\n" +
                "          tured under conditions that fail to comply with minimum OSHA standards\n" +
                " \n" +
                "          <B><U>The People of the State of New York, represented in Senate and  Assem-</U></B>\n" +
                "        <B><U>bly, do enact as follows:</U></B>\n" +
                " \n" +
                "     1    Section 1. Section 165 of the state finance law is amended by adding a\n" +
                "     2  new subdivision 9 to read as follows:\n" +
                "     3    <B><U>9. Products manufactured in compliance with minimum Occupational Safe-</U></B>\n" +
                "     4  <B><U>ty and Health Act (OSHA) standards.</U></B>\n" +
                "     5    <B><U>a.  As  used  in this subdivision, \"minimum OSHA standards\" shall mean</U></B>\n" +
                "     6  <B><U>those standards issued pursuant to the Occupational  Safety  and  Health</U></B>\n" +
                "     7  <B><U>Act of 1970, as amended.</U></B>\n" +
                "     8    <B><U>b.  The  commissioner  of general services shall have the power and it</U></B>\n" +
                "     9  <B><U>shall be his or her duty to prepare a list  of  all  products  that  are</U></B>\n" +
                "    10  <B><U>manufactured  under  conditions  that  fail  to  observe:  (i) standards</U></B>\n" +
                "    11  <B><U>promulgated under OSHA; or (ii) standards that are at least as effective</U></B>\n" +
                "    12  <B><U>as the standards promulgated under OSHA, which relate to the same issues</U></B>\n" +
                "    13  <B><U>thereunder, where such a manufacturing operation occurs in  a  jurisdic-</U></B>\n" +
                "    14  <B><U>tion not otherwise required to comply with such standards, including any</U></B>\n" +
                "    15  <B><U>operation  which  takes  place in any other country, nation or province.</U></B>\n" +
                "    16  <B><U>The commissioner of general services shall prepare a companion  list  of</U></B>\n" +
                "    17  <B><U>the manufacturers of such products. The commissioner of general services</U></B>\n" +
                "    18  <B><U>shall  add  or  delete  from said lists any product or manufacturer upon</U></B>\n" +
                "    19  <B><U>good cause shown. The commissioner of general services shall cause  such</U></B>\n" +
                "    20  <B><U>lists to be published on the website of the office of general services.</U></B>\n" +
                "    21    <B><U>c. A state agency shall not enter into any contract for procurement of</U></B>\n" +
                "    22  <B><U>a  (i)  product  contained  on  the list prepared by the commissioner of</U></B>\n" +
                "    23  <B><U>general services pursuant to paragraph b of this  subdivision;  or  (ii)</U></B>\n" +
                "    24  <B><U>product manufacturer by a manufacturer contained on the list prepared by</U></B>\n" +
                "    25  <B><U>the  commissioner  of  general  services pursuant to paragraph b of this</U></B>\n" +
                " \n" +
                "         EXPLANATION--Matter in <B><U>italics</U></B> (underscored) is new; matter in brackets\n" +
                "                              [<B><S> </S></B>] is old law to be omitted.\n" +
                "                                                                   LBD00491-01-7\n" +
                "</PRE><P CLASS=\"brk\"><PRE WIDTH=\"94\">\n" +
                "        A. 5457                             2\n" +
                " \n" +
                "     1  <B><U>subdivision. The provisions of this paragraph may be waived by the  head</U></B>\n" +
                "     2  <B><U>of  the state agency if the head of the state agency determines in writ-</U></B>\n" +
                "     3  <B><U>ing that it is in the best interests of the state to do so. The head  of</U></B>\n" +
                "     4  <B><U>the  state  agency shall deliver each such waiver to the commissioner of</U></B>\n" +
                "     5  <B><U>general services.</U></B>\n" +
                "     6    &#167; 2. This act shall take effect on the ninetieth day  after  it  shall\n" +
                "     7  have become a law.\n" +
                "</pre>\n";

        String preTagContext = parseHTMLtext(hText);


        String sample ="\n" +
                " \n" +
                "                           S T A T E   O F   N E W   Y O R K\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "                                          5457\n" +
                " \n" +
                "                               2017-2018 Regular Sessions\n" +
                " \n" +
                "                                 I N  A S S E M B L Y\n" +
                " \n" +
                "                                    February 9, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        Introduced by M. of A. WEPRIN -- read once and referred to the Committee\n" +
                "          on Governmental Operations\n" +
                " \n" +
                "        AN  ACT to amend the state finance law, in relation to products manufac-\n" +
                "          tured under conditions that fail to comply with minimum OSHA standards\n" +
                " \n" +
                "          THE PEOPLE OF THE STATE OF NEW YORK, REPRESENTED IN SENATE AND ASSEM-\n" +
                "        BLY, DO ENACT AS FOLLOWS:\n" +
                " \n" +
                "     1    Section 1. Section 165 of the state finance law is amended by adding a\n" +
                "     2  new subdivision 9 to read as follows:\n" +
                "     3    9. PRODUCTS MANUFACTURED IN COMPLIANCE WITH MINIMUM OCCUPATIONAL SAFE-\n" +
                "     4  TY AND HEALTH ACT (OSHA) STANDARDS.\n" +
                "     5    A. AS USED IN THIS SUBDIVISION, \"MINIMUM OSHA STANDARDS\" SHALL MEAN\n" +
                "     6  THOSE STANDARDS ISSUED PURSUANT TO THE OCCUPATIONAL SAFETY AND HEALTH\n" +
                "     7  ACT OF 1970, AS AMENDED.\n" +
                "     8    B. THE COMMISSIONER OF GENERAL SERVICES SHALL HAVE THE POWER AND IT\n" +
                "     9  SHALL BE HIS OR HER DUTY TO PREPARE A LIST OF ALL PRODUCTS THAT ARE\n" +
                "    10  MANUFACTURED UNDER CONDITIONS THAT FAIL TO OBSERVE: (I) STANDARDS\n" +
                "    11  PROMULGATED UNDER OSHA; OR (II) STANDARDS THAT ARE AT LEAST AS EFFECTIVE\n" +
                "    12  AS THE STANDARDS PROMULGATED UNDER OSHA, WHICH RELATE TO THE SAME ISSUES\n" +
                "    13  THEREUNDER, WHERE SUCH A MANUFACTURING OPERATION OCCURS IN A JURISDIC-\n" +
                "    14  TION NOT OTHERWISE REQUIRED TO COMPLY WITH SUCH STANDARDS, INCLUDING ANY\n" +
                "    15  OPERATION WHICH TAKES PLACE IN ANY OTHER COUNTRY, NATION OR PROVINCE.\n" +
                "    16  THE COMMISSIONER OF GENERAL SERVICES SHALL PREPARE A COMPANION LIST OF\n" +
                "    17  THE MANUFACTURERS OF SUCH PRODUCTS. THE COMMISSIONER OF GENERAL SERVICES\n" +
                "    18  SHALL ADD OR DELETE FROM SAID LISTS ANY PRODUCT OR MANUFACTURER UPON\n" +
                "    19  GOOD CAUSE SHOWN. THE COMMISSIONER OF GENERAL SERVICES SHALL CAUSE SUCH\n" +
                "    20  LISTS TO BE PUBLISHED ON THE WEBSITE OF THE OFFICE OF GENERAL SERVICES.\n" +
                "    21    C. A STATE AGENCY SHALL NOT ENTER INTO ANY CONTRACT FOR PROCUREMENT OF\n" +
                "    22  A (I) PRODUCT CONTAINED ON THE LIST PREPARED BY THE COMMISSIONER OF\n" +
                "    23  GENERAL SERVICES PURSUANT TO PARAGRAPH B OF THIS SUBDIVISION; OR (II)\n" +
                "    24  PRODUCT MANUFACTURER BY A MANUFACTURER CONTAINED ON THE LIST PREPARED BY\n" +
                "    25  THE COMMISSIONER OF GENERAL SERVICES PURSUANT TO PARAGRAPH B OF THIS\n" +
                " \n" +
                "         EXPLANATION--Matter in ITALICS (underscored) is new; matter in brackets\n" +
                "                              [ ] is old law to be omitted.\n" +
                "                                                                   LBD00491-01-7\n" +
                "\n" +
                "        A. 5457                             2\n" +
                " \n" +
                "     1  SUBDIVISION. THE PROVISIONS OF THIS PARAGRAPH MAY BE WAIVED BY THE HEAD\n" +
                "     2  OF THE STATE AGENCY IF THE HEAD OF THE STATE AGENCY DETERMINES IN WRIT-\n" +
                "     3  ING THAT IT IS IN THE BEST INTERESTS OF THE STATE TO DO SO. THE HEAD OF\n" +
                "     4  THE STATE AGENCY SHALL DELIVER EACH SUCH WAIVER TO THE COMMISSIONER OF\n" +
                "     5  GENERAL SERVICES.\n" +
                "     6    § 2. This act shall take effect on the ninetieth day  after  it  shall\n" +
                "     7  have become a law.\n";
        assertEquals("The files are the same",preTagContext,sample);
    }

    @Test
    public void BudgetBillTextHTMLparseTest() {
        String htmlText = "Filler";
        String parsedText;
        String expectedAnswer = "AlsoFiller";

        File htmlEnhancedFile = new File(testFileDir, "BudgetBillTextHTMLParse.txt");
        File expectedAnswerFile = new File(testFileDir, "BudgetBillTextExpected.txt");

        try {
            htmlText = FileUtils.readFileToString(htmlEnhancedFile);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not convert BudgetBillTextHTMLParse to a string");
        }
        try {
            expectedAnswer = FileUtils.readFileToString(expectedAnswerFile);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not convert BudgetBillTextExpected to a string");
        }

        long startTime = System.nanoTime();


        parsedText = parseHTMLtext(htmlText);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("It took " + duration + "ms");
        assertEquals("The texts are the same", expectedAnswer,parsedText);

    }


    /**
     * Tests XML SENMEMO files
     */
    @Test
    public void senMemoHTMLparseTest()  {
        String inputText ="\n" +
                "<center><B>NEW YORK STATE SENATE<BR>INTRODUCER'S MEMORANDUM IN SUPPORT<BR>submitted in accordance with Senate Rule VI. Sec 1<br></b></center>\n" +
                "<STYLE>\n" +
                "<!--\n" +
                "U  {color: Green}\n" +
                "S  {color: RED}\n" +
                "I  {color: DARKBLUE; background-color:yellow}\n" +
                "P.brk {page-break-before:always}\n" +
                "-->\n" +
                "</STYLE>\n" +
                "<BASEFONT SIZE=3>\n" +
                "<PRE WIDTH=\"80\">\n" +
                "&nbsp\n" +
                "<B><U>BILL NUMBER:</U></B> S2458A\n" +
                " \n" +
                "<B><U>SPONSOR:</U></B> HAMILTON<BR>\n" +
                "&nbsp\n" +
                "<B><U>TITLE OF BILL</U></B>:  An act to amend the transportation law, in relation to\n" +
                "enacting the \"taxi driver protection act\" requiring the posting of signs\n" +
                "in for-hire vehicles to prevent assault\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>PURPOSE OR GENERAL IDEA OF THE BILL</U></B>:\n" +
                " \n" +
                "To protect Drivers from being assaulted on the job by posting signs in\n" +
                "For Hire vehicles alerting passengers of the punishment for assaulting\n" +
                "drivers.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>SUMMARY OF SPECIFIC PROVISIONS</U></B>:\n" +
                " \n" +
                "Section 1 titles the bill the \"taxi driver protection act.\"\n" +
                " \n" +
                "Section 2 amends section 140 of the transportation law to allow every\n" +
                "for-hire vehicle to post a sign on the interior of such vehicle that\n" +
                "states \"ATTENTION: Assaulting a Taxi Driver Is Punishable by up To Twen-\n" +
                "ty-Five Years in Prison.\"\n" +
                " \n" +
                "Section 3 provides the Commissioner to promulgate any rules and regu-\n" +
                "lations necessary form the implementation as well as determining a\n" +
                "punishment for failure to post signs.\n" +
                " \n" +
                "Section 4 provides for the act to take effect on the 180th Day.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>JUSTIFICATION</U></B>:\n" +
                " \n" +
                "According to the Occupational Safety and Health Administration, taxi\n" +
                "workers are thirty times more likely to be killed on the job than other\n" +
                "workers are. New York City is home to the largest taxi driver industry\n" +
                "in the country. Posting warning signs in taxicabs would facilitate a\n" +
                "public information campaign designed to inform the public that attacks\n" +
                "on taxi workers are a very serious offense.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>PRIOR LEGISLATIVE HISTORY</U></B>:\n" +
                " \n" +
                "S. 4355-A of 2015/16 Legislative Session\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>FISCAL IMPLICATIONS FOR STATE AND LOCAL GOVERNMENTS</U></B>:\n" +
                " \n" +
                "None.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>EFFECTIVE DATE</U></B>:\n" +
                "This ACT shall take effect on the 180th Day\n" +
                "</pre>\n";

        String preTagContext = parseHTMLtext(inputText);

        String output = "\n" +
                " \n" +
                "BILL NUMBER: S2458A\n" +
                " \n" +
                "SPONSOR: HAMILTON\n" +
                " \n" +
                "TITLE OF BILL:  An act to amend the transportation law, in relation to\n" +
                "enacting the \"taxi driver protection act\" requiring the posting of signs\n" +
                "in for-hire vehicles to prevent assault\n" +
                " \n" +
                " \n" +
                "PURPOSE OR GENERAL IDEA OF THE BILL:\n" +
                " \n" +
                "To protect Drivers from being assaulted on the job by posting signs in\n" +
                "For Hire vehicles alerting passengers of the punishment for assaulting\n" +
                "drivers.\n" +
                " \n" +
                " \n" +
                "SUMMARY OF SPECIFIC PROVISIONS:\n" +
                " \n" +
                "Section 1 titles the bill the \"taxi driver protection act.\"\n" +
                " \n" +
                "Section 2 amends section 140 of the transportation law to allow every\n" +
                "for-hire vehicle to post a sign on the interior of such vehicle that\n" +
                "states \"ATTENTION: Assaulting a Taxi Driver Is Punishable by up To Twen-\n" +
                "ty-Five Years in Prison.\"\n" +
                " \n" +
                "Section 3 provides the Commissioner to promulgate any rules and regu-\n" +
                "lations necessary form the implementation as well as determining a\n" +
                "punishment for failure to post signs.\n" +
                " \n" +
                "Section 4 provides for the act to take effect on the 180th Day.\n" +
                " \n" +
                " \n" +
                "JUSTIFICATION:\n" +
                " \n" +
                "According to the Occupational Safety and Health Administration, taxi\n" +
                "workers are thirty times more likely to be killed on the job than other\n" +
                "workers are. New York City is home to the largest taxi driver industry\n" +
                "in the country. Posting warning signs in taxicabs would facilitate a\n" +
                "public information campaign designed to inform the public that attacks\n" +
                "on taxi workers are a very serious offense.\n" +
                " \n" +
                " \n" +
                "PRIOR LEGISLATIVE HISTORY:\n" +
                " \n" +
                "S. 4355-A of 2015/16 Legislative Session\n" +
                " \n" +
                " \n" +
                "FISCAL IMPLICATIONS FOR STATE AND LOCAL GOVERNMENTS:\n" +
                " \n" +
                "None.\n" +
                " \n" +
                " \n" +
                "EFFECTIVE DATE:\n" +
                "This ACT shall take effect on the 180th Day\n";

        assertTrue(preTagContext.equals(output));
    }
}
