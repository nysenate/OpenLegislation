package gov.nysenate.openleg.legislation.transcripts.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.bill.BillId;

public record BillMention(BillId billId, Position position) implements ViewObject {
    public BillMention {
        if (billId == null) {
            throw new IllegalArgumentException("Bill mention must have a billId");
        }
        if (position == null) {
            throw new IllegalArgumentException("Bill mention must have a line position");
        }
    }

    @JsonIgnore
    public String getTagStart() {
        String billHrefStr = '/' + billId().getSession().toString() + '/' + billId().getPrintNo();
        String idStr = String.join("-", billId().getSession().toString(), billId().getPrintNo(),
                "p" + position.pageNumStart(), "l" + position.lineNumStart());
        return """
                <a href="%s" target="_blank" class="link" id="%s">""".formatted(billHrefStr, idStr);
    }

    @Override
    public String getViewType() {
        return "bill-mention";
    }
}
