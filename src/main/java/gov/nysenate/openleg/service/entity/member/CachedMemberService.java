package gov.nysenate.openleg.service.entity.member;

import gov.nysenate.openleg.dao.entity.member.MemberDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.processor.base.ParseError;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class CachedMemberService implements MemberService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedMemberService.class);

    private Cache memberCache;

    @Autowired
    private CacheManager cacheManager;

    @Resource(name = "sqlMember")
    private MemberDao memberDao;

    @PostConstruct
    private void init() {
        cacheManager.addCache("members");
        this.memberCache = cacheManager.getCache("members");
    }

    /** {@inheritDoc} */
    public Member getMemberById(int memberId, SessionYear sessionYear) throws MemberNotFoundEx {
        if (memberId <= 0) {
            throw new IllegalArgumentException("Member Id cannot be less than or equal to 0.");
        }
        try {
            return memberDao.getMemberById(memberId, sessionYear);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(memberId, sessionYear);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Member getMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx {
        SimpleKey key = new SimpleKey(sessionMemberId);
        if (memberCache.isKeyInCache(key)) {
            return (Member) memberCache.get(key).getObjectValue();
        }
        try {
            Member member = memberDao.getMemberBySessionId(sessionMemberId);
            putMemberInCache(member);
            return member;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(sessionMemberId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Member getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) throws MemberNotFoundEx {
        if (lbdcShortName == null || chamber == null) {
            throw new IllegalArgumentException("Shortname and/or chamber cannot be null.");
        }
        try {
            return memberDao.getMemberByShortName(lbdcShortName, sessionYear, chamber);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(lbdcShortName, sessionYear, chamber);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Member getMemberByShortNameEnsured(String lbdcShortName, SessionYear sessionYear, Chamber chamber) {
        try {
            return getMemberByShortName(lbdcShortName, sessionYear, chamber);
        }
        catch (MemberNotFoundEx ex) {
            try {
                Member member = Member.newMakeshiftMember(lbdcShortName, sessionYear, chamber);
                memberDao.insertUnverifiedSessionMember(member);
                return member;
            }
            catch (ParseError pe) {
                logger.error(pe.getMessage());
                return null;
            }
        }
    }

    private void putMemberInCache(Member member) {
        memberCache.put(new Element(new SimpleKey(member.getSessionMemberId()), member, true));
    }
}