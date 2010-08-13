package gov.nysenate.openleg.util;

import gov.nysenate.openleg.PMF;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.BillEvent;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.Section;
import gov.nysenate.openleg.model.calendar.Sequence;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Meeting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings({"unused"})
public class JsonConverter {
	
	public static void main(String[] args) throws Exception {
//		/*bill from db*/
//		Bill b = PMF.getDetachedBill("S5000");		
//		System.out.println(getJson(b));
//		
//		/*calendar from db*/
//		System.out.println("\n\n-----CALENDAR-----\n\n");
//		Calendar c = (Calendar)PMF.getDetachedObject(Calendar.class, "id", "cal-active-00060-2009", "no descending");	
//		System.out.println(getJson(c));
//		
//		/*transcript from db to xstream xml*/
//		System.out.println("\n\n\n-----TRANSCRIPT-----\n\n");
//		Transcript t = PMF.getDetachedTranscript("292");
//		System.out.println(getJson(t));
//		
//		/*meeting from db to xstream xml*/
//		System.out.println("\n\n\n-----MEETING-----\n\n");
//		Collection<Meeting> meetings = PMF.getDetachedObjects(Meeting.class, "committeeName", ".*" + "Aging" + ".*", "meetingDateTime descending", 0, 1);
//		List<String> meeting_exclude = new ArrayList<String>();
//		meeting_exclude.add("votes");
//		for(Meeting m:meetings) {
//			System.out.println(getJson(m));
//		}
	}
	
