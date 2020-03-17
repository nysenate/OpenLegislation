package gov.nysenate.openleg.model.bill;

import java.util.List;
import java.util.Objects;

public class BillText {

    private List<TextDiff> diffs;

    public BillText(List<TextDiff> diffs) {
        this.diffs = diffs;
    }

    public String getPlainText() {
        StringBuilder plainText = new StringBuilder();
        for (TextDiff diff : this.diffs) {
            plainText.append(diff.getPlainText());
        }
        return plainText.toString();
    }

    // TODO toPlainText, toHtmlText, to...


    @Override
    public String toString() {
        return "BillText{" +
                "diffs=" + diffs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillText billText = (BillText) o;
        return Objects.equals(diffs, billText.diffs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diffs);
    }
}
