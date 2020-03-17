package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.PaginationResponse;
import gov.nysenate.openleg.client.view.entity.FullMemberView;
import gov.nysenate.openleg.client.view.entity.SessionMemberView;
import gov.nysenate.openleg.controller.api.ApiTest;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.search.SearchException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class MemberGetCtrlTest extends ApiTest {
    @Autowired
    private MemberGetCtrl testCtrl;

    @Test
    public void getAllMembersTest() throws SearchException, MemberNotFoundEx {
        testCtrl.getAllMembers("shortName:asc", true, testRequest);
    }

    @Test
    public void getMembersByYearTest() throws SearchException, MemberNotFoundEx {
        BaseResponse baseResponse = testCtrl.getMembersByYear(2015, "shortName:asc", false, testRequest);
        ListViewResponse<?> paginationResponse = (ListViewResponse<?>) baseResponse;
        assertEquals(paginationResponse.getTotal(), 224);

        long numAlternates = paginationResponse.getResult().getItems().stream().filter(sm ->
                sm instanceof SessionMemberView && ((SessionMemberView) sm).isAlternate()).count();
        assertEquals(numAlternates, 0);

        addParam("limit", "all");
        baseResponse = testCtrl.getMembersByYear(2015, "shortName:asc", true, testRequest);
        paginationResponse = (ListViewResponse<?>) baseResponse;
        FullMemberView testFmv = (FullMemberView) paginationResponse.getResult().getItems().stream().filter(fm ->
                fm instanceof FullMemberView && ((FullMemberView) fm).getMemberId() == 591).collect(Collectors.toList()).get(0);
        assertEquals(2, testFmv.getSessionShortNameMap().get(2015).size());
    }

    @Test
    public void getMembersByYearAndChamberTest() throws SearchException, MemberNotFoundEx {
        BaseResponse baseResponse = testCtrl.getMembersByYearAndChamber(2017, "senate", "shortName:asc", false, testRequest);
        assertEquals(((PaginationResponse) baseResponse).getTotal(), 67);
        baseResponse = testCtrl.getMembersByYearAndChamber(2017, "assembly", "shortName:asc", false, testRequest);
        assertEquals(((PaginationResponse) baseResponse).getTotal(), 163);
    }
}
