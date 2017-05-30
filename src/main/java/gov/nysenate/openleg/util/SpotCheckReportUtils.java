package gov.nysenate.openleg.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SpotCheckReportUtils {

    public static LocalDateTime getReportStartDateTime(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime getReportEndDateTime(LocalDate date) {
        return date.atTime(23, 59, 59);
    }
}
