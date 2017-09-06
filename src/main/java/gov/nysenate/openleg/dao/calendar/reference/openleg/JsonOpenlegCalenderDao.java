package gov.nysenate.openleg.dao.calendar.reference.openleg;

import gov.nysenate.openleg.service.spotcheck.openleg.JsonOpenlegDaoUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import gov.nysenate.openleg.client.view.calendar.CalendarView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Repository
public class JsonOpenlegCalenderDao implements OpenlegCalenderDao {
    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegCalenderDao.class);

    private String callHeader = "http://legislation.nysenate.gov/api/3/calendars/";
    private HttpURLConnection connection = null;
    private int offset = 0;
    private int total = 0;

    public List<CalendarView> getOpenlegCalenderView(String sessionYear, String apiKey) {
        List<CalendarView> calendarViews = new LinkedList<>();
        StringBuffer response = new StringBuffer();

        connection = JsonOpenlegDaoUtils.setConnection(callHeader + sessionYear  + "?full=true&limit=1000&key=" + apiKey, "GET", false, true);
        JsonOpenlegDaoUtils.readInputStream(connection, response);
        mapJSONToCalendarView(response, calendarViews);
        connection.disconnect();

        while (offset < total) {
            StringBuffer restOfCalender = new StringBuffer();
            connection = JsonOpenlegDaoUtils.setConnection(callHeader + sessionYear + "?full=true&key=" + apiKey + "&limit=1000&offset=" + (offset + 1),"GET",false,true );
            JsonOpenlegDaoUtils.readInputStream(connection, restOfCalender);
            mapJSONToCalendarView(restOfCalender, calendarViews);
            connection.disconnect();
        }
        return calendarViews;
    }

    private List<CalendarView> toCalendarView(JsonNode node) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        List<CalendarView> calendarViewList = new LinkedList<>();
        if (node.get("result").get("items") == null) { // if there is only 1 available bill
            calendarViewList.add(mapper.readValue(node.get("result").toString(), CalendarView.class));
        } else { // if there are many available bills.
            Iterator<JsonNode> nodeIterator = node.get("result").get("items").iterator();
            while (nodeIterator.hasNext()) {
                JsonNode node1 = nodeIterator.next();
                calendarViewList.add(mapper.readValue(node1.toString(), CalendarView.class));
            }
        }
        return calendarViewList;
    }

    private void mapJSONToCalendarView(StringBuffer response, List<CalendarView> calendarViews) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new GuavaModule());

            JsonNode node = null;
            node = mapper.readTree(response.toString());

            logger.info("Fetching calender from openleg ref with offset " + offset);
            setOffset( node.get("offsetEnd").asInt() );
            setTotal( node.get("total").asInt() );

            calendarViews.addAll(toCalendarView(node));
        } catch (IOException e) {
            logger.error("The JSON Object could not be mapped to a bill view");
            e.printStackTrace();
        }
    }

    private void setOffset(int update) {
        offset = update;
    }

    private void setTotal(int total) {
        this.total = total;
    }
}
