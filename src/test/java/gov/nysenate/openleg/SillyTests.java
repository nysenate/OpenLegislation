package gov.nysenate.openleg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.client.view.agenda.AgendaSummaryView;
import gov.nysenate.openleg.client.view.agenda.AgendaView;
import gov.nysenate.openleg.client.view.bill.BillActionView;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.client.view.calendar.CalendarView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillUpdateEvent;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.entity.MemberService;
import gov.nysenate.openleg.util.OutputUtils;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class SillyTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    @Autowired
    Client searchClient;

    @Autowired
    EventBus eventBus;

    @Autowired
    NamedParameterJdbcTemplate jdbcNamed;

    //@Autowired
    public DataSource dataSource;

    //@Autowired
    private MemberService memberService;

    @Autowired
    private BillDao billDao;

    @Autowired
    private BillDataService billData;

    @Autowired
    private CalendarDataService calendarData;

    @Autowired
    private AgendaDataService agendaDataService;

    @Test
    public void testName() throws Exception {
        eventBus.post(new BillUpdateEvent(null, LocalDateTime.now()));
    }

    @Test
    public void testAgendaView() throws Exception {
        logger.info("{}", OutputUtils.toJson(new AgendaView(agendaDataService.getAgenda(new AgendaId(5, 2014)))));
    }

    @Test
    public void testSomething() throws Exception {
        Bill bill = billDao.getBill(new BillId("S1234", 2013));
        String json = OutputUtils.toJson(new BillView(bill));
        logger.info("{}", json);

        StopWatch sw = new StopWatch();
        sw.start();
        OutputUtils.getJsonMapper().readValue(json, BillView.class);
        sw.stop();
        logger.info("{}", sw.getTime());
    }

    @Test
    public void testMEm() throws Exception {
        logger.info("{}");
    }

    @Test
    public void testBillCount() throws Exception {
        logger.info("{}", billDao.getBillCount());
    }

    @Test
    public void testBillCache() throws Exception {
        Bill bill = billDao.getBill(new BillId("S1234", 2013));
        BillAmendment ba = bill.getAmendment(Version.DEFAULT);
        Assert.assertNotSame(ba, ba.shallowClone());


        Bill cloneBill = bill.shallowClone();
        Assert.assertNotSame(bill, cloneBill);
        Assert.assertNotSame(bill.getAmendment(Version.DEFAULT), cloneBill.getAmendment(Version.DEFAULT));
        Assert.assertEquals(OutputUtils.toJson(bill), OutputUtils.toJson(cloneBill));
    }

    @Test
    public void playwithcalendars() {
        List<gov.nysenate.openleg.model.calendar.Calendar> calendars = calendarData.getCalendars(2013, SortOrder.ASC, LimitOffset.ALL);
        BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
        calendars.stream()
               .map(c -> new CalendarView(calendarData.getCalendar(c.getId())))
               .forEach(v -> bulkRequest.add(searchClient.prepareIndex("calendars", "2013", Integer.toString(v.getCalendarNumber()))
                       .setSource(OutputUtils.toJson(v))));
//                    CalendarView(c)))));
        bulkRequest.execute().actionGet();
    }

    @Test
    public void playWithSearch() {
        LimitOffset limOff = LimitOffset.HUNDRED;
        List<BaseBillId> ids = billDao.getBillIds(SessionYear.current(), limOff, SortOrder.ASC);
        while (!ids.isEmpty()) {
            logger.info("{}", ids.get(0));
            BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
            List<BillView> billList =
            ids.parallelStream().map(b -> new BillView(billDao.getBill(b))).collect(Collectors.toList());
            billList.forEach(b ->
                bulkRequest.add(searchClient.prepareIndex("bills", Integer.toString(b.getSession()), b.getBasePrintNo())
                        .setSource(OutputUtils.toJson(b)))
            );
            bulkRequest.execute().actionGet();
            limOff = limOff.next();
            ids = billDao.getBillIds(SessionYear.current(), limOff, SortOrder.ASC);
            logger.info("Starting round {}", limOff.getOffsetStart());
        }

//        GetResponse getResponse = searchClient.prepareGet("bills", "2013", "S1637").execute().actionGet();
//        logger.info("{}", getResponse.getFields());
    }

    @Test
    public void testHuh() throws Exception {
        SearchResponse resp = searchClient.prepareSearch("bills")//.setTypes("2013")
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setQuery(QueryBuilders.queryString("S1234"))
                .setFetchSource(false)
                .setFrom(0)
                .execute()
                .actionGet();
        logger.info("{}", resp.getHits().getTotalHits());
        for (SearchHit hit : resp.getHits().getHits()) {
            logger.info("{} {}", hit.getId(), hit.getType());
        }
    }

    @Test
    public void testDeserialize() throws Exception {
        String s=  searchClient.prepareGet("bills", "2013", "S1234").execute().get().getSourceAsString();
        ObjectMapper om = new ObjectMapper();
        om.readValue(s, BillInfoView.class);
    }

    @Test
    public void testMapSomethign() throws Exception {
        logger.info("{}", "Moose\\nhahaha".replaceAll("\\\\n", " "));

    }

    @Test
    public void testHstorePostgres() throws Exception {
        jdbcNamed.query("SELECT hstore_to_array(key) as k from master.sobi_change_log limit 1", (rs, row) -> {
            logger.info("{}", getHstore(rs, "k"));
            return null;
        });
    }

    private Map<String, String> getHstore(ResultSet rs, String column) throws SQLException {
        String[] hstoreArr = (String[]) rs.getArray(column).getArray();
        Map<String, String> hstoreMap = new HashMap<>();
        String key = "";
        for (int i = 0; i < hstoreArr.length; i++) {
            if (i % 2 == 0) {
                key = hstoreArr[i];
            }
            else {
                hstoreMap.put(key, hstoreArr[i]);
            }
        }
        return hstoreMap;
    }

    @Test
    public void testInsertHstore() throws Exception {
        Map<String, String> keys = new HashMap<>();
        keys.put("apple", "NY");
        keys.put("potatoes", "ID");
        String keystr = keys.entrySet().stream().map(kv -> kv.getKey() + "=>" + kv.getValue()).collect(Collectors.joining(","));
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("key", keystr);
        jdbcNamed.update("INSERT INTO public.test (key) VALUES (hstore(:key))", params);

    }
}
