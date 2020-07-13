package gov.nysenate.openleg.controller.api.entity;

import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.entity.FullMemberView;
import gov.nysenate.openleg.client.view.entity.SessionMemberView;
import gov.nysenate.openleg.controller.api.ApiTest;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.search.SearchException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class MemberSearchCtrlTest extends ApiTest {

    @Autowired
    private MemberSearchCtrl testCtrl;

    /**
     * Tests that a Member is retrieved correctly.
     */
    @Test
    public void aSimpleTest() throws SearchException {
        Person expectedPerson = new Person(498, "Aurelia Greene", "Aurelia", "", "Greene", "", "Assembly Member", "", "no_image.jpg");
        Member expectedMember = new Member(expectedPerson, 676, Chamber.ASSEMBLY, false);
        SessionMember expectedSessionMember = new SessionMember(664, expectedMember, "GREENE", new SessionYear(2009), 77, false);

        ListViewResponse<?> listResponse = (ListViewResponse<?>) testCtrl.globalSearch("memberId:676", "", false, testRequest);
        assertEquals(1, listResponse.getTotal());

        SessionMember actualSessionMember = ((SessionMemberView) listResponse.getResult().getItems().asList().get(0)).toSessionMember();
        assertEquals(expectedSessionMember, actualSessionMember);
    }

    /**
     * Tests that session members are retrieved correctly.
     */
    @Test
    public void searchBySessionMemberId() throws SearchException {
        Person testP = new Person(499, "Edward Hennessey", "Edward", null, "Hennessey", null, null, null, "no_image.jpg");
        Member testM = new Member(testP, 677, Chamber.ASSEMBLY, false);
        SessionMember testSm = new SessionMember(666, testM, "HENNESSEY", new SessionYear(2013), 3, false);
        SessionMember testSmAlt = new SessionMember(testSm);
        testSmAlt.setSessionMemberId(667);
        testSmAlt.setLbdcShortName("HENNESSY");
        testSmAlt.setAlternate(true);

        ListViewResponse<?> listResponse = (ListViewResponse<?>) testCtrl.globalSearch(2013, "sessionShortNameMap.2013.sessionMemberId:666", "", false, testRequest);
        assertEquals(1, listResponse.getTotal());

        SessionMember actualSm = ((SessionMemberView) listResponse.getResult().getItems().get(0)).toSessionMember();
        assertEquals(testSm, actualSm);

        listResponse = (ListViewResponse<?>) testCtrl.globalSearch(2013, "sessionShortNameMap.2013.sessionMemberId:667", "", true, testRequest);
        assertEquals(1, listResponse.getTotal());

        List<SessionMemberView> actual2013Smvs = ((FullMemberView) listResponse.getResult().getItems().get(0)).getSessionShortNameMap().get(2013);
        assertEquals(2, actual2013Smvs.size());
        assertEquals(testSm, actual2013Smvs.get(0).toSessionMember());
        assertEquals(testSmAlt, actual2013Smvs.get(1).toSessionMember());
    }
}
