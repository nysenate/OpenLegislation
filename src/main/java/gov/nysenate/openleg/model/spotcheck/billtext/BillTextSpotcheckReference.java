package gov.nysenate.openleg.model.spotcheck.billtext;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;

import javax.xml.bind.SchemaOutputResolver;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by kyle on 3/3/15.
 */
public class BillTextSpotcheckReference{
    //print number for referenced bill eg. "S100"
    private String printNo;
    //DateTime this reference was generated
    private LocalDateTime referenceDate;
    //main text of the bill
    private String text;
    //text in the memo of the bill
    private String memo;
    private Version amendment;
    //session year that the bill referenced is from
    private SessionYear sessionYear;

    public BillTextSpotcheckReference(){}

    //Not really using BaseBillId in class, could change

    /**
     *
     * @param billId
     * @param referenceDate
     * @param text
     * @param memo
     */
    public BillTextSpotcheckReference(BaseBillId billId, LocalDateTime referenceDate, String text, String memo){
        this.printNo = billId.getPrintNo();
        this.sessionYear = billId.getSession();
        this.amendment = billId.getVersion();

        this.referenceDate = referenceDate;
        this.text = text;
        this.memo = memo;

    }

    /**
     *
     * @param printNo, bill type and bill number
     * @param sessionYear, session year that the bill was presented in
     * @param referenceDate, DateTime that the reference was generated
     * @param text
     * @param memo
     * @param amendment
     */
    public BillTextSpotcheckReference(String printNo, SessionYear sessionYear, LocalDateTime referenceDate, String text, String memo, Version amendment){
        this.printNo = printNo;
        this.referenceDate = referenceDate;
        this.text = text;
        this.sessionYear = sessionYear;
        this.memo = memo;
        this.amendment = amendment;
    }
    public SpotCheckReferenceId getReferenceId() {
        return new SpotCheckReferenceId(SpotCheckRefType.LBDC_BILL, this.referenceDate.truncatedTo(ChronoUnit.DAYS));
    }

    public String getPrintNo() {
        return printNo;
    }
    public void setPrintNo(String printNo) {
        this.printNo = printNo;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getReferenceDate() {
        return referenceDate;
    }
    public void setReferenceDate(LocalDateTime referenceDate) {
        this.referenceDate = referenceDate;
    }

    public int getSessionYear() {
        return sessionYear.getYear();
    }
    public void setSessionYear(SessionYear sessionYear) {
        this.sessionYear = sessionYear;
    }

    public String getMemo(){
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }
    public Version getAmendment(){
        return amendment;
    }
    public void setAmendment(Version amendment) {
        this.amendment = amendment;
    }
}
