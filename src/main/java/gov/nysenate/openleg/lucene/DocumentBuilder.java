package gov.nysenate.openleg.lucene;

import gov.nysenate.openleg.converter.LuceneJsonConverter;
import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.PublicHearing;
import gov.nysenate.openleg.model.Section;
import gov.nysenate.openleg.model.Sequence;
import gov.nysenate.openleg.model.Supplemental;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class DocumentBuilder
{
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Document build(PublicHearing hearing)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new StoredField("oid", hearing.getOid()));
        document.add(new StringField("oid", hearing.getOid().toLowerCase(), Field.Store.NO));

        // Basic document filters
        document.add(new StringField("otype", "hearing", Field.Store.YES));
        document.add(new IntField("year", hearing.getYear(), Field.Store.YES));
        document.add(new StringField("active", String.valueOf(hearing.isActive()), Field.Store.YES));
        document.add(new LongField("modified", hearing.getModifiedDate().getTime(), Field.Store.YES));
        document.add(new LongField("published", hearing.getPublishDate().getTime(), Field.Store.YES));
        document.add(new StringField("modified", dateFormat.format(hearing.getModifiedDate()), Field.Store.NO));
        document.add(new StringField("published", dateFormat.format(hearing.getPublishDate()), Field.Store.NO));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        document.add(new TextField("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO));

        // Other various search fields and filters
        document.add(new StringField("sorttitle", hearing.getOid().toLowerCase(), Field.Store.NO));

        document.add(new StoredField("odata", LuceneJsonConverter.toString(hearing)));
        return document;
    }

    public static Document build(Vote vote)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new StoredField("oid", vote.getOid()));
        document.add(new StringField("oid", vote.getOid().toLowerCase(), Field.Store.NO));

        // Basic document filters
        document.add(new StringField("otype", "vote", Field.Store.YES));
        document.add(new IntField("year", vote.getYear(), Field.Store.YES));
        document.add(new StringField("active", String.valueOf(vote.isActive()), Field.Store.YES));
        document.add(new LongField("modified", vote.getModifiedDate().getTime(), Field.Store.YES));
        document.add(new LongField("published", vote.getPublishDate().getTime(), Field.Store.YES));
        document.add(new StringField("modified", dateFormat.format(vote.getModifiedDate()), Field.Store.NO));
        document.add(new StringField("published", dateFormat.format(vote.getPublishDate()), Field.Store.NO));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(vote.getBill().getBillId());
        if (vote.getVoteType()==Vote.VOTE_TYPE_COMMITTEE) {
            searchTerms.add("Committee Vote");
            searchTerms.add(vote.getBill().getCurrentCommittee());
        }
        else {
            searchTerms.add("Floor Vote");
        }
        document.add(new TextField("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO));
        document.add(new StringField("sorttitle", vote.getOid().toLowerCase(), Field.Store.NO));

        // Other various search fields and filters
        String title = vote.getBill().getBillId()+" - "+DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate());
        switch(vote.getVoteType()) {
        case Vote.VOTE_TYPE_COMMITTEE:
            title += " - Committee Vote"; break;
        case Vote.VOTE_TYPE_FLOOR:
            title +=  " - Floor Vote"; break;
        }
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new TextField("summary", DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate()), Field.Store.YES));
        document.add(new TextField("billno",vote.getBill().getBillId(), Field.Store.YES));
        document.add(new TextField("othersponsors",StringUtils.join(vote.getBill().getOtherSponsors(), ", "), Field.Store.YES));
        document.add(new TextField("sponsor", vote.getBill().getSponsor().getFullname(), Field.Store.YES));
        document.add(new TextField("abstain", StringUtils.join(vote.getAbstains(), " "), Field.Store.YES));
        document.add(new TextField("aye", StringUtils.join(vote.getAyes(), " "), Field.Store.YES));
        document.add(new TextField("excused", StringUtils.join(vote.getExcused(), " "), Field.Store.YES));
        document.add(new TextField("nay", StringUtils.join(vote.getNays(), " "), Field.Store.YES));
        document.add(new LongField("when", vote.getVoteDate().getTime(), Field.Store.YES));
        if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE) {
            document.add(new TextField("committee", (vote.getDescription().isEmpty() ? vote.getBill().getCurrentCommittee() : vote.getDescription()), Field.Store.YES));
        }

        document.add(new StoredField("odata", LuceneJsonConverter.toString(vote)));
        return document;
    }


    public static Document build(Transcript transcript)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new StoredField("oid", transcript.getOid()));
        document.add(new StringField("oid", transcript.getOid().split("_")[0].toLowerCase(), Field.Store.NO));
        document.add(new StringField("oid", transcript.getOid().toLowerCase(), Field.Store.NO));

        // Basic document filters
        document.add(new StringField("otype", "transcript", Field.Store.YES));
        document.add(new IntField("year", transcript.getYear(), Field.Store.YES));
        document.add(new StringField("active", String.valueOf(transcript.isActive()), Field.Store.YES));
        document.add(new LongField("modified", transcript.getModifiedDate().getTime(), Field.Store.YES));
        document.add(new LongField("published", transcript.getPublishDate().getTime(), Field.Store.YES));
        document.add(new StringField("modified", dateFormat.format(transcript.getModifiedDate()), Field.Store.YES));
        document.add(new StringField("published", dateFormat.format(transcript.getPublishDate()), Field.Store.YES));
        document.add(new StringField("modified_date", dateFormat.format(transcript.getModifiedDate()), Field.Store.YES));
        document.add(new StringField("publish_date", dateFormat.format(transcript.getPublishDate()), Field.Store.YES));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(transcript.getTranscriptText());
        document.add(new TextField("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO));

        // Other various search fields and filters
        ArrayList<String> billIds = new ArrayList<String>();
        for (Bill bill : transcript.getRelatedBills()) {
            billIds.add(bill.getBillId());
        }
        document.add(new TextField("title", transcript.getType(), Field.Store.YES));
        document.add(new TextField("summary", transcript.getLocation(), Field.Store.YES));
        document.add(new TextField("relatedBills", StringUtils.join(billIds, ", "), Field.Store.YES));
        document.add(new TextField("full", transcript.getTranscriptText(), Field.Store.YES));
        document.add(new TextField("session-type", transcript.getType(), Field.Store.YES));
        document.add(new TextField("location", transcript.getLocation(), Field.Store.YES));
        document.add(new LongField("when", transcript.getTimeStamp().getTime(), Field.Store.YES));
        document.add(new StringField("sorttitle", transcript.getOid().toLowerCase(), Field.Store.NO));

        document.add(new StoredField("odata", LuceneJsonConverter.toString(transcript)));
        return document;
    }

    public static Document build(Meeting meeting)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new StoredField("oid", meeting.getOid()));
        document.add(new StringField("oid", meeting.getOid().toLowerCase(), Field.Store.YES));

        // Basic document filters
        document.add(new StringField("otype", "meeting", Field.Store.YES));
        document.add(new IntField("year", meeting.getYear(), Field.Store.YES));
        document.add(new StringField("active", String.valueOf(meeting.isActive()), Field.Store.YES));
        document.add(new LongField("modified", meeting.getModifiedDate().getTime(), Field.Store.YES));
        document.add(new LongField("published", meeting.getPublishDate().getTime(), Field.Store.YES));
        document.add(new StringField("modified", dateFormat.format(meeting.getModifiedDate()), Field.Store.YES));
        document.add(new StringField("published", dateFormat.format(meeting.getPublishDate()), Field.Store.YES));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(meeting.getCommitteeName());
        searchTerms.add(meeting.getCommitteeChair());
        searchTerms.add(meeting.getLocation());
        searchTerms.add(meeting.getNotes());
        document.add(new TextField("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO));

        ArrayList<String> billIds = new ArrayList<String>();
        for (Bill bill : meeting.getBills()) {
            billIds.add(bill.getBillId());
        }

        // Other various search fields and filters
        document.add(new TextField("title", meeting.getCommitteeName()+" - "+DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(meeting.getMeetingDateTime()), Field.Store.YES));
        document.add(new TextField("summary", meeting.getLocation(), Field.Store.NO));
        document.add(new TextField("location", meeting.getLocation(), Field.Store.YES));
        document.add(new TextField("committee", meeting.getCommitteeName(), Field.Store.YES));
        document.add(new TextField("chair", meeting.getCommitteeChair(), Field.Store.YES));
        document.add(new TextField("notes", meeting.getNotes().toString(), Field.Store.YES));
        document.add(new TextField("bills", StringUtils.join(billIds, ", "), Field.Store.YES));
        document.add(new LongField("when", meeting.getMeetingDateTime().getTime(), Field.Store.YES));
        document.add(new StringField("sortindex", meeting.getMeetingDateTime().getTime()+meeting.getCommitteeName(), Field.Store.NO));
        document.add(new StringField("sorttitle", document.getField("title").stringValue().toLowerCase(), Field.Store.NO));

        document.add(new StoredField("odata", LuceneJsonConverter.toString(meeting)));
        return document;
    }

    public static Document build(Calendar calendar)
    {
        Document document = new Document();

        // Allow identification based id only
        String oid = calendar.getType()+"-"+new SimpleDateFormat("MM-dd-yyyy").format(calendar.getDate());
        document.add(new StoredField("oid", oid));
        document.add(new StringField("oid", oid.toLowerCase(), Field.Store.NO));

        // Basic document filters
        document.add(new StringField("otype", "calendar", Field.Store.YES));
        document.add(new IntField("year", calendar.getYear(), Field.Store.YES));
        document.add(new StringField("active", String.valueOf(calendar.isActive()), Field.Store.YES));
        document.add(new LongField("modified", calendar.getModifiedDate().getTime(), Field.Store.YES));
        document.add(new LongField("published", calendar.getPublishDate().getTime(), Field.Store.YES));
        document.add(new StringField("modified", dateFormat.format(calendar.getModifiedDate()), Field.Store.YES));
        document.add(new StringField("published", dateFormat.format(calendar.getPublishDate()), Field.Store.YES));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(calendar.getTitle());
        document.add(new TextField("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO));

        // Other various search fields and filters
        StringBuilder bills = new StringBuilder("");
        StringBuilder calendarEntries = new StringBuilder();
        StringBuilder summaryBuffer = new StringBuilder();
        if (calendar.getType().equals("floor")) {
            for (Supplemental supplemental : calendar.getSupplementals()) {
                for (Section section : supplemental.getSections()) {
                    summaryBuffer.append(section.getName()).append(": ");
                    summaryBuffer.append(section.getCalendarEntries().size()).append(" bill(s); ");

                    for(CalendarEntry entry:section.getCalendarEntries()) {
                        bills.append(entry.getBill().getBillId()).append(", ");
                        calendarEntries.append(entry.getNo()).append("-").append(entry.getBill().getBillId()).append(", ");
                    }
                }
            }
        }
        else {
            for (Supplemental supplemental : calendar.getSupplementals()) {
                int total = 0;
                for (Sequence sequence : supplemental.getSequences()) {
                    total += sequence.getCalendarEntries().size();

                    for(CalendarEntry entry : sequence.getCalendarEntries()) {
                        bills.append(entry.getBill().getBillId()).append(", ");
                        calendarEntries.append(entry.getNo()).append("-").append(entry.getBill().getBillId()).append(", ");
                    }

                    summaryBuffer.append(total).append(" bill(s)");
                }
            }
        }

        document.add(new StringField("ctype", calendar.getType().toLowerCase(), Field.Store.YES));
        document.add(new TextField("bills", bills.toString(), Field.Store.YES));
        document.add(new TextField("calendarentries",calendarEntries.toString(), Field.Store.YES));
        document.add(new TextField("summary", summaryBuffer.toString().trim(), Field.Store.YES));
        document.add(new TextField("title", calendar.getTitle(), Field.Store.YES));
        document.add(new LongField("when", calendar.getDate().getTime(), Field.Store.YES));
        document.add(new StringField("sorttitle", document.getField("title").stringValue().toLowerCase(), Field.Store.NO));

        document.add(new StoredField("odata", LuceneJsonConverter.toString(calendar)));
        return document;
    }

    public static Document build(Action action)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new StoredField("oid", action.getOid()));
        document.add(new StringField("oid", action.getOid().toLowerCase(), Field.Store.NO));

        // Basic document filters
        document.add(new StringField("otype", "action", Field.Store.YES));
        document.add(new IntField("year", action.getYear(), Field.Store.YES));
        document.add(new StringField("active", String.valueOf(action.isActive()), Field.Store.YES));
        document.add(new LongField("modified", action.getModifiedDate().getTime(), Field.Store.YES));
        document.add(new LongField("published", action.getPublishDate().getTime(), Field.Store.YES));
        document.add(new StringField("modified", dateFormat.format(action.getModifiedDate()), Field.Store.YES));
        document.add(new StringField("published", dateFormat.format(action.getPublishDate()), Field.Store.YES));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(action.getBill().getBillId());
        searchTerms.add(action.getText());
        document.add(new TextField("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO));

        // Other various search fields and filters
        document.add(new LongField("when", action.getDate().getTime(), Field.Store.YES));
        document.add(new TextField("billno", action.getBill().getBillId().toLowerCase(), Field.Store.YES));
        document.add(new TextField("title", action.getText(), Field.Store.YES));
        document.add(new TextField("summary", DateFormat.getDateInstance(DateFormat.MEDIUM).format(action.getDate()), Field.Store.YES));
        document.add(new StringField("sorttitle", action.getBill().getBillId()+" "+action.getText().toLowerCase(), Field.Store.NO));

        document.add(new StoredField("odata", LuceneJsonConverter.toString(action)));
        return document;
    }

    public static Document build(Bill bill)
    {
        Document document = new Document();

        // Allow identification based on printNumber as well as oid; e.g. S11 or S11-2013
        document.add(new StoredField("oid", bill.getOid()));
        document.add(new StringField("oid", bill.getOid().toLowerCase(), Field.Store.NO));
        document.add(new StringField("oid", bill.getPrintNumber().toLowerCase(), Field.Store.NO));

        // Basic document filters
        document.add(new StringField("otype", "bill", Field.Store.YES));
        document.add(new IntField("year", bill.getSession(), Field.Store.YES));
        document.add(new StringField("active", String.valueOf(bill.isActive()), Field.Store.YES));
        document.add(new LongField("modified", bill.getModifiedDate().getTime(), Field.Store.YES));
        document.add(new LongField("published", bill.getPublishDate().getTime(), Field.Store.YES));
        document.add(new StringField("modified", dateFormat.format(bill.getModifiedDate()), Field.Store.YES));
        document.add(new StringField("published", dateFormat.format(bill.getPublishDate()), Field.Store.YES));

        // When searching without a field, match against the following terms.
        // TODO: Use field boosting to rank results better
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(bill.getPrintNumber());
        searchTerms.add(String.valueOf(bill.getSession()));
        searchTerms.add(bill.getBillId());
        searchTerms.add(bill.getSameAs());
        searchTerms.add(bill.getSponsor() != null ? bill.getSponsor().getFullname() : "");
        searchTerms.add(bill.getTitle());
        searchTerms.add(bill.getSummary());
        searchTerms.add(bill.getFulltext());
        document.add(new TextField("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO));

        // Sorting should prioritize senate bills over assembly bills and resolutions.
        String sortId = bill.getPaddedBillId();
        switch (sortId.charAt(0)) {
        case 'S': sortId = "01_"+sortId; break;
        case 'A': sortId = "02_"+sortId; break;
        default: sortId = "99_"+sortId;
        }
        document.add(new StringField("sortindex", sortId, Field.Store.NO));

        // Other various search fields and filters
        document.add(new TextField("title", bill.getTitle(), Field.Store.YES));
        document.add(new TextField("summary", bill.getSummary(), Field.Store.YES));
        document.add(new TextField("lawsection", bill.getLawSection(), Field.Store.YES));
        document.add(new TextField("sameas", bill.getSameAs(), Field.Store.YES));
        document.add(new TextField("sponsor", bill.getSponsor() == null ? "" : bill.getSponsor().toString(), Field.Store.YES));
        document.add(new TextField("cosponsors", StringUtils.join(bill.getCoSponsors(), ", "), Field.Store.YES));
        document.add(new TextField("multisponsors", StringUtils.join(bill.getMultiSponsors(), ", "), Field.Store.YES));
        document.add(new TextField("othersponsors", StringUtils.join(bill.getOtherSponsors(), ", "), Field.Store.YES));
        document.add(new TextField("pastcommittees", StringUtils.join(bill.getPastCommittees(), ", "), Field.Store.YES));
        document.add(new TextField("actions", StringUtils.join(bill.getActions(), ", "), Field.Store.YES));
        document.add(new TextField("committee", bill.getCurrentCommittee(), Field.Store.YES));
        document.add(new TextField("full", bill.getFulltext(), Field.Store.YES));
        document.add(new TextField("memo", bill.getMemo(), Field.Store.YES));
        document.add(new TextField("law", bill.getLaw(), Field.Store.YES));
        document.add(new TextField("actclause", bill.getActClause(), Field.Store.YES));
        document.add(new LongField("when", bill.getModifiedDate().getTime(), Field.Store.YES));
        document.add(new StringField("unibill", String.valueOf(bill.isUniBill()), Field.Store.YES));
        document.add(new StringField("stricken", String.valueOf(bill.isStricken()), Field.Store.YES));
        document.add(new StringField("sorttitle", bill.getTitle().toLowerCase(), Field.Store.NO));

        // The current status of the document, usually a filter on actions will be more useful
        String billStatus = "";
        List<Action> actions = bill.getActions();
        if (!actions.isEmpty()) {
            billStatus = actions.get(actions.size()-1).getText();
        }
        document.add(new TextField("status", billStatus, Field.Store.YES));

        document.add(new StoredField("odata", LuceneJsonConverter.toString(bill)));
        return document;
    }
}
