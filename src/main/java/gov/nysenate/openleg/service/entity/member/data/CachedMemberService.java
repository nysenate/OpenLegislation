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
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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

    /** --- MemberService implementation --- */

    /** {@inheritDoc} */ //
    @Override
    public SessionMember getMemberById(int memberId, SessionYear sessionYear) throws MemberNotFoundEx {
//        SimpleKey key = new SimpleKey(memberId);
//        if (fullMemberIdCache.isKeyInCache(key)) {
//            FullMember fullMember = (FullMember) fullMemberIdCache.getCache().get(key).getObjectValue();
//            return fullMember.getSessionMemberForYear(sessionYear).get();
//        }
        SessionMember sessionMember = fullMemberIdCache.getMemberById(memberId, sessionYear);

        if( sessionMember != null) {
            return sessionMember;
        }

        try {
            return memberDao.getMemberById(memberId, sessionYear);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(memberId, sessionYear);
        }
    }

    @Override //
    public FullMember getMemberById(int memberId) throws MemberNotFoundEx {
//        SimpleKey key = new SimpleKey(memberId);
//        if (fullMemberIdCache.isKeyInCache(key)) {
//            return (FullMember) fullMemberIdCache.getCache().get(key).getObjectValue();
//        }
        FullMember fullMember = fullMemberIdCache.getMemberById(memberId);
        if (fullMember != null) {
            return fullMember;
        }
        FullMember fullMember1 = memberDao.getMemberById(memberId);
        fullMemberIdCache.putMemberInCache(fullMember1);
        return fullMember1;
    }

    /** {@inheritDoc} */ //
    @Override
    public SessionMember getMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx {
//        SimpleKey key = new SimpleKey(sessionMemberId);
//        if (sessionMemberIdCache.isKeyInCache(key)) {
//            return (SessionMember) sessionMemberIdCache.getCache().get(key).getObjectValue();
//        }
        SessionMember sessionMember = sessionMemberIdCache.getMemberBySessionId(sessionMemberId);
        if (sessionMember != null) {
            return sessionMember;
        }
        try {
            SessionMember member = memberDao.getMemberBySessionId(sessionMemberId);
            sessionMemberIdCache.putMemberInCache(member);
            return member;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(sessionMemberId);
        }
    }

    /** {@inheritDoc} */ //
    @Override
    public SessionMember getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws MemberNotFoundEx {
        if (lbdcShortName == null || chamber == null) {
            throw new IllegalArgumentException("Shortname and/or chamber cannot be null.");
        }
//        SimpleKey key = new SimpleKey(sessionChamberShortNameCache.genCacheKey(lbdcShortName, sessionYear, chamber));
//        if (sessionChamberShortNameCache.isKeyInCache(key)) {
//            return (SessionMember) sessionChamberShortNameCache.getCache().get(key).getObjectValue();
//        }
        SessionMember sessionMember = sessionChamberShortNameCache.getMemberByShortName(lbdcShortName,sessionYear,chamber);
        if (sessionMember != null) {
            return sessionMember;
        }
        try {
            SessionMember sessionMember1 = memberDao.getMemberByShortName(lbdcShortName, sessionYear, chamber);
            sessionChamberShortNameCache.putMemberInCache(
                    sessionChamberShortNameCache.genCacheKey(lbdcShortName, sessionYear, chamber),
                    sessionMember1);
            return sessionMember1;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(lbdcShortName, sessionYear, chamber);
        }
    }

    /** {@inheritDoc} */
    @Override
    public SessionMember getMemberByShortNameEnsured(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws ParseError {
        try {
            return getMemberByShortName(lbdcShortName, sessionYear, chamber);
        }
        catch (MemberNotFoundEx ex) {
            SessionMember member = SessionMember.newMakeshiftMember(lbdcShortName, sessionYear, chamber);
            memberDao.updatePerson(member);
            memberDao.updateMember(member);
            memberDao.updateSessionMember(member);
            eventBus.post(new UnverifiedMemberEvent(member, LocalDateTime.now()));
            updateCaches();
            return member;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SessionMember> getAllMembers(SortOrder sortOrder, LimitOffset limOff) {
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
                .collect(Collectors.toMap(Person::getPersonId, Function.identity(), (a,b) -> b))
                .values();

        Collection<SessionMember> members = sessionMembers.stream()
                .collect(Collectors.toMap(SessionMember::getMemberId, Function.identity(), (a,b) -> b))
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