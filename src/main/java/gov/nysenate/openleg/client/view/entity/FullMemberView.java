package gov.nysenate.openleg.client.view.entity;

import com.google.common.collect.TreeMultimap;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.model.entity.SessionMember;

import java.util.*;
import java.util.stream.Collectors;

public class FullMemberView extends ExtendedMemberView {

    protected Map<Integer, List<SimpleMemberView>> sessionShortNameMap;

    public FullMemberView(FullMember member) {
        super(member.getLatestSessionMember().orElse(null));
        this.sessionShortNameMap = member.getSessionMemberMap().keySet().stream()
                .collect(Collectors.toMap(SessionYear::getYear,
                        session -> member.getSessionMemberMap().get(session).stream()
                                .map(SimpleMemberView::new)
                                .collect(Collectors.toList())));
    }

    /**
     * This constructor is used for unverified session members, which will only have a single session member, member and person
     * @param member Member
     */
    public FullMemberView(SessionMember member) {
        super(member);
        this.sessionShortNameMap = new HashMap<>();
        if (member != null && member.getSessionYear() != null) {
            this.sessionShortNameMap.put(member.getSessionYear().getYear(),
                    Collections.singletonList(new SimpleMemberView(member)));
        }
    }

    public FullMemberView(Collection<SessionMember> members) {
        super(members.stream().max(SessionMember::compareTo).orElse(null));
        this.sessionShortNameMap = members.stream()
                .sorted()
                .map(SimpleMemberView::new)
                .collect(Collectors.groupingBy(SimpleMemberView::getSessionYear));
    }

    public Map<Integer, List<SimpleMemberView>> getSessionShortNameMap() {
        return sessionShortNameMap;
    }

    @Override
    public String getViewType() {
        return "member-sessions";
    }
}
