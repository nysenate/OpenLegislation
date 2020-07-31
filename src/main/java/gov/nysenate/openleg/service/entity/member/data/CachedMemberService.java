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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
    public List<SessionMember> getAllSessionMembers(SortOrder sortOrder, LimitOffset limOff) {
        return fullMemberIdCache.getAllMembers(sortOrder, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public List<FullMember> getAllFullMembers() {
        return fullMemberIdCache.getAllFullMembers();
    }

}
