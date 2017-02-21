package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.MismatchSummary;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MismatchSummaryView implements ViewObject {

//    protected MapV

    @Override
    public String getViewType() {
        return "mismatch-summary";
    }
}
