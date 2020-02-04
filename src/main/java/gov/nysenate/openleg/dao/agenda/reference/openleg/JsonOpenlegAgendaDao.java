package gov.nysenate.openleg.dao.agenda.reference.openleg;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.view.agenda.AgendaSummaryView;
import gov.nysenate.openleg.client.view.agenda.AgendaView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.service.spotcheck.openleg.JsonOpenlegDaoUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class JsonOpenlegAgendaDao implements OpenlegAgendaDao {

    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegAgendaDao.class);

    private static final String agendasForYearUriTemplate = BaseCtrl.BASE_API_PATH + "/agendas/${year}";
    private static final String getAgendaUriTemplate = agendasForYearUriTemplate + "/${agendaNo}";

    private final JsonOpenlegDaoUtils jsonOpenlegDaoUtils;

    @Autowired
    public JsonOpenlegAgendaDao(JsonOpenlegDaoUtils jsonOpenlegDaoUtils) {
        this.jsonOpenlegDaoUtils = jsonOpenlegDaoUtils;
    }

    @Override
    public List<AgendaView> getAgendaViews(int year) {

        final String agendasForYearUri =
                StringSubstitutor.replace(agendasForYearUriTemplate, ImmutableMap.of("year", year));
        PaginatedList<AgendaSummaryView> agendaSummaries =
                jsonOpenlegDaoUtils.queryForViewObjects(AgendaSummaryView.class, agendasForYearUri, LimitOffset.ALL);

        return agendaSummaries.getResults().stream()
                .map(AgendaSummaryView::getId)
                .map(aiv -> new AgendaId(aiv.getNumber(), aiv.getYear()))
                .map(this::getAgendaView)
                .collect(Collectors.toList());
    }

    @Override
    public AgendaView getAgendaView(AgendaId agendaId) {
        Map<String, ?> subMap = ImmutableMap.of(
                "year", agendaId.getYear(),
                "agendaNo", agendaId.getNumber()
        );
        final String agendaUri = StringSubstitutor.replace(getAgendaUriTemplate, subMap);
        return jsonOpenlegDaoUtils.queryForViewObject(AgendaView.class, agendaUri);
    }
}
