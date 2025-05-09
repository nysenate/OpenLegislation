package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;
import gov.nysenate.openleg.legislation.member.dao.MemberDao;
import gov.nysenate.openleg.legislation.member.dao.MemberChangeType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class MemberProcessorIT extends BaseTests {
    @Autowired
    private MemberProcessor memberProcessor;
    @Autowired
    private MemberDao memberDao;

    private Set<String> createAttributes = new HashSet<>();

    @Test
    public void testMemberProcessor() throws IOException, SAXException {
        // Step 1: Create Person XmlProcessor
        Path path = Paths.get("src/test/resources/xml.memberchange/createPerson.xml");
        int id = memberProcessor.process(path);

        // Fetch the person record from the database
        Person createdPersonRecord = memberDao.getPerson(id);
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
//        Member createdMemberRecord2 = memberDao.getMember(m_id);
//        assertEquals(createdMemberRecord2.getMemberId(), m_id);
//        assertEquals(707, createdMemberRecord2.getPersonId().intValue());
//        assertEquals("SENATE", (createdMemberRecord2.getChamber()).toString());

        // Fetch session member from the database
        SessionMember createdSessionRecord = memberDao.getSessionMember(s_id);
        assertEquals(468, createdSessionRecord.getMember().getMemberId());
        assertEquals(2023, createdSessionRecord.getSessionYear().year());
        assertEquals(12208, createdSessionRecord.getDistrictCode().intValue());

        // Step 4: Update Person XmlProcessor
        Path personUpdatePath = Paths.get("src/test/resources/xml.memberchange/updatePerson.xml");
        memberProcessor.process(personUpdatePath);

        // Fetch the updated person record
        Person updatedPerson = memberDao.getPerson(283);
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
        Member updatedMember = memberDao.getMember(1115);
        assertTrue(updatedMember.isIncumbent());

        // Step 6: Update Session XmlProcessor
        Path sessionUpdatePath = Paths.get("src/test/resources/xml.memberchange/updateSession.xml");
        memberProcessor.process(sessionUpdatePath);

        // Fetch the updated session record
        SessionMember updatedSessionMember = memberDao.getSessionMember(1); // Assuming 1 is the updated session ID
        assertEquals(1300,updatedSessionMember.getDistrictCode().intValue());
        assertTrue("Alternate status was not updated", updatedSessionMember.isAlternate());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePersonWithNoFirstName() throws IllegalArgumentException, SAXException, IOException {
        //Create Person with no firstName
        Path path2 = Paths.get("src/test/resources/xml.memberchange/personWithNoFirstName.xml");
        memberProcessor.process(path2);
    }

    @Test(expected = MemberNotFoundEx.class)
    public void deleteMember() throws MemberNotFoundEx, IOException, SAXException {
            Path deletePath = Paths.get("src/test/resources/xml.memberchange/deleteSessionMember.xml");
            memberProcessor.process(deletePath);
            memberDao.getMember(1);
            fail("Session record was not deleted, expected exception to be thrown");

            Path deletePath2 = Paths.get("src/test/resources/xml.memberchange/deleteMember.xml");
            memberDao.getMember(632);
            memberProcessor.process(deletePath2);
            fail("Member record was not deleted, expected exception to be thrown");

            Path deletePath3 = Paths.get("src/test/resources/xml.memberchange/deletePerson.xml");
            memberProcessor.process(deletePath3);
            memberDao.getPerson(454);
            fail("Person record was not deleted, expected exception to be thrown");
    }

    @Test
    public void testGetPersonXmlBuilder() {
        var modelMap = new LinkedHashMap<String, String>();
        modelMap.put("firstName", "John");
        modelMap.put("middleName", "M");
        modelMap.put("lastName", "Doe");
        modelMap.put("suffix", "Jr.");
        modelMap.put("email", "john.doe@example.com");
        modelMap.put("imgName", "profile.jpg");

        String xmlString = MemberProcessor.getXmlString(MemberChangeType.CREATE, modelMap, MemberType.PERSON, createAttributes);

        String expectedXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="PERSON" action="CREATE">
            \t<firstName>John</firstName>
            \t<middleName>M</middleName>
            \t<lastName>Doe</lastName>
            \t<suffix>Jr.</suffix>
            \t<email>john.doe@example.com</email>
            \t<imgName>profile.jpg</imgName>
            </actionDetails>
            """;

        assertEquals(expectedXml, xmlString);

        // Test UPDATE action
        var modelMap2 = new LinkedHashMap<String, String>();
        modelMap2.put("id", "123");
        modelMap2.putAll(modelMap);
        modelMap2.put("firstName", "John2");
        modelMap2.put("lastName", "Doe2");

        createAttributes = Set.of("firstName", "lastName");

        xmlString = MemberProcessor.getXmlString(MemberChangeType.UPDATE, modelMap2, MemberType.MEMBER, createAttributes);

        String expectedUpdateXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="MEMBER" action="UPDATE">
            \t<id>123</id>
            \t<firstName action="UPDATE">John2</firstName>
            \t<middleName>M</middleName>
            \t<lastName action="UPDATE">Doe2</lastName>
            \t<suffix>Jr.</suffix>
            \t<email>john.doe@example.com</email>
            \t<imgName>profile.jpg</imgName>
            </actionDetails>
            """;

        assertEquals(expectedUpdateXml, xmlString);

        xmlString = MemberProcessor.getXmlString(MemberChangeType.DELETE, modelMap2, MemberType.MEMBER, Set.of());

        String expectedDeleteXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="MEMBER" action="DELETE">
            \t<id>123</id>
            \t<firstName>John2</firstName>
            \t<middleName>M</middleName>
            \t<lastName>Doe2</lastName>
            \t<suffix>Jr.</suffix>
            \t<email>john.doe@example.com</email>
            \t<imgName>profile.jpg</imgName>
            </actionDetails>
            """;

        assertEquals(expectedDeleteXml, xmlString);
    }

    @Test
    public void testGetSessionXmlBuilder_Create() {
        var modelMap = new HashMap<String, String>();
        modelMap.put("id", "123");
        modelMap.put("memberId", "456");
        modelMap.put("sessionYear", "2025");
        modelMap.put("lbdcShortName", "LB1");
        modelMap.put("districtCode", "101");
        modelMap.put("alternate", "true");

        createAttributes.clear();

        // Test CREATE action
        String xmlString = MemberProcessor.getXmlString(MemberChangeType.CREATE, modelMap, MemberType.SESSION_MEMBER, createAttributes);

        String expectedXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="SESSION_MEMBER" action="CREATE">
                <memberId>456</memberId>
                <sessionYear>2025</sessionYear>
                <lbdcShortName>LB1</lbdcShortName>
                <districtCode>101</districtCode>
                <alternate>true</alternate>
            </actionDetails>
            """;

        assertEquals(expectedXml, xmlString);

        // Test UPDATE action with a different alternate value
        modelMap.put("id", "123");
        modelMap.put("alternate", "false");
        createAttributes.add("alternate");

        xmlString = MemberProcessor.getXmlString(MemberChangeType.UPDATE, modelMap, MemberType.SESSION_MEMBER, createAttributes);

        String expectedUpdateXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="SESSION_MEMBER" action="UPDATE">
            \t<id>123</id>
            \t<memberId>456</memberId>
            \t<sessionYear>2025</sessionYear>
            \t<lbdcShortName>LB1</lbdcShortName>
            \t<districtCode>101</districtCode>
            \t<alternate action="UPDATE">false</alternate>
            </actionDetails>
            """;

        assertEquals(expectedUpdateXml, xmlString);

        createAttributes.clear();

        // Test DELETE action
        xmlString = MemberProcessor.getXmlString(MemberChangeType.DELETE, modelMap, MemberType.SESSION_MEMBER,createAttributes);

        String expectedDeleteXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="SESSION_MEMBER" action="DELETE">
            \t<id>123</id>
            \t<memberId>456</memberId>
            \t<sessionYear>2025</sessionYear>
            \t<lbdcShortName>LB1</lbdcShortName>
            \t<districtCode>101</districtCode>
            \t<alternate>false</alternate>
            </actionDetails>
            """;

        assertEquals(expectedDeleteXml, xmlString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noExpression() throws Exception {
        Path path = Paths.get("src/test/resources/xml.memberchange/noRequiredArgs.xml");
        memberProcessor.process(path);
    }

    @Test
    public void testGetMemberXmlBuilder() {
        HashMap<String, String> modelMap = new HashMap<>();
        modelMap.put("personId", "123");
        modelMap.put("chamber", "Senate");
        modelMap.put("id", "456");

        createAttributes.clear();

        String xmlString = MemberProcessor.getXmlString(MemberChangeType.CREATE, modelMap, MemberType.MEMBER, createAttributes);

        String expectedXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <actionDetails tableName="MEMBER" action="CREATE">
                \t<personId>123</personId>
                \t<chamber>Senate</chamber>
                \t<incumbent>null</incumbent>
                </actionDetails>
                """;
        assertEquals(expectedXml, xmlString);

        modelMap.put("incumbent", "false");
        createAttributes.add("incumbent");

        xmlString = MemberProcessor.getXmlString(MemberChangeType.UPDATE, modelMap, MemberType.MEMBER, createAttributes);

        String expectedXml2 = """
                <?xml version="1.0" encoding="UTF-8"?>
                <actionDetails tableName="MEMBER" action="UPDATE">
                \t<id>456</id>
                \t<personId>123</personId>
                \t<chamber>Senate</chamber>
                \t<incumbent action="UPDATE">false</incumbent>
                </actionDetails>
                """;
        assertEquals(expectedXml2, xmlString);

        createAttributes.clear();

        xmlString = MemberProcessor.getXmlString(MemberChangeType.DELETE, modelMap, MemberType.MEMBER, createAttributes);

        String expectedDeleteXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <actionDetails tableName="MEMBER" action="DELETE">
                \t<id>456</id>
                \t<personId>123</personId>
                \t<chamber>Senate</chamber>
                \t<incumbent>false</incumbent>
                </actionDetails>
                """;
        assertEquals(expectedDeleteXml, xmlString);
    }
}
