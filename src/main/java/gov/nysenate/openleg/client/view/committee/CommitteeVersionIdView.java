package gov.nysenate.openleg.client.view.committee;

import gov.nysenate.openleg.model.entity.CommitteeVersionId;

public class CommitteeVersionIdView extends CommitteeSessionIdView implements Comparable<CommitteeVersionIdView>
{
    protected String referenceDate;

    public CommitteeVersionIdView(CommitteeVersionId committeeVersionId) {
        super(committeeVersionId);
        if (committeeVersionId != null) {
            this.referenceDate = committeeVersionId.getReferenceDate().toString();
        }
    }

    public String getReferenceDate() {
        return referenceDate;
    }

    @Override
    public String getViewType() {
        return "committee-version-id";
    }

    @Override
    public int compareTo(CommitteeVersionIdView o) {
        return this.referenceDate.compareTo(o.getReferenceDate());
    }
}
