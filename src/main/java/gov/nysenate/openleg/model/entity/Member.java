package gov.nysenate.openleg.model.entity;

public class Member extends Person
{
    /** Current mapping to LBDC's representation of the member id.
     *  This shortName is only unique to the scope of a (2 year) session */
    private String lbdcShortName;

    /** The session year the member is active in. */
    private int sessionYear;

    /** The legislative chamber this member is associated with. */
    private Chamber chamber;

    /** Indicates if the member is currently an incumbent. */
    private boolean incumbent;

    /** The district number the member is serving in during the given session year. */
    private Integer districtCode;

    /** --- Constructors --- */

    public Member() {}

    /** --- Basic Getters/Setters --- */

    public Chamber getChamber() {
        return chamber;
    }

    public void setChamber(Chamber chamber) {
        this.chamber = chamber;
    }

    public boolean isIncumbent() {
        return incumbent;
    }

    public void setIncumbent(boolean incumbent) {
        this.incumbent = incumbent;
    }

    public String getLbdcShortName() {
        return lbdcShortName;
    }

    public void setLbdcShortName(String lbdcShortName) {
        this.lbdcShortName = lbdcShortName;
    }

    public int getSessionYear() {
        return sessionYear;
    }

    public void setSessionYear(int sessionYear) {
        this.sessionYear = sessionYear;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Integer districtCode) {
        this.districtCode = districtCode;
    }
}