package gov.nysenate.openleg.service.hearing;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingFile;
import gov.nysenate.openleg.model.hearing.PublicHearingId;

import java.util.List;

public interface PublicHearingDataService
{
    public PublicHearing getPublicHearing(PublicHearingId publicHearingId);

    public List<PublicHearingId> getPublicHearingIds(SessionYear sessionYear, LimitOffset limitOffset);

    public void savePublicHearing(PublicHearing publicHearing, PublicHearingFile publicHearingFile);

}
