package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.entity.FullMemberView;
import gov.nysenate.openleg.client.view.entity.SessionMemberView;
import gov.nysenate.openleg.controller.api.ApiTest;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.search.SearchException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class MemberSearchCtrlTest extends ApiTest {
    static {
        indicesToTest.add(SearchIndex.MEMBER);
    }
    @Autowired
    private MemberSearchCtrl testCtrl;

    @Test
    public void aSimpleTest() throws SearchException {
        Person testP = new Person(498, "Greene", "", "", "", "", "", "", false, "no_image.jpg");
        Member testM = new Member(testP, 676, Chamber.ASSEMBLY, false);
        SessionMember testSm = new SessionMember(664, testM, "GREENE", new SessionYear(2009), 0, false);

        ListViewResponse<?> listResponse = (ListViewResponse<?>) testCtrl.globalSearch("memberId:676", "", false, testRequest);
        assertEquals(1, listResponse.getTotal());

        SessionMember actualSm = ((SessionMemberView) listResponse.getResult().getItems().asList().get(0)).toSessionMember();
        assertTrue(actualSm.exactEquals(testSm));
    }

    @Test
    public void searchBySessionMemberId() throws SearchException {
        Person testP = new Person(499, "Edward Hennessey", "Edward", null, "Hennessey", null, null, null, true, "no_image.jpg");
        Member testM = new Member(testP, 677, Chamber.ASSEMBLY, false);
        SessionMember testSm = new SessionMember(666, testM, "HENNESSEY", new SessionYear(2013), 3, false);
        SessionMember testSmAlt = new SessionMember(testSm);
        testSmAlt.setSessionMemberId(667);
        testSmAlt.setLbdcShortName("HENNESSY");
        testSmAlt.setAlternate(true);

        ListViewResponse<?> listResponse = (ListViewResponse<?>) testCtrl.globalSearch(2013, "sessionShortNameMap.2013.sessionMemberId:666", "", false, testRequest);
        assertEquals(1, listResponse.getTotal());

        SessionMember actualSm = ((SessionMemberView) listResponse.getResult().getItems().get(0)).toSessionMember();
        assertTrue(actualSm.exactEquals(testSm));

        listResponse = (ListViewResponse<?>) testCtrl.globalSearch(2013, "sessionShortNameMap.2013.sessionMemberId:667", "", true, testRequest);
        assertEquals(1, listResponse.getTotal());

        List<SessionMemberView> actual2013Smvs = ((FullMemberView) listResponse.getResult().getItems().get(0)).getSessionShortNameMap().get(2013);
        assertEquals(2, actual2013Smvs.size());
        assertTrue(actual2013Smvs.get(0).toSessionMember().exactEquals(testSm));
        assertTrue(actual2013Smvs.get(1).toSessionMember().exactEquals(testSmAlt));
    }
}
