package gov.nysenate.openleg.util.serialize;

import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Supplemental;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.json.JSONException;
import org.json.JSONWriter;

public class OriginalApiConverter {

    public static String doJson(Object o) {
        if(o instanceof Bill) {
            try {
                return billJson((Bill)o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(o instanceof Calendar || o instanceof Supplemental) {
            return JsonConverter.getJson(o).toString();
        }
        if(o instanceof Meeting) {
            return JsonConverter.getJson(o).toString();
        }
        if(o instanceof Transcript) {
            try {
                return transcriptJson((Transcript)o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String doXml(Object o) {
        if(o instanceof Bill) {
            return billXml((Bill)o);
        }
        if(o instanceof Calendar) {
            try {
                return calendarXml((Calendar)o);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        if(o instanceof Supplemental) {
            try {
                return supplementalXml((Supplemental)o);
            }
            catch(JAXBException e) {
                e.printStackTrace();
            }
        }
        if(o instanceof Meeting) {
            try {
                return meetingXml((Meeting)o);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        if(o instanceof Transcript) {
            return transcriptXml((Transcript)o);
        }

        return null;
    }

    public static String billXml(Bill bill) {
        return BillRenderer.renderBill(bill);

    }

    public static String billJson(Bill bill) throws JSONException {
        org.json.JSONStringer js = new org.json.JSONStringer();

        JSONWriter mainObj = js.array();

        JSONWriter locObj = mainObj.object();

        locObj.key("year");
        locObj.value(bill.getYear());

        locObj.key("senateId");
        locObj.value(bill.getSenateBillNo());

        locObj.key("sponsor");
        locObj.value(bill.getSponsor().getFullname());

        locObj.key("cosponsors");
        JSONWriter locObjCosponsors = mainObj.array();
        if (bill.getCoSponsors() != null) {
            Iterator<Person> it = bill.getCoSponsors().iterator();

            while (it.hasNext()) {
                JSONWriter locObjPerson = locObjCosponsors.object();

                locObjPerson.key("cosponsor");
                locObjPerson.value(it.next().getFullname());

                locObjPerson.endObject();
            }
        }
        locObjCosponsors.endArray();


        locObj.key("title");
        if (bill.getTitle()!=null)
            locObj.value(bill.getTitle());
        else
            locObj.value("");

        locObj.key("summary");
        if (bill.getSummary()!=null)
            locObj.value(bill.getSummary());
        else
            locObj.value("");

        locObj.key("actions");
        JSONWriter locObjActions = mainObj.array();
        try
        {
            for(Action be:bill.getActions()) {
                Date aDate = be.getDate();
                String action = be.getText();

                JSONWriter locObjAction = locObjActions.object();

                locObjActions.key("action");
                locObjActions.value(action);

                locObjActions.key("timestamp");
                locObjActions.value(aDate.getTime() +"");

                locObjAction.endObject();
            }

        } catch (Exception e) {}

        locObjActions.endArray();

        if (bill.getVotes()!=null && bill.getVotes().size()>0)
        {

            locObj.key("votes");
            JSONWriter locObjVotes = mainObj.array();

            Iterator<Vote> itVotes = bill.getVotes().iterator();

            Vote vote = null;

            while (itVotes.hasNext())
            {
                JSONWriter locObjVote = locObjVotes.object();

                vote = itVotes.next();

                locObjVote.key("timestamp");
                locObjVote.value(vote.getVoteDate().getTime() + "");

                if (vote.getAyes()!=null)
                {
                    locObjVote.key("ayes");
                    locObjVote.value(vote.getAyes().size()+"");
                }

                if (vote.getNays()!=null)
                {
                    locObjVote.key("nays");
                    locObjVote.value(vote.getNays().size()+"");
                }

                if (vote.getAbstains()!=null)
                {
                    locObjVote.key("abstains");
                    locObjVote.value(vote.getAbstains().size()+"");
                }

                if (vote.getExcused()!=null)
                {
                    locObjVote.key("excused");
                    locObjVote.value(vote.getExcused().size()+"");
                }

                locObj.key("voters");

                JSONWriter locObjVoters = mainObj.array();
                Iterator<String> it = null;

                if (vote.getAyes()!=null)
                {
                    it = vote.getAyes().iterator();

                    while (it.hasNext())
                    {
                        JSONWriter locObjVoter = locObjVoters.object();

                        locObjVoter.key("name");
                        locObjVoter.value(it.next());

                        locObjVoter.key("vote");
                        locObjVoter.value("aye");

                        locObjVoter.endObject();
                    }
                }

                if (vote.getNays()!=null)
                {
                    it = vote.getNays().iterator();

                    while (it.hasNext())
                    {
                        JSONWriter locObjVoter = locObjVoters.object();

                        locObjVoter.key("name");
                        locObjVoter.value(it.next());

                        locObjVoter.key("vote");
                        locObjVoter.value("nay");

                        locObjVoter.endObject();
                    }
                }

                if (vote.getAbstains()!=null) {
                    it = vote.getAbstains().iterator();
                    while (it.hasNext()) {

                        JSONWriter locObjVoter = locObjVoters.object();

                        locObjVoter.key("name");
                        locObjVoter.value(it.next());

                        locObjVoter.key("vote");
                        locObjVoter.value("abstain");

                        locObjVoter.endObject();
                    }
                }

                if (vote.getExcused()!=null) {
                    it = vote.getExcused().iterator();

                    while (it.hasNext()) {
                        JSONWriter locObjVoter = locObjVoters.object();

                        locObjVoter.key("name");
                        locObjVoter.value(it.next());

                        locObjVoter.key("vote");
                        locObjVoter.value("excused");

                        locObjVoter.endObject();
                    }
                }

                locObjVoters.endArray();
                locObjVote.endObject();
            }

            locObjVotes.endArray();
        }

        locObj.endObject();
        mainObj.endArray();

        return mainObj.toString();
    }

    public static String calendarXml(Calendar calendar) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Calendar.class);

        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        m.marshal(calendar, baos);

        return baos.toString();
    }

    public static String supplementalXml(Supplemental supplemental) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Supplemental.class);

        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        m.marshal(supplemental, baos);

        return baos.toString();
    }

    public static String meetingXml(Meeting meeting) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Meeting.class);

        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        m.marshal(meeting, baos);

        return baos.toString();
    }

    public static String transcriptJson(Transcript transcript) throws JSONException {
        org.json.JSONStringer js = new org.json.JSONStringer();

        JSONWriter mainObj = js.array();

        JSONWriter locObj = mainObj.object();

        try
        {
            locObj.key("id");
            locObj.value(transcript.getId());

            locObj.key("timestamp");
            locObj.value(transcript.getTimeStamp());

            locObj.key("location");
            locObj.value(transcript.getLocation());

            locObj.key("session");
            locObj.value(transcript.getType());

            locObj.key("text");
            locObj.value(transcript.getTranscriptText());

        }
        catch (Exception e)
        {
            //error with this bill

        }

        locObj.endObject();

        mainObj.endArray();

        return mainObj.toString();
    }

    @SuppressWarnings("deprecation")
    public static String transcriptXml(Transcript transcript) {
        String ret = "";

        ret += "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n";
        ret += "<transcript id=\"" + transcript.getId() + "\">" + "\n";
        ret += "<timestamp>" + transcript.getTimeStamp().toLocaleString() + "</timestamp>" + "\n";
        ret += "<location>" + transcript.getLocation() + "</location>" + "\n";
        ret += "<session>" + transcript.getType() + "</session>" + "\n";
        ret += "<text><![CDATA[" + TextFormatter.clean(cleanInvalidXmlChars(transcript.getTranscriptText())) + "]]></text>" + "\n";
        ret += "</transcript>";

        return ret;
    }
    public static String cleanInvalidXmlChars(String text)  {
        text = text.replace((char)0x0C,' ');
        text = text.replace((char)0x20,' ');

        return text;
    }
}
