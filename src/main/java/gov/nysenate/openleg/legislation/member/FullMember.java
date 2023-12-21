package gov.nysenate.openleg.legislation.member;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.TreeMultimap;
import gov.nysenate.openleg.legislation.SessionYear;

import java.util.Collection;
import java.util.Optional;
import java.util.TreeSet;

/**
 * A member with session members for each session the member was active
 */
public class FullMember extends Member {
    /** Map of session year to this member's session member aliases for the given year */
    private final TreeMultimap<SessionYear, SessionMember> sessionMemberMap =
            TreeMultimap.create();

    public FullMember(Collection<SessionMember> sessionMembers) {
        super(sessionMembers.stream().max(SessionMember::compareTo).orElse(new SessionMember()).member);
        for (var sm : sessionMembers) {
            if (sm.member.getMemberId() != this.getMemberId()) {
                throw new IllegalArgumentException("All supplied session members must have" +
                        "the same member id");
            }
        }
        sessionMembers.forEach(sm -> sessionMemberMap.put(sm.getSessionYear(), sm));
    }

    /** --- Functional Getters / Setters --- */

    /**
     * @return Optional<SessionMember> this session member's most recent, primary session member
     */
    public Optional<SessionMember> getLatestSessionMember() {
        return sessionMemberMap.keySet().stream()
                .max(SessionYear::compareTo)
                .flatMap(this::getSessionMemberForYear);
    }

    /**
     * @param year SessionYear
     * @return This member's primary session member for the given year
     */
    public Optional<SessionMember> getSessionMemberForYear(SessionYear year) {
        return Optional.of(sessionMemberMap.get(year)).orElse(new TreeSet<>()).stream()
                .max(SessionMember::compareTo);
    }

    /**
     * @return ImmutableMultimap<SessionYear, SessionMember>
     *     an immutable copy of the session member map
     */
    public ImmutableMultimap<SessionYear, SessionMember> getSessionMemberMap() {
        return ImmutableMultimap.copyOf(sessionMemberMap);
    }
}
