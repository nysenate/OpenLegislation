package gov.nysenate.openleg.dao.entity;


import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Committee;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

public interface CommitteeDao {

    public Committee getCommittee(String name, Chamber chamber);
    public Committee getCommittee(String name, Chamber chamber, Date time);

    public List<Committee> getCommitteeList(Chamber chamber);

    public List<Committee> getCommitteeHistory(String name, Chamber chamber);

    public void updateCommittee(Committee committee);

    public void deleteCommittee(Committee committee);
}
