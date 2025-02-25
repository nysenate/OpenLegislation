package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.common.util.XmlHelper;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.*;
import gov.nysenate.openleg.legislation.member.dao.MemberChangeType;
import gov.nysenate.openleg.legislation.member.dao.MemberDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.w3c.dom.Node;

@Service
public class MemberProcessor extends AbstractDataProcessor {
    private final MemberDao memberDao;
    private final XmlHelper xmlHelper;
    private static final Logger logger = LoggerFactory.getLogger(MemberProcessor.class);

    @Autowired
    public MemberProcessor(MemberDao memberDao, XmlHelper xmlHelper) {
        this.memberDao = memberDao;
        this.xmlHelper = xmlHelper;
    }

    public int handlePerson(MemberChangeType action, Node rootNode) {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final String firstName = xmlHelper.getStringSafe("firstName", rootNode);
        final String lastName = xmlHelper.getStringSafe("lastName", rootNode);
        final String email = xmlHelper.getStringSafe("email", rootNode);
        final String middleName = xmlHelper.getStringSafe("middleName", rootNode);
        final String suffix = xmlHelper.getStringSafe("suffix", rootNode);
        final String imgName = xmlHelper.getStringSafe("imgName", rootNode);

        if ("CREATE".equals(action) && (firstName == null || lastName == null)) {
            logger.error("Missing required attribute FirstName or LastName");
            return 0;
        }
        Person existingRecord = null;
        if (id != -1) {
             existingRecord = (action.equals("UPDATE")) ? memberDao.getPersonByPersonId(id) : null;
        }
        Person person = new Person(id,
                new PersonName("","",
                        firstName != null ? firstName : (existingRecord != null ? existingRecord.name().firstName() : ""),
                        middleName != null ? middleName : (existingRecord != null ? existingRecord.name().middleName() : ""),
                        lastName != null ? lastName : (existingRecord != null ? existingRecord.name().lastName() : ""),
                        suffix != null ? suffix : (existingRecord != null ? existingRecord.name().suffix() : "")
                ),
                email != null ? email : (existingRecord != null ? existingRecord.email() : ""),
                imgName != null ? imgName : (existingRecord != null ? existingRecord.imgName() : "")
        );

        switch (action) {
            case CREATE:
                if (firstName == null || lastName == null) {
                    logger.error("Missing required attribute FirstName or LastName");
                }
                return memberDao.handlePersonChange(MemberChangeType.CREATE, person);
            case UPDATE:
                return memberDao.handlePersonChange(MemberChangeType.UPDATE, person);
            case DELETE:
                return memberDao.handlePersonChange(MemberChangeType.DELETE, person);
            default:
                return 0;
        }
    }


    public int handleMember(MemberChangeType action, Node rootNode) {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final Integer personId = xmlHelper.getIntegerSafe("personId", rootNode);
        final String chamber = xmlHelper.getStringSafe("chamber", rootNode);
        final boolean incumbent = xmlHelper.getBooleanSafe("incumbent", rootNode);

        Member member = null;
        FullMember existingRecord = null;
        if (id > 0){
             existingRecord = memberDao.getMemberById(id);
        }

        switch (action) {
            case CREATE:
                if (personId == -1 || chamber == null) {
                    logger.error("Missing required attribute PersonId | Chamber");
                }
                member = new Member(personId, id, Chamber.valueOf(chamber), incumbent);
                return memberDao.handleMemberChange(MemberChangeType.CREATE, member);
            case UPDATE:
                member = new Member(existingRecord.getPerson(),id, existingRecord.getChamber(), incumbent);
                return memberDao.handleMemberChange(MemberChangeType.UPDATE, member);
            case DELETE:
                member = new Member(id, member.getPerson().personId(),  existingRecord.getChamber(), incumbent);
                return memberDao.handleMemberChange(MemberChangeType.DELETE, member);
        }
        return 0;
    }



    public int handleSessionMember(MemberChangeType action, Node rootNode) {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final Integer memberId = xmlHelper.getIntegerSafe("memberId", rootNode);
        final String lbdcShortName = xmlHelper.getStringSafe("lbdcShortName", rootNode);
        final Integer districtCode = xmlHelper.getIntegerSafe("districtCode", rootNode);
        final Boolean alternate = xmlHelper.getBooleanSafe("alternate", rootNode);
        final Integer sessionYear = xmlHelper.getIntegerSafe("sessionYear", rootNode);

        SessionMember sessionMember = null;

        switch (action) {
            case CREATE:
                if (memberId == -1 || lbdcShortName == null || sessionYear == -1 || districtCode == -1) {
                    logger.error("Missing required attributes: MemberId, lbdcShortName, sessionYear, or districtCode");
                }
                sessionMember = new SessionMember(id, memberId, lbdcShortName, new SessionYear(sessionYear), districtCode, alternate);
                return memberDao.handleSessionMemberChange(MemberChangeType.CREATE, sessionMember);
            case UPDATE:
                SessionMember existingRecord = memberDao.getMemberBySessionId(id);
                if (existingRecord == null) {
                    logger.error("No existing session member found for id: {}", id);
                }
                sessionMember = new SessionMember(id, existingRecord.getMember(), existingRecord.getLbdcShortName(), existingRecord.getSessionYear(),
                        districtCode != -1 ? districtCode : existingRecord.getDistrictCode(), alternate != null ? alternate : existingRecord.isAlternate());
                return memberDao.handleSessionMemberChange(MemberChangeType.UPDATE, sessionMember);
            case DELETE:
                sessionMember = new SessionMember(id, -1, "", new SessionYear(0), 0, false);
                return memberDao.handleSessionMemberChange(MemberChangeType.DELETE, sessionMember);
    }
        return 0;
    }

    public int process(Path path) throws IOException, SAXException {
        try{
            File file = path.toFile();
            Document document = xmlHelper.parse(file);
            Node rootNode = document.getDocumentElement();
            final String tableName = xmlHelper.getStringSafe("@tableName", rootNode);
            MemberType memberType = MemberType.getMemberType(tableName);
            MemberChangeType action = MemberChangeType.valueOf(xmlHelper.getStringSafe("@action", rootNode));
            if (memberType == null) {
                logger.error("Invalid action type: {}", action);
                return 0;
            }
            switch (memberType) {
                case PERSON:
                    return handlePerson(action, rootNode);
                case MEMBER:
                    return handleMember(action, rootNode);
                case SESSION:
                    return handleSessionMember(action, rootNode);
                default:
                    logger.error("Unhandled action: {}", action);
            }

        } catch (Exception e) {
            logger.error("Error While Parsing MemberProcessorXML", e);
        }
        return 0;
    }
}
