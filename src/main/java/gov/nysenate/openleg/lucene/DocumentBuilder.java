package gov.nysenate.openleg.lucene;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
import gov.nysenate.openleg.model.ISenateSerializer;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.PublicHearing;
import gov.nysenate.openleg.model.Section;
import gov.nysenate.openleg.model.Sequence;
import gov.nysenate.openleg.model.Supplemental;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class DocumentBuilder
{
    public static Document build(PublicHearing hearing, Collection<ISenateSerializer> serializers)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new Field("oid", "", Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Basic document filters
        document.add(new Field("otype", "hearing", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("year", String.valueOf(hearing.getYear()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("active", String.valueOf(hearing.isActive()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("modified", String.valueOf(hearing.getModified()), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        document.add(new Field("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO, Field.Index.ANALYZED));

        // Other various search fields and filters

        // Add in any serializations of the bill that we might need
        for(ISenateSerializer lst:serializers) {
            document.add(new Field(lst.getType(), lst.getData(hearing), Field.Store.YES, Field.Index.NO));
        }

        return document;
    }

    public static Document build(Vote vote, Collection<ISenateSerializer> serializers)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new Field("oid", vote.getId().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Basic document filters
        document.add(new Field("otype", "vote", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("year", String.valueOf(vote.getYear()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("active", String.valueOf(vote.isActive()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("modified", String.valueOf(vote.getModified()), Field.Store.YES, Field.Index.NOT_ANALYZED));

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
        document.add(new Field("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO, Field.Index.ANALYZED));

        // Other various search fields and filters
        String title = vote.getBill().getBillId()+" - "+DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate());
        switch(vote.getVoteType()) {
        case Vote.VOTE_TYPE_COMMITTEE:
            title += " - Committee Vote"; break;
        case Vote.VOTE_TYPE_FLOOR:
            title +=  " - Floor Vote"; break;
        }
        document.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("summary", DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate()), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("billno",vote.getBill().getBillId(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("othersponsors",StringUtils.join(vote.getBill().getOtherSponsors(), ", "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("sponsor", vote.getBill().getSponsor().getFullname(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("abstain", StringUtils.join(vote.getAbstains(), " "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("aye", StringUtils.join(vote.getAyes(), " "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("excused", StringUtils.join(vote.getExcused(), " "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("nay", StringUtils.join(vote.getNays(), " "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("when",String.valueOf(vote.getVoteDate().getTime()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE) {
            document.add(new Field("committee", (vote.getDescription().isEmpty() ? vote.getBill().getCurrentCommittee() : vote.getDescription()), Field.Store.YES, Field.Index.ANALYZED));
        }

        // Add in any serializations of the bill that we might need
        for(ISenateSerializer lst:serializers) {
            document.add(new Field(lst.getType(), lst.getData(vote), Field.Store.YES, Field.Index.NO));
        }

        return document;
    }


    public static Document build(Transcript transcript, Collection<ISenateSerializer> serializers)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new Field("oid", transcript.getId().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Basic document filters
        document.add(new Field("otype", "transcript", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("year", String.valueOf(transcript.getYear()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("active", String.valueOf(transcript.isActive()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("modified", String.valueOf(transcript.getModified()), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(transcript.getTranscriptText());
        document.add(new Field("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO, Field.Index.ANALYZED));

        // Other various search fields and filters
        ArrayList<String> billIds = new ArrayList<String>();
        for (Bill bill : transcript.getRelatedBills()) {
            billIds.add(bill.getBillId());
        }
        document.add(new Field("title", transcript.getType(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("summary", transcript.getLocation(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("relatedBills", StringUtils.join(billIds, ", "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("full", transcript.getTranscriptText(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("session-type", transcript.getType(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("location", transcript.getLocation(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("when", String.valueOf(transcript.getTimeStamp().getTime()), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Add in any serializations of the bill that we might need
        for(ISenateSerializer lst:serializers) {
            document.add(new Field(lst.getType(), lst.getData(transcript), Field.Store.YES, Field.Index.NO));
        }

        return document;
    }

    public static Document build(Meeting meeting, Collection<ISenateSerializer> serializers)
    {
        Document document = new Document();

        // Allow identification based id only

        document.add(new Field("oid", meeting.getOid().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Basic document filters
        document.add(new Field("otype", "meeting", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("year", String.valueOf(meeting.getYear()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("active", String.valueOf(meeting.isActive()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("modified", String.valueOf(meeting.getModified()), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(meeting.getCommitteeName());
        searchTerms.add(meeting.getCommitteeChair());
        searchTerms.add(meeting.getLocation());
        searchTerms.add(meeting.getNotes());
        document.add(new Field("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO, Field.Index.ANALYZED));

        ArrayList<String> billIds = new ArrayList<String>();
        for (Bill bill : meeting.getBills()) {
            billIds.add(bill.getBillId());
        }

        ArrayList<String> addendumIds = new ArrayList<String>();
        for (Addendum addendum : meeting.getAddendums()) {
            addendumIds.add(addendum.getId()+"-"+addendum.getPublicationDateTime()+"-"+addendum.getMeetings());
        }

        // Other various search fields and filters
        document.add(new Field("title", meeting.getCommitteeName()+" - "+DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(meeting.getMeetingDateTime()), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("summary", meeting.getLocation(), Field.Store.NO, Field.Index.ANALYZED));
        document.add(new Field("location", meeting.getLocation(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("committee", meeting.getCommitteeName(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("chair", meeting.getCommitteeChair(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("notes", meeting.getNotes().toString(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("bills", StringUtils.join(billIds, ", "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("addendums", StringUtils.join(addendumIds, ", "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("when", String.valueOf(meeting.getMeetingDateTime().getTime()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("sortindex", meeting.getMeetingDateTime().getTime()+meeting.getCommitteeName(), Field.Store.NO, Field.Index.NOT_ANALYZED));

        // Add in any serializations of the bill that we might need
        for(ISenateSerializer lst:serializers) {
            document.add(new Field(lst.getType(), lst.getData(meeting), Field.Store.YES, Field.Index.NO));
        }

        return document;
    }

    public static Document build(Calendar calendar, Collection<ISenateSerializer> serializers)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new Field("oid", calendar.getOid().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Basic document filters
        document.add(new Field("otype", "calendar", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("year", String.valueOf(calendar.getYear()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("active", String.valueOf(calendar.isActive()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("modified", String.valueOf(calendar.getModified()), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(calendar.getTitle());
        document.add(new Field("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO, Field.Index.ANALYZED));

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

        document.add(new Field("ctype", calendar.getType().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("bills", bills.toString(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("calendarentries",calendarEntries.toString(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("summary", summaryBuffer.toString().trim(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("title", calendar.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("when",String.valueOf(calendar.getDate().getTime()), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Add in any serializations of the bill that we might need
        for(ISenateSerializer lst:serializers) {
            document.add(new Field(lst.getType(), lst.getData(calendar), Field.Store.YES, Field.Index.NO));
        }

        return document;
    }

    public static Document build(Action action, Collection<ISenateSerializer> serializers)
    {
        Document document = new Document();

        // Allow identification based id only
        document.add(new Field("oid", action.getId().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Basic document filters
        document.add(new Field("otype", "action", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("year", String.valueOf(action.getYear()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("active", String.valueOf(action.isActive()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("mod)ified", String.valueOf(action.getModified()), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(action.getBillId());
        searchTerms.add(action.getText());
        document.add(new Field("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO, Field.Index.ANALYZED));

        // Other various search fields and filters
        document.add(new Field("when",String.valueOf(action.getDate().getTime()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("billno",action.getBillId().toLowerCase(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("title", action.getText(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("summary", DateFormat.getDateInstance(DateFormat.MEDIUM).format(action.getDate()), Field.Store.YES, Field.Index.ANALYZED));

        // Add in any serializations of the bill that we might need
        for(ISenateSerializer lst:serializers) {
            document.add(new Field(lst.getType(), lst.getData(action), Field.Store.YES, Field.Index.NO));
        }

        return document;
    }

    public static Document build(Bill bill, Collection<ISenateSerializer> serializers)
    {
        Document document = new Document();

        // Allow identification based on printNumber as well as oid; e.g. S11 or S11-2013
        document.add(new Field("oid", bill.getOid().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("oid", bill.getPrintNumber().toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Basic document filters
        document.add(new Field("otype", "bill", Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("year", String.valueOf(bill.getYear()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("active", String.valueOf(bill.isActive()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("modified", String.valueOf(bill.getModified()), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // When searching without a field, match against the following terms.
        ArrayList<String> searchTerms = new ArrayList<String>();
        searchTerms.add(bill.getPrintNumber());
        searchTerms.add(String.valueOf(bill.getYear()));
        searchTerms.add(bill.getBillId());
        searchTerms.add(bill.getSameAs());
        searchTerms.add(bill.getSponsor() != null ? bill.getSponsor().getFullname() : "");
        searchTerms.add(bill.getTitle());
        searchTerms.add(bill.getSummary());
        searchTerms.add(bill.getFulltext());
        document.add(new Field("osearch", StringUtils.join(searchTerms, "; "), Field.Store.NO, Field.Index.ANALYZED));

        // Sorting should prioritize senate bills over assembly bills and resolutions.
        String sortId = bill.getPaddedBillId();
        switch (sortId.charAt(0)) {
        case 'S': sortId = "01_"+sortId; break;
        case 'A': sortId = "02_"+sortId; break;
        default: sortId = "99_"+sortId;
        }
        document.add(new Field("sortindex", sortId, Field.Store.NO, Field.Index.NOT_ANALYZED));

        // Other various search fields and filters
        document.add(new Field("title", bill.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("summary", bill.getSummary(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("lawsection", bill.getLawSection(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("sameas", bill.getSameAs(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("sponsor", bill.getSponsor() == null ? "" : bill.getSponsor().toString(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("cosponsors", StringUtils.join(bill.getCoSponsors(), ", "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("multisponsors", StringUtils.join(bill.getMultiSponsors(), ", "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("othersponsors", StringUtils.join(bill.getOtherSponsors(), ", "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("pastcommittees", StringUtils.join(bill.getPastCommittees(), ", "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("actions", StringUtils.join(bill.getActions(), ", "), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("committee", bill.getCurrentCommittee(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("full", bill.getFulltext(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("memo", bill.getMemo(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("law", bill.getLaw(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("when", String.valueOf(bill.getModified()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("unibill", String.valueOf(bill.isUniBill()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("stricken", String.valueOf(bill.isStricken()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("actclause", bill.getActClause(), Field.Store.YES, Field.Index.ANALYZED));

        // The current status of the document, usually a filter on actions will be more useful
        String billStatus = "";
        List<Action> actions = bill.getActions();
        if (!actions.isEmpty()) {
            billStatus = actions.get(actions.size()-1).getText();
        }
        document.add(new Field("status", billStatus, Field.Store.YES, Field.Index.ANALYZED));

        // Add in any serializations of the bill that we might need
        for(ISenateSerializer lst:serializers) {
            document.add(new Field(lst.getType(), lst.getData(bill), Field.Store.YES, Field.Index.NO));
        }

        return document;
    }
}
