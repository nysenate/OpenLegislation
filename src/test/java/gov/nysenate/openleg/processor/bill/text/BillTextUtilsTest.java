package gov.nysenate.openleg.processor.bill.text;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.util.BillTextUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class BillTextUtilsTest {
    final String bill = "\n<STYLE><!--U  {color: Green}S  {color: RED} I  {color: DARKBLUE; background-color:yellow}\n" +
            "        P.brk {page-break-before:always}--></STYLE>\n" +
            "<BASEFONT SIZE=3>\n" +
            "<PRE WIDTH=\"136\">\n" +
            "\n" +
            "<FONT SIZE=5><B>                STATE OF NEW YORK</B></FONT>\n" +
            "                ________________________________________________________________________\n" +
            "        155\n" +
            "        2019-2020 Regular Sessions\n" +
            "\n" +
            "<FONT SIZE=5><B>                    IN SENATE</B></FONT>\n" +
            "                                       <B><U>(Prefiled)</U></B>\n" +
            "!!text has been abridged for test purposes!!\n" +
            "                EXPLANATION--Matter in <B><U>italics</U></B> (underscored) is new; matter in brackets\n" +
            "                [<B><S>removed text</S></B>] is old law to be omitted.\n" +
            "        LBD04958-01-9\n";


    @Test
    public void senateBillTextFormatTest() {
        String inputText = " \n" +
                "                STATE OF NEW YORK\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "                                         100--A\n" +
                " \n" +
                "                               2017-2018 Regular Sessions\n" +
                " \n" +
                "                    IN SENATE\n" +
                " \n" +
                "                                       (Prefiled)\n" +
                " \n" +
                "                                     January 4, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        Introduced  by  Sen. HOYLMAN -- read twice and ordered printed, and when\n" +
                "          printed to be committed to the Committee  on  Consumer  Protection  --\n" +
                "          recommitted to the Committee on Consumer Protection in accordance with\n" +
                "          Senate  Rule  6, sec. 8 -- committee discharged, bill amended, ordered";
        String expectedResult = " \n" +
                "                            S T A T E   O F   N E W   Y O R K\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "                                         100--A\n" +
                " \n" +
                "                               2017-2018 Regular Sessions\n" +
                " \n" +
                "                                    I N  S E N A T E\n" +
                " \n" +
                "                                       (PREFILED)\n" +
                " \n" +
                "                                     January 4, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        Introduced  by  Sen. HOYLMAN -- read twice and ordered printed, and when\n" +
                "          printed to be committed to the Committee  on  Consumer  Protection  --\n" +
                "          recommitted to the Committee on Consumer Protection in accordance with\n" +
                "          Senate  Rule  6, sec. 8 -- committee discharged, bill amended, ordered";
        assertEquals(expectedResult, BillTextUtils.formatHtmlExtractedBillText(inputText));
    }

    @Test
    public void assemblyBillTextFormatTest() {
        String inputText = " \n" +
                "                STATE OF NEW YORK\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "                                         1051--A\n" +
                "                                                                 Cal. No. 17\n" +
                " \n" +
                "                               2017-2018 Regular Sessions\n" +
                " \n" +
                "                   IN ASSEMBLY\n" +
                " \n" +
                "                                    January 10, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        Introduced  by  M.  of  A.  SIMON,  ARROYO,  BLAKE, BARRETT, BRAUNSTEIN,";
        String expectedResult = " \n" +
                "                            S T A T E   O F   N E W   Y O R K\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "                                         1051--A\n" +
                "                                                                 Cal. No. 17\n" +
                " \n" +
                "                               2017-2018 Regular Sessions\n" +
                " \n" +
                "                                  I N  A S S E M B L Y\n" +
                " \n" +
                "                                    January 10, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        Introduced  by  M.  of  A.  SIMON,  ARROYO,  BLAKE, BARRETT, BRAUNSTEIN,";
        assertEquals(expectedResult, BillTextUtils.formatHtmlExtractedBillText(inputText));
    }

    @Test
    public void uniBillTextFormatTest() {
        String inputText = " \n" +
                "                STATE OF NEW YORK\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "            S. 2005--C                                            A. 3005--C\n" +
                " \n" +
                "                SENATE - ASSEMBLY\n" +
                " \n" +
                "                                    January 23, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        IN  SENATE -- A BUDGET BILL, submitted by the Governor pursuant to arti-";
        String expectedResult = " \n" +
                "                            S T A T E   O F   N E W   Y O R K\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "            S. 2005--C                                            A. 3005--C\n" +
                " \n" +
                "                              S E N A T E - A S S E M B L Y\n" +
                " \n" +
                "                                    January 23, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        IN  SENATE -- A BUDGET BILL, submitted by the Governor pursuant to arti-";
        assertEquals(expectedResult, BillTextUtils.formatHtmlExtractedBillText(inputText));
    }

    @Test
    public void resolutionReplacesFirstLines() {
        String expected = "\nLEGISLATIVE RESOLUTION commemorating  the  30th  Anniversary of ACCORD, A\n" +
                "        Center for Dispute Resolution, Inc.";
        String lrsText = "\n\n" +
                "Senate Resolution No. 4405\n" +
                " \n" +
                "BY: Senator LIBOUS\n" +
                " \n" +
                "        COMMEMORATING  the  30th  Anniversary of ACCORD, A\n" +
                "        Center for Dispute Resolution, Inc.";
        String actual = BillTextUtils.formatHtmlExtractedResoText(lrsText);
        assertEquals(expected, actual);
    }
}
