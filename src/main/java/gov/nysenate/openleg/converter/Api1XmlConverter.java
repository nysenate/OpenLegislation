package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.Section;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.model.Sequence;
import gov.nysenate.openleg.model.Supplemental;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.jdom2.CDATA;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class Api1XmlConverter
{
    protected final Document doc;
    protected final XMLOutputter xmlOutputter;
    protected final String encoding = "UTF-8";

    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");

    public Api1XmlConverter()
    {
        xmlOutputter = new XMLOutputter();
        doc = new Document();
        doc.setDocType(new DocType("xml"));
        doc.setProperty("version", "1.0");
        doc.setProperty("encoding", "UTF-8");
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
        write(root, out);
    }

    protected void write(Bill bill, OutputStream out) throws IOException
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

        billElement.addContent(makeElement("summary", bill.getSummary()));
        billElement.addContent(makeElement("committee", bill.getCurrentCommittee()));

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
            billElement.addContent(makeElement("text", new CDATA(bill.getFulltext())));
        }

        if (bill.getMemo()!=null) {
            billElement.addContent(makeElement("memo", new CDATA(bill.getMemo())));
        }

        write(makeElement("docket", billElement), out);
    }

    protected void write(Transcript transcript, OutputStream out) throws IOException
    {
        Element root = new Element("transcript");
        root.setAttribute("id", transcript.getId());
        root.addContent(makeElement("location", String.valueOf(transcript.getTimeStamp().getTime())));
        root.addContent(makeElement("location", transcript.getLocation()));
        root.addContent(makeElement("session", String.valueOf(transcript.getSession())));
        root.addContent(makeElement("text", new CDATA(transcript.getTranscriptText())));
        write(root, out);
    }

    protected void write(Meeting meeting, OutputStream out) throws IOException
    {
        write(makeElement("meeting", meeting), out);
    }

    protected void write(Calendar calendar, OutputStream out) throws IOException
    {
        write(makeElement("calendar", calendar), out);
    }

    protected void write(Element root, OutputStream out) throws IOException
    {
        doc.setContent(root);
        xmlOutputter.output(doc, out);
    }

    protected Element makeElement(String tag, Calendar value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.setAttribute("year", String.valueOf(value.getYear()));
            root.setAttribute("type", value.getType());
            root.setAttribute("sessionYear", String.valueOf(value.getSession()));
            root.setAttribute("no", String.valueOf(value.getNo()));
            root.addContent(makeElementList("supplementals", "supplementals", value.getSupplementals()));
        }
        return root;
    }

    protected Element makeElement(String tag, Supplemental value)
    {
        Element root = new Element(tag);
        if (value != null) {
            if (value.getSections() != null && !value.getSections().isEmpty()) {
                root.addContent(makeElementList("sections", "section", value.getSections()));
            }
            if (value.getSequences() != null && !value.getSequences().isEmpty()) {
                root.addContent(makeElementList("sequences", "sequence", value.getSequences()));
            }
        }
        return root;
    }

    protected Element makeElement(String tag, Sequence value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.setAttribute("no", value.getNo());
            root.addContent(makeElement("actCalDate", value.getActCalDate()));
            root.addContent(makeElement("releaseDateTime", value.getReleaseDateTime()));
            root.addContent(makeElementList("calendarEntries", "calendarEntry", value.getCalendarEntries()));
        }
        return root;
    }

    protected Element makeElement(String tag, Section value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.addContent(makeElement("name", value.getName()));
            root.addContent(makeElement("type", value.getType()));
            root.addContent(makeElement("cd", value.getCd()));
            root.addContent(makeElementList("calendarEntries", "calendarEntry", value.getCalendarEntries()));
        }
        return root;
    }

    protected Element makeElement(String tag, CalendarEntry value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.setAttribute("no", value.getNo());
            root.addContent(makeShortElement("bill", value.getBill()));
            root.addContent(makeShortElement("subBill", value.getBill()));
            root.addContent(makeElement("billHigh", value.getBillHigh()));
        }
        return root;
    }

    protected Element makeElement(String tag, Meeting value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.addContent(makeElement("meetday", value.getMeetday()));
            root.addContent(makeElement("location", value.getLocation()));
            root.addContent(makeElement("committeeName", value.getCommitteeName()));
            root.addContent(makeElement("committeeChair", value.getCommitteeChair()));
            root.addContent(makeElementList("bills", "bill", value.getBills()));
            root.addContent(makeElement("notes", value.getNotes()));
        }
        return root;
    }

    protected Element makeShortElement(String tag, Bill value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.addContent(makeElement("active", String.valueOf(value.isActive())));
            root.addContent(makeElement("year", String.valueOf(value.getSession())));
            root.addContent(makeElement("senateId", value.getBillId()));
            root.addContent(makeElement("title", value.getTitle()));
            root.addContent(makeElement("sameAs", value.getSameAs()));
            root.addContent(makeElement("sponsor", value.getSponsor().getFullname()));
            root.addContent(makeElementList("otherSponsors", "string", value.getOtherSponsors()));
            root.addContent(makeElement("frozen", "false"));
            root.addContent(makeElementList("amendments", "string", value.getAmendments()));
            root.addContent(makeElement("summary", value.getSummary()));
            root.addContent(makeElement("uniBill", String.valueOf(value.isUniBill())));
        }
        return root;
    }

    protected Element makeElementList(String listTag, String itemTag, Collection<? extends Object> list)
    {
        Element element = new Element(listTag);
        if (list != null) {
            for (Object item : list) {
                if(Bill.class.isInstance(item)) {
                    element.addContent(makeShortElement(itemTag, (Bill)item));
                }
                else if(Supplemental.class.isInstance(item)) {
                    element.addContent(makeElement(itemTag, (Supplemental)item));
                }
                else if(Section.class.isInstance(item)) {
                    element.addContent(makeElement(itemTag, (Section)item));
                }
                else if(Sequence.class.isInstance(item)) {
                    element.addContent(makeElement(itemTag, (Sequence)item));
                }
                else if(CalendarEntry.class.isInstance(item)) {
                    element.addContent(makeElement(itemTag, (CalendarEntry)item));
                }
                else if(String.class.isInstance(item)) {
                    element.addContent(makeElement(itemTag, (String)item));
                }
                else {
                    throw new RuntimeException("Invalid array node type: "+item.getClass());
                }
            }
        }
        return element;
    }


    protected Element makeElement(String tag, Date value)
    {
        Element element = new Element(tag);
        element.addContent(dateFormat.format(value));
        return element;
    }

    protected Element makeElement(String tag, String value)
    {
        Element element = new Element(tag);
        element.addContent(value);
        return element;
    }

    protected Element makeElement(String tag, CDATA value)
    {
        Element element = new Element(tag);
        element.addContent(value);
        return element;
    }

    protected Element makeElement(String tag, Element...values)
    {
        Element element = new Element(tag);
        for (Element value : values) {
            element.addContent(value);
        }
        return element;
    }
}
