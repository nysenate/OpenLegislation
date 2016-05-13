package gov.nysenate.openleg.model.spotcheck.senatesite.agenda;

import gov.nysenate.openleg.client.view.agenda.AgendaItemView;

/**
 * Created by PKS on 4/28/16.
 */
public class SenateSiteAgendaBill {
    protected Integer field_ol_aye_count;
    protected Integer field_ol_nay_count;
    protected Integer field_ol_aye_wr_count;
    protected Integer field_ol_excused_count;
    protected Integer field_ol_abstained_count;
    protected String field_ol_bill_message;
    protected AgendaItemView field_ol_bill_name;

    /** --- Getters / Setters --- */

    public void setField_ol_aye_count(Integer aye_count){
        this.field_ol_aye_count = aye_count;
    }

    public void setField_ol_nay_count(Integer nay_count){
        this.field_ol_nay_count = nay_count;
    }

    public void setField_ol_aye_wr_count(Integer aye_wr_count){
        this.field_ol_aye_wr_count = aye_wr_count;
    }

    public void setField_ol_excused_count(Integer excused_count){
        this.field_ol_excused_count = excused_count;
    }

    public void setField_ol_abstained_count(Integer abstained_count){
        this.field_ol_abstained_count = abstained_count;
    }

    public void setField_ol_bill_message(String bill_message){
        this.field_ol_bill_message = bill_message;
    }

    public void setField_ol_bill_name(AgendaItemView bill_name){
        this.field_ol_bill_name = bill_name;
    }

    public Integer getField_ol_aye_count(){
        return field_ol_aye_count;
    }

    public Integer getField_ol_nay_count(){
        return field_ol_nay_count;
    }

    public Integer getField_ol_aye_wr_count(){
        return field_ol_aye_wr_count;
    }

    public Integer getField_ol_excused_count(){
        return field_ol_excused_count;
    }

    public Integer getField_ol_abstained_count(){
        return field_ol_abstained_count;
    }

    public String getField_ol_bill_message(){
        return field_ol_bill_message;
    }

    public AgendaItemView getField_ol_bill_name(){
        return field_ol_bill_name;
    }
}
