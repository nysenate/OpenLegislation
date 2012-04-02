package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.DocumentBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("calendar")
@XmlRootElement
public class Calendar extends SenateObject {

    @XStreamAsAttribute
    protected int year;

    @XStreamAsAttribute
    protected String type;

    @XStreamAsAttribute
    protected int sessionYear;

    @XStreamAsAttribute
    protected int no;

    //	@HideFrom({Calendar.class, Suppplemental.class})
    protected List<Supplemental> supplementals;

    protected String id;

    public final static String TYPE_FLOOR = "floor";

    public final static String TYPE_ACTIVE = "active";

    public Calendar() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getSessionYear() {
        return sessionYear;
    }

    public void setSessionYear(int sessionYear) {
        this.sessionYear = sessionYear;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Supplemental> getSupplementals() {
        return supplementals;
    }

    public void setSupplementals(List<Supplemental> supplementals) {
        this.supplementals = supplementals;
    }

    @JsonIgnore
    public void addSupplemental(Supplemental supplemental) {
        if(supplementals ==  null) {
            supplementals = new ArrayList<Supplemental>();
        }

        int index = -1;
        if((index = supplementals.indexOf(supplemental)) != -1) {
            supplementals.remove(index);
        }
        supplementals.add(supplemental);
    }

    @JsonIgnore
    public void removeSupplemental(Supplemental supplemental) {
        if(supplementals ==  null) {
            supplementals = new ArrayList<Supplemental>();
        }
        else {
            supplementals.remove(supplemental);
        }
    }


    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof Calendar)
        {
            if ( ((Calendar)obj).getId().equals(this.getId()))
                return true;
        }

        return false;
    }


    @Override
    public String luceneOid() {
        String oid = "";

        if(this.getSupplementals() != null && this.getSupplementals().size() > 0) {
            Supplemental supplemental = this.getSupplementals().get(0);

            if(getId().startsWith("cal-floor")) {
                Date date = supplemental.getCalendarDate();

                if(date == null) return null;

                oid = "floor-" + new SimpleDateFormat("MM-dd-yyyy").format(date);

            }
            else {
                if(supplemental.getSequences() != null && supplemental.getSequences().size() > 0) {
                    Date date = supplemental.getSequences().get(0).getActCalDate();

                    if(date == null) return null;

                    oid = "active-" + new SimpleDateFormat("MM-dd-yyyy").format(date);
                }
            }

            return oid;
        }

        return null;
    }

    @Override
    public String fileSystemId() {
        return id;
    }

    @Override
    public String luceneOsearch() {
        return "";
    }

    @Override
    public String luceneOtype() {
        return "calendar";
    }

    @Override
    public HashMap<String,Fieldable> luceneFields() {
        HashMap<String,Fieldable> fields = new HashMap<String,Fieldable>();

        Calendar calendar = this;

        if(this.getSupplementals() == null || this.getSupplementals().isEmpty()) return fields;

        Supplemental supplemental = this.getSupplementals().get(0);

        fields.put("ctype",new Field("ctype",this.getType(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        StringBuilder searchContent = new StringBuilder();
        String title;

        title = this.getNo() + " - " + this.getType();

        if (supplemental.getCalendarDate()!=null) {
            title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(supplemental.getCalendarDate());
        }
        else if (supplemental.getReleaseDateTime()!=null)
        {
            title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(supplemental.getReleaseDateTime());
        }

        if(calendar.getId().startsWith("cal-floor")) {
            fields.put("when", new Field("when",supplemental.getCalendarDate().getTime()+"",DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        }
        else if (supplemental.getSequences()!=null && supplemental.getSequences().size() > 0) {
            Sequence sequence = supplemental.getSequences().get(0);

            title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(supplemental.getSequences().get(0).getActCalDate());

            fields.put("when", new Field("when",sequence.getActCalDate().getTime()+"",DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        }

        StringBuilder bills = new StringBuilder("");
        StringBuilder calendarEntries = new StringBuilder();

        searchContent.append(title);

        StringBuilder sbSummary = new StringBuilder();

        for(Supplemental s:this.getSupplementals()) {
            List<Section> sections = s.getSections();
            List<Sequence> sequences = s.getSequences();

            if (sections != null) {
                for(Section section:sections) {
                    sbSummary.append(section.getName()).append(": ");
                    sbSummary.append(section.getCalendarEntries().size()).append(" bill(s); ");

                    for(CalendarEntry ce:section.getCalendarEntries()) {
                        bills.append(ce.getBill().getSenateBillNo()).append(", ");
                        calendarEntries.append(ce.getNo()).append("-")
                        .append(ce.getBill().getSenateBillNo())
                        .append(", ");
                    }
                }
            }

            if (sequences != null) {
                if(sequences.size() > 0) {
                    int total = 0;
                    for(Sequence seq:sequences) {
                        total += seq.getCalendarEntries().size();

                        for(CalendarEntry ce:seq.getCalendarEntries()) {
                            bills.append(ce.getBill().getSenateBillNo()).append(", ");
                            calendarEntries.append(ce.getNo()).append("-")
                            .append(ce.getBill().getSenateBillNo())
                            .append(", ");
                        }
                    }

                    sbSummary.append(total).append(" bill(s)");
                }
            }
        }

        fields.put("bills",new Field("bills",bills.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        fields.put("calendarentries",new Field("calendarentries",calendarEntries.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        String summary = sbSummary.toString().trim();

        fields.put("summary",new Field("summary",summary, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        fields.put("title",new Field("title",title, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        fields.put("osearch",new Field("osearch",searchContent.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        //fields.put("oid",new Field("oid",oid, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        return fields;
    }

    @Override
    public String luceneTitle() {
        return "";
    }

    @Override
    public String luceneSummary() {
        return "";
    }

    @Override
    public void merge(ISenateObject obj) {
        if(!(obj instanceof Calendar))
            return;

        super.merge(obj);

        this.setId(((Calendar)obj).getId());
        this.setNo(((Calendar)obj).getNo());
        this.setSessionYear(((Calendar)obj).getSessionYear());

        if(this.supplementals == null || this.supplementals.isEmpty()) {
            this.supplementals = ((Calendar)obj).getSupplementals();
        }
        else {
            if(((Calendar)obj).getSupplementals() != null) {
                this.supplementals =  ((Calendar)obj).getSupplementals();
            }

            for(int i = this.getSupplementals().size() - 1; i >= 0; i--) {
                Supplemental supp = this.getSupplementals().get(i);
                if((supp.getSections() == null || supp.getSections().isEmpty())
                        && (supp.getSequences() == null || supp.getSequences().isEmpty())) {

                    this.getSupplementals().remove(i);
                }
            }
        }

        this.setType(((Calendar)obj).getType());
        this.setYear(((Calendar)obj).getYear());
    }
}
