package gov.nysenate.openleg.processor.bill.text;

import gov.nysenate.openleg.util.BillTextUtils;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by uros on 3/30/17.
 */
@Transactional
public class BillHTMLparserTest extends BillTextUtils {

    // IN PROGRESS
    @Test
    public void preTag() {
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

    }
}
