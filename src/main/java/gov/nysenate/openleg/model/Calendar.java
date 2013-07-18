package gov.nysenate.openleg.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("calendar")
@XmlRootElement
public class Calendar extends BaseObject
{
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

    @JsonIgnore
    public Date getDate() {
        if (this.getType().equals("active")) {
            if (this.getSupplementals() != null && this.getSupplementals().size() != 0 && this.getSupplementals().get(0).getSequences() != null && this.getSupplementals().get(0).getSequences().size() != 0 && this.getSupplementals().get(0).getSequences().get(0).getActCalDate() != null) {
                return this.getSupplementals().get(0).getSequences().get(0).getActCalDate();
            }
            else {
                return null;
            }
        }
        else {
            if (this.getSupplementals() != null && this.getSupplementals().size() != 0 && this.getSupplementals().get(0).getCalendarDate() != null) {
                return this.getSupplementals().get(0).getCalendarDate();
            }
            else {
                return null;
            }
        }
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
    public String luceneOsearch() {
        return "";
    }

    @Override
    public String luceneOtype() {
        return "calendar";
    }

    @Override
    public Collection<Fieldable> luceneFields() {
        Collection<Fieldable> fields = new ArrayList<Fieldable>();

        Calendar calendar = this;

        if(this.getSupplementals() == null || this.getSupplementals().isEmpty()) return fields;

        Supplemental supplemental = this.getSupplementals().get(0);

        fields.add(new Field("ctype",this.getType(), Field.Store.YES, Field.Index.ANALYZED));

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
            fields.add(new Field("when",supplemental.getCalendarDate().getTime()+"", Field.Store.YES, Field.Index.ANALYZED));

        }
        else if (supplemental.getSequences()!=null && supplemental.getSequences().size() > 0) {
            Sequence sequence = supplemental.getSequences().get(0);

            title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(supplemental.getSequences().get(0).getActCalDate());

            fields.add(new Field("when",sequence.getActCalDate().getTime()+"", Field.Store.YES, Field.Index.ANALYZED));
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

        fields.add(new Field("bills",bills.toString(), Field.Store.YES, Field.Index.ANALYZED));
        fields.add(new Field("calendarentries",calendarEntries.toString(), Field.Store.YES, Field.Index.ANALYZED));

        String summary = sbSummary.toString().trim();

        fields.add(new Field("summary", summary, Field.Store.YES, Field.Index.ANALYZED));
        fields.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
        fields.add(new Field("osearch",searchContent.toString(), Field.Store.YES, Field.Index.ANALYZED));

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
}
