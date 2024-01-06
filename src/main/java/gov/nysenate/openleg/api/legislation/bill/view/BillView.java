package gov.nysenate.openleg.api.legislation.bill.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.agenda.view.CommAgendaIdView;
import gov.nysenate.openleg.api.legislation.calendar.view.CalendarIdView;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeVersionIdView;
import gov.nysenate.openleg.api.legislation.member.view.MemberView;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillTextFormat;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

/**
 * A complete representation of a bill including its amendments.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillView extends BillInfoView implements ViewObject
{
    protected ListView<String> amendmentVersions;
    protected MapView<String, BillAmendmentView> amendments;
    protected ListView<BillVoteView> votes;
    protected ListView<VetoMessageView> vetoMessages;
    protected ApprovalMessageView approvalMessage;
    protected String activeVersion;
    protected ListView<MemberView> additionalSponsors;
    protected ListView<CommitteeVersionIdView> pastCommittees;
    protected ListView<BillActionView> actions;
    protected ListView<BillIdView> previousVersions;
    protected ListView<CommAgendaIdView> committeeAgendas;
    protected ListView<CalendarIdView> calendars;

    public BillView(){}

    public BillView(Bill bill, Set<BillTextFormat> fullTextFormats) {
        super(bill != null ? bill.getBillInfo() : null);
        if (bill != null) {
            // Only output amendments that are currently published
            TreeMap<String, BillAmendmentView> amendmentMap = new TreeMap<>();
            bill.getAmendPublishStatusMap().forEach((k,v) -> {
                if (v.isPublished() && bill.hasAmendment(k)) {
                    amendmentMap.put(k.toString(), new BillAmendmentView(bill.getAmendment(k), v, fullTextFormats));
                }
            });

            this.amendments = MapView.of(amendmentMap);
            this.amendmentVersions = ListView.ofStringList(new ArrayList<>(amendmentMap.keySet()));

            this.votes = ListView.of(bill.getAmendmentList().stream()
                .flatMap(a -> a.getVotesList().stream())
                .sorted()
                .map(BillVoteView::new)
                    .toList());

            this.vetoMessages = ListView.of(bill.getVetoMessages().values().stream()
                .map(VetoMessageView::new).toList());

            this.approvalMessage = bill.getApprovalMessage() != null ?
                new ApprovalMessageView(bill.getApprovalMessage()) : null;

            this.activeVersion = bill.getActiveVersion().toString();

            this.additionalSponsors = ListView.of(bill.getAdditionalSponsors().stream()
                .map(MemberView::new).toList());

            this.pastCommittees = ListView.of(bill.getPastCommittees().stream()
                .map(CommitteeVersionIdView::new).toList());

            this.actions = ListView.of(bill.getActions().stream()
                .map(BillActionView::new).toList());

            this.previousVersions = ListView.of(bill.getAllPreviousVersions().stream()
                .map(BillIdView::new)
                .sorted((a,b) -> Integer.compareUnsigned(b.getSession(), a.getSession())).toList());

            this.committeeAgendas = ListView.of(bill.getCommitteeAgendas().stream()
                .map(CommAgendaIdView::new).toList());

            this.calendars = ListView.of(bill.getCalendars().stream()
                .map(CalendarIdView::new).toList());
        }
    }

    @Override
    public String getViewType() {
        return "bill";
    }

    public ListView<String> getAmendmentVersions() {
        return amendmentVersions;
    }

    public MapView<String, BillAmendmentView> getAmendments() {
        return amendments;
    }

    public ListView<BillVoteView> getVotes() {
        return votes;
    }

    public ListView<VetoMessageView> getVetoMessages() {
        return vetoMessages;
    }

    public ApprovalMessageView getApprovalMessage() {
        return approvalMessage;
    }

    public ListView<MemberView> getAdditionalSponsors() {
        return additionalSponsors;
    }

    public ListView<CommitteeVersionIdView> getPastCommittees() {
        return pastCommittees;
    }

    public ListView<BillActionView> getActions() {
        return actions;
    }

    public ListView<BillIdView> getPreviousVersions() {
        return previousVersions;
    }

    public ListView<CommAgendaIdView> getCommitteeAgendas() {
        return committeeAgendas;
    }

    public ListView<CalendarIdView> getCalendars() {
        return calendars;
    }

    public String getActiveVersion() {
        return activeVersion;
    }
}