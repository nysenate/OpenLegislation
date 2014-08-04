package gov.nysenate.openleg.service.entity;

import gov.nysenate.openleg.dao.entity.MemberDao;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class CachedMemberService implements MemberService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedMemberService.class);

    @Autowired
    private CacheManager cacheManager;

    @Resource(name = "sqlMember")
    private MemberDao memberDao;

    @PostConstruct
    private void init() {
        cacheManager.addCache("memberShortName");
        cacheManager.addCache("memberId");
    }

    @Cacheable("memberId")
    public Member getMemberById(int memberId, int sessionYear) throws MemberNotFoundEx {
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

    @Override
    @Cacheable("memberShortName")
    public Member getMemberByLBDCName(String lbdcShortName, int sessionYear, Chamber chamber) throws MemberNotFoundEx {
        if (lbdcShortName == null || chamber == null) {
            throw new IllegalArgumentException("Shortname and/or chamber cannot be null.");
        }
        try {
            return memberDao.getMemberByLBDCName(lbdcShortName, sessionYear, chamber);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(lbdcShortName, sessionYear, chamber);
        }
    }
}
