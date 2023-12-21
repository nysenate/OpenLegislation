package gov.nysenate.openleg.spotchecks.sensite.agenda;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.legislation.agenda.view.AgendaItemView;
import gov.nysenate.openleg.api.legislation.bill.view.BillIdView;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillVoteCode;

import java.util.Map;
import java.util.Optional;

import static gov.nysenate.openleg.legislation.bill.BillVoteCode.*;

/**
 * Created by PKS on 4/28/16.
 */
public class SenateSiteAgendaBill {
    protected Integer ayeCount;
    protected Integer nayCount;
    protected Integer aye_wrCount;
    protected Integer excusedCount;
    protected Integer abstainedCount;
    protected Integer absentCount;
    protected String billMessage;
    protected AgendaItemView billName;

    /* --- Functional Getters --- */

    public BillId getBillId() {
        return Optional.ofNullable(billName)
                .map(AgendaItemView::billId)
                .map(BillIdView::toBillId)
                .orElse(null);
    }

    /**
     * Get a map of vote code -> int indicating the number of votes for each vote type
     * @return
     */
    public Map<BillVoteCode, Integer> getVoteCounts() {
        return ImmutableMap.<BillVoteCode, Integer>builder()
                .put(AYE, getCountValue(ayeCount))
                .put(NAY, getCountValue(nayCount))
                .put(AYEWR, getCountValue(aye_wrCount))
                .put(EXC, getCountValue(excusedCount))
                .put(ABD, getCountValue(abstainedCount))
                .put(ABS, getCountValue(absentCount))
                .build();
    }

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

    public void setBillMessage(String billMessage){
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

    public Integer getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(Integer absentCount) {
        this.absentCount = absentCount;
    }

    /* --- Internal Methods --- */

    private int getCountValue(Integer count) {
        return Optional.ofNullable(count)
                .orElse(0);
    }

}
