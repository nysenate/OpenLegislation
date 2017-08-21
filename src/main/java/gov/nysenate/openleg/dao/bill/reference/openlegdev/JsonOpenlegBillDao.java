package gov.nysenate.openleg.dao.bill.reference.openlegdev;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import gov.nysenate.openleg.client.view.bill.BillView;
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
 *  This Repository is used to provide json data from Openleg Dev and use Jackson to convert json string to BillView.
 * Created by Chenguang He on 2017/3/21.
 */
@Repository
public class JsonOpenlegBillDao implements OpenlegBillDao {
    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegBillDao.class);

    String callHeader = "http://legislation.nysenate.gov/api/3/bills/";
    HttpURLConnection connection = null;

    @Override
    public List<BillView> getOpenlegBillView(String sessionYear, String apiKey) {

        List<BillView> billViews = new LinkedList<>();
        StringBuilder response = new StringBuilder();
        try {
            // request json
            URL url = new URL(callHeader + sessionYear  + "?full=true&limit=1000&key=" + apiKey);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new GuavaModule());
            JsonNode node = mapper.readTree(response.toString());
            billViews.addAll(toBillView(node));
            connection.disconnect();
            //   if it is a large request, then get rest of bill
            int offset = node.get("offsetEnd").asInt();
            int total = node.get("total").asInt();
            logger.info("Fetching bill from openleg ref with offset " + offset);
            while (offset < total) {
                //get next page of bill
                StringBuffer sb = new StringBuffer();
                URL next = new URL(callHeader + sessionYear + "?full=true&key=" + apiKey + "&limit=1000&offset=" + (offset + 1));
                connection = (HttpURLConnection) next.openConnection();
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                InputStream inputStream_next = connection.getInputStream();
                BufferedReader reader_next = new BufferedReader(new InputStreamReader(inputStream_next));
                String line_next;
                while ((line_next = reader_next.readLine()) != null) {
                    sb.append(line_next);
                }
                reader_next.close();
                JsonNode node_next = mapper.readTree(sb.toString());
                billViews.addAll(toBillView(node_next));
                connection.disconnect();
                //update variables
                offset = node_next.get("offsetEnd").asInt();
                logger.info("Fetching bill from openleg dev with offset " + offset);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                billViewList.add(mapper.readValue(node1.toString(), BillView.class));
            }
        }
        return billViewList;
    }

}
