package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;
import gov.nysenate.openleg.legislation.member.dao.MemberDao;
import gov.nysenate.openleg.legislation.member.dao.MemberChangeType;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.legislation.member.dao.SqlMemberDao;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class MemberProcessorIT extends BaseTests {
    @Autowired
    private MemberProcessor memberProcessor;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private SqlMemberDao sqlMemberDao;

    @Test
    public void testMemberProcessor() throws IOException, SAXException {
        // Step 1: Create Person XmlProcessor
        Path path = Paths.get("src/test/resources/xml.memberchange/createPerson.xml");
        int id = memberProcessor.process(path);

        // Fetch the person record from the database
        Person createdPersonRecord = memberDao.getPersonByPersonId(id);
        assertEquals("john.doe@example.com", createdPersonRecord.email());
        assertEquals("John", createdPersonRecord.name().firstName());
        assertEquals("Doe", createdPersonRecord.name().lastName());
        assertEquals("Michael", createdPersonRecord.name().middleName());
        assertEquals("Jr.", createdPersonRecord.name().suffix());
        assertEquals("john_doe.jpg", createdPersonRecord.imgName());

        //Step 2: Create Member XmlProcessor
        Path memberPath = Paths.get("src/test/resources/xml.memberchange/createMember.xml");
        int m_id = memberProcessor.process(memberPath);
        assertTrue(m_id >0);

        // Step 3: Create Session XmlProcessor
        Path sessionPath = Paths.get("src/test/resources/xml.memberchange/createSessionMember.xml");
        int s_id = memberProcessor.process(sessionPath);
        assertTrue(s_id >0);

        // Fetch full member record for verification
//        Member createdMemberRecord2 = memberDao.getMemberByMemberId(m_id);
//        assertEquals(createdMemberRecord2.getMemberId(), m_id);
//        assertEquals(707, createdMemberRecord2.getPersonId().intValue());
//        assertEquals("SENATE", (createdMemberRecord2.getChamber()).toString());

        // Fetch session member from the database
        SessionMember createdSessionRecord = memberDao.getMemberBySessionId(s_id);
        assertEquals(468, createdSessionRecord.getMember().getMemberId());
        assertEquals(2023, createdSessionRecord.getSessionYear().year());
        assertEquals(12208, createdSessionRecord.getDistrictCode().intValue());

        // Step 4: Update Person XmlProcessor
        Path personUpdatePath = Paths.get("src/test/resources/xml.memberchange/updatePerson.xml");
        memberProcessor.process(personUpdatePath);

        // Fetch the updated person record
        Person updatedPerson = memberDao.getPersonByPersonId(283);
        assertEquals("John2", updatedPerson.name().firstName());
        assertEquals("Doe2", updatedPerson.name().lastName());
        assertEquals("Michael2", updatedPerson.name().middleName());
        assertEquals("Mr.", updatedPerson.name().suffix());
        assertEquals("john_doe.jpg", updatedPerson.imgName());
        assertEquals("john.doe@.gmail.com", updatedPerson.email());

        // Step 5: Update Member XmlProcessor
        Path memberUpdatePath = Paths.get("src/test/resources/xml.memberchange/updateMember.xml");
        memberProcessor.process(memberUpdatePath);

        // Fetch the updated member record
        Member updatedMember = sqlMemberDao.getMemberByMemberId(1115);
        assertTrue(updatedMember.isIncumbent());

        // Step 6: Update Session XmlProcessor
        Path sessionUpdatePath = Paths.get("src/test/resources/xml.memberchange/updateSession.xml");
        memberProcessor.process(sessionUpdatePath);

        // Fetch the updated session record
        SessionMember updatedSessionMember = sqlMemberDao.getMemberBySessionId(1); // Assuming 1 is the updated session ID
        assertEquals(1300,updatedSessionMember.getDistrictCode().intValue());
        assertTrue("Alternate status was not updated", updatedSessionMember.isAlternate());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePersonWithNoFirstName() throws IllegalArgumentException, SAXException, IOException {
        //Create Person with no firstName
        Path path2 = Paths.get("src/test/resources/xml.memberchange/personWithNoFirstName.xml");
        int id2 = memberProcessor.process(path2);
    }

    @Test(expected = MemberNotFoundEx.class)
    public void deleteMember() throws MemberNotFoundEx, IOException, SAXException {
            Path deletePath = Paths.get("src/test/resources/xml.memberchange/deleteSessionMember.xml");
            memberProcessor.process(deletePath);
            SessionMember deletedSession = sqlMemberDao.getMemberBySessionId(357);
            fail("Session record was not deleted, expected exception to be thrown");

            Path deletePath2 = Paths.get("src/test/resources/xml.memberchange/deleteMember.xml");
            Member deletedMember = memberDao.getMemberByMemberId(632);
            memberProcessor.process(deletePath2);
            fail("Member record was not deleted, expected exception to be thrown");

            Path deletePath3 = Paths.get("src/test/resources/xml.memberchange/deletePerson.xml");
            memberProcessor.process(deletePath3);
            Person deletedPerson = memberDao.getPersonByPersonId(454);
            fail("Person record was not deleted, expected exception to be thrown");
    }

    @Test
    public void testGetPersonXmlBuilder() {
        Map<String, String> modelMap = new HashMap<>();
        modelMap.put("id", "123");
        modelMap.put("firstName", "John");
        modelMap.put("lastName", "Doe");
        modelMap.put("email", "john.doe@example.com");
        modelMap.put("middleName", "M");
        modelMap.put("imgName", "profile.jpg");
        modelMap.put("suffix", "Jr");

        // Test CREATE action
        StringBuilder xmlBuilder = memberProcessor.getPersonXmlBuilder(MemberChangeType.CREATE, modelMap, MemberType.MEMBER);

        String expectedXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="MEMBER" action="CREATE">
            <firstName>John</firstName>
            <lastName>Doe</lastName>
            <middleName>M</middleName>
            <suffix>Jr</suffix>
            <email>john.doe@example.com</email>
            <imgName>profile.jpg</imgName>
            </actionDetails>\n""";

        assertEquals(expectedXml, xmlBuilder.toString());

        // Test UPDATE action
        modelMap.put("id", "123");
        modelMap.put("firstName", "John");
        modelMap.put("lastName", "Doe");
        modelMap.put("email", "john.doe@example.com");
        modelMap.put("middleName", "M");
        modelMap.put("imgName", "profile.jpg");
        modelMap.put("suffix", "Jr");

        StringBuilder xmlUpdateBuilder = memberProcessor.getPersonXmlBuilder(MemberChangeType.UPDATE, modelMap, MemberType.MEMBER);

        String expectedUpdateXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="MEMBER" action="UPDATE">
            <id>123</id>
            <firstName>John</firstName>
            <lastName>Doe</lastName>
            <middleName>M</middleName>
            <suffix>Jr</suffix>
            <email>john.doe@example.com</email>
            <imgName>profile.jpg</imgName>
            </actionDetails>\n""";

        assertEquals(expectedUpdateXml, xmlUpdateBuilder.toString());

        // Test DELETE action
        modelMap.put("id", "123");

        StringBuilder xmlDeleteBuilder = memberProcessor.getPersonXmlBuilder(MemberChangeType.DELETE, modelMap, MemberType.MEMBER);

        String expectedDeleteXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="MEMBER" action="DELETE">
            <id>123</id>
            </actionDetails>\n""";

        assertEquals(expectedDeleteXml, xmlDeleteBuilder.toString());

        assertEquals(expectedUpdateXml, xmlUpdateBuilder.toString());
    }

    @Test
    public void testGetPersonXmlBuilder_UpdateWithNullAttributes() {
        Map<String, String> modelMap = new HashMap<>();
        modelMap.put("id", "123");
        modelMap.put("firstName", "abcd");
        modelMap.put("lastName", "efg");
        modelMap.put("email", null);
        modelMap.put("middleName", null);
        modelMap.put("imgName", null);
        modelMap.put("suffix", null);

        // Test UPDATE action
        StringBuilder xmlBuilder = memberProcessor.getPersonXmlBuilder(MemberChangeType.UPDATE, modelMap, MemberType.MEMBER);

        String expectedXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="MEMBER" action="UPDATE">
            <id>123</id>
            <firstName>abcd</firstName>
            <lastName>efg</lastName>
            <middleName>null</middleName>
            <suffix>null</suffix>
            <email>null</email>
            <imgName>null</imgName>
            </actionDetails>\n""";

        assertEquals(expectedXml, xmlBuilder.toString());

        // Test CREATE action with the same attributes
        StringBuilder xmlCreateBuilder = memberProcessor.getPersonXmlBuilder(MemberChangeType.CREATE, modelMap, MemberType.MEMBER);

        String expectedCreatedXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="MEMBER" action="CREATE">
            <firstName>abcd</firstName>
            <lastName>efg</lastName>
            <middleName>null</middleName>
            <suffix>null</suffix>
            <email>null</email>
            <imgName>null</imgName>
            </actionDetails>\n""";

        assertEquals(expectedCreatedXml, xmlCreateBuilder.toString());
    }

    @Test
    public void testGetSessionXmlBuilder_Create() {
        Map<String, String> modelMap = new HashMap<>();
        modelMap.put("id", "123");
        modelMap.put("memberId", "456");
        modelMap.put("sessionYear", "2025");
        modelMap.put("lbdcShortName", "LB1");
        modelMap.put("districtCode", "101");
        modelMap.put("alternate", "true");

        // Test CREATE action
        StringBuilder xmlBuilder = memberProcessor.getSessionXmlBuilder(MemberChangeType.CREATE, modelMap, MemberType.SESSION_MEMBER);

        String expectedXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="SESSION" action="CREATE">
            <memberId>456</memberId>
            <sessionYear>2025</sessionYear>
            <lbdcShortName>LB1</lbdcShortName>
            <districtCode>101</districtCode>
            </actionDetails>\n""";

        assertEquals(expectedXml, xmlBuilder.toString());

        // Test UPDATE action with a different alternate value
        modelMap.put("alternate", "false");

        StringBuilder xmlUpdateBuilder = memberProcessor.getSessionXmlBuilder(MemberChangeType.UPDATE, modelMap, MemberType.SESSION_MEMBER);

        String expectedUpdateXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="SESSION" action="UPDATE">
            <id>123</id>
            <districtCode>101</districtCode>
            <alternate>false</alternate>
            </actionDetails>\n""";

        assertEquals(expectedUpdateXml, xmlUpdateBuilder.toString());

        // Test DELETE action
        modelMap.put("id", "123");

        StringBuilder xmlDeleteBuilder = memberProcessor.getSessionXmlBuilder(MemberChangeType.DELETE, modelMap, MemberType.SESSION_MEMBER);

        String expectedDeleteXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="SESSION" action="DELETE">
            <id>123</id>
            </actionDetails>\n""";

        assertEquals(expectedDeleteXml, xmlDeleteBuilder.toString());
    }

    @Test
    public void testGetSessionXmlBuilder_CreateWithNullAttributes() {
        Map<String, String> modelMap = new HashMap<>();
        modelMap.put("id", "123");
        modelMap.put("memberId", "11");
        modelMap.put("sessionYear", "null");
        modelMap.put("lbdcShortName", "null");
        modelMap.put("districtCode", "null");
        modelMap.put("alternate", "null");

        // Test CREATE action with null attributes
        StringBuilder xmlCreateBuilder = memberProcessor.getSessionXmlBuilder(MemberChangeType.CREATE, modelMap, MemberType.SESSION_MEMBER);

        String expectedCreateXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="SESSION" action="CREATE">
            <memberId>11</memberId>
            <sessionYear>null</sessionYear>
            <lbdcShortName>null</lbdcShortName>
            <districtCode>null</districtCode>
            </actionDetails>\n""";

        assertEquals(expectedCreateXml, xmlCreateBuilder.toString());

        // Test UPDATE action with null attributes
        StringBuilder xmlUpdateBuilder = memberProcessor.getSessionXmlBuilder(MemberChangeType.UPDATE, modelMap, MemberType.SESSION_MEMBER);

        String expectedUpdateXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="SESSION" action="UPDATE">
            <id>123</id>
            <districtCode>null</districtCode>
            <alternate>null</alternate>
            </actionDetails>\n""";

        assertEquals(expectedUpdateXml, xmlUpdateBuilder.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noExpression() throws Exception {
        Path path = Paths.get("src/test/resources/xml.memberchange/noRequiredArgs.xml");
        int id = memberProcessor.process(path);  // This should throw IllegalArgumentException as missing lastname
    }

    @Test
    public void testGetMemberXmlBuilder() {

        Map<String, String> modelMap = new HashMap<>();
        modelMap.put("personId", "123");
        modelMap.put("chamber", "Senate");
        modelMap.put("id", "456");

        StringBuilder xmlBuilder = memberProcessor.getMemberXmlBuilder(MemberChangeType.CREATE, modelMap, MemberType.MEMBER);

        String expectedXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <actionDetails tableName="MEMBER" action="CREATE">
                <personId>123</personId>
                <chamber>Senate</chamber>
                </actionDetails>
                """;
        assertEquals(expectedXml, xmlBuilder.toString());

        modelMap.clear();
        modelMap.put("personId", "123");
        modelMap.put("incumbent", "true");
        modelMap.put("chamber", "Senate");
        modelMap.put("id", "456");

        StringBuilder xmlBuilder2 = memberProcessor.getMemberXmlBuilder(MemberChangeType.UPDATE, modelMap, MemberType.MEMBER);

        String expectedXml2 = """
                <?xml version="1.0" encoding="UTF-8"?>
                <actionDetails tableName="MEMBER" action="UPDATE">
                <id>456</id>
                <incumbent>true</incumbent>
                </actionDetails>
                """;
        assertEquals(expectedXml2, xmlBuilder2.toString());

        modelMap.clear();
        modelMap.put("personId", "123");
        modelMap.put("incumbent", "true");
        modelMap.put("chamber", "Senate");
        modelMap.put("id", "456");

        StringBuilder xmlDeleteBuilder = memberProcessor.getMemberXmlBuilder(MemberChangeType.DELETE, modelMap, MemberType.MEMBER);

        String expectedDeleteXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <actionDetails tableName="MEMBER" action="DELETE">
                <id>456</id>
                </actionDetails>
                """;
        assertEquals(expectedDeleteXml, xmlDeleteBuilder.toString());

        // Re-initialize modelMap for another UPDATE case to check consistency
        modelMap.clear();
        modelMap.put("personId", "123");
        modelMap.put("incumbent", "true");
        modelMap.put("chamber", "Senate");
        modelMap.put("id", "456");

        StringBuilder xmlUpdateBuilder = memberProcessor.getMemberXmlBuilder(MemberChangeType.UPDATE, modelMap, MemberType.MEMBER);

        String expectedUpdateXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <actionDetails tableName="MEMBER" action="UPDATE">
                <id>456</id>
                <incumbent>true</incumbent>
                </actionDetails>
                """;
        assertEquals(expectedUpdateXml, xmlUpdateBuilder.toString());
    }

}
