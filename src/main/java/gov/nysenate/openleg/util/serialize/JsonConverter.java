package gov.nysenate.openleg.util.serialize;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.CalendarEntry;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Section;
import gov.nysenate.openleg.model.Sequence;
import gov.nysenate.openleg.model.Supplemental;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings({"unused"})
public class JsonConverter {

    static HashMap<String,JsonObject> cachedSimpleBills = new HashMap<String,JsonObject>();

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


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

        if(o instanceof Bill) {
            try {
                cachedSimpleBills.remove(((Bill)o).getSenateBillNo());
                node = converter(o,bill_exclude());
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        else if(o instanceof Action)
            try {
                node = converter(o,action_exclude());
            } catch (Exception e) {
                e.printStackTrace();
            }
        else if(o instanceof Vote)
            try {
                node = converter(o,vote_exclude());
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

                    if(!exclude.contains(f.getName())) {
                        f.setAccessible(true);
                        Object obj = f.get(o);
                        f.setAccessible(false);

                        if(obj == null) {
                            root.add(f.getName(), null);
                        }
                        else {
                            if(isPrimitive(obj)) {
                                root.addProperty(f.getName(), obj.toString());
                            }
                            else if(type.equals("Bill")) {
                                Bill bill = (Bill)obj;

                                if(!cachedSimpleBills.containsKey(bill.getSenateBillNo())) {
                                    cachedSimpleBills.put(bill.getSenateBillNo(), converter(obj,simple_bill_exclude()));
                                }

                                root.add(f.getName(), cachedSimpleBills.get(bill.getSenateBillNo()));
                            }
                            else if(type.equals("Date")) {
                                Date d = (Date)obj;
                                root.addProperty(f.getName(), (d != null) ? d.getTime() + "":"");
                            }
                            else if(type.equals("List")) {
                                try {
                                    root.add(f.getName(),
                                            (JsonElement)JsonConverter.class.getDeclaredMethod("list" + o.getClass().getSimpleName(),Collection.class)
                                            .invoke(null,(List<?>)obj));

                                }
                                catch (Exception e) {
                                    //e.printStackTrace();
                                }
                            }
                            else if(type.equals("Person")) {
                                root.add(f.getName(),converter(obj,person_exclude()));
                            }
                            else if (type.equals("Agenda")) {
                                root.add(f.getName(),converter(obj,agenda_exclude()));
                            }
                            else if (type.equals("Addendum")) {
                                root.add(f.getName(),converter(obj,addendum_exclude()));
                            }
                            else {
                                throw (new JsonConverter()).new UnknownTypeException("UNKNOWN: " + type + "(type):" + name + " (name) IN CLASS " + o.getClass().getSimpleName());
                            }
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

    private static boolean isPrimitive(Object obj) {
        return obj != null && (
                obj.getClass().isPrimitive()
                || obj instanceof Boolean
                || obj instanceof Byte
                || obj instanceof Character
                || obj instanceof Double
                || obj instanceof Float
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Short
                || obj instanceof String);
    }

    /**
     * The following methods that begin with "list"+<object type> all handle particular
     * list fields where special formatting or output is necessary.  In some cases
     * they loop back to converter, in other cases they are simply iterated through and
     * returned.
     */

    @SuppressWarnings("unchecked")
    private static JsonArray listBill(Collection<?> c)  throws Exception  {
        JsonArray jarray = new JsonArray();

        if(((List<?>)c).iterator().hasNext()) {

            Object o = ((List<?>)c).iterator().next();

            if(o instanceof String) {
                List<String> strings = (List<String>) c;
                for(String string:strings) {
                    JsonPrimitive jp = new JsonPrimitive(string);

                    jarray.add(jp);
                }
            }

            if(o instanceof Bill) {
                List<Bill> bills = (List<Bill>) c;
                for(Bill bill: bills) {
                    jarray.add(converter(bill, null));
                }
            }

            else if(o instanceof Action) {
                List<Action> events = (List<Action>) c;
                for(Action be:events) {
                    jarray.add(converter(be, internal_action_exclude()));
                }


            }

            else if(o instanceof Person) {
                List<Person> persons = (List<Person>) c;
                for(Person p:persons) {
                    jarray.add(converter(p, person_exclude()));
                }

            }

            else if(o instanceof Vote) {

                List<Vote> votes = (List<Vote>) c;
                for(Vote v:votes) {
                    jarray.add((converter(v, internal_vote_exclude())));

                }

            }
        }

        return jarray;
    }

    @SuppressWarnings("unchecked")
    private static JsonArray listVote(Collection<?> c)  throws Exception  {
        JsonArray jarray = new JsonArray();

        if(((List<?>)c).iterator().hasNext()) {
            Object o = ((List<?>)c).iterator().next();

            if(o instanceof String) {
                List<String> votes = (List<String>)c;

                for(String name:votes) {
                    JsonPrimitive jp = new JsonPrimitive(name);

                    jarray.add(jp);
                }
            }
        }

        return jarray;
    }

    @SuppressWarnings("unchecked")
    private static JsonArray listSupplemental(Collection<?> c)  throws Exception  {
        JsonArray jarray = new JsonArray();


        if(((List<?>)c).iterator().hasNext()) {
            Object o = ((List<?>)c).iterator().next();
            if(o instanceof Section) {
                List<Section> sections = (List<Section>)c;

                for(Section s:sections) {
                    jarray.add(converter(s, section_exclude()));

                }
            }
            else if(o instanceof Sequence) {
                List<Sequence> sequences = (List<Sequence>)c;

                for(Sequence s:sequences) {
                    jarray.add(converter(s, sequence_exclude()));

                }
            }
        }

        return jarray;
    }

    @SuppressWarnings("unchecked")
    private static JsonArray listCalendar(Collection<?> c)  throws Exception  {
        JsonArray jarray = new JsonArray();

        if(((List<?>)c).iterator().hasNext()) {
            Object o = ((List<?>)c).iterator().next();

            if(o instanceof Supplemental) {
                List<Supplemental> supplementals = (List<Supplemental>)c;

                for(Supplemental s:supplementals) {
                    jarray.add(converter(s, supplemental_exclude()));

                }
            }
        }

        return jarray;
    }

    @SuppressWarnings("unchecked")
    private static JsonArray listMeeting(Collection<?> c)  throws Exception  {
        JsonArray jarray = new JsonArray();

        if(((List<?>)c).iterator().hasNext()) {
            Object o = ((List<?>)c).iterator().next();

            if(o instanceof Bill) {
                List<Bill> bills = (List<Bill>)c;

                for(Bill b:bills) {
                    jarray.add(converter(b,simple_bill_exclude()));
                }
            }
            else if (o instanceof Addendum)
            {
                List<Addendum> addendums = (List<Addendum>)c;

                for(Addendum a:addendums) {
                    jarray.add(converter(a,addendum_exclude()));
                }
            }
        }

        return jarray;
    }

    @SuppressWarnings("unchecked")
    private static JsonArray listSection(Collection<?> c)  throws Exception  {
        JsonArray jarray = new JsonArray();

        if(((List<?>)c).iterator().hasNext()) {
            Object o = ((List<?>)c).iterator().next();

            if(o instanceof CalendarEntry) {
                List<CalendarEntry> calendarEntries = (List<CalendarEntry>)c;

                for(CalendarEntry entry:calendarEntries) {
                    jarray.add(converter(entry,calendar_entry_exclude()));
                }
            }
        }

        return jarray;
    }

    @SuppressWarnings("unchecked")
    private static JsonArray listSequence(Collection<?> c)  throws Exception  {
        JsonArray jarray = new JsonArray();

        if(((List<?>)c).iterator().hasNext()) {
            Object o = ((List<?>)c).iterator().next();

            if(o instanceof CalendarEntry) {
                List<CalendarEntry> calendarEntries = (List<CalendarEntry>)c;

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

        // simple_bill_exclude.add("lawSection");
        simple_bill_exclude.add("previousVersions");
        // simple_bill_exclude.add("otherSponsors");
        simple_bill_exclude.add("coSponsors");
        simple_bill_exclude.add("multiSponsors");
        simple_bill_exclude.add("currentCommittee");
        simple_bill_exclude.add("actions");
        simple_bill_exclude.add("fulltext");
        simple_bill_exclude.add("memo");
        simple_bill_exclude.add("law");
        // simple_bill_exclude.add("actClause");
        simple_bill_exclude.add("sortIndex");
        simple_bill_exclude.add("votes");
        simple_bill_exclude.add("stricken");
        simple_bill_exclude.add("pastCommittees");

        return simple_bill_exclude;
    }

    private static List<String> calendar_entry_exclude() {
        List<String> calendar_entry_exclude = new ArrayList<String>();

        calendar_entry_exclude.add("section");
        calendar_entry_exclude.add("sequence");
        calendar_entry_exclude.add("id");

        return calendar_entry_exclude;
    }

    private static List<String> sequence_exclude() {
        List<String> sequence_exclude = new ArrayList<String>();

        sequence_exclude.add("supplemental");
        sequence_exclude.add("notes");
        sequence_exclude.add("id");

        return sequence_exclude;
    }

    private static List<String> addendum_exclude() {
        List<String> exclude = new ArrayList<String>();

        exclude.add("id");

        return exclude;
    }

    private static List<String> agenda_exclude() {
        List<String> exclude = new ArrayList<String>();

        exclude.add("addendum");
        exclude.add("id");

        return exclude;
    }

    private static List<String> supplemental_exclude() {
        List<String> supplemental_exclude = new ArrayList<String>();

        supplemental_exclude.add("calendar");
        supplemental_exclude.add("supplementalId");
        supplemental_exclude.add("id");

        return supplemental_exclude;
    }

    private static List<String> section_exclude() {
        List<String> section_exclude = new ArrayList<String>();

        section_exclude.add("calendar");
        section_exclude.add("supplemental");
        section_exclude.add("id");

        return section_exclude;
    }

    private static List<String> vote_exclude() {
        List<String> vote_exclude = new ArrayList<String>();

        vote_exclude.add("id");

        return vote_exclude;
    }

    private static List<String> internal_vote_exclude() {
        List<String> vote_exclude = new ArrayList<String>();

        vote_exclude.add("bill");

        return vote_exclude;
    }

    private static List<String> action_exclude() {
        List<String> action_exclude = new ArrayList<String>();

        action_exclude.add("id");

        return action_exclude;
    }

    private static List<String> internal_action_exclude() {
        List<String> action_exclude = new ArrayList<String>();

        action_exclude.add("bill");

        return action_exclude;
    }

    private static List<String> transcript_exclude() {
        List<String> transcript_exclude = new ArrayList<String>();

        transcript_exclude.add("relatedBills");
        transcript_exclude.add("transcriptTextProcessed");
        transcript_exclude.add("id");

        return transcript_exclude;
    }

    private static List<String> person_exclude() {
        List<String> person_exclude = new ArrayList<String>();

        person_exclude.add("id");
        person_exclude.add("contactInfo");
        person_exclude.add("branch");
        person_exclude.add("guid");
        person_exclude.add("position");

        return person_exclude;
    }

    private static List<String> meeting_exclude() {
        List<String> meeting_exclude = new ArrayList<String>();

        meeting_exclude.add("id");

        return meeting_exclude;
    }

    private static List<String> bill_exclude() {
        List<String> bill_exclude = new ArrayList<String>();

        bill_exclude.add("sortIndex");
        bill_exclude.add("latestAmendment");
        bill_exclude.add("pastCommittees");
        bill_exclude.add("stricken");
        // bill_exclude.add("actClause");

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
