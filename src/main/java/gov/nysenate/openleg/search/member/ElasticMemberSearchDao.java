package gov.nysenate.openleg.search.member;

import gov.nysenate.openleg.api.legislation.member.view.FullMemberView;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import org.springframework.stereotype.Repository;

@Repository
public class ElasticMemberSearchDao extends ElasticBaseDao<Integer, FullMemberView, FullMember> {
    /** {@inheritDoc} */
    @Override
    public SearchIndex indexType() {
        return SearchIndex.MEMBER;
    }

    @Override
    protected String getId(FullMember data) {
        return String.valueOf(data.getMemberId());
    }

    @Override
    protected FullMemberView getDoc(FullMember data) {
        return new FullMemberView(data);
    }

    @Override
    protected Integer toId(String idStr) {
        return Integer.parseInt(idStr);
    }
}
