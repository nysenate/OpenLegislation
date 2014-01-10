package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.Section;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.model.Sequence;
import gov.nysenate.openleg.model.Supplemental;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jdom2.CDATA;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class Api2XmlConverter
{
    protected static Logger logger = Logger.getLogger(Api2XmlConverter.class);

    protected final Document doc;
    protected final XMLOutputter xmlOutputter;
    protected final String encoding = "UTF-8";

    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");

    public Api2XmlConverter()
    {
        xmlOutputter = new XMLOutputter();
        doc = new Document();
        doc.setDocType(new DocType("xml"));
        doc.setProperty("version", "1.0");
        doc.setProperty("encoding", "UTF-8");
    }

    public void write(SenateResponse response, OutputStream out) throws IOException
    {
        Element root = makeElement(
            "response",
            makeElement(
                "metadata",
                makeElement(
                    "totalresults",
                    response.getMetadata().get("totalresults").toString()
                )
            )
        );

        Element results = new Element("results");
        for (Result result : response.getResults()) {
            String oid;
            Element urlElement, oidElement;
            if (result.getOtype().equals("vote")) {
                oid = ((Vote)result.getObject()).getBill().getBillId();
                urlElement = makeElement("url", "http://open.nysenate.gov/legislation/bill/"+oid);
                oidElement = makeElement("oid", oid);
            }
            else if (result.getOtype().equals("action")) {
                oid = ((Action)result.getObject()).getBill().getBillId();
                urlElement = makeElement("url", "http://open.nysenate.gov/legislation/bill/"+oid);
                oidElement = makeElement("oid", oid);
            }
            else {
                urlElement = makeElement("url", "http://open.nysenate.gov/legislation/"+result.getOtype()+"/"+result.getOid());
                oidElement = makeElement("oid", result.getOid());
            }

            Element resultNode = makeElement(
                "result",
                makeElement("otype", result.getOtype()),
                oidElement,
                urlElement,
                makeElement(result.getObject())
            );
            results.addContent(resultNode);
        }
        root.addContent(results);
        doc.setContent(root);
        xmlOutputter.output(doc, out);
    }

    protected Element makeElementList(String listTag, String itemTag, Collection<? extends Object> list)
    {
        Element element = new Element(listTag);
        if (list != null) {
            for (Object item : list) {
                if(Bill.class.isInstance(item)) {
                    element.addContent(makeShortElement(itemTag, (Bill)item));
                }
                else if(Vote.class.isInstance(item)) {
                    element.addContent(makeShortElement(itemTag, (Vote)item));
                }
                else if(Action.class.isInstance(item)) {
                    element.addContent(makeShortElement(itemTag, (Action)item));
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
                else if(Person.class.isInstance(item)) {
                    element.addContent(makeElement(itemTag, (Person)item));
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

    protected Element makeElement(IBaseObject object) throws IOException
    {
        if (object.getOtype().equals("bill")) {
            return makeElement("bill", (Bill)object);
        }
        else if (object.getOtype().equals("calendar")) {
            return makeElement("calendar", (Calendar)object);
        }
        else if (object.getOtype().equals("meeting")) {
            return makeElement("meeting", (Meeting)object);
        }
        else if (object.getOtype().equals("transcript")) {
            return makeElement("transcript", (Transcript)object);
        }
        else if (object.getOtype().equals("vote")) {
            return makeElement("vote", (Vote)object);
        }
        else if (object.getOtype().equals("action")) {
            return makeElement("action", (Action)object);
        }
        else {
            throw new RuntimeException("Invalid base object otype: "+object.getOtype());
        }
    }

    protected Element makeElement(String tag, Transcript value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.addContent(makeElement("timeStamp", value.getTimeStamp()));
            root.addContent(makeElement("location", value.getLocation()));
            root.addContent(makeElement("type", value.getType()));
            root.addContent(makeElement("full", new CDATA(value.getTranscriptText())));
        }
        return root;
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
            root.addContent(makeElement("meetingDateTime", value.getMeetingDateTime()));
            root.addContent(makeElement("meetday", value.getMeetday()));
            root.addContent(makeElement("location", value.getLocation()));
            root.addContent(makeElement("committeeName", value.getCommitteeName()));
            root.addContent(makeElement("committeeChair", value.getCommitteeChair()));
            root.addContent(makeElementList("bills", "bill", value.getBills()));
            root.addContent(makeElement("notes", value.getNotes()));
        }
        return root;
    }

    protected Element makeElement(String tag, Bill value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.addContent(makeElement("active", String.valueOf(value.isActive())));
            root.addContent(makeElement("year", String.valueOf(value.getSession())));
            root.addContent(makeElement("senateId", value.getBillId()));
            root.addContent(makeElement("title", value.getTitle()));
            root.addContent(makeElement("lawSection", value.getLawSection()));
            root.addContent(makeElement("sameAs", value.getSameAs()));
            root.addContent(makeElementList("previousVersions", "billNo", value.getPreviousVersions()));
            root.addContent(makeElement("sponsor", (value.getSponsor() != null ? value.getSponsor().getFullname() : "")));
            root.addContent(makeElementList("otherSponsors", "string", value.getOtherSponsors()));
            root.addContent(makeElement("frozen", "false"));
            root.addContent(makeElementList("amendments", "string", value.getAmendments()));
            root.addContent(makeElementList("cosponsors", "string", value.getCoSponsors()));
            root.addContent(makeElementList("multisponsors", "string", value.getMultiSponsors()));
            root.addContent(makeElement("summary", value.getSummary()));
            root.addContent(makeElement("committee", value.getCurrentCommittee()));
            root.addContent(makeElementList("actions", "action", value.getActions()));
            root.addContent(makeElement("text", new CDATA(value.getFulltext())));
            root.addContent(makeElement("memo", new CDATA(value.getMemo())));
            root.addContent(makeElement("law", value.getLaw()));
            root.addContent(makeElementList("votes", "vote", value.getVotes()));
            root.addContent(makeElement("uniBill", String.valueOf(value.isUniBill())));
        }
        return root;
    }

    protected Element makeElement(String tag, Action value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.addContent(makeElement("date", value.getDate()));
            root.addContent(makeElement("text", value.getText()));
            root.addContent(makeShortElement("bill", value.getBill()));
        }
        return root;
    }

    protected Element makeElement(String tag, Vote value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.addContent(makeElement("voteType", String.valueOf(value.getVoteType())));
            root.addContent(makeElement("voteDate", value.getVoteDate()));
            root.addContent(makeElementList("ayes", "member", value.getAyes()));
            root.addContent(makeElementList("nays", "member", value.getNays()));
            root.addContent(makeElementList("abstains", "member", value.getAbstains()));
            root.addContent(makeElementList("absent", "member", value.getAbsent()));
            root.addContent(makeElementList("excused", "member", value.getExcused()));
            root.addContent(makeElementList("ayeswr", "member", value.getAyeswr()));
            root.addContent(makeShortElement("bill", value.getBill()));
            root.addContent(makeElement("description", value.getDescription()));
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

    protected Element makeShortElement(String tag, Action value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.setAttribute("timestamp", String.valueOf(value.getDate().getTime()));
            root.addContent(value.getText());
        }
        return root;
    }

    protected Element makeShortElement(String tag, Vote value)
    {
        Element root = new Element(tag);
        if (value != null) {
            root.addContent(makeElement("voteType", String.valueOf(value.getVoteType())));
            root.addContent(makeElement("voteDate", value.getVoteDate()));
            root.addContent(makeElementList("ayes", "member", value.getAyes()));
            root.addContent(makeElementList("nays", "member", value.getNays()));
            root.addContent(makeElementList("abstains", "member", value.getAbstains()));
            root.addContent(makeElementList("absent", "member", value.getAbsent()));
            root.addContent(makeElementList("excused", "member", value.getExcused()));
            root.addContent(makeElementList("ayeswr", "member", value.getAyeswr()));
            root.addContent(makeElement("description", value.getDescription()));
        }
        return root;
    }

    protected Element makeElement(String tag, Person value)
    {
        Element element = new Element(tag);
        element.addContent(value.getFullname());
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
