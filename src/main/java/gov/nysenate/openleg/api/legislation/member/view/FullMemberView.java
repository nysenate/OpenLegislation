package gov.nysenate.openleg.api.legislation.member.view;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.SessionMember;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FullMemberView extends MemberView {

    protected PersonView personView;
    protected Map<Integer, List<SessionMemberView>> sessionShortNameMap;

    public FullMemberView(FullMember member) {
        super(member.getLatestSessionMember().orElse(null));
        this.personView = new PersonView(member.getPerson());
        this.sessionShortNameMap = member.getSessionMemberMap().keySet().stream()
                .collect(Collectors.toMap(SessionYear::year,
                        session -> member.getSessionMemberMap().get(session).stream()
                                .map(SessionMemberView::new)
                                .sorted((sm1, sm2) -> Boolean.compare(sm1.alternate, sm2.alternate))
                                .toList()));
    }

    public FullMemberView(Collection<SessionMember> sessionMembers) {
        this(new FullMember(sessionMembers));
    }

    public PersonView getPerson() {
        return personView;
    }

    public Map<Integer, List<SessionMemberView>> getSessionShortNameMap() {
        return sessionShortNameMap;
    }

    @Override
    public String getViewType() {
        return "member-sessions";
    }
}
