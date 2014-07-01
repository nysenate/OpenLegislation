package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlMemberDao extends SqlBaseDao implements MemberDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlMemberDao.class);

    @Override
    public Member getMemberByLBDCShortName(String lbdcShortName) {
        return null;
    }

    @Override
    public Member getMemberByLBDCShortName(String lbdcShortName, int sessionYear) {
        return null;
    }

    @Override
    public void updateMember(Member member) {

    }

    @Override
    public void deleteMember(Member member) {

    }
}
