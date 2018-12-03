package gov.nysenate.openleg.dao.calendar.reference.openleg;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.view.calendar.CalendarView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.service.spotcheck.openleg.JsonOpenlegDaoUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JsonOpenlegCalendarDao implements OpenlegCalendarDao {

    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegCalendarDao.class);

    private static final String getCalendarsUriTemplate = BaseCtrl.BASE_API_PATH + "/calendars/${year}?full=true";

    private final JsonOpenlegDaoUtils jsonOpenlegDaoUtils;

    @Autowired
    public JsonOpenlegCalendarDao(JsonOpenlegDaoUtils jsonOpenlegDaoUtils) {
        this.jsonOpenlegDaoUtils = jsonOpenlegDaoUtils;
    }

    @Override
    public List<CalendarView> getCalendarViews(int year) {
        final String getCalsUri = StrSubstitutor.replace(getCalendarsUriTemplate, ImmutableMap.of("year", year));
        return jsonOpenlegDaoUtils.queryForViewObjects(CalendarView.class, getCalsUri, LimitOffset.ALL).getResults();
    }
}
