package gov.nysenate.openleg.model.entity;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.TreeMultimap;
import gov.nysenate.openleg.model.base.SessionYear;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;

/**
 * A member with session members for each session the member was active
 */
public class FullMember extends Member {

    /** Map of session year to this member's session member aliases for the given year */
    protected final TreeMultimap<SessionYear, SessionMember> sessionMemberMap = TreeMultimap.create();

    public FullMember(Collection<SessionMember> sessionMembers) {
        super(sessionMembers.stream().max(SessionMember::compareTo).orElse(null));
        sessionMembers.stream()
                .peek(sm -> {
                    if (sm.memberId != this.memberId) {
                        throw new IllegalArgumentException("All supplied session members must have the same member id");
                    }
                })
                .forEach(sm -> sessionMemberMap.put(sm.getSessionYear(), sm));
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
        return getSessionMembersForYear(year).stream().max(SessionMember::compareTo);
    }

    /**
     * @param year SessionYear
     * @return NavigableSet<SessionMember> - all of this member's session aliases for the given year
     *          returns an empty set if this member was not active on the given year
     */
    public NavigableSet<SessionMember> getSessionMembersForYear(SessionYear year) {
        return Optional.ofNullable(sessionMemberMap.get(year))
                .orElse(new TreeSet<>());
    }

    /**
     * @return ImmutableMultimap<SessionYear, SessionMember>
     *     an immutable copy of the session member map
     */
    public ImmutableMultimap<SessionYear, SessionMember> getSessionMemberMap() {
        return ImmutableMultimap.copyOf(sessionMemberMap);
    }
}
