package gov.nysenate.openleg.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.api.ViewObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class MapViewSerializer extends JsonSerializer<MapView> {
    private static final Logger logger = LoggerFactory.getLogger(MapViewSerializer.class);

    @Override
    public void serialize(MapView value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(ListView.of(new ArrayList<ViewObject>(value.getItems().values())));
    }
}
