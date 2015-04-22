package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.agenda.CommAgendaIdView;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.calendar.CalendarIdView;
import gov.nysenate.openleg.client.view.committee.CommitteeVersionIdView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.bill.Bill;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * A complete representation of a bill including it's amendments.
 */
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

    public BillView(Bill bill) {
        super(bill != null ? bill.getBillInfo() : null);
        if (bill != null) {
            // Only output amendments that are currently published
            TreeMap<String, BillAmendmentView> amendmentMap = new TreeMap<>();
            bill.getAmendPublishStatusMap().forEach((k,v) -> {
                if (v.isPublished() && bill.hasAmendment(k)) {
                    amendmentMap.put(k.getValue(), new BillAmendmentView(bill.getAmendment(k), v));
                }
            });
            this.amendments = MapView.of(amendmentMap);
            this.amendmentVersions = ListView.ofStringList(amendmentMap.keySet().stream().collect(Collectors.toList()));

            this.votes = ListView.of(bill.getAmendmentList().stream()
                .flatMap(a -> a.getVotesList().stream())
                .sorted()
                .map(v -> new BillVoteView(v))
                .collect(Collectors.toList()));

            this.vetoMessages = ListView.of(bill.getVetoMessages().values().stream()
                .map(VetoMessageView::new)
                .collect(Collectors.toList()));

            this.approvalMessage = bill.getApprovalMessage() != null ?
                new ApprovalMessageView(bill.getApprovalMessage()) : null;

            this.activeVersion = bill.getActiveVersion().getValue();

            this.additionalSponsors = ListView.of(bill.getAdditionalSponsors().stream()
                .map(MemberView::new)
                .collect(Collectors.toList()));

            this.pastCommittees = ListView.of(bill.getPastCommittees().stream()
                .map(CommitteeVersionIdView::new)
                .collect(Collectors.toList()));

            this.actions = ListView.of(bill.getActions().stream()
                .map(BillActionView::new)
                .collect(Collectors.toList()));

            this.previousVersions = ListView.of(bill.getPreviousVersions().stream()
                .map(BillIdView::new)
                .sorted((a,b) -> Integer.compareUnsigned(b.getSession(), a.getSession()))
                .collect(Collectors.toList()));

            this.committeeAgendas = ListView.of(bill.getCommitteeAgendas().stream()
                .map(CommAgendaIdView::new)
                .collect(Collectors.toList()));

            this.calendars = ListView.of(bill.getCalendars().stream()
                .map(CalendarIdView::new)
                .collect(Collectors.toList()));
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
}