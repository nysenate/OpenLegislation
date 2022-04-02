package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;

record ShortNameKey(String lbdcShortName, SessionYear sessionYear, Chamber chamber) {}
