package gov.nysenate.openleg.legislation.member.dao;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class CachedMemberService implements MemberService {
    private final EventBus eventBus;
    private final MemberDao memberDao;
    private final FullMemberIdCache fullMemberIdCache;
    private final SessionMemberIdCache sessionMemberIdCache;
    private final SessionMemberNonIdCache sessionMemberNonIdCache;

    @Autowired
    public CachedMemberService(EventBus eventBus, MemberDao memberDao,
                               FullMemberIdCache fullMemberIdCache,
                               SessionMemberIdCache sessionMemberIdCache,
                               SessionMemberNonIdCache sessionMemberNonIdCache) {
        this.eventBus = eventBus;
        this.memberDao = memberDao;
        this.fullMemberIdCache = fullMemberIdCache;
        this.sessionMemberIdCache = sessionMemberIdCache;
        this.sessionMemberNonIdCache = sessionMemberNonIdCache;
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
            FullMember member = fullMemberIdCache.getMember(memberId);
            Optional<SessionMember> sessionMemOpt = member.getSessionMemberForYear(sessionYear);
            if (sessionMemOpt.isPresent()) {
                return sessionMemOpt.get();
            }
        } catch (MemberNotFoundEx ignored) {}
        throw new MemberNotFoundEx(memberId, sessionYear);
    }

    @Override
    public FullMember getFullMemberById(int memberId) throws MemberNotFoundEx {
        return fullMemberIdCache.getMember(memberId);
    }

    /** {@inheritDoc} */
    @Override
    public SessionMember getSessionMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx {
        return sessionMemberIdCache.getMember(sessionMemberId);
    }

    /** {@inheritDoc} */
    @Override
    public SessionMember getSessionMemberByShortName(String lbdcShortName, SessionYear sessionYear,
                                                     Chamber chamber) throws MemberNotFoundEx {
        if (lbdcShortName == null || chamber == null)
            throw new IllegalArgumentException("Shortname and/or chamber cannot be null.");
        return sessionMemberNonIdCache.getMember(new ShortNameKey(lbdcShortName, sessionYear, chamber));
    }

    /** {@inheritDoc} */
    @Override
    public List<SessionMember> getAllSessionMembers(SortOrder sortOrder, LimitOffset limOff) {
        return memberDao.getAllSessionMembers(sortOrder, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public List<FullMember> getAllFullMembers() {
        return memberDao.getAllFullMembers();
    }

}
