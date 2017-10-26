package gov.nysenate.openleg.dao.agenda.reference.openleg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import gov.nysenate.openleg.client.view.agenda.AgendaView;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.service.spotcheck.openleg.JsonOpenlegDaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Repository
public class JsonOpenlegAgendaDao implements OpenlegAgendaDao {
    private static final Logger logger = LoggerFactory.getLogger(JsonOpenlegAgendaDao.class);

    @Autowired
    Environment env;

    HttpURLConnection connection = null;

    @Override
    public List<AgendaView> getOpenlegAgendaView(String sessionYear, String apiKey) {
        List<AgendaView> agendaViews = new LinkedList<>();
        StringBuffer response = new StringBuffer();

        for (AgendaId agendaId: getAgendaIds(sessionYear)) {
            connection = JsonOpenlegDaoUtils.setConnection(env.getRefUrl()+"/api/3/agendas/" + sessionYear  + "/" + agendaId.getNumber() + "?key=" + apiKey, "GET", false, true);
            JsonOpenlegDaoUtils.readInputStream(connection, response);
            mapJSONToAgendaView(response, agendaViews);
            connection.disconnect();
        }
        return agendaViews;
    }

    private List<AgendaView> toAgendaView(JsonNode node) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        List<AgendaView> agendaViewList = new LinkedList<>();
        if (node.get("result").get("items") == null) { // if there is only 1 available agenda
            agendaViewList.add(mapper.readValue(node.get("result").toString(), AgendaView.class));
        } else { // if there are many available agendas
            Iterator<JsonNode> nodeIterator = node.get("result").get("items").iterator();
            while (nodeIterator.hasNext()) {
                JsonNode node1 = nodeIterator.next();
                agendaViewList.add(mapper.readValue(node1.toString(), AgendaView.class));
            }
        }
        return agendaViewList;
    }

    private void mapJSONToAgendaView(StringBuffer response, List<AgendaView> agendaViews) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new GuavaModule());

            JsonNode node = null;
            node = mapper.readTree(response.toString());
            agendaViews.addAll(toAgendaView(node));
        } catch (IOException e) {
            logger.error("The JSON Object could not be mapped to a  AgendaView");
            e.printStackTrace();
        }
    }

    //***********************
    //GET AGENDA IDS METHODS

    private List<AgendaId> toAgendaId(JsonNode node) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
        List<AgendaId> agendaIdList = new LinkedList<>();
        if (node.get("result").get("items") == null) { // if there is only 1 available agendaId
            agendaIdList.add(mapper.readValue(node.get("result").toString(), AgendaId.class));
        } else { // if there are many available agendaId.
            Iterator<JsonNode> nodeIterator = node.get("result").get("items").iterator();
            while (nodeIterator.hasNext()) {
                JsonNode node1 = nodeIterator.next().get("id");
                agendaIdList.add(mapper.readValue(node1.toString(), AgendaId.class));
            }
        }
        return agendaIdList;
    }

    private void mapJSONToAgendaId(StringBuffer response, List<AgendaId> agendaIds) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new GuavaModule());

            JsonNode node = null;
            node = mapper.readTree(response.toString());
            agendaIds.addAll(toAgendaId(node));
        } catch (IOException e) {
            logger.error("The JSON Object could not be mapped to a AgendaId");
            e.printStackTrace();
        }
    }

    private List<AgendaId> getAgendaIds(String sessionYear) {
        List<AgendaId> agendaIds = new LinkedList<>();
        StringBuffer idResponse = new StringBuffer();
        connection = JsonOpenlegDaoUtils.setConnection(env.getRefUrl()+"/api/3/agendas/" + sessionYear,"GET", false, true);
        JsonOpenlegDaoUtils.readInputStream(connection, idResponse);
        mapJSONToAgendaId(idResponse, agendaIds);
        connection.disconnect();
        return agendaIds;
    }
}
