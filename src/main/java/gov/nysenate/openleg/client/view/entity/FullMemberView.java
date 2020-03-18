package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.model.entity.SessionMember;

import java.util.*;
import java.util.stream.Collectors;

public class FullMemberView extends MemberView {

    protected PersonView personView;
    protected Map<Integer, List<SessionMemberView>> sessionShortNameMap;

    public FullMemberView(FullMember member) {
        super(member.getLatestSessionMember().orElse(null));
        this.personView = new PersonView(member);
        this.sessionShortNameMap = member.getSessionMemberMap().keySet().stream()
                .collect(Collectors.toMap(SessionYear::getYear,
                        session -> member.getSessionMemberMap().get(session).stream()
                                .map(SessionMemberView::new)
                                .collect(Collectors.toList())));
    }

    /**
     * This constructor is used for unverified session members, which will only have a single session member, member and person
     * @param member Member
     */
    public FullMemberView(SessionMember member) {
        this(new FullMember(Collections.singletonList(member)));
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

    public boolean exactEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullMemberView that = (FullMemberView) o;
        if (!Objects.equals(sessionShortNameMap.keySet(), that.sessionShortNameMap.keySet()))
            return false;
        for (Integer key : sessionShortNameMap.keySet()) {
            List<SessionMemberView> thisSms = sessionShortNameMap.get(key);
            List<SessionMemberView> thatSms = that.sessionShortNameMap.get(key);
            if (thisSms.size() != thatSms.size())
                return false;
            for (int i = 0; i < thisSms.size(); i++) {
                if (!thisSms.get(i).toSessionMember().exactEquals(thatSms.get(i).toSessionMember()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public String getViewType() {
        return "member-sessions";
    }
}
