package gov.nysenate.openleg.xstream;

import gov.nysenate.openleg.model.committee.Meeting;

import java.util.ArrayList;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CommitteeMeetingConverter implements Converter {

	@Override
	@SuppressWarnings("unchecked")
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		
		if(((ArrayList)value).iterator().hasNext()) {
			Object o = ((ArrayList)value).iterator().next();
			
			if(o instanceof Meeting) {				
				ArrayList<Meeting> meetings = (ArrayList<Meeting>) value;
				for(Meeting m: meetings) {
					writer.startNode("meeting");
					writer.addAttribute("meetingDateTime", m.getMeetingDateTime().toString());
					writer.addAttribute("meetDay", m.getMeetday());
					writer.addAttribute("location",m.getLocation());
					writer.addAttribute("id",m.getId());
					writer.addAttribute("committeeName",m.getCommitteeName());
					writer.addAttribute("committeeChair",m.getCommitteeChair());
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
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {		
		return (clazz == ArrayList.class);
	}

}