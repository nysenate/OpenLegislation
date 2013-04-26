package gov.nysenate.openleg.xstream;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.Agenda;
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
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.OpenLegConstants;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author Graylin Kim (graylin.kim@gmail.com)
 * 
 * XStreamBuilder fully wraps up all the logic for dealing with the XStream library
 * Usage of the class is simple for JSON and XML serialization respectively:
 * 		XStreamBuilder.json(ObjectToSerialize)
 * 		XStreamBuilder.xml(ObjectToSerialize)
 * 
 * The builder relies primary in the SenateObjectConverter to produce quality
 * serializations. Other tweaks and changes to serializations can be made
 * through a combination of additional Converters and annotations.
 */
public class XStreamBuilder implements OpenLegConstants {

    /**
     * Top level method for serializing any given object into JSON using XStream
     **/
    public static String json(Object obj) {
        HierarchicalStreamDriver driver = new JsonHierarchicalStreamDriver();
        return getXStream(driver,"json",obj.getClass()).toXML(obj);
    }

    private static final Pattern billPattern = Pattern.compile("<bill>##(.*)##</bill>");

    /**
     * Top level method for serializing any given object into XML using XStream
     **/
    public static String xml(Object obj){
        HierarchicalStreamDriver driver = new DomDriver();
        String xml = getXStream(driver,"xml",obj.getClass()).toXML(obj);

        Matcher m = billPattern.matcher(xml);

        StringBuffer newXml = new StringBuffer();

        while(m.find()) {
            m.appendReplacement(
                    newXml,
                    Matcher.quoteReplacement(
                            SenateObjectConverter.cachedSimpleBills.get(m.group(1)
                                    )));
        }
        m.appendTail(newXml);

        return newXml.toString();
    }

    /**
     * Configures XStream to process OpenLegislation models in 3 steps
     * 		1. Builds the XStream object and disables references
     * 		2. Processes annotations and registers global object converters
     * 		3. Run class specific configuration function for the object to be processed
     * 				These functions take the following signature:
     * 					private XStream setup<ClassName>(XStream xstream){ ... }
     * 				If a matching function is not found, no further configuration is done
     * 
     * Returns fully configured XStream object
     **/
    public static XStream getXStream(HierarchicalStreamDriver driver,String type,Class<?> objClass) {

        //Initialize XStream with the provided driver, the driver determines the output type
        //and the libraries used for serialization (not all are created equal)
        XStream xstream = new XStream(driver);

        //References are useless to end users and don't work in JSON
        //This also makes us vulnerable to CircularReferencing class specific
        //setups should be created to take care of these on a case by case basis
        xstream.setMode(XStream.NO_REFERENCES);

        //Pre-process the class annotations (safer this way than runtime)
        xstream.processAnnotations(new Class[]{
                Bill.class,			Action.class,
                Person.class,		Transcript.class,
                Vote.class,			SenateResponse.class,	Meeting.class,
                Calendar.class,		Sequence.class,			Section.class,
                Supplemental.class
        });

        //Register some global object converters

        //Try to run a class specific setup (if it exists)
        try {
            //Use the convention 'setup'+ObjectClassName, i.e. setupBill
            String funcName = "setup"+ (objClass == null ? "Null" : objClass.getSimpleName());

            //to lookup a setup method for this object
            xstream = (XStream)XStreamBuilder.class.getDeclaredMethod(funcName, XStream.class).invoke(null, xstream);

        }
        catch (NoSuchMethodException e) {
            //if the method doesn't exist
        }
        catch (InvocationTargetException e) {
            //This can't happen because they are static methods
        }
        catch (IllegalAccessException e) {
            //This shouldn't happen because we are inside of the class
        }

        //Serialize the object with our formatted XStream

        xstream.registerConverter(new SenateResponseConverter());
        xstream.registerConverter(new SenateObjectConverter(xstream.getMapper(),type.equals("json")));

        return xstream;
    }

    @SuppressWarnings("unused")
    private static XStream setupNull(XStream xstream) {
        return condensedBillFormat(xstream);
    }

    @SuppressWarnings("unused")
    private static XStream setupBill(XStream xstream) {
        //omitting what is currently unavailable in api 1.0
        xstream.omitField(Bill.class, "sortIndex");
        xstream.omitField(Bill.class, "latestAmendment");
        xstream.omitField(Bill.class, "pastCommittees");
        xstream.omitField(Bill.class, "stricken");
        xstream.omitField(Bill.class, "actClause");

        xstream.omitField(Person.class,"branch");
        xstream.omitField(Person.class,"contactInfo");
        xstream.omitField(Person.class,"guid");

        //Removes the backward (circular) reference
        xstream.omitField(Vote.class, "bill");
        xstream.omitField(Vote.class, "id");

        //properly lists lists
        xstream.registerConverter(new BillPersonConverter());
        xstream.registerConverter(new BillListConverter());

        return xstream;
    }

    @SuppressWarnings("unused")
    private static XStream setupAction(XStream xstream) {
        xstream = condensedBillFormat(xstream);

        xstream.omitField(Action.class, "id");

        return xstream;
    }

    @SuppressWarnings("unused")
    private static XStream setupVote(XStream xstream) {
        xstream = condensedBillFormat(xstream);

        xstream.omitField(Vote.class, "id");

        return xstream;
    }

    @SuppressWarnings("unused")
    private static XStream setupMeeting(XStream xstream) {
        xstream = condensedBillFormat(xstream);

        xstream.omitField(Meeting.class, "id");
        xstream.omitField(Meeting.class, "addendums");

        xstream.omitField(Addendum.class, "id");
        xstream.omitField(Agenda.class, "id");

        return xstream;
    }

    @SuppressWarnings("unused")
    private static XStream setupTranscript(XStream xstream) {

        xstream.omitField(Transcript.class, "relatedBills");
        xstream.omitField(Transcript.class, "transcriptTextProcessed");
        xstream.omitField(Transcript.class, "id");

        return xstream;
    }

    @SuppressWarnings("unused")
    private static XStream setupSupplemental(XStream xstream) {
        return setupCalendar(xstream);
    }

    private static XStream setupCalendar(XStream xstream) {

        xstream = condensedBillFormat(xstream);

        xstream.omitField(Calendar.class, "id");

        xstream.omitField(CalendarEntry.class, "section");
        xstream.omitField(CalendarEntry.class, "sequence");
        xstream.omitField(CalendarEntry.class, "id");

        xstream.omitField(Sequence.class, "supplemental");
        xstream.omitField(Sequence.class,"notes");
        xstream.omitField(Sequence.class,"id");

        xstream.omitField(Section.class, "supplemental");
        xstream.omitField(Section.class, "id");

        xstream.omitField(Supplemental.class, "calendar");
        xstream.omitField(Supplemental.class, "supplementalId");
        xstream.omitField(Supplemental.class, "id");

        return xstream;
    }

    private static XStream condensedBillFormat(XStream xstream) {

        xstream.omitField(Bill.class, "lawSection");
        xstream.omitField(Bill.class, "previousVersions");
        xstream.omitField(Bill.class, "coSponsors");
        xstream.omitField(Bill.class, "multiSponsors");
        xstream.omitField(Bill.class, "currentCommittee");
        xstream.omitField(Bill.class, "actions");
        xstream.omitField(Bill.class, "fulltext");
        xstream.omitField(Bill.class, "memo");
        xstream.omitField(Bill.class, "law");
        xstream.omitField(Bill.class, "actClause");
        xstream.omitField(Bill.class, "sortIndex");
        xstream.omitField(Bill.class, "votes");
        xstream.omitField(Bill.class, "stricken");
        xstream.omitField(Bill.class, "pastCommittees");

        xstream.registerConverter(new BillPersonConverter());

        return xstream;
    }

    /**
     * This function is a terrible hack of a function to get around an apparent
     * limitation of XStream XStream cannot write values without escaping them
     * which makes writing of pre-written serializations pulled from Lucene impossible.
     * 
     * If I could figure out how to create a TextNode  (org.w3.dom) with the raw
     * serialization (not escaped) I could hack it together. Drew  a blank so far.
     */
    public static String writeResponse(String type, SenateResponse response) {

        //Append all the results into a single listing
        StringBuilder results = new StringBuilder("");

        Iterator<Result> itResults = response.getResults().iterator();
        Result result = null;

        while (itResults.hasNext()) //Fields in JSON are separated by commas,
        {
            result = itResults.next();

            String oid = result.oid;
            String otype = result.otype;
            StringBuffer url = new StringBuffer();
            url.append("http://open.nysenate.gov/legislation/");

            if(result.otype.equals("vote") || result.otype.equals("action")) {
                oid = result.getFields().get("billno");
                url.append("bill/").append(oid);
            }
            else {
                url.append(result.getOtype()).append("/").append(result.getOid());
            }

            //#TODO: Figure out if we need to remove semicolons from end of results
            if(type.equals("json")) {


                results.append("{");
                results.append("\"otype\": \""+otype+"\", ");
                results.append("\"oid\": \""+oid+"\", ");
                results.append("\"url\": \"" + url.toString() + "\", ");
                results.append("\"data\": " + result.getData());
                results.append(" }");

                if (itResults.hasNext())
                    results.append(",\n");
            }
            else if (type.equals("xml")) {
                results.append("<result>");
                results.append("<otype>"+otype+"</otype>");
                results.append("<oid>"+oid+"</oid>");
                results.append("<url>" + url.toString() + "</url>");
                results.append(result.getData());
                results.append("</result>");
            }
        }

        //Render the response to the appropriate serialization using XStream
        StringBuilder responseStr = new StringBuilder(
                (type.equals("json")) ? XStreamBuilder.json(response) : XStreamBuilder.xml(response)
                );

        int start = responseStr.indexOf(REGEX_API_KEY);
        int end = start + REGEX_API_KEY.length();

        if (type.equals("json")) //need to include the surrounding quotes for json as well
        {
            start--;
            end++;

            //#TODO: Figure out how to fix indentation (shift right)
            return responseStr.replace(start, end,"\n[\n" + results.toString() + "\n]\n").toString();

        }
        else
        {
            return responseStr.replace(start, end, results.toString() ).toString();
        }
    }
}
