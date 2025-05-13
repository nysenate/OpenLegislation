package gov.nysenate.openleg.legislation.member.dao;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.common.util.RegexUtils;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.notifications.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.notifications.model.NotificationType.BAD_MEMBER_NAME;

@Service
class CachedMemberService extends CachingService<Integer, FullMember> implements MemberService {
    private static final Logger logger = LoggerFactory.getLogger(CachedMemberService.class);

    private final MemberDao memberDao;
    private Map<Integer, SessionMember> sessionMemberIdMap;
    private Map<ShortNameKey, SessionMember> sessionMemberShortnameMap;

    private record ShortNameKey(String lbdcShortName, SessionYear sessionYear, Chamber chamber) {
        private ShortNameKey(SessionMember sm) {
            this(sm.getLbdcShortName(), sm.getSessionYear(), sm.getMember().getChamber());
        }
    }

    @Autowired
    public CachedMemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @PostConstruct
    private void init() {
        refreshOtherData();
    }

    @Override
    protected CacheType cacheType() {
        return CacheType.MEMBER;
    }

    @Override
    protected Map<Integer, FullMember> initialEntries() {
        List<FullMember> fullMembers = memberDao.getAllFullMembers();
        fullMembers.forEach(this::checkName);
        return fullMembers.stream().collect(Collectors.toMap(FullMember::getMemberId, Function.identity()));
    }

    @Override
    protected void clearCache(boolean warmCaches) {
        if (!warmCaches) {
            logger.warn("Tried to clear cache without warming: will clear and warm instead.");
        }
        super.clearCache(true);
        refreshOtherData();
    }

    private void refreshOtherData() {
        List<SessionMember> sessionMembers = memberDao.getAllSessionMembers();
        sessionMemberIdMap = ImmutableMap.copyOf(
                sessionMembers.stream()
                        .collect(Collectors.toMap(SessionMember::getSessionMemberId, Function.identity()))
        );
        sessionMemberShortnameMap = ImmutableMap.copyOf(
                sessionMembers.stream().collect(Collectors.toMap(ShortNameKey::new, Function.identity())));
    }

    private void checkName(FullMember member) {
        Optional<SessionMember> smOpt = member.getLatestSessionMember();
        if (smOpt.isEmpty()) {
            return;
        }
        // Tests for consistency between the person's last name, and their most recent shortname
        // (which is in all caps and has accents removed).
        var name = member.getPerson().name();
        String expectedShortname = RegexUtils.removeAccentedCharacters(name.lastName())
                .toUpperCase();
        char firstInitial = name.firstName().charAt(0);
        // The shortname may have the first and middle initial appended to it.
        String namePattern = "(%s)( %c.?)?".formatted(expectedShortname, firstInitial);
        String currShortname = smOpt.get().getLbdcShortName();
        if (!currShortname.matches(namePattern)) {
            eventBus.post(new Notification(BAD_MEMBER_NAME, LocalDateTime.now(),
                    "There is a member name mismatch.",
                    "Member " + name.fullName() + "'s last name doesn't match their most recent session member."));
        }
    }

    /* --- MemberService implementation --- */

    /** {@inheritDoc} */
    @Override
    public SessionMember getSessionMemberById(int memberId, SessionYear sessionYear) throws MemberNotFoundEx {
        try {
            FullMember member = cache.get(memberId);
            Optional<SessionMember> sessionMemOpt = member.getSessionMemberForYear(sessionYear);
            if (sessionMemOpt.isPresent()) {
                return sessionMemOpt.get();
            }
        } catch (MemberNotFoundEx ignored) {}
        throw new MemberNotFoundEx(memberId, sessionYear);
    }

    @Override
    public FullMember getFullMemberById(int memberId) throws MemberNotFoundEx {
        return cache.get(memberId);
    }

    /** {@inheritDoc} */
    @Override
    public SessionMember getSessionMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx {
        return sessionMemberIdMap.get(sessionMemberId);
    }

    /** {@inheritDoc} */
    @Override
    public SessionMember getSessionMemberByShortName(String lbdcShortName, SessionYear sessionYear,
                                                     Chamber chamber) throws MemberNotFoundEx {
        if (lbdcShortName == null || chamber == null)
            throw new IllegalArgumentException("Shortname and/or chamber cannot be null.");
        return sessionMemberShortnameMap.get(new ShortNameKey(lbdcShortName, sessionYear, chamber));
    }

    /** {@inheritDoc} */
    @Override
    public List<FullMember> getAllFullMembers() {
        List<FullMember> fullMembers = new ArrayList<>();
        for (var entry : cache) {
            fullMembers.add(entry.getValue());
        }
        return fullMembers;
    }
}
