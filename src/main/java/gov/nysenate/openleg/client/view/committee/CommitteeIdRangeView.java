package gov.nysenate.openleg.client.view.committee;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.view.temporal.DateTimeRangeView;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.time.LocalDateTime;

public class CommitteeIdRangeView extends CommitteeIdView {

    protected DateTimeRangeView dateTimeRange;

    public CommitteeIdRangeView(CommitteeId committeeId, Range<LocalDateTime> dateTimeRange) {
        super(committeeId);
        this.dateTimeRange = new DateTimeRangeView(dateTimeRange);
    }

    @Override
    public String getViewType() {
        return "committee-id-range";
    }

    public DateTimeRangeView getDateTimeRange() {
        return dateTimeRange;
    }
}
