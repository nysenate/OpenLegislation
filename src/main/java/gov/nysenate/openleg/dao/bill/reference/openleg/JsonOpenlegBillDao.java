package gov.nysenate.openleg.dao.bill.reference.openleg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.service.spotcheck.openleg.JsonOpenlegDaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *  This Repository is used to provide json data from Openleg and use Jackson to convert json string to BillView.
 * Created by Chenguang He on 2017/3/21.
 */
@Repository
public class JsonOpenlegBillDao implements OpenlegBillDao {
    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegBillDao.class);

    String callHeader = "http://legislation.nysenate.gov/api/3/bills/";
    HttpURLConnection connection = null;
    int offset = 0;
    int total = 0;

    @Override
    public List<BillView> getOpenlegBillView(String sessionYear, String apiKey) {
        List<BillView> billViews = new LinkedList<>();
        StringBuffer response = new StringBuffer();

        connection = JsonOpenlegDaoUtils.setConnection(callHeader + sessionYear  + "?full=true&limit=1000&key=" + apiKey, "GET", false, true);
        JsonOpenlegDaoUtils.readInputStream(connection, response);
        mapJSONToBillView(response, billViews);
        connection.disconnect();

        while (offset < total) {
            StringBuffer restOfBill = new StringBuffer();
            connection = JsonOpenlegDaoUtils.setConnection(callHeader + sessionYear + "?full=true&key=" + apiKey + "&limit=1000&offset=" + (offset + 1),"GET",false,true );
            JsonOpenlegDaoUtils.readInputStream(connection, restOfBill);
            mapJSONToBillView(restOfBill, billViews);
            connection.disconnect();
        }
        return billViews;
    }

    private List<BillView> toBillView(JsonNode node) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        List<BillView> billViewList = new LinkedList<>();
        if (node.get("result").get("items") == null) { // if there is only 1 available bill
            billViewList.add(mapper.readValue(node.get("result").toString(), BillView.class));
        } else { // if there are many available bills.
            Iterator<JsonNode> nodeIterator = node.get("result").get("items").iterator();
            while (nodeIterator.hasNext()) {
                JsonNode node1 = nodeIterator.next();
                billViewList.add(mapper.readValue(new String(node1.toString().getBytes("UTF-8")), BillView.class));
            }
        }
        return billViewList;
    }

    private void mapJSONToBillView(StringBuffer response, List<BillView> billViews) {
        try {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());

        JsonNode node = null;
        node = mapper.readTree(response.toString());

        logger.info("Fetching bill from openleg ref with offset " + offset);
        setOffset( node.get("offsetEnd").asInt() );
        setTotal( node.get("total").asInt() );

        billViews.addAll(toBillView(node));
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
