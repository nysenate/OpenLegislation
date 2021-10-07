package gov.nysenate.openleg.api.response;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.util.DateUtils;

import java.time.LocalDateTime;
import java.util.List;

public class DateRangeListViewResponse<ViewType> extends ListViewResponse<ViewType>
{
    protected LocalDateTime fromDateTime;
    protected LocalDateTime toDateTime;

    protected DateRangeListViewResponse(ListView<ViewType> result, Range<LocalDateTime> dateTimeRange,
                                        int total, LimitOffset limitOffset) {
        super(result, total, limitOffset);
        this.fromDateTime = DateUtils.startOfDateTimeRange(dateTimeRange);
        this.toDateTime = DateUtils.endOfDateTimeRange(dateTimeRange);
    }

    public static <ViewType extends ViewObject> DateRangeListViewResponse<ViewType> of(
        List<ViewType> items, Range<LocalDateTime> dateTimeRange, int total, LimitOffset limitOffset) {
        return new DateRangeListViewResponse<>(ListView.of(items), dateTimeRange, total, limitOffset);
    }

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }
}
