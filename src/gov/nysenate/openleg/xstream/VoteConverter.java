package gov.nysenate.openleg.xstream;

import java.util.ArrayList;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class VoteConverter implements Converter {
	
	@SuppressWarnings("unchecked")
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
	
		if(((ArrayList)value).iterator().hasNext()) {
			
			Object o = ((ArrayList)value).iterator().next();
			
			if(o instanceof String) {
				ArrayList<String> members = (ArrayList<String>)value;
				for(String s:members) {
					writer.startNode("member");
					writer.setValue(s);
					writer.endNode();
				}				
			}
		}
	}
	
	
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return (clazz == ArrayList.class);
	}

}