	/**
	 * accepts and sends applicable objects to be converted to json via converter(object,list)
	 * this is necessary to give each object it's "exclude" list
	 */
	public static JsonObject getJson(Object o) {
		if(o == null) {
			return null;
		}
		
		JsonObject root = new JsonObject();
		
		JsonObject node = null;

		if(o instanceof Bill)
			try {
				node = converter(o,bill_exclude());
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if(o instanceof Meeting)
			try {
				node = converter(o,meeting_exclude());
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if(o instanceof Transcript)
			try {
				node = converter(o,transcript_exclude());
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if(o instanceof Calendar)
			try {
				node = converter(o,null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		root.add(o.getClass().getSimpleName().toLowerCase(), node);
		
		return root;
	}
	
	/**
	 * accepts an object and a list of fields that should be excluded from json output.
	 * any field in the object aside from those noted as excluded will be processed,
	 * in particular this method will explicitly handle generic arguments and branches
	 * to other methods for more complex data types (dependent on type)
	 */
	private static JsonObject converter(Object o, List<String> exclude) throws Exception {
		Field[] fields = o.getClass().getDeclaredFields();
		
		JsonObject root = new JsonObject();
				
		if(exclude == null)
			exclude = new ArrayList<String>();
		try {
			for(Field f:fields) {
								
				if(!f.getName().contains("jdo") && !Modifier.isStatic(f.getModifiers())) {
					
					String name = fixCase(f.getName());
					
					String type = f.getType().getSimpleName();
					
					Method method = o.getClass().getDeclaredMethod("get" + name);
					
					if(!exclude.contains(f.getName())) {
						
						if(type.equals("Bill")) {
							root.add(f.getName(), converter(method.invoke(o),simple_bill_exclude()));
						
						}
						else if(type.equals("Date")) {
							Date d = (Date)method.invoke(o);
							root.addProperty(f.getName(), (d != null) ? d.toString():"");
						
						}
						else if(type.equals("int")) {
							root.addProperty(f.getName(), (Integer)method.invoke(o));
							
						}
						else if(type.equals("List")) {
							
							try {
								root.add(f.getName(),
										(JsonElement)JsonConverter.class.getDeclaredMethod("list" + o.getClass().getSimpleName(),Collection.class)
										.invoke(null,(List<?>)method.invoke(o)));
								
							}
							catch (Exception e) {
								
							}
							
						}
						else if(type.equals("Person")) {
							root.addProperty(f.getName(), ((Person)method.invoke(o)).getFullname());
						
						}
						else if(type.equals("Sequence")) {
							root.add(f.getName(),converter(method.invoke(o),sequence_exclude()));
							
						}
						else if(type.equals("String")) {
							root.addProperty(f.getName(), (String)method.invoke(o));
							
						}
						else {
							throw (new JsonConverter()).new UnknownTypeException("UNKNOWN: " + type + "(type):" + name + " (name) IN CLASS " + o.getClass().getSimpleName());
						}
					}
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
			
		}
		return root;
	}
	
	/**
	 * The following methods that begin with "list"+<object type> all handle particular
	 * list fields where special formatting or output is necessary.  In some cases 
	 * they loop back to converter, in other cases they are simply iterated through and
	 * returned.
	 */
	
	@SuppressWarnings("unchecked")
	private static JsonArray listBill(Collection c)  throws Exception  {
		JsonArray jarray = new JsonArray();
		
		if(((ArrayList)c).iterator().hasNext()) {
			
			Object o = ((ArrayList)c).iterator().next();
			
			if(o instanceof Bill) {
				ArrayList<Bill> bills = (ArrayList<Bill>) c;
				for(Bill bill: bills) {
					JsonPrimitive jp = new JsonPrimitive(bill.getSenateBillNo());
					
					jarray.add(jp);
				}
			}
			
			else if(o instanceof BillEvent) {
				ArrayList<BillEvent> events = (ArrayList<BillEvent>) c;
				for(BillEvent be:events) {
					JsonObject jo = new JsonObject();					
					jo.addProperty("timestamp", be.getEventDate().toString());
					jo.addProperty("event", be.getEventText());
					
					jarray.add(jo);
				}
			}
			
			else if(o instanceof Person) {
				ArrayList<Person> persons = (ArrayList<Person>) c;
				for(Person p:persons) {
					JsonPrimitive jp = new JsonPrimitive(p.getFullname());
					
					jarray.add(jp);
				}
			}
			
			else if(o instanceof Vote) {
				ArrayList<Vote> votes = (ArrayList<Vote>) c;
				JsonObject json_vote = new JsonObject();
				for(Vote v:votes) {
					json_vote.add("vote",(converter(v, vote_exclude())));
					
				}
				
				jarray.add(json_vote);
			}
		}
		
		return jarray;
	}
	
	@SuppressWarnings("unchecked")
	private static JsonArray listVote(Collection c)  throws Exception  {
		JsonArray jarray = new JsonArray();
		
		if(((ArrayList)c).iterator().hasNext()) {
			Object o = ((ArrayList)c).iterator().next();
			
			if(o instanceof String) {
				ArrayList<String> votes = (ArrayList<String>)c;
				
				for(String name:votes) {
					JsonPrimitive jp = new JsonPrimitive(name);
					
					jarray.add(jp);
				}
			}
		}
		
		return jarray;
	}
	
	@SuppressWarnings("unchecked")
	private static JsonArray listSupplemental(Collection c)  throws Exception  {
		JsonArray jarray = new JsonArray();
		
		if(((ArrayList)c).iterator().hasNext()) {
			Object o = ((ArrayList)c).iterator().next();
			
			if(o instanceof Section) {
				ArrayList<Section> sections = (ArrayList<Section>)c;
				
				for(Section s:sections) {
					jarray.add(converter(s, section_exclude()));
					
				}
			}
		}
		
		return jarray;
	}
	
	@SuppressWarnings("unchecked")
	private static JsonArray listCalendar(Collection c)  throws Exception  {
		JsonArray jarray = new JsonArray();
		
		if(((ArrayList)c).iterator().hasNext()) {
			Object o = ((ArrayList)c).iterator().next();
			
			if(o instanceof Supplemental) {
				ArrayList<Supplemental> supplementals = (ArrayList<Supplemental>)c;
				
				for(Supplemental s:supplementals) {
					jarray.add(converter(s, supplemental_exclude()));
					
				}
			}
		}
		
		return jarray;
	}
	
	@SuppressWarnings("unchecked")
	private static JsonArray listMeeting(Collection c)  throws Exception  {
		JsonArray jarray = new JsonArray();
		
		if(((ArrayList)c).iterator().hasNext()) {
			Object o = ((ArrayList)c).iterator().next();
			
			if(o instanceof Bill) {
				ArrayList<Bill> bills = (ArrayList<Bill>)c;
				
				for(Bill b:bills) {
					jarray.add(converter(b,simple_bill_exclude()));
				}
			}
		}
		
		return jarray;
	}
	
	@SuppressWarnings("unchecked")
	private static JsonArray listSection(Collection c)  throws Exception  {
		JsonArray jarray = new JsonArray();
		
		if(((ArrayList)c).iterator().hasNext()) {
			Object o = ((ArrayList)c).iterator().next();
			
			if(o instanceof CalendarEntry) {
				List<CalendarEntry> calendarEntries = (ArrayList<CalendarEntry>)c;
				
				for(CalendarEntry entry:calendarEntries) {
					jarray.add(converter(entry,calendar_entry_exclude()));
				}
			}
		}
		
		return jarray;
	}
	
	@SuppressWarnings("unchecked")
	private static JsonArray listSequence(Collection c)  throws Exception  {
		JsonArray jarray = new JsonArray();
		
		if(((ArrayList)c).iterator().hasNext()) {
			Object o = ((ArrayList)c).iterator().next();
			
			if(o instanceof CalendarEntry) {
				List<CalendarEntry> calendarEntries = (ArrayList<CalendarEntry>)c;
				
				for(CalendarEntry entry:calendarEntries) {
					jarray.add(converter(entry,calendar_entry_exclude()));
				}
			}
		}
		
		return jarray;
	}
	
	/**
	 * The following <object type>_exclude methods
	 * 
	 * 
	 */
	
	private static List<String> simple_bill_exclude() {
		List<String> simple_bill_exclude = new ArrayList<String>();		
		
		simple_bill_exclude.add("actClause");
		simple_bill_exclude.add("amendments");
		simple_bill_exclude.add("billEvents");
		simple_bill_exclude.add("fulltext");
		simple_bill_exclude.add("latestAmendment");
		simple_bill_exclude.add("law");
		simple_bill_exclude.add("memo");
		simple_bill_exclude.add("sortIndex");
		
		return simple_bill_exclude;
	}
	
	private static List<String> calendar_entry_exclude() {
		List<String> calendar_entry_exclude = new ArrayList<String>();		
		
		calendar_entry_exclude.add("billHigh");
		calendar_entry_exclude.add("subBill");
		calendar_entry_exclude.add("motionDate");
		calendar_entry_exclude.add("section");
		calendar_entry_exclude.add("sequence");
		
		return calendar_entry_exclude;
	}
	
	private static List<String> sequence_exclude() {
		List<String> sequence_exclude = new ArrayList<String>();
		
		sequence_exclude.add("supplemental");
		sequence_exclude.add("notes");
		
		return sequence_exclude;
	}
	
	private static List<String> supplemental_exclude() {
		List<String> supplemental_exclude = new ArrayList<String>();
		
		supplemental_exclude.add("calendar");
		supplemental_exclude.add("supplementalId");
		
		return supplemental_exclude;
	}
	
	private static List<String> section_exclude() {
		List<String> section_exclude = new ArrayList<String>();
		
		section_exclude.add("calendar");
		section_exclude.add("supplementalId");
		
		return section_exclude;
	}
	
	private static List<String> vote_exclude() {
		List<String> vote_exclude = new ArrayList<String>();
		
		vote_exclude.add("bill");
		vote_exclude.add("description");
		
		return vote_exclude;
	}
	
	private static List<String> transcript_exclude() {
		List<String> transcript_exclude = new ArrayList<String>();
		
		transcript_exclude.add("relatedBills");
		transcript_exclude.add("transcriptTextProcessed");
		
		return transcript_exclude;
	}
	
	private static List<String> meeting_exclude() {
		List<String> meeting_exclude = new ArrayList<String>();
		
		meeting_exclude.add("votes");
		meeting_exclude.add("committee");
		meeting_exclude.add("addendums");
		
		return meeting_exclude;
	}
	
	private static List<String> bill_exclude() {
		List<String> bill_exclude = new ArrayList<String>();
		
		bill_exclude.add("law");
		bill_exclude.add("actClause");
		bill_exclude.add("sortIndex");
		bill_exclude.add("latestAmendment");
		bill_exclude.add("votes");
		
		return bill_exclude;
	}
	
	/**
	 * returns given string with first character upper case
	 */

	private static String fixCase(String s) {
		char[] chars = s.toCharArray();
		
		chars[0] = Character.toUpperCase(chars[0]);
		
		return new String(chars);
	}
	
	public class UnknownTypeException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public UnknownTypeException(String message) {
			super(message);
		}
		public UnknownTypeException(String message, Throwable t) {
			super(message,t);
		}
	}
}
