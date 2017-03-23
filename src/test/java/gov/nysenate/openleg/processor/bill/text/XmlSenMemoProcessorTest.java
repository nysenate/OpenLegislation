package gov.nysenate.openleg.processor.bill.text;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.sponsor.XmlSenMemoProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


/**
 * Created by uros on 3/23/17.
 */
@Transactional
public class XmlSenMemoProcessorTest extends BaseXmlProcessorTest {

    @Autowired private BillDao billDao;
    @Autowired private XmlSenMemoProcessor senMemo;


    @Override
    protected SobiProcessor getSobiProcessor() {
        return senMemo;
    }

    @Test
    public void processSimpleSenMemo()  {
        String xmlPath="processor/bill/senmemo/2016-11-17-10.10.49.554307_SENMEMO_S00100.XML";
        processXmlFile(xmlPath);

        Bill bill = billDao.getBill(new BillId("S0100",2015));


        BillAmendment amendment = bill.getAmendment(Version.DEFAULT);

        BillAmendment sample = new BillAmendment(new BaseBillId("S0100",2015),Version.DEFAULT);
        sample.setMemo("\n" +
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
                "<B><U>BILL NUMBER:</U></B> S100\n" +
                " \n" +
                "<B><U>SPONSOR:</U></B> BOYLE<BR>\n" +
                "&nbsp\n" +
                "<B><U>TITLE OF BILL</U></B>:  An act to amend the penal law, in relation to creating\n" +
                "a presumption of intent to sell\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>PURPOSE OR GENERAL IDEA OF BILL</U></B>:\n" +
                " \n" +
                "To assist in the prosecution of heroin dealers who are profiting from\n" +
                "the epidemic that is claiming so many young lives, by creating a\n" +
                "presumption that the possession of 50 or more individual packages\n" +
                "containing heroin and/or having an aggregate value of $300.00 is\n" +
                "possession with intent to sell.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>SUMMARY OF SPECIFIC PROVISIONS</U></B>:\n" +
                " \n" +
                "Section 1. The penal law is amended by adding a new section 220.26 that\n" +
                "for the purposes of a prosecution of Criminal Possession of a Controlled\n" +
                "Substance in the Third Degree where the person knowingly and unlawfully\n" +
                "possesses a narcotic drug with intent to sell (PL 220.16 (1), fifty or\n" +
                "more individual packages of heroin, or heroin with the aggregate value\n" +
                "of three hundred dollars or more is presumptive evidence of intent to\n" +
                "sell.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>JUSTIFICATION</U></B>:\n" +
                " \n" +
                "Heroin is destroying the fabric of our society, evidenced by the daily\n" +
                "news of overdose deaths throughout New York State. Because of the phys-\n" +
                "ical nature of heroin, dealers can carry large quantities of the drug\n" +
                "before triggering a felony charge of possession. Conversely, due to the\n" +
                "nature of heroin use and addiction, heroin users do not possess more\n" +
                "heroin than they intend to use at that time, as one high on heroin has\n" +
                "no impulse control and will continue to consume all heroin available\n" +
                "until it is gone. Some prosecutors have called expert witnesses to\n" +
                "testify to the nature of this addiction to support a prosecution for\n" +
                "Criminal Possession of a Controlled Substance in the Third Degree under\n" +
                "the intent to sell subdivision, but not all prosecuting offices have the\n" +
                "means to do so, yet are still faced with the increase in heroin sales\n" +
                "and heroin overdose deaths. This bill would provide all communities with\n" +
                "the necessary tool to target heroin dealers and stop the flow of this\n" +
                "dangerous drug.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>PRIOR LEGISLATIVE HISTORY</U></B>:\n" +
                " \n" +
                "None\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>FISCAL IMPLICATIONS</U></B>:\n" +
                " \n" +
                "None\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>EFFECTIVE DATE</U></B>:\n" +
                "This act shall take effect on the ninetieth day after it shall have\n" +
                "become a law.\n" +
                "</pre>\n");

        assertTrue(amendment.getMemo().equals(sample.getMemo()));
    }

    @Test
    public void processAmendmentSenMemo()   {
        String xmlPath="processor/bill/senmemo/2017-02-08-18.50.01.467654_SENMEMO_S00957A.XML";
        processXmlFile(xmlPath);

        Bill bill = billDao.getBill(new BillId("S0957",2017));


        BillAmendment amendment = bill.getAmendment(Version.A);

        BillAmendment sample = new BillAmendment(new BaseBillId("S0957",2017),Version.A);
        sample.setMemo("\n" +
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
                "<B><U>BILL NUMBER:</U></B> S957A\n" +
                " \n" +
                "<B><U>SPONSOR:</U></B> CROCI<BR>\n" +
                "&nbsp\n" +
                "<B><U>TITLE OF BILL</U></B>:  An act to amend the penal law, in relation to creating\n" +
                "the crime of stolen valor\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>PURPOSE OR GENERAL IDEA OF BILL</U></B>:\n" +
                " \n" +
                "This bill establishes the new crime of stolen valor and directs that a\n" +
                "stolen valor fee be assessed against those convicted and deposited in to\n" +
                "the veterans cemetery fund.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>SUMMARY OF SPECIFIC PROVISIONS</U></B>:\n" +
                " \n" +
                "Sections one amends the penal law by defining member of the military or\n" +
                "reserves and defining veteran.\n" +
                " \n" +
                "Section two amends the penal law by establishing the new crime of stolen\n" +
                "valor, which is classified as a class E felony.\n" +
                " \n" +
                "Section three amends the penal law by establishing a stolen valor fee\n" +
                "and providing that any person convicted of the crime of stolen valor\n" +
                "shall pay a stolen valor fee in the amount of $250. Further, this\n" +
                "section provides that stolen valor fees shall be deposited in to the\n" +
                "veterans remembrance and cemetery maintenance and operation fund.\n" +
                " \n" +
                "Section four provides the effective date.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>JUSTIFICATION</U></B>:\n" +
                " \n" +
                "Impersonating the men and women who bravely serve our country for the\n" +
                "purpose of obtaining money or other benefits is a deplorable act.\n" +
                "Sadly, occurrences such as these have become all too common. In addition\n" +
                "to establishing the state crime of stolen valor, this bill directs that\n" +
                "a stolen valor fee be assessed against those convicted, and that the fee\n" +
                "be deposited in to the veterans remembrance and cemetery maintenance\n" +
                "operation fund. Requiring these criminals to pay a stolen valor fee,\n" +
                "which will be used to establish and maintain a state Veterans Cemetery,\n" +
                "is a fitting way to ensure that our real veterans are honored appropri-\n" +
                "ately.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>PRIOR LEGISLATIVE HISTORY</U></B>:\n" +
                " \n" +
                "S.5201- 2015/16: Passed in the Senate\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>FISCAL IMPLICATIONS</U></B>:\n" +
                " \n" +
                "None to the State.\n" +
                " \n" +
                "&nbsp\n" +
                "<B><U>EFFECTIVE DATE</U></B>:\n" +
                "This act shall take effect immediately.\n" +
                "</pre>\n");

        assertTrue(amendment.getMemo().equals(sample.getMemo()));

    }
}
