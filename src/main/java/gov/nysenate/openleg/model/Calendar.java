package gov.nysenate.openleg.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

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

    protected List<Supplemental> supplementals;

    protected String id;

    public Calendar() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getOid()
    {
        return this.getType()+"-"+new SimpleDateFormat("MM-dd-yyyy").format(this.getDate());
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

    @JsonIgnore
    public String getTitle()
    {
        return this.getNo()+" - "+this.getType()+" - "+DateFormat.getDateInstance(DateFormat.MEDIUM).format(this.getDate());
    }
}
