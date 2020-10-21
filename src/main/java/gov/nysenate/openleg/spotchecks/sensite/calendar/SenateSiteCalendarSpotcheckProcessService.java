package gov.nysenate.openleg.spotchecks.sensite.calendar;

import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteSpotcheckProcessService;
import org.springframework.stereotype.Service;

@Service
public class SenateSiteCalendarSpotcheckProcessService extends SenateSiteSpotcheckProcessService {
    @Override
    protected SpotCheckRefType getRefType() {
        return SpotCheckRefType.SENATE_SITE_CALENDAR;
    }
}
