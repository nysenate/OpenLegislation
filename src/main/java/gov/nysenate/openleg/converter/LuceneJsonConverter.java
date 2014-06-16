package gov.nysenate.openleg.converter;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.BaseObject;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@SuppressWarnings("unchecked")
public class LuceneJsonConverter
{
    public static final Logger logger = Logger.getLogger(LuceneJsonConverter.class);
    protected static HashMap<String,JsonObject> cachedSimpleBills = new HashMap<String,JsonObject>();
    protected static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * accepts and sends applicable objects to be converted to json via converter(object,list)
     * this is necessary to give each object it's "exclude" list
     */
    public static String toString(BaseObject o) {
        if(o != null) {
            JsonObject root = new JsonObject();
            try {
                JsonObject node = null;
                if(o instanceof Bill) {
                    cachedSimpleBills.remove(((Bill)o).getBillId());
                    node = converter(o, null);
                }
                else if(o instanceof Meeting) {
                    node = converter(o, null);
                }
                else if(o instanceof Transcript) {
                    node = converter(o, transcript_exclude());
                }
                else if(o instanceof Calendar) {
                    node = converter(o, null);
                }
                else if(o instanceof Action) {
                    node = converter(o, null);
                }
                else if(o instanceof Vote) {
                    node = converter(o, null);
                }
                else {
                    throw new RuntimeException("Cannot convert BaseObject of type: "+o.getOtype());
                }
                root.add(o.getOtype(), node);
                return root.toString();
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
        else {
            return null;
        }
    }


    /**
     * accepts an object and a list of fields that should be excluded from json output.
     * any field in the object aside from those noted as excluded will be processed,
     * in particular this method will explicitly handle generic arguments and branches
     * to other methods for more complex data types (dependent on type)
     */
    private static JsonObject converter(Object o, List<String> exclude) throws Exception
    {
        List<Field> fields = new ArrayList<Field>();
        Class<?> cls = o.getClass();
        while (cls != null) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }

        JsonObject root = new JsonObject();
        if(exclude == null) {
            exclude = new ArrayList<String>();
        }

        try {
            for(Field f:fields) {
                if(!f.getName().contains("jdo") && !Modifier.isStatic(f.getModifiers())) {
                    String name = StringUtils.capitalize(f.getName());
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

                                if(!cachedSimpleBills.containsKey(bill.getBillId())) {
                                    cachedSimpleBills.put(bill.getBillId(), converter(obj,internal_bill_exclude()));
                                }

                                root.add(f.getName(), cachedSimpleBills.get(bill.getBillId()));
                            }
                            else if(type.equals("Date")) {
                                Date d = (Date)obj;
                                root.addProperty(f.getName(), (d != null) ? d.getTime() + "":"");
                            }
                            else if(type.equals("List") || type.equals("HashSet")) {
                                Collection<?> collection = ((Collection<?>)obj);
                                Iterator<?> iter = collection.iterator();
                                JsonArray jarray = new JsonArray();
                                if (iter.hasNext()) {
                                    if (iter.next() instanceof String) {
                                        for(String str :(Collection<String>)collection) {
                                            JsonPrimitive jp = new JsonPrimitive(str);
                                            jarray.add(jp);
                                        }
                                    }
                                    else {
                                        jarray = (JsonArray)LuceneJsonConverter.class.getDeclaredMethod("list" + o.getClass().getSimpleName(),Collection.class).invoke(null,collection);
                                    }
                                }
                                root.add(f.getName(), jarray);
                            }
                            else if(type.equals("Person")) {
                                root.add(f.getName(),converter(obj, null));
                            }
                            else if (type.equals("Agenda")) {
                                root.add(f.getName(),converter(obj,agenda_exclude()));
                            }
                            else {
                                throw new RuntimeException("UNKNOWN: " + type + "(type):" + name + " (name) IN CLASS " + o.getClass().getSimpleName());
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return root;
    }

    private static boolean isPrimitive(Object obj)
    {
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
    @SuppressWarnings("unused")
    private static JsonArray listBill(Collection<?> c) throws Exception
    {
        JsonArray jarray = new JsonArray();

        if(c.iterator().hasNext()) {
            Object o = c.iterator().next();
            if(o instanceof Bill) {
                for(Bill bill: (Collection<Bill>) c) {
                    jarray.add(converter(bill, null));
                }
            }
            else if(o instanceof Action) {
                for(Action be:(Collection<Action>) c) {
                    jarray.add(converter(be, internal_action_exclude()));
                }
            }
            else if(o instanceof Person) {
                for(Person p:(Collection<Person>) c) {
                    jarray.add(converter(p, null));
                }
            }
            else if(o instanceof Vote) {
                for(Vote v:(Collection<Vote>) c) {
                    jarray.add((converter(v, internal_vote_exclude())));
                }
            }
        }
        return jarray;
    }

    @SuppressWarnings("unused")
    private static JsonArray listSupplemental(Collection<?> c) throws Exception
    {
        JsonArray jarray = new JsonArray();
        if(c.iterator().hasNext()) {
            Object o = c.iterator().next();
            if(o instanceof Section) {
                for(Section s:(Collection<Section>)c) {
                    jarray.add(converter(s, section_exclude()));
                }
            }
            else if(o instanceof Sequence) {
                for(Sequence s:(Collection<Sequence>)c) {
                    jarray.add(converter(s, sequence_exclude()));

                }
            }
        }
        return jarray;
    }

    @SuppressWarnings("unused")
    private static JsonArray listCalendar(Collection<?> c) throws Exception
    {
        JsonArray jarray = new JsonArray();
        if(c.iterator().hasNext()) {
            Object o = c.iterator().next();
            if(o instanceof Supplemental) {
                for(Supplemental s:(Collection<Supplemental>)c) {
                    jarray.add(converter(s, supplemental_exclude()));
                }
            }
        }
        return jarray;
    }

    @SuppressWarnings("unused")
    private static JsonArray listMeeting(Collection<?> c) throws Exception
    {
        JsonArray jarray = new JsonArray();
        if(c.iterator().hasNext()) {
            Object o = c.iterator().next();
            if(o instanceof Bill) {
                for(Bill b:(Collection<Bill>)c) {
                    jarray.add(converter(b,internal_bill_exclude()));
                }
            }
            else if (o instanceof Addendum) {
                for(Addendum a:(Collection<Addendum>)c) {
                    jarray.add(converter(a,addendum_exclude()));
                }
            }
        }
        return jarray;
    }

    @SuppressWarnings("unused")
    private static JsonArray listSection(Collection<?> c) throws Exception
    {
        JsonArray jarray = new JsonArray();
        if(c.iterator().hasNext()) {
            Object o = c.iterator().next();
            if(o instanceof CalendarEntry) {
                for(CalendarEntry entry:(Collection<CalendarEntry>)c) {
                    jarray.add(converter(entry,calendar_entry_exclude()));
                }
            }
        }
        return jarray;
    }

    @SuppressWarnings("unused")
    private static JsonArray listSequence(Collection<?> c) throws Exception
    {
        JsonArray jarray = new JsonArray();
        if(c.iterator().hasNext()) {
            Object o = c.iterator().next();
            if(o instanceof CalendarEntry) {
                for(CalendarEntry entry:(Collection<CalendarEntry>)c) {
                    jarray.add(converter(entry,calendar_entry_exclude()));
                }
            }
        }
        return jarray;
    }

    private static List<String> addendum_exclude()
    {
        return Arrays.asList("meetings");
    }

    private static List<String> agenda_exclude()
    {
        return Arrays.asList("addendums");
    }

    private static List<String> supplemental_exclude()
    {
        return Arrays.asList("calendar");
    }

    private static List<String> section_exclude()
    {
        return Arrays.asList("supplemental");
    }

    private static List<String> sequence_exclude()
    {
        return Arrays.asList("supplemental");
    }

    private static List<String> calendar_entry_exclude()
    {
        return Arrays.asList("section", "sequence");
    }

    private static List<String> internal_vote_exclude()
    {
        return Arrays.asList("bill");
    }

    private static List<String> internal_action_exclude()
    {
        return Arrays.asList("bill");
    }

    private static List<String> internal_bill_exclude()
    {
        return Arrays.asList("actions", "fulltext", "memo", "sortIndex", "votes");
    }

    private static List<String> transcript_exclude()
    {
        return Arrays.asList("relatedBills", "transcriptTextProcessed");
    }
}
