package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.SessionMember;

record ShortNameKey(String lbdcShortName, SessionYear sessionYear, Chamber chamber) {
    ShortNameKey(SessionMember sm) {
        this(sm.getLbdcShortName(), sm.getSessionYear(), sm.getMember().getChamber());
    }
}
