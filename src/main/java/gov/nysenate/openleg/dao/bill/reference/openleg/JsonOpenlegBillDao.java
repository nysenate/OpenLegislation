package gov.nysenate.openleg.dao.bill.reference.openleg;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.service.spotcheck.openleg.JsonOpenlegDaoUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
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
        return StrSubstitutor.replace(getBillsForSessionUriTemplate, ImmutableMap.of("session", sessionYear));
    }
}
