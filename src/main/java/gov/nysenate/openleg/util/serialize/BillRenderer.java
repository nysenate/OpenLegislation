package gov.nysenate.openleg.util.serialize;


import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class BillRenderer {

    private static Logger logger = Logger.getLogger(BillRenderer.class);

    public static String renderBills (Collection<?> bills, boolean includeFullText)
    {
        logger.info("rendering " + bills.size() + " bills. includeFullText=" + includeFullText);

        Iterator<?> it = bills.iterator();


        Element elemRoot = new Element("docket");
        Document doc = new Document(elemRoot);

        while (it.hasNext())
        {
            Bill bill = (Bill)it.next();

            try
            {
                Element elemBill = getBillElement(bill,includeFullText);

                if (elemBill != null)
                    elemRoot.addContent(elemBill);
            }
            catch (Exception e)
            {
                logger.info("unable to render bill: " + bill.getSenateBillNo(),e);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            XMLOutputter outputter = new XMLOutputter();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));
            outputter.output(doc, writer);
        } catch (Exception e) {
            logger.warn("error writing bill output",e);
        }


        return baos.toString();
    }

    public static String renderBill (Bill bill)
    {
        ArrayList<Bill> list = new ArrayList<Bill>();
        list.add(bill);
        return renderBills(list,true);
    }

    private static Element getBillElement(Bill bill, boolean includeFullText)
    {
        Element elemBill = new Element("bill");

        elemBill.setAttribute("year", bill.getYear()+"");
        elemBill.setAttribute("senateId", bill.getSenateBillNo());
        elemBill.setAttribute("billId", bill.getSenateBillNo());

        try
        {


            if (bill.getTitle()!=null)
                elemBill.setAttribute("title", bill.getTitle());

            if (bill.getLaw()!=null)
                elemBill.setAttribute("law", bill.getLaw());

            if (bill.getLawSection()!=null)
                elemBill.setAttribute("lawSection", bill.getLawSection());

            if (bill.getSponsor()!=null && bill.getSponsor().getFullname()!=null)
                elemBill.setAttribute("sponsor",bill.getSponsor().getFullname());
            else
                elemBill.setAttribute("sponsor","n/a");



            try
            {

                Element elemCos = new Element("cosponsors");
                if (bill.getCoSponsors() != null)
                {
                    Iterator<Person> itCos = bill.getCoSponsors().iterator();
                    Person coSponsor = null;

                    while (itCos.hasNext())
                    {
                        Element elemCosChild = new Element("cosponsor");
                        coSponsor = itCos.next();

                        if (coSponsor.getFullname()!=null)
                        {
                            elemCosChild.setText(coSponsor.getFullname());
                            elemCos.addContent(elemCosChild);
                        }
                    }
                }

                elemBill.addContent(elemCos);
            }
            catch (Exception e)
            {

            }



            if (bill.getSameAs()!=null)
            {
                elemBill.setAttribute("assemblySameAs",bill.getSameAs());
                elemBill.setAttribute("sameAs",bill.getSameAs());
            }

            try
            {
                Element elemSummary = new Element("summary");

                if (bill.getSummary()!=null)
                    elemSummary.setText(bill.getSummary());
                else
                    elemSummary.setText("");

                elemBill.addContent(elemSummary);

            }
            catch (Exception e)
            {

            }

            try
            {
                Element elemComm = new Element("committee");

                if (bill.getCurrentCommittee() != null)
                    elemComm.setText(bill.getCurrentCommittee());
                else
                    elemComm.setText("");

                elemBill.addContent(elemComm);
            }
            catch (Exception e)
            {

            }



            try
            {

                String baseSenateId = bill.getSenateBillNo();

                if (baseSenateId.matches(".*[A-Z]"))
                    baseSenateId = baseSenateId.substring(0,baseSenateId.length()-1);

                StringBuilder actionSearch = new StringBuilder();

                actionSearch.append("otype:action AND (");
                actionSearch.append("billno:");
                actionSearch.append(baseSenateId);
                actionSearch.append(" OR ");

                for (int i = 65; i < 91; i++)
                {
                    actionSearch.append("billno:");
                    actionSearch.append(baseSenateId);
                    actionSearch.append((char)i);

                    if ( i < 90)
                        actionSearch.append(" OR ");
                }

                actionSearch.append(")");

            }
            catch (Exception e)
            {

            }


            try
            {


                if (bill.getVotes()!=null&&bill.getVotes().size()>0)
                {
                    Element elemVotes = new Element("votes");

                    Iterator<Vote> itVotes = bill.getVotes().iterator();

                    Vote vote = null;

                    while (itVotes.hasNext())
                    {
                        vote = itVotes.next();

                        Element elemVote = new Element("vote");

                        elemVote.setAttribute("timestamp", vote.getVoteDate().getTime() + "");

                        if (vote.getAyes()!=null)
                            elemVote.setAttribute("ayes",vote.getAyes().size()+"");

                        if (vote.getNays()!=null)
                            elemVote.setAttribute("nays",vote.getNays().size()+"");

                        if (vote.getAbstains()!=null)
                            elemVote.setAttribute("abstains",vote.getAbstains().size()+"");

                        if (vote.getExcused()!=null)
                            elemVote.setAttribute("excused",vote.getExcused().size()+"");

                        Iterator<String> it = vote.getAyes().iterator();

                        while (it.hasNext())
                        {
                            Element elemVoter = new Element("voter");
                            elemVoter.setAttribute("name",it.next());
                            elemVoter.setAttribute("vote","aye");
                            elemVote.addContent(elemVoter);
                        }

                        it = vote.getNays().iterator();

                        while (it.hasNext())
                        {
                            Element elemVoter = new Element("voter");
                            elemVoter.setAttribute("name",it.next());
                            elemVoter.setAttribute("vote","nay");
                            elemVote.addContent(elemVoter);
                        }

                        it = vote.getAbstains().iterator();

                        while (it.hasNext())
                        {
                            Element elemVoter = new Element("voter");
                            elemVoter.setAttribute("name",it.next());
                            elemVoter.setAttribute("vote","abstain");
                            elemVote.addContent(elemVoter);
                        }

                        it = vote.getExcused().iterator();

                        while (it.hasNext())
                        {
                            Element elemVoter = new Element("voter");
                            elemVoter.setAttribute("name",it.next());
                            elemVoter.setAttribute("vote","excused");
                            elemVote.addContent(elemVoter);
                        }

                        elemVotes.addContent(elemVote);


                    }

                    elemBill.addContent(elemVotes);

                }

            }
            catch (Exception e)
            {

            }


            if (includeFullText)
            {
                if (bill.getFulltext()!=null)
                {
                    Element elem = new Element("text");
                    String text = processText(bill.getFulltext());


                    elem.setText(text);
                    elemBill.addContent(elem);

                }


                if (bill.getMemo()!=null)
                {
                    Element elem = new Element("memo");

                    String text = processText(bill.getMemo());


                    elem.setText(text);
                    elemBill.addContent(elem);

                }
            }


        }
        catch (Exception e)
        {
            logger.warn("error rendering bill:" + bill.getSenateBillNo(),e);
        }


        return elemBill;
    }

    private final static String processText (String text)
    {
        text = text.replace((char)0x1a,' ');//sub
        text = text.replace((char)0x1e,' ');//rs
        text = text.replace((char)0xB5,' ');

        return text;

    }
}
