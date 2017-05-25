package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.spotcheck.MismatchStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class MismatchStatusUtils {

    private int reportLength;

    public MismatchStatusUtils(@Value("${spotcheck.report.period.length:1}") final int reportLength) {
        this.reportLength = reportLength;
    }

    public LocalDateTime getStatusStartDateTime(MismatchStatus status, LocalDateTime reportEndDateTime) {
        LocalDateTime startDateTime = null;
        switch(status) {
            case NEW:
            case RESOLVED:
                startDateTime = reportEndDateTime.minusDays(reportLength - 1).truncatedTo(ChronoUnit.DAYS);
                break;
            case EXISTING:
            case OPEN:
                startDateTime = SessionYear.of(reportEndDateTime.getYear()).getStartDateTime();
                break;
        }
        return startDateTime;
    }

}
