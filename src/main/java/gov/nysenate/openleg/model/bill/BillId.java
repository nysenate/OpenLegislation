package gov.nysenate.openleg.model.bill;

public class BillId
{
    private String basePrintNo;

    private int session;

    private String version;

    /* --- Constructors --- */

    public BillId(String printNo, int session) {
        if (printNo != null && printNo.trim().matches(".*[ A-Z]$")) {
            printNo = printNo.trim();
            this.basePrintNo = printNo.substring(0, printNo.length() - 1);
            this.version = printNo.substring(printNo.length() - 1);
        }
        else {
            this.basePrintNo = printNo;
        }
        this.session = session;
    }

    public BillId(String basePrintNo, int session, String version) {
        this.basePrintNo = (basePrintNo != null) ? basePrintNo.trim() : null;
        this.session = session;
        this.version = (version != null) ? version.trim() : null;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return basePrintNo + ((version != null) ? version : "") + "-" + session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillId billId = (BillId) o;
        if (session != billId.session) return false;
        if (!basePrintNo.equals(billId.basePrintNo)) return false;
        if (version != null ? !version.equals(billId.version) : billId.version != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = basePrintNo.hashCode();
        result = 31 * result + session;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    /** --- Basic Getters/Setters --- */

    public String getBasePrintNo() {
        return basePrintNo;
    }

    public void setBasePrintNo(String basePrintNo) {
        this.basePrintNo = basePrintNo;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
