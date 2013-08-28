package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class Api1XmlConverter
{
    protected final String encoding = "UTF-8";
    protected final XMLOutputter xmlOutputter;

    public Api1XmlConverter()
    {
        xmlOutputter = new XMLOutputter();
        Document doc = new Document();
        Element root = new Element("bill");
        doc.setRootElement(root);
    }

    public String toString(Bill value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(Calendar value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(Meeting value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(Transcript value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(SenateResponse value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public String toString(BaseObject value) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(value, stream);
        return stream.toString(this.encoding);
    }

    public void write(BaseObject object, OutputStream out) throws IOException
    {
        if (object.getOtype().equals("bill")) {
            write((Bill)object, out);
        }
        else if (object.getOtype().equals("calendar")) {
            write((Calendar)object, out);
        }
        else if (object.getOtype().equals("meeting")) {
            write((Meeting)object, out);
        }
        else if (object.getOtype().equals("transcript")) {
            write((Transcript)object, out);
        }
        else {
            throw new RuntimeException("Invalid base object otype: "+object.getOtype());
        }
    }

    public void write(SenateResponse response, OutputStream out) throws IOException
    {
        Element root = new Element("results");
        root.setAttribute("total", response.getMetadata().get("totalresults").toString());
        for (Result result : response.getResults()) {
            Element resultNode = new Element("result");
            resultNode.setAttribute("type", result.getOtype());
            resultNode.setAttribute("id", result.getOid());
            resultNode.setAttribute("title", result.getTitle());
            if (result.getOtype().equals("action")) {
                Action action = (Action)result.getObject();
                resultNode.setAttribute("billno", action.getBill().getBillId());
            }
            else if (result.getOtype().equals("vote")) {
                Vote vote = (Vote)result.getObject();
                resultNode.setAttribute("billno", vote.getBill().getBillId());
                resultNode.setAttribute("sponsor", vote.getBill().getSponsor().getFullname());
            }
            else if (result.getOtype().equals("bill")) {
                root.setName("docket");
                resultNode.setName("bill");
                Bill bill = (Bill)result.getObject();
                resultNode.setAttribute("billId", bill.getBillId());
                resultNode.setAttribute("senateId", bill.getBillId());
                resultNode.setAttribute("year", String.valueOf(bill.getYear()));
                resultNode.setAttribute("law", bill.getLaw());
                resultNode.setAttribute("lawSection", bill.getLawSection());
                resultNode.setAttribute("sponsor", bill.getSponsor().getFullname());
                resultNode.setAttribute("assemblySameAs", bill.getSameAs());
                resultNode.setAttribute("sameAs", bill.getSameAs());

                Element cosponsorsNode = new Element("cosponsors");
                for (Person cosponsor : bill.getCoSponsors()) {
                    Element cosponsorNode = new Element("cosponsor");
                    cosponsorNode.addContent(cosponsor.getFullname());
                }
                resultNode.addContent(cosponsorsNode);

                Element summaryNode = new Element("summary");
                summaryNode.addContent(bill.getSummary());
                resultNode.addContent(summaryNode);

                Element committeeNode = new Element("committee");
                committeeNode.addContent(bill.getCurrentCommittee());
                resultNode.addContent(committeeNode);
            }
            else if (result.getOtype().equals("meeting")) {
                Meeting meeting = (Meeting)result.getObject();
                resultNode.setAttribute("committee", meeting.getCommitteeName());
                resultNode.setAttribute("location", meeting.getLocation());
                resultNode.setAttribute("chair", meeting.getCommitteeChair());
            }
            else if (result.getOtype().equals("transcript")) {
                Transcript transcript = (Transcript)result.getObject();
                resultNode.setAttribute("location", transcript.getLocation());
            }
            else if (result.getOtype().equals("calendar")) {
                // no additional fields.
            }
            root.addContent(resultNode);
        }
        xmlOutputter.output(root, out);
    }

    public void write(Meeting meeting, OutputStream out) throws IOException
    {
        Element root = new Element("meeting");
        /// Full serialization...
        /// ?? Too broken to tell, find the code.
        xmlOutputter.output(root, out);
    }

    public void write(Calendar calendar, OutputStream out) throws IOException
    {
        Element root = new Element("calendar");
        /// Full serialization..
        /// ?? Too broken to tell, find the code.
        xmlOutputter.output(root, out);
    }

    public void write(Bill bill, OutputStream out) throws IOException
    {
        Element billElement = new Element("bill");
        billElement.setAttribute("year", bill.getSession()+"");
        billElement.setAttribute("senateId", bill.getBillId());
        billElement.setAttribute("billId", bill.getBillId());
        billElement.setAttribute("title", bill.getTitle());
        billElement.setAttribute("law", bill.getLaw());
        billElement.setAttribute("lawSection", bill.getLawSection());
        billElement.setAttribute("assemblySameAs",bill.getSameAs());
        billElement.setAttribute("sameAs",bill.getSameAs());
        billElement.setAttribute("sponsor", bill.getSponsor() != null ? bill.getSponsor().getFullname() : "n/a");

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
        billElement.addContent(elemCos);



        Element summaryElement = new Element("summary");
        summaryElement.setText(bill.getSummary());
        billElement.addContent(summaryElement);

        Element committeeElement = new Element("committee");
        committeeElement.setText(bill.getCurrentCommittee());
        billElement.addContent(committeeElement);

        Element votesElement = new Element("votes");
        for(Vote vote : bill.getVotes()) {
            Element voteElement = new Element("vote");
            voteElement.setAttribute("timestamp", vote.getVoteDate().getTime() + "");
            voteElement.setAttribute("ayes",vote.getAyes().size()+"");
            voteElement.setAttribute("nays",vote.getNays().size()+"");
            voteElement.setAttribute("abstains",vote.getAbstains().size()+"");
            voteElement.setAttribute("excused",vote.getExcused().size()+"");

            Iterator<String> it = vote.getAyes().iterator();

            while (it.hasNext()) {
                Element elemVoter = new Element("voter");
                elemVoter.setAttribute("name",it.next());
                elemVoter.setAttribute("vote","aye");
                voteElement.addContent(elemVoter);
            }

            it = vote.getNays().iterator();

            while (it.hasNext()) {
                Element elemVoter = new Element("voter");
                elemVoter.setAttribute("name",it.next());
                elemVoter.setAttribute("vote","nay");
                voteElement.addContent(elemVoter);
            }

            it = vote.getAbstains().iterator();

            while (it.hasNext()) {
                Element elemVoter = new Element("voter");
                elemVoter.setAttribute("name",it.next());
                elemVoter.setAttribute("vote","abstain");
                voteElement.addContent(elemVoter);
            }

            it = vote.getExcused().iterator();

            while (it.hasNext()) {
                Element elemVoter = new Element("voter");
                elemVoter.setAttribute("name",it.next());
                elemVoter.setAttribute("vote","excused");
                voteElement.addContent(elemVoter);
            }

            votesElement.addContent(voteElement);
        }
        billElement.addContent(votesElement);



        if (bill.getFulltext()!=null) {
            Element elem = new Element("text");
            elem.addContent(new CDATA(bill.getFulltext()));
            billElement.addContent(elem);
        }

        if (bill.getMemo()!=null) {
            Element elem = new Element("memo");
            elem.addContent(new CDATA(bill.getMemo()));
            billElement.addContent(elem);
        }

        Element docket = new Element("docket");
        docket.addContent(billElement);
        xmlOutputter.output(docket, out);
    }

    public void write(Transcript transcript, OutputStream out) throws IOException
    {
        Element root = new Element("transcript");
        root.setAttribute("id", transcript.getId());

        Element timestampNode = new Element("timestamp");
        timestampNode.addContent(String.valueOf(transcript.getTimeStamp().getTime()));
        root.addContent(timestampNode);

        Element locationNode = new Element("location");
        locationNode.addContent(transcript.getLocation());
        root.addContent(locationNode);

        Element sessionNode = new Element("session");
        sessionNode.addContent(String.valueOf(transcript.getSession()));
        root.addContent(sessionNode);

        Element textNode = new Element("text");
        textNode.addContent(transcript.getTranscriptText());
        root.addContent(textNode);

        xmlOutputter.output(root, out);
    }

}
