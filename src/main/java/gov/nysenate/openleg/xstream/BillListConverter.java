package gov.nysenate.openleg.xstream;

import gov.nysenate.openleg.model.Action;

import java.util.ArrayList;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * 
 * @author graylin
 *
 * converts ArrayLists from Bill.class to the format required
 *
 *
 */
public class BillListConverter implements Converter {

    public BillListConverter() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        if(((ArrayList<?>)value).iterator().hasNext()) {
            Object o = ((ArrayList<?>)value).iterator().next();

            //bill events
            if(o instanceof Action) {
                ArrayList<Action> events = (ArrayList<Action>) value;
                for(Action be:events) {
                    writer.startNode("action");
                    writer.addAttribute("timestamp",be.getDate().getTime()+"");
                    writer.setValue(be.getText());
                    writer.endNode();
                }
            }

            //past versions
            if(o instanceof String) {
                ArrayList<String> strings = (ArrayList<String>) value;
                for(String string:strings) {
                    writer.startNode("billNo");
                    writer.setValue(string);
                    writer.endNode();
                }
            }
        }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return (clazz == ArrayList.class);
    }

}
