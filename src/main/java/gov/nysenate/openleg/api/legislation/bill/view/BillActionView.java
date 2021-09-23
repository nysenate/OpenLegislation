package gov.nysenate.openleg.api.legislation.bill.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.bill.BillAction;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.committee.Chamber;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class BillActionView implements ViewObject {

    protected BillIdView billId;
    protected String date;
    protected String chamber;
    protected Integer sequenceNo;
    protected String text;

    protected BillActionView() {}

    public BillActionView(BillAction billAction) {
        Optional<BillAction> billActionOpt = Optional.ofNullable(billAction);
        billId = billActionOpt.map(BillAction::getBillId)
                .map(BillIdView::new)
                .orElse(null);
        date = billActionOpt.map(BillAction::getDate)
                .map(LocalDate::toString)
                .orElse(null);
        chamber = billActionOpt.map(BillAction::getChamber)
                .map(Chamber::name)
                .orElse(null);
        sequenceNo = billActionOpt.map(BillAction::getSequenceNo)
                .orElse(null);
        text = billActionOpt.map(BillAction::getText)
                .orElse(null);
    }

    @JsonIgnore
    public BillAction toBillAction() {
        LocalDate date = Optional.ofNullable(this.date)
                .map(LocalDate::parse).orElse(null);
        Chamber chamber = Optional.ofNullable(this.chamber)
                .map(Chamber::getValue).orElse(null);
        BillId billId = null;
        try {
            billId = Optional.ofNullable(this.billId)
                    .map(BillIdView::toBillId).orElse(null);
        } catch (IllegalArgumentException | NullPointerException ignored) {}
        return new BillAction( date, text, chamber, sequenceNo, billId);
    }

    public BillIdView getBillId() {
        return billId;
    }

    public String getDate() {
        return date;
    }

    public String getChamber() {
        return chamber;
    }

    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "bill-action";
    }
}
