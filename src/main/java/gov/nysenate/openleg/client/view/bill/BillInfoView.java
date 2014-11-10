package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.committee.CommitteeIdView;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.model.bill.BillStatusType;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Just the essentials for displaying a Bill in a search result for example.
 */
public class BillInfoView extends SimpleBillInfoView implements ViewObject
{
    protected String summary;
    protected boolean passed;
    protected SponsorView sponsor;
    protected BillStatusView status;
    protected ListView<BillStatusView> milestones;
    protected ProgramInfoView programInfo;

    public BillInfoView(BillInfo billInfo) {
        super(billInfo);
        if (billInfo != null) {
            summary = billInfo.getSummary();
            passed = !billInfo.getMilestones().isEmpty() &&
                billInfo.getMilestones().getLast().getStatusType().equals(BillStatusType.SIGNED_BY_GOV);
            sponsor = new SponsorView(billInfo.getSponsor());
            billType = new BillTypeView(billInfo.getBillId().getBillType());
            programInfo = billInfo.getProgramInfo() != null ? new ProgramInfoView(billInfo.getProgramInfo()) : null;
            status = new BillStatusView(billInfo.getStatus());
            milestones = ListView.of(billInfo.getMilestones().stream().map(BillStatusView::new).collect(toList()));
        }
    }

    public String getSummary() {
        return summary;
    }

    public boolean isPassed() {
        return passed;
    }

    public SponsorView getSponsor() {
        return sponsor;
    }

    public BillStatusView getStatus() {
        return status;
    }

    public ListView<BillStatusView> getMilestones() {
        return milestones;
    }

    public ProgramInfoView getProgramInfo() {
        return programInfo;
    }

    @Override
    public String getViewType() {
        return "bill-info";
    }
}