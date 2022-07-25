package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseHearingCtrl extends BaseCtrl {
    @Autowired
    private HearingDataService hearingData;


}
