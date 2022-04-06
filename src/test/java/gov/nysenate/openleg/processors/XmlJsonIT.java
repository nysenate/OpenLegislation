package gov.nysenate.openleg.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import gov.nysenate.openleg.api.legislation.bill.view.BillView;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.config.annotation.UnitTest;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Category(UnitTest.class)
public class XmlJsonIT {

    private final File testFileDir = FileIOUtils.getResourceFile("sourcefile/");

    private List<BillView> billViews = new LinkedList<>();
    private File billJsonFile = new File(testFileDir, "S609-2017_JSON");
    private String billJson = readInFileToString(billJsonFile);
    private JsonNode node = null;


    @Test
    @PostConstruct
    public void JsonMappingTest() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new GuavaModule());
            node = mapper.readTree(billJson);
            toBillView(node);
            verifyJSONParse();
        } catch (IOException e) {
            fail("The JSON Object could not be mapped to a bill view");
            e.printStackTrace();
        }
    }

    private void verifyJSONParse() {
       assertEquals("Billview and actual active versions are the same", "A", billViews.get(0).getActiveVersion());
    }

    private String readInFileToString(File file) {
        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not convert the file to a string");
        }
        return "";
    }

    private void toBillView(JsonNode node) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());

        if (node.get("result").get("items") == null) { // if there is only 1 available bill
            billViews.add(mapper.readValue(node.get("result").toString(), BillView.class));
        } else { // if there are many available bills.
            Iterator<JsonNode> nodeIterator = node.get("result").get("items").iterator();
            while (nodeIterator.hasNext()) {
                JsonNode node1 = nodeIterator.next();
                billViews.add(mapper.readValue(node1.toString(), BillView.class));
            }
        }
    }
}
