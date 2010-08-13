package gov.nysenate.openleg.xstream;

import java.util.ArrayList;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.BillEvent;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;

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
		if(((ArrayList)value).iterator().hasNext()) {
			Object o = ((ArrayList)value).iterator().next();
			
			//amended bills
			if(o instanceof Bill) {
				ArrayList<Bill> bills = (ArrayList<Bill>) value;
				for(Bill bill: bills) {
					writer.startNode("amendment");
					writer.addAttribute("id",bill.getSenateBillNo());
					writer.endNode();
				}
			}
			
			//bill events
			if(o instanceof BillEvent) {
				ArrayList<BillEvent> events = (ArrayList<BillEvent>) value;
				for(BillEvent be:events) {
					writer.startNode("action");
					writer.addAttribute("timestamp",be.getEventDate().toString());
					writer.setValue(be.getEventText());
					writer.endNode();
				}
			}
			
			//cosponsors
			if(o instanceof Person) {
				ArrayList<Person> persons = (ArrayList<Person>) value;
				for(Person p:persons) {
//					writer.startNode("cosponsor");
					writer.setValue(p.getFullname());
//					writer.endNode();
				}
			}
			
			//votes
			if(o instanceof Vote) {
				ArrayList<Vote> votes = (ArrayList<Vote>) value;
				for(Vote v:votes) {
					writer.setValue(v.getDescription());
				}
			}
		}
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return (clazz == ArrayList.class);
	}

}
