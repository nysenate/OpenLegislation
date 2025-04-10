package gov.nysenate.openleg.api.legislation.member;

import gov.nysenate.openleg.api.ApiTest;
import gov.nysenate.openleg.api.legislation.member.view.FullMemberView;
import gov.nysenate.openleg.api.legislation.member.view.SessionMemberView;
import gov.nysenate.openleg.api.response.*;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;
import gov.nysenate.openleg.search.SearchException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.*;

import static gov.nysenate.openleg.legislation.member.dao.MemberChangeType.*;
import static gov.nysenate.openleg.processors.MemberType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class MemberGetCtrlIT extends ApiTest {
    @Autowired
    private MemberGetCtrl testCtrl;

    /**
     * Tests that all members of a certain year are correctly retrieved.
     */
    @Test
    public void getMembersByYearTest() throws SearchException, MemberNotFoundEx {
        ListViewResponse<?> listResponse = (ListViewResponse<?>) testCtrl.getMembersByYear(2013, "shortName:asc", false, testRequest);
        assertEquals(216, listResponse.getTotal());
        // If just SessionMemberViews are returned, there should be no alternates.
        long numAlternates = listResponse.getResult().getItems().stream().filter(sm ->
                sm instanceof SessionMemberView && ((SessionMemberView) sm).isAlternate()).count();
        assertEquals(0, numAlternates);

        listResponse = (ListViewResponse<?>) testCtrl.getMembersByYear(2013, "shortName:asc", true, testRequest);
        FullMemberView testFmv = (FullMemberView) listResponse.getResult().getItems().stream().filter(fm ->
                fm instanceof FullMemberView && ((FullMemberView) fm).getMemberId() == 591).toList().get(0);
        assertEquals(2, testFmv.getSessionShortNameMap().get(2013).size());
    }

    /**
     * Tests many session members of a particular member.
     */
    @Test
    public void getMembersByYearAndIdTest() {
        String name = "HASSELL-THOMPSO";
        PersonName pName = new PersonName("Ruth Hassell-Thompson", "Senator", "Ruth", "",
                "Hassell-Thompson", "");
        Person testPerson = new Person(199, pName, null,
                "380_ruth_hassell-thompson.jpg");
        Member testMember = new Member(testPerson, 380, Chamber.SENATE, false);
        SessionMember nonAlt2011 = new SessionMember(74, testMember, name + "N", new
                SessionYear(2011), 36, false);

        BaseResponse resp = testCtrl.getMembersByYearAndId(testMember.getMemberId(), 2011, false);
        SessionMember actualSm = ((SessionMemberView) (((ViewObjectResponse<?>) resp).getResult())).toSessionMember();
        assertEquals(nonAlt2011, actualSm);

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
        resp = testCtrl.getMembersByYearAndId(testMember.getMemberId(), 2015, true);
        FullMemberView actualFmv = (FullMemberView) (((ViewObjectResponse<?>) resp).getResult());
        assertTrue(isFullMemberViewEqual(testFmv, actualFmv));
    }

    /**
     * Tests that the correct number of members are in each chamber.
     */
    @Test
    public void getMembersByYearAndChamberTest() throws SearchException, MemberNotFoundEx {
        BaseResponse baseResponse = testCtrl.getMembersByYearAndChamber(2009, "senate", "shortName:asc", false, testRequest);
        assertEquals(63, ((PaginationResponse) baseResponse).getTotal());
        baseResponse = testCtrl.getMembersByYearAndChamber(2009, "assembly", "shortName:asc", false, testRequest);
        assertEquals(160, ((PaginationResponse) baseResponse).getTotal(), 160);
    }

    /**
     * Just to round out coverage.
     */
    @Test
    public void handleMemberNotFoundExTest() {
        ErrorResponse resp = testCtrl.handleMemberNotFoundEx(null);
        assertEquals(resp.getErrorCode(), ErrorCode.MEMBER_NOT_FOUND.getCode());
    }

    private boolean isFullMemberViewEqual(FullMemberView expected, FullMemberView actual) {
        if (expected == actual) return true;
        if (expected == null || actual == null || expected.getClass() != actual.getClass()) return false;
        if (!Objects.equals(expected.getSessionShortNameMap().keySet(), actual.getSessionShortNameMap().keySet())) {
            return false;
        }
        for (Integer key : expected.getSessionShortNameMap().keySet()) {
            List<SessionMemberView> thisSms = expected.getSessionShortNameMap().get(key);
            List<SessionMemberView> thatSms = actual.getSessionShortNameMap().get(key);
            if (thisSms.size() != thatSms.size())
                return false;
            for (int i = 0; i < thisSms.size(); i++) {
                if (!thisSms.get(i).toSessionMember().equals(thatSms.get(i).toSessionMember()))
                    return false;
            }
        }
        return true;
    }
    @Test
    public void testGetMemberXml() {
        // Create Map<String, String> for CREATE, UPDATE, DELETE
        Map<String, String> createMap = new HashMap<>();
        createMap.put("personId", "12345");
        createMap.put("chamber", "Senate");

        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("id", "1115");
        updateMap.put("incumbent", "true");

        Map<String, String> deleteMap = new HashMap<>();
        deleteMap.put("id", "1115");

        // Call the method with the proper Map
        BaseResponse result = testCtrl.createMemberXml(MEMBER, CREATE, createMap);
        BaseResponse result2 = testCtrl.createMemberXml(MEMBER, UPDATE, updateMap);
        BaseResponse result3 = testCtrl.createMemberXml(MEMBER, DELETE, deleteMap);

        // Assert the result
        assertTrue("The method should return success indicating true for CREATE", result.isSuccess());
        assertTrue("The method should return success indicating true for UPDATE", result2.isSuccess());
        assertTrue("The method should return success indicating true for DELETE", result3.isSuccess());
    }


    @Test
    public void testGetPersonXml() throws IOException {
        // Create Map<String, String> for CREATE, UPDATE, DELETE
        Map<String, String> createMap = new HashMap<>();
        createMap.put("firstName", "John");
        createMap.put("lastName", "Doe");
        createMap.put("email", "john.doe@example.com");
        createMap.put("middleName", "Edward");
        createMap.put("imgName", "john_doe.jpg");
        createMap.put("suffix", "Jr");

        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("id", "1115");
        updateMap.put("email", "john.doe2@example.com");
        updateMap.put("middleName", "Edward2");

        Map<String, String> deleteMap = new HashMap<>();
        deleteMap.put("id", "1115");

        // Call the method with the proper Map
        BaseResponse result = testCtrl.createMemberXml(PERSON, CREATE, createMap);
        BaseResponse result2 = testCtrl.createMemberXml(PERSON, UPDATE, updateMap);
        BaseResponse result3 = testCtrl.createMemberXml(PERSON, DELETE, deleteMap);

        // Assert the result
        assertTrue("The method should return success indicating true for CREATE", result.isSuccess());
        assertTrue("The method should return success indicating true for UPDATE", result2.isSuccess());
        assertTrue("The method should return success indicating true for DELETE", result3.isSuccess());
    }

    @Test
    public void testgetSessionXml() throws IOException {
        // Create Map<String, String> for CREATE, UPDATE, DELETE
        Map<String, String> createMap = new HashMap<>();
        createMap.put("memberId", "1001");
        createMap.put("sessionYear", "2023");
        createMap.put("lbdcShortName", "JohnDoe");
        createMap.put("districtCode", "45");

        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("id", "1001");
        updateMap.put("districtCode", "2024");
        updateMap.put("alternate", "true");

        Map<String, String> deleteMap = new HashMap<>();
        deleteMap.put("id", "1001");

        // Call the method with the proper Map
        BaseResponse result = testCtrl.createMemberXml(SESSION_MEMBER, CREATE, createMap);
        BaseResponse result2 = testCtrl.createMemberXml(SESSION_MEMBER, UPDATE, updateMap);
        BaseResponse result3 = testCtrl.createMemberXml(SESSION_MEMBER, DELETE, deleteMap);

        // Assert the result
        assertTrue("The method should return success indicating true for CREATE", result.isSuccess());
        assertTrue("The method should return success indicating true for UPDATE", result2.isSuccess());
        assertTrue("The method should return success indicating true for DELETE", result3.isSuccess());
    }


}
