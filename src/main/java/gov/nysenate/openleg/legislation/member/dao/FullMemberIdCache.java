package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.util.RegexUtils;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.notifications.model.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.notifications.model.NotificationType.BAD_MEMBER_NAME;

@Component
final class FullMemberIdCache extends AbstractMemberCache<Integer, FullMember> {

    public FullMemberIdCache(MemberDao memberDao) {
        super(memberDao);
    }

    @Override
    protected CacheType cacheType() {
        return CacheType.FULL_MEMBER;
    }

    @Override
    public Map<Integer, FullMember> initialEntries() {
        List<FullMember> fullMembers = memberDao.getAllFullMembers();
        fullMembers.forEach(this::checkName);
        return fullMembers.stream()
                .collect(Collectors.toMap(FullMember::getMemberId, Function.identity()));
    }

    @Override
    protected FullMember getMemberFromDao(Integer memberId) {
        return memberDao.getMemberById(memberId);
    }

    private void checkName(FullMember member) {
        // Tests for consistency between the person's last name, and their most recent shortname
        // (which is in all caps and has accents removed).
        var name = member.getPerson().name();
        String expectedShortname = RegexUtils.removeAccentedCharacters(name.lastName())
                .toUpperCase();
        char firstInitial = name.firstName().charAt(0);
        // The shortname may have the first and middle initial appended to it.
        String namePattern = "(%s)( %c.?)?".formatted(expectedShortname, firstInitial);
        String currShortname = member.getLatestSessionMember()
                .orElse(new SessionMember()).getLbdcShortName();
        if (!currShortname.matches(namePattern)) {
            eventBus.post(new Notification(BAD_MEMBER_NAME, LocalDateTime.now(),
                    "There is a member name mismatch.",
                    "Member " + name.fullName() + "'s last name doesn't match their most recent session member."));
        }
    }
}
