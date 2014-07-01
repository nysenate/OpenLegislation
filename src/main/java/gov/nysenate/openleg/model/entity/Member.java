package gov.nysenate.openleg.model.entity;

import java.util.List;
import java.util.Map;

public class Member extends Person
{
    /** The legislative chamber this member is associated with. */
    private Chamber chamber;

    /** List of party affiliations. */
    private List<String> partyAffiliations;

    /** Indicates if the member is currently an incumbent. */
    private boolean active;

    /** Current mapping to LBDC's representation of the member id.
     *  This shortName is only unique to the scope of a (2 year) session */
    private String lbdcShortName;

    /** Historical mapping of session years -> LBDC's short name. */
    private Map<Integer, String> sessionLBDCShortNameMap;

    /** Mapping of session years to district codes. */
    private Map<Integer, Integer> sessionDistrictMap;

    /** --- Constructors --- */

    public Member() {}

    /** --- Basic Getters/Setters --- */

    public Chamber getChamber() {
        return chamber;
    }

    public void setChamber(Chamber chamber) {
        this.chamber = chamber;
    }

    public List<String> getPartyAffiliations() {
        return partyAffiliations;
    }

    public void setPartyAffiliations(List<String> partyAffiliations) {
        this.partyAffiliations = partyAffiliations;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLbdcShortName() {
        return lbdcShortName;
    }

    public void setLbdcShortName(String lbdcShortName) {
        this.lbdcShortName = lbdcShortName;
    }

    public Map<Integer, String> getSessionLBDCShortNameMap() {
        return sessionLBDCShortNameMap;
    }

    public void setSessionLBDCShortNameMap(Map<Integer, String> sessionLBDCShortNameMap) {
        this.sessionLBDCShortNameMap = sessionLBDCShortNameMap;
    }

    public Map<Integer, Integer> getSessionDistrictMap() {
        return sessionDistrictMap;
    }

    public void setSessionDistrictMap(Map<Integer, Integer> sessionDistrictMap) {
        this.sessionDistrictMap = sessionDistrictMap;
    }
}