package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
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

@Category(SillyTest.class)
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
    public void testProcessMember() throws IOException, SAXException {
        // Insert test file path as needed. Then, you can use memberService to retrieve and test the data.
        // I recommend creating test files in "src/test/resources" under a new "members" folder.
        memberProcessor.process(null);
    }

    @Test
    public void testMemberProcessor() throws IOException, SAXException {

        //Create Person XmlProcessor
        Path path = Paths.get("/home/nystech/Desktop/Createperson.xml");
        int id = memberProcessor.process(path);
        Person createdPersonRecord =memberDao.getPersonByPersonId(id);
        System.out.println("Created Person id\t" + createdPersonRecord.email());

        //Create Member XmlProcessor
        Path memberPath = Paths.get("/home/nystech/Desktop/Createmember.xml");
        int m_id = memberProcessor.process(memberPath);
        System.out.println("Created Member id\t" + m_id);

        //Create Session XmlProcessor
        Path sessionPath = Paths.get("/home/nystech/Desktop/Createsessionmember.xml");
        int s_id = memberProcessor.process(sessionPath);
        SessionMember createdSessionRecord  = memberDao.getMemberBySessionId(s_id);
        System.out.println("Created Session id\t" + createdSessionRecord.getSessionYear());

        FullMember createdMemberRecord2 = memberDao.getMemberById(m_id);
        System.out.println("Created Member id\t" + createdMemberRecord2.getMemberId());

        //Update Person XmlProcessor
        Path personUpdatePath = Paths.get("/home/nystech/Desktop/Updateperson.xml");
        memberProcessor.process(personUpdatePath);
        Person updatedPerson = memberDao.getPersonByPersonId(283);
        System.out.print("UpdatedPerosn" + updatedPerson.name() + updatedPerson.email());

        //Update Member XmlProcessor
        Path memberUpdatePath = Paths.get("/home/nystech/Desktop/Updatemember.xml");
        memberProcessor.process(memberUpdatePath);
        FullMember updatedMember = sqlMemberDao.getMemberById(1115);
        System.out.println("Incumbent"+ updatedMember.isIncumbent());

        //Update Session XmlProcessor
        Path sessionUpdatePath = Paths.get("/home/nystech/Desktop/UpdateSession.xml");
        memberProcessor.process(sessionUpdatePath);
        SessionMember updatedSessionMember = sqlMemberDao.getMemberBySessionId(1);
        System.out.println("Member details after updation\n" + "Updated member Chamber, Incumbent" + "\t"+ updatedSessionMember.getDistrictCode() +  updatedSessionMember.isAlternate());

        //Delete XML parser
        Path deletePath = Paths.get("/home/nystech/Desktop/Delete.xml");
        memberProcessor.process(deletePath);

    }


    @Test
    public void testMember(){

        //Create Person Record
        Person person = new Person(-1,new PersonName("NikhithaBi", "x", "Nikhitha","B","L","Jr" ), "bijjanikhitha@", "");
        int p_id = sqlMemberDao.handlePersonChange(MemberChangeType.CREATE, person);
        System.out.println("ID OF CREATED PERSON RECORD\n" + p_id);
        Person person2 = new Person(p_id,new PersonName("NikhithaBi", "x", "Nikhitha","B","L","Jr" ), "bijjanikhitha@", "");

        //Create Member Record
        Member member = new Member(person2,-1, Chamber.SENATE, false);
        int m_id = sqlMemberDao.handleMemberChange(MemberChangeType.CREATE, member);
        System.out.println("ID OF CREATED MER RECORD\n" + m_id);

        //Create Session Record
        Member member2 = new Member(person2,m_id, Chamber.SENATE, false);
        SessionMember sessionMember = new SessionMember(-1, member2, "",  new SessionYear(2024), 12207,false);
        int s_id = sqlMemberDao.handleSessionMemberChange(MemberChangeType.CREATE, sessionMember);
        System.out.println("ID OF CREATED SESSION RECORD\n" + s_id);

        SessionMember existingRecord = sqlMemberDao.getMemberBySessionId(s_id);

        // Person Record Update
        Person updatedPerson = new Person(p_id,new PersonName("NikhithaBijjala", "", "Nikhitha","","Bijjala","" ), "nbijjala@senate.gov", "");
        sqlMemberDao.handlePersonChange(MemberChangeType.UPDATE, updatedPerson);


        //Member Record Update
        Member updatedMember = new Member(updatedPerson,m_id, Chamber.ASSEMBLY, true);
        sqlMemberDao.handleMemberChange(MemberChangeType.UPDATE, updatedMember);

        //Session Record Update
        SessionMember updateSessionMember = new SessionMember(s_id, updatedMember, "",  new SessionYear(2024), 12208,true);
        sqlMemberDao.handleSessionMemberChange(MemberChangeType.UPDATE, updateSessionMember);

        SessionMember y = sqlMemberDao.getMemberBySessionId(s_id);
        System.out.println("Member details after updation\n" + "Updated member Chamber, Incumbent" + "\t" +y.getMember().getChamber() + "\t" + y.getMember().isIncumbent() +"\nUpdated Person name and email\t"+ y.getMember().getPerson().name()+ "\t"+y.getMember().getPerson().email()
                + "\nUpdated Session Member lbdc district code and alternatee \t"+ y.getSessionYear()  + "\t" + y.getDistrictCode() + "\t" + y.isAlternate());

        sqlMemberDao.handleSessionMemberChange(MemberChangeType.DELETE, updateSessionMember);
        try{
            SessionMember z = sqlMemberDao.getMemberBySessionId(s_id);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        sqlMemberDao.handleMemberChange(MemberChangeType.DELETE, updatedMember);
        try{
            FullMember z = sqlMemberDao.getMemberById(m_id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        sqlMemberDao.handlePersonChange(MemberChangeType.DELETE, updatedPerson);

    }

}
