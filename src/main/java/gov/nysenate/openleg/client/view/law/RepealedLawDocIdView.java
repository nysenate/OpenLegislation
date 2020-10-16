package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.model.law.RepealedLawDocId;

import java.time.LocalDate;

public class RepealedLawDocIdView extends LawDocIdView {
    protected LocalDate repealedDate;

    public RepealedLawDocIdView(RepealedLawDocId id) {
        super(id);
        this.repealedDate = id.getRepealedDate();
    }

    @Override
    public String getViewType() {
        return super.getViewType() + "with-repealed";
    }

    public LocalDate getRepealedDate() {
        return repealedDate;
    }
}
