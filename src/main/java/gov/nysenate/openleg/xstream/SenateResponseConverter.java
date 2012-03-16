package gov.nysenate.openleg.xstream;

import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.OpenLegConstants;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * 
 * @author graylin
 *
 *	Customizes the serialization of the SenateReponse object to conform.
 *	Also implements a hack around the only XStream limitation found so far.
 *	Detailed below.
 *
 */
public class SenateResponseConverter implements Converter, OpenLegConstants {

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        SenateResponse response = (SenateResponse) value;

        //Cleans up the display of the metadata, allows for storing of complex object in metadata
        writer.startNode("metadata");
        for (String key: response.getMetadata().keySet()) {
            writer.startNode(key);
            context.convertAnother(response.getMetadataByKey(key));
            writer.endNode();
        }
        writer.endNode();

        /**
         * This is part of a terrible hack of a function to get around an apparent
         * limitation of XStream XStream cannot write values without escaping them
         * which makes writing of pre-written serializations pulled from Lucene impossible.
         * 
         * If I could figure out how to create a TextNode (org.w3.dom) with the raw
         * serialization (not escaped) I could hack it together. Drew  a blank so far.
         */
        writer.startNode("results");
        writer.setValue(REGEX_API_KEY);

        /*
		 //possible idea from n8fr8 re solving this escape problem
		for (Result result: response.getResults())
		{
			writer.startNode("result");
			context.convertAnother(result);

			writer.endNode();
		}*/

        writer.endNode();

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class clazz) {
        return (clazz == SenateResponse.class);
    }

}
