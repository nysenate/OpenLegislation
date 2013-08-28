package gov.nysenate.openleg.util;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

public class Api2JsonConverter
{
    protected final String encoding = "UTF-8";
    protected final JsonFactory jsonFactory;
    protected final ObjectMapper objectMapper;
    protected final PrettyPrinter prettyPrinter;

    public Api2JsonConverter()
    {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(Feature.INDENT_OUTPUT);
        this.jsonFactory = this.objectMapper.getJsonFactory();
        this.prettyPrinter = new DefaultPrettyPrinter();
    }
}
