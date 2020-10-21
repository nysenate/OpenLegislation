package gov.nysenate.openleg.spotchecks.openleg.bill;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.legislation.bill.view.BillView;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.spotchecks.openleg.JsonOpenlegDaoUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *  This Repository is used to provide json data from Openleg and use Jackson to convert json string to BillView.
 * Created by Chenguang He on 2017/3/21.
 */
@Repository
public class JsonOpenlegBillDao implements OpenlegBillDao {

    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegBillDao.class);

    private static final String getBillsForSessionUriTemplate =
            BaseCtrl.BASE_API_PATH + "/bills/${session}?full=true";

    private final JsonOpenlegDaoUtils jsonOpenlegDaoUtils;

    @Autowired
    public JsonOpenlegBillDao(JsonOpenlegDaoUtils jsonOpenlegDaoUtils) {
        this.jsonOpenlegDaoUtils = jsonOpenlegDaoUtils;
    }

    @Override
    public PaginatedList<BillView> getBillViews(SessionYear sessionYear, LimitOffset limitOffset) {
        return jsonOpenlegDaoUtils.queryForViewObjects(BillView.class, getSessionUri(sessionYear), limitOffset);
    }

    private String getSessionUri(SessionYear sessionYear) {
        return StringSubstitutor.replace(getBillsForSessionUriTemplate, ImmutableMap.of("session", sessionYear));
    }
}
