package gov.nysenate.openleg.model.spotcheck.senatesite.agenda;

import gov.nysenate.openleg.client.view.agenda.AgendaItemView;

/**
 * Created by PKS on 4/28/16.
 */
public class SenateSiteAgendaBill {
    protected Integer ayeCount;
    protected Integer nayCount;
    protected Integer aye_wrCount;
    protected Integer excusedCount;
    protected Integer abstainedCount;
    protected String billMessage;
    protected AgendaItemView billName;

    /** --- Getters / Setters --- */

    public void setAyeCount(Integer ayeCount){
        this.ayeCount = ayeCount;
    }

    public void setNayCount(Integer nayCount){
        this.nayCount = nayCount;
    }

    public void setAyeWrCount(Integer aye_wrCount){
        this.aye_wrCount = aye_wrCount;
    }

    public void setExcusedCount(Integer excusedCount){
        this.excusedCount = excusedCount;
    }

    public void setAbstainedCount(Integer abstainedCount){
        this.abstainedCount = abstainedCount;
    }

    public void setBillMessage(String billNessage){
        this.billMessage = billMessage;
    }

    public void setBillName(AgendaItemView billName){
        this.billName = billName;
    }

    public Integer getAyeCount(){
        return ayeCount;
    }

    public Integer getAayCount(){
        return nayCount;
    }

    public Integer getAyeWrCount(){
        return aye_wrCount;
    }

    public Integer getExcusedCount(){
        return excusedCount;
    }

    public Integer getAbstainedCount(){
        return abstainedCount;
    }

    public String getBillMessage(){
        return billMessage;
    }

    public AgendaItemView getBillName(){
        return billName;
    }
}
