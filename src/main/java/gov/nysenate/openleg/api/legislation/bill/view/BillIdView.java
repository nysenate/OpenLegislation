package gov.nysenate.openleg.api.legislation.bill.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.bill.BillId;

import java.util.Optional;

public class BillIdView extends BaseBillIdView implements ViewObject
{
    protected String printNo;
    protected String version;

    protected BillIdView() {}

    public BillIdView(BillId billId) {
        super(billId);
        if (billId != null) {
            this.printNo = billId.getPrintNo();
            this.version = Optional.ofNullable(billId.getVersion())
                    .map(Version::toString).orElse(null);
        }
    }

    @JsonIgnore
    public BillId toBillId() {
        return toBaseBillId().withVersion(Version.of(version));
    }

    public String getPrintNo() {
        return printNo;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getViewType() {
        return "bill-id";
    }
}
