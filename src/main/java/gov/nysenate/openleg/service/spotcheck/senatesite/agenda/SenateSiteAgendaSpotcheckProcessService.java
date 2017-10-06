package gov.nysenate.openleg.service.spotcheck.senatesite.agenda;

import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.service.spotcheck.senatesite.base.SenateSiteSpotcheckProcessService;
import org.springframework.stereotype.Service;

@Service
public class SenateSiteAgendaSpotcheckProcessService extends SenateSiteSpotcheckProcessService {
    @Override
    protected SpotCheckRefType getRefType() {
        return SpotCheckRefType.SENATE_SITE_AGENDA;
    }
}
