package gov.nysenate.openleg.service.entity.member.data;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.entity.member.data.MemberDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.search.RebuildIndexEvent;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.service.entity.member.event.UnverifiedMemberEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CachedMemberService implements MemberService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedMemberService.class);

    private EventBus eventBus;

    private SessionMemberIdCache sessionMemberIdCache;

    private FullMemberIdCache fullMemberIdCache;

    private SessionChamberShortNameCache sessionChamberShortNameCache;

    @Resource(name = "sqlMember")
    private MemberDao memberDao;

    @Autowired
    public CachedMemberService(EventBus eventBus, SessionMemberIdCache sessionMemberIdCache,
                               FullMemberIdCache fullMemberIdCache, SessionChamberShortNameCache shortNameCache) {
        this.eventBus = eventBus;
        this.sessionMemberIdCache = sessionMemberIdCache;
        this.fullMemberIdCache = fullMemberIdCache;
        this.sessionChamberShortNameCache = shortNameCache;
    }

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /* --- MemberService implementation --- */

    /** {@inheritDoc} */
    @Override
    public SessionMember getSessionMemberById(int memberId, SessionYear sessionYear) throws MemberNotFoundEx {
        try {
            FullMember member = fullMemberIdCache.getMemberById(memberId);
            Optional<SessionMember> sessionMembOpt = member.getSessionMemberForYear(sessionYear);
            if (sessionMembOpt.isPresent()) {
                return sessionMembOpt.get();
            }
        } catch (MemberNotFoundEx ignored) {}
        throw new MemberNotFoundEx(memberId, sessionYear);
    }

    @Override
    public FullMember getFullMemberById(int memberId) throws MemberNotFoundEx {
        return fullMemberIdCache.getMemberById(memberId);
    }

    /** {@inheritDoc} */
    @Override
    public SessionMember getSessionMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx {
        return sessionMemberIdCache.getMemberBySessionId(sessionMemberId);
    }

    /** {@inheritDoc} */
    @Override
    public SessionMember getSessionMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws MemberNotFoundEx {
        return sessionChamberShortNameCache.getMemberByShortName(lbdcShortName, sessionYear, chamber);
    }

    /** {@inheritDoc} */
    @Override
    public SessionMember getSessionMemberByShortNameEnsured(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws ParseError {
        try {
            return getSessionMemberByShortName(lbdcShortName, sessionYear, chamber);
        }
        catch (MemberNotFoundEx ex) {
            SessionMember sessionMember = SessionMember.newMakeshiftMember(lbdcShortName, sessionYear, chamber);
            memberDao.updatePerson(sessionMember.getMember());
            memberDao.updateMember(sessionMember.getMember());
            memberDao.updateSessionMember(sessionMember);
            eventBus.post(new UnverifiedMemberEvent(sessionMember, LocalDateTime.now()));
            return sessionMember;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SessionMember> getAllSessionMembers(SortOrder sortOrder, LimitOffset limOff) {
        return fullMemberIdCache.getAllMembers(sortOrder, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public List<FullMember> getAllFullMembers() {
        return fullMemberIdCache.getAllFullMembers();
    }

    /** {@inheritDoc} */
    @Override
    public void updateMembers(List<SessionMember> sessionMembers) {
        Collection<? extends Person> persons = sessionMembers.stream()
                .collect(Collectors.toMap((a -> a.getMember().getPersonId()), SessionMember::getMember, (a, b) -> b))
                .values();

        Collection<Member> members = sessionMembers.stream()
                .collect(Collectors.toMap(a -> a.getMember().getMemberId(), SessionMember::getMember, (a,b) -> b))
                .values();

        persons.forEach(memberDao::updatePerson);
        members.forEach(memberDao::updateMember);
        sessionMembers.forEach(memberDao::updateSessionMember);

        memberDao.clearOrphans();

        // We need to rebuild cache and search index to account for session members that were
        //      tangentially modified via a person or member update
        updateCaches();

    }

    /**
     * This method calls the event bus actions to update the caches.
     */
    private void updateCaches() {
        eventBus.post(new CacheWarmEvent(Collections.singleton(ContentCache.SESSION_CHAMBER_SHORTNAME)));
        eventBus.post(new CacheWarmEvent(Collections.singleton(ContentCache.FULL_MEMBER)));
        eventBus.post(new CacheWarmEvent(Collections.singleton(ContentCache.SESSION_MEMBER)));
        eventBus.post(new RebuildIndexEvent(Collections.singleton(SearchIndex.MEMBER)));
    }
}