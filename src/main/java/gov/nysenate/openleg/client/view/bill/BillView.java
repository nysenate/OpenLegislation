package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.committee.CommitteeVersionIdView;
import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.client.view.entity.SimpleMemberView;
import gov.nysenate.openleg.model.bill.Bill;

import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Map.*;
import static java.util.AbstractMap.*;

/**
 * A complete representation of a bill including it's amendments.
 */
public class BillView extends BillInfoView implements ViewObject
{
    protected MapView<String, BillAmendmentView> amendments;
    protected MapView<String, PublishStatusView> amendmentPublishStatuses;
    protected ListView<VetoMessageView> vetoMessages;
    protected ApprovalMessageView approvalMessage;
    protected String activeVersion;
    protected ListView<MemberView> additionalSponsors;
    protected ListView<CommitteeVersionIdView> pastCommittees;
    protected ListView<BillActionView> actions;
    protected ListView<BillIdView> previousVersions;
    protected ProgramInfoView programInfo;

    public BillView(Bill bill) {
        super(bill != null ? bill.getBillInfo() : null);
        if (bill != null) {
            this.amendments = MapView.of(bill.getAmendmentList().stream()
                    .map(BillAmendmentView::new)
                    .collect(Collectors.toMap(BillAmendmentView::getVersion, Function.identity(), (a, b) -> b, TreeMap::new)));
            this.amendmentPublishStatuses = MapView.of(bill.getAmendPublishStatusMap().entrySet().stream()
                    .map(entry -> new SimpleEntry<>(entry.getKey().toString(), new PublishStatusView(entry.getValue())))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
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
                    .collect(Collectors.toList()));
            this.programInfo = new ProgramInfoView(bill.getProgramInfo());
        }
    }

    @Override
    public String getViewType() {
        return "bill";
    }

    public MapView<String, BillAmendmentView> getAmendments() {
        return amendments;
    }

    public MapView<String, PublishStatusView> getAmendmentPublishStatuses() {
        return amendmentPublishStatuses;
    }

    public ListView<VetoMessageView> getVetoMessages() {
        return vetoMessages;
    }

    public ApprovalMessageView getApprovalMessage() {
        return approvalMessage;
    }

    public String getActiveVersion() {
        return activeVersion;
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

    public ProgramInfoView getProgramInfo() {
        return programInfo;
    }
}
