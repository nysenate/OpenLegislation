package gov.nysenate.openleg.api.legislation.member.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.member.FullMember;

import java.util.List;

public class MemberServiceView implements ViewObject {

    private List<Integer> sessionYearsServed;

    public MemberServiceView(FullMember fullMember) {
        if (fullMember == null) {
            this.sessionYearsServed = List.of();
            return;
        }
        this.sessionYearsServed = List.copyOf(
                fullMember.getSessionMemberMap().keySet().stream()
                        .map(SessionYear::year)
                        .sorted()
                        .toList()
        );
    }

    public List<Integer> getSessionYearsServed() {
        return sessionYearsServed;
    }

    @Override
    public String getViewType() {
        return "member-service";
    }
}
