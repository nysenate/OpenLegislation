package gov.nysenate.openleg.model;


import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


@XStreamAlias("calendarEntry")
public class CalendarEntry {

    @XStreamAsAttribute
    private String id;

    @XStreamAsAttribute
    private String no;

    private Bill bill;

    //	@HideFrom({Calendar.class, Supplemental.class})
    private String billHigh;

    //	@HideFrom({Calendar.class, Supplemental.class})
    private Bill subBill;

    //	@HideFrom({Calendar.class, Supplemental.class})
    private Date motionDate;

    //	@HideFrom({Calendar.class, Supplemental.class})
    private Section section;

    //@HideFrom({Calendar.class, Supplemental.class})
    private Sequence sequence;

    public String getBillHigh() {
        return billHigh;
    }

    public void setBillHigh(String billHigh) {
        this.billHigh = billHigh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    @JsonIgnore
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Date getMotionDate() {
        return motionDate;
    }

    public void setMotionDate(Date motionDate) {
        this.motionDate = motionDate;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Bill getSubBill() {
        return subBill;
    }

    public void setSubBill(Bill subBill) {
        this.subBill = subBill;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof CalendarEntry)
        {
            if ( ((CalendarEntry)obj).getId().equals(this.getId()))
                return true;
        }

        return false;
    }


}

