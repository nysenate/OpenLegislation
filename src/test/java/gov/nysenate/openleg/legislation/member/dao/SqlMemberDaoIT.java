package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class SqlMemberDaoIT extends BaseTests {
    @Autowired
    private SqlMemberDao memberDao;
    @Autowired
    private SqlMemberDao sqlMemberDao;


    @Test
    public void testHandlePersonChange(){
        //Create Person Record
        Person person = new Person(-1, new PersonName("Doe","","John","Jr" ), "john@gmail.com", "566_John_Doe_Img.jpg");
        MemberChangeType dataType = MemberChangeType.CREATE;
        int createdId = sqlMemberDao.handlePersonChange(dataType, person);
        assertTrue("The person ID should be greater than 0 after creation.",createdId > 0);
        Person createdPerson = sqlMemberDao.getPerson(createdId);
        assertEquals(createdPerson.name().lastName(), person.name().lastName());
        assertEquals(createdPerson.name().firstName(), person.name().firstName());
        assertEquals(createdPerson.name().middleName(), person.name().middleName());
        assertEquals(createdPerson.name().suffix(), person.name().suffix());
        assertEquals(createdPerson.email(), person.email());
        assertEquals(createdPerson.imgName(), person.imgName());

        //Update Person
        Person person2 = new Person(283, new PersonName("Doe","","John","Jr" ), "john@gmail.com", "566_John_Doe_Img.jpg");
        int x = sqlMemberDao.handlePersonChange(MemberChangeType.UPDATE, person2);
        assertTrue("The person ID should be greater than 0 after creation.",createdId > 0);
        Person updatedPerson = sqlMemberDao.getPerson(283);
        assertEquals(updatedPerson.name().lastName(), person.name().lastName());
        assertEquals(updatedPerson.name().firstName(), person.name().firstName());
        assertEquals(updatedPerson.name().middleName(), person.name().middleName());
        assertEquals(updatedPerson.name().suffix(), person.name().suffix());
        assertEquals(updatedPerson.email(), person.email());
        assertEquals(updatedPerson.imgName(), person.imgName());

    }

    @Test
    public void testHandleMemberChange(){

        //Create Member
        Member member = new Member(924,-1, Chamber.SENATE, false);
        int m_id = sqlMemberDao.handleMemberChange(MemberChangeType.CREATE, member);
        Member memberCreated = sqlMemberDao.getMember(m_id);
        assertEquals( Chamber.SENATE, memberCreated.getChamber());
        assertEquals( 924, memberCreated.getPersonId().intValue());

        //Update Member
        Person person = sqlMemberDao.getPerson(454);
        Member updateMember = new Member(person,632, Chamber.ASSEMBLY, true);
        sqlMemberDao.handleMemberChange(MemberChangeType.UPDATE, updateMember);
        FullMember memberUpdated = sqlMemberDao.getMemberById(632);
        assertEquals( Chamber.ASSEMBLY, memberUpdated.getChamber());
        assertEquals( 454, memberUpdated.getPersonId().intValue());
        assertTrue(updateMember.isIncumbent());

        //Delete Session Member first then the Member, to prevent foreign key error
        SessionMember sessionMember = new SessionMember(357,member, "", new SessionYear(2023), 0, false );
        sqlMemberDao.handleSessionMemberChange(MemberChangeType.DELETE , sessionMember);
        try{
            SessionMember s_member = sqlMemberDao.getMemberBySessionId(357);
            fail("Failed to Session Member, Should have thrown an exception");
        }catch(MemberNotFoundEx ex){
            assertEquals("Member with session member id of " + 357 + " was not found.", ex.getMessage());

        }

       //Delete Member
       sqlMemberDao.handleMemberChange(MemberChangeType.DELETE, updateMember);
       try {
           Member d_member = sqlMemberDao.getMember(updateMember.getMemberId());
           fail("Failed to delete Member, Should have thrown an exception");
       }catch (Exception e){
           assertEquals("Member with id: " + updateMember.getMemberId() + " was not found!", e.getMessage());
       }

       person = sqlMemberDao.getPerson(454);
       sqlMemberDao.handlePersonChange(MemberChangeType.DELETE, person);
       try {
            Person p_member = sqlMemberDao.getPerson(454);
            fail("Failed to delete Person, Should have thrown an exception");
       }catch (NoSuchElementException e){
           assertEquals("Person with ID " + 454 + " does not exist.", e.getMessage());
       }

    }

    @Test
    public void testHandleSessionMemberChange(){

        //Create Session Member
        Member member = sqlMemberDao.getMemberById(632);
        SessionMember sessionMember = new SessionMember(-1, 632, "NEW",  new SessionYear(2024), 12207,false);
        int s_id = sqlMemberDao.handleSessionMemberChange(MemberChangeType.CREATE, sessionMember);
        SessionMember createdSessionMember = sqlMemberDao.getMemberBySessionId(s_id);
        assertEquals(member.getMemberId(), createdSessionMember.getMember().getMemberId());
        assertEquals(2023, createdSessionMember.getSessionYear().year());
        assertEquals("NEW", createdSessionMember.getLbdcShortName());
        assertEquals(12207, createdSessionMember.getDistrictCode().intValue());

        //Update Session Member
        SessionMember updateSessionMember = new SessionMember(357, 632, "",  new SessionYear(2024), 1300,true);
        sqlMemberDao.handleSessionMemberChange(MemberChangeType.UPDATE, updateSessionMember);
        SessionMember updatedSessionMember2 = sqlMemberDao.getMemberBySessionId(357);
        assertEquals(632, updatedSessionMember2.getMember().getMemberId());
        assertEquals(2023, updatedSessionMember2.getSessionYear().year());
        assertEquals(1300, updatedSessionMember2.getDistrictCode().intValue());
        assertTrue(updateSessionMember.isAlternate());

    }

    @Test
    public void testGetMemberBySessionMemberId() {
        final int sessionMemberId = 306;
        final String shortName = "PEOPLES-STOKES";
        SessionMember member = memberDao.getMemberBySessionId(sessionMemberId);
        assertNotNull(member);
        assertEquals(sessionMemberId, member.getSessionMemberId());
        assertEquals(shortName, member.getLbdcShortName());
    }

}
