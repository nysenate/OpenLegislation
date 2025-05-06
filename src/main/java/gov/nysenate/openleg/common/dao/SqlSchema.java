package gov.nysenate.openleg.common.dao;

public enum SqlSchema {
    BILL, TRANSCRIPT, AGENDA, CALENDAR, LAW, USER, SPOTCHECK, COMMITTEE, LEG_DATA, MISC;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
