package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.PaginationResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.view.entity.FullMemberView;
import gov.nysenate.openleg.client.view.entity.SessionMemberView;
import gov.nysenate.openleg.controller.api.ApiTest;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.search.SearchException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
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
        ListViewResponse<?> listResponse = (ListViewResponse<?>) baseResponse;
        assertEquals(listResponse.getTotal(), 224);

        long numAlternates = listResponse.getResult().getItems().stream().filter(sm ->
                sm instanceof SessionMemberView && ((SessionMemberView) sm).isAlternate()).count();
        assertEquals(numAlternates, 0);

        addParam("limit", "all");
        baseResponse = testCtrl.getMembersByYear(2015, "shortName:asc", true, testRequest);
        listResponse = (ListViewResponse<?>) baseResponse;
        FullMemberView testFmv = (FullMemberView) listResponse.getResult().getItems().stream().filter(fm ->
                fm instanceof FullMemberView && ((FullMemberView) fm).getMemberId() == 591).collect(Collectors.toList()).get(0);
        assertEquals(2, testFmv.getSessionShortNameMap().get(2015).size());
    }

    @Test
    public void getMembersByYearAndIdTest() {
        String name = "HASSELL-THOMPSO";
        Person testP = new Person(199, "Ruth Hassell-Thompson", "Ruth", null, "Hassell-Thompson",
                "hassellt@senate.state.ny.us", "Senator", null, true, "380_ruth_hassell-thompson.jpg");
        Member testM = new Member(testP, 380, Chamber.SENATE, false);
        SessionMember nonAlt2011 = new SessionMember(74, testM, name + "N", new
                SessionYear(2011), 36, false);

        BaseResponse resp = testCtrl.getMembersByYearAndId(testM.getMemberId(), 2011, false, testRequest);
        SessionMember actualSm = ((SessionMemberView)(((ViewObjectResponse<?>) resp).getResult())).toSessionMember();
        assertTrue(nonAlt2011.exactEquals(actualSm));

        SessionMember nonAlt2009 = new SessionMember(nonAlt2011);
        nonAlt2009.setSessionMemberId(12);
        nonAlt2009.setSessionYear(new SessionYear(2009));

        SessionMember alt2009 = new SessionMember(nonAlt2009);
        nonAlt2009.setSessionMemberId(668);
        nonAlt2009.setAlternate(true);
        nonAlt2009.setLbdcShortName(name.replaceFirst("-", "_").replaceFirst("L", ""));

        SessionMember alt2011 = new SessionMember(nonAlt2011);
        alt2011.setSessionMemberId(669);
        alt2011.setAlternate(true);
        alt2011.setLbdcShortName(name);

        SessionMember nonAlt2013 = new SessionMember(nonAlt2011);
        nonAlt2013.setSessionMemberId(136);
        nonAlt2013.setSessionYear(new SessionYear(2013));

        SessionMember alt2013 = new SessionMember(nonAlt2013);
        alt2013.setSessionMemberId(670);
        alt2013.setAlternate(true);
        alt2013.setLbdcShortName(name);

        SessionMember only2015 = new SessionMember(nonAlt2011);
        only2015.setSessionMemberId(693);
        only2015.setSessionYear(new SessionYear(2015));

        FullMemberView testFmv = new FullMemberView(new FullMember(Arrays.asList(alt2009, nonAlt2009,
                alt2011, nonAlt2011, alt2013, nonAlt2013, only2015)));
        resp = testCtrl.getMembersByYearAndId(testM.getMemberId(), 2015, true, testRequest);
        FullMemberView actualFmv = (FullMemberView)(((ViewObjectResponse<?>) resp).getResult());
        assertTrue(testFmv.exactEquals(actualFmv));
    }

    @Test
    public void getMembersByYearAndChamberTest() throws SearchException, MemberNotFoundEx {
        BaseResponse baseResponse = testCtrl.getMembersByYearAndChamber(2017, "senate", "shortName:asc", false, testRequest);
        assertEquals(((PaginationResponse) baseResponse).getTotal(), 67);
        baseResponse = testCtrl.getMembersByYearAndChamber(2017, "assembly", "shortName:asc", false, testRequest);
        assertEquals(((PaginationResponse) baseResponse).getTotal(), 163);
    }

    @Test
    public void handleMemberNotFoundExTest() {
        ErrorResponse resp = testCtrl.handleMemberNotFoundEx(null);
        assertEquals(resp.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND.getCode());
    }
}
