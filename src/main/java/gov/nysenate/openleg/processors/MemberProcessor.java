package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.common.util.XmlHelper;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;
import gov.nysenate.openleg.legislation.member.dao.MemberChangeType;
import gov.nysenate.openleg.legislation.member.dao.MemberDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;

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

    public int handlePerson(MemberChangeType action, Node rootNode) throws IllegalArgumentException {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final String firstName = xmlHelper.getStringSafe("firstName", rootNode);
        final String lastName = xmlHelper.getStringSafe("lastName", rootNode);
        final String email = xmlHelper.getStringSafe("email", rootNode);
        final String middleName = StringUtils.defaultIfEmpty(xmlHelper.getStringSafe("middleName", rootNode),"");
        final String suffix = StringUtils.defaultIfEmpty(xmlHelper.getStringSafe("suffix", rootNode),"");
        final String imgName = xmlHelper.getStringSafe("imgName", rootNode);

        if ((MemberChangeType.CREATE == action) && (firstName == null || lastName == null)) {
            logger.error("Missing required attribute FirstName or LastName");
            throw new IllegalArgumentException("Missing required attribute FirstName or LastName");
        }
        Person existingRecord = null;
        if (id != null) {
            existingRecord = (action == MemberChangeType.UPDATE) ? memberDao.getPersonByPersonId(id) : null;
        }
        Person person = new Person(id,
                new PersonName("", "",
                        firstName != null ? firstName : (existingRecord != null ? existingRecord.name().firstName() : ""),
                        middleName != null ? middleName : (existingRecord != null ? existingRecord.name().middleName() : ""),
                        lastName != null ? lastName : (existingRecord != null ? existingRecord.name().lastName() : ""),
                        suffix != null ? suffix : (existingRecord != null ? existingRecord.name().suffix() : "")
                ),
                email != null ? email : (existingRecord != null ? existingRecord.email() : ""),
                imgName != null ? imgName : (existingRecord != null ? existingRecord.imgName() : "")
        );

        return switch (action) {
            case CREATE -> {
                yield memberDao.handlePersonChange(MemberChangeType.CREATE, person);
            }
            case UPDATE -> {
                if (id == null) {
                    throw new MemberNotFoundEx();
                }
                yield memberDao.handlePersonChange(MemberChangeType.UPDATE, person);
            }
            case DELETE -> {
                if (id == null) {
                    throw new MemberNotFoundEx();
                }
                yield memberDao.handlePersonChange(MemberChangeType.DELETE, person);
            }
        };
    }

    public int handleMember(MemberChangeType action, Node rootNode) {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final Integer personId = xmlHelper.getIntegerSafe("personId", rootNode);
        final String chamber = xmlHelper.getStringSafe("chamber", rootNode);
        final Boolean incumbent = xmlHelper.getBooleanSafe("incumbent", rootNode);

        Member member = null;
        FullMember existingRecord = null;
        if (id != null) {
            existingRecord = memberDao.getMemberById(id);
        }

        switch (action) {
            case CREATE:
                if (personId == null || chamber == null) {
                    logger.error("Missing required attribute PersonId | Chamber");
                }
                member = new Member(personId, -1, Chamber.valueOf(chamber), incumbent);
                return memberDao.handleMemberChange(MemberChangeType.CREATE, member);
            case UPDATE:
                if (existingRecord == null) {
                    throw new MemberNotFoundEx();
                }
                member = new Member(existingRecord.getPerson(), id, existingRecord.getChamber(), incumbent);
                return memberDao.handleMemberChange(MemberChangeType.UPDATE, member);
            case DELETE:
                if (id == null) {
                    throw new MemberNotFoundEx();
                }
                member = new Member(-1, id,Chamber.valueOf("SENATE"), false);
                return memberDao.handleMemberChange(MemberChangeType.DELETE, member);
        }
        return 0;
    }


    public int handleSessionMember(MemberChangeType action, Node rootNode) throws XPathExpressionException {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final Integer memberId = xmlHelper.getIntegerSafe("memberId", rootNode);
        final String lbdcShortName = xmlHelper.getStringSafe("lbdcShortName", rootNode);
        final Integer districtCode = xmlHelper.getIntegerSafe("districtCode", rootNode);
        final Boolean alternate = xmlHelper.getBooleanSafe("alternate", rootNode);
        final Integer sessionYear = xmlHelper.getIntegerSafe("sessionYear", rootNode);

        SessionMember sessionMember = null;

        switch (action) {
            case CREATE:
                if (memberId == null || lbdcShortName == null || sessionYear == -1 || districtCode == -1) {
                    logger.error("Missing required attributes: MemberId, lbdcShortName, sessionYear, or districtCode");
                    throw new MemberNotFoundEx();
                }
                FullMember member = memberDao.getMemberById(memberId);
                sessionMember = new SessionMember(-1, member, lbdcShortName, new SessionYear(sessionYear), districtCode, alternate);
                return memberDao.handleSessionMemberChange(MemberChangeType.CREATE, sessionMember);
            case UPDATE:
                SessionMember existingRecord = memberDao.getMemberBySessionId(id);
                if (existingRecord == null) {
                    logger.error("No existing session member found for id: {}", id);
                    throw new MemberNotFoundEx();
                }
                sessionMember = new SessionMember(id, existingRecord.getMember(), existingRecord.getLbdcShortName(), existingRecord.getSessionYear(),
                        districtCode != null? districtCode : existingRecord.getDistrictCode(), alternate != null ? alternate : existingRecord.isAlternate());
                return memberDao.handleSessionMemberChange(MemberChangeType.UPDATE, sessionMember);
            case DELETE:
                sessionMember = new SessionMember(id, -1, "", new SessionYear(2), 0, false);
                return memberDao.handleSessionMemberChange(MemberChangeType.DELETE, sessionMember);}
        return 0;
    }

    public int process(Path path) throws IOException, SAXException {
        try{
            File file = path.toFile();
            Document document = xmlHelper.parse(file);
            Node rootNode = document.getDocumentElement();
            final String tableName = xmlHelper.getString("@tableName", rootNode);
            MemberType memberType = MemberType.getMemberType(tableName);
            MemberChangeType action = MemberChangeType.valueOf(xmlHelper.getString("@action", rootNode));
            if (memberType == null) {
                logger.error("Invalid action type: {}", action);
            }
            switch (Objects.requireNonNull(memberType)) {
                case PERSON:
                    return handlePerson(action, rootNode);
                case MEMBER:
                    return handleMember(action, rootNode);
                case SESSION:
                    return handleSessionMember(action, rootNode);
                default:
                    logger.error("Unhandled action: {}", action);
            }

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            logger.error("Error While Parsing MemberProcessorXML", e);
            throw new SAXException(e);
        }
        return 0;
    }

    public StringBuilder getMemberXmlBuilder(MemberChangeType changeType, Map<String, String> modelMap, MemberType memberTable) {
        String personIdStr = modelMap.get("personId");
        String incumbentStr = modelMap.get("incumbent");
        String chamber = modelMap.get("chamber");
        String memberIdStr = modelMap.get("id");

        Integer personId = (personIdStr != null && !personIdStr.equals("null")) ? Integer.valueOf(personIdStr) : null;
        Boolean incumbent = (incumbentStr != null && !incumbentStr.equals("null")) ? Boolean.valueOf(incumbentStr) : null;
        Integer memberId = (memberIdStr != null && !memberIdStr.equals("null")) ? Integer.valueOf(memberIdStr) : null;

        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<actionDetails tableName=\"").append(memberTable).append("\" action=\"").append(changeType.name()).append("\">\n");

        if (changeType == MemberChangeType.CREATE) {
            xmlBuilder.append("<personId>").append(personId).append("</personId>\n")
                    .append("<chamber>").append(chamber).append("</chamber>\n");
        } else if (changeType == MemberChangeType.UPDATE) {
            xmlBuilder.append("<id>").append(memberId).append("</id>\n")
                    .append("<incumbent>").append(incumbent).append("</incumbent>\n");
        } else if (changeType == MemberChangeType.DELETE) {
            xmlBuilder.append("<id>").append(memberId).append("</id>\n");
        }

        xmlBuilder.append("</actionDetails>\n");
        return xmlBuilder;
    }


    public StringBuilder getPersonXmlBuilder(MemberChangeType changeType, Map<String, String> modelMap, MemberType memberTable) {
        String personIdStr = modelMap.get("id");
        String firstName = modelMap.get("firstName");
        String lastName = modelMap.get("lastName");
        String email = modelMap.get("email");
        String middleName = modelMap.get("middleName");
        String imgName = modelMap.get("imgName");
        String suffix = modelMap.get("suffix");

        Integer personId = (personIdStr != null && !personIdStr.equals("null")) ? Integer.valueOf(personIdStr) : null;

        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<actionDetails tableName=\"").append(memberTable).append("\" action=\"").append(changeType.name()).append("\">\n");

        if (changeType == MemberChangeType.CREATE) {
            xmlBuilder.append("<firstName>").append(firstName).append("</firstName>\n")
                    .append("<lastName>").append(lastName).append("</lastName>\n")
                    .append(middleName != null ? "<middleName>" + middleName + "</middleName>\n" : "")
                    .append(suffix != null ? "<suffix>" + suffix + "</suffix>\n" : "")
                    .append(email != null ? "<email>" + email + "</email>\n" : "")
                    .append(imgName != null ? "<imgName>" + imgName + "</imgName>\n" : "");

        } else if (changeType == MemberChangeType.UPDATE) {
            xmlBuilder.append("<id>").append(personId).append("</id>\n")
                    .append(firstName != null ? "<firstName>" + firstName + "</firstName>\n" : "")
                    .append(lastName != null ? "<lastName>" + lastName + "</lastName>\n" : "")
                    .append(middleName != null ? "<middleName>" + middleName + "</middleName>\n" : "")
                    .append(suffix != null ? "<suffix>" + suffix + "</suffix>\n" : "")
                    .append(email != null ? "<email>" + email + "</email>\n" : "")
                    .append(imgName != null ? "<imgName>" + imgName + "</imgName>\n" : "");

        } else if (changeType == MemberChangeType.DELETE) {
            xmlBuilder.append("<id>").append(personId).append("</id>\n");
        }

        xmlBuilder.append("</actionDetails>");
        return xmlBuilder;
    }


    public StringBuilder getSessionXmlBuilder(MemberChangeType changeType, Map<String, String> modelMap, MemberType memberTable) {
        String idStr = modelMap.get("id");
        String memberIdStr = modelMap.get("memberId");
        String sessionYearStr = modelMap.get("sessionYear");
        String lbdcShortName = modelMap.get("lbdcShortName");
        String districtCodeStr = modelMap.get("districtCode");
        String alternateStr = modelMap.get("alternate");

        Integer id = (idStr != null && !idStr.equals("null")) ? Integer.valueOf(idStr) : null;
        Integer memberId = (memberIdStr != null && !memberIdStr.equals("null")) ? Integer.valueOf(memberIdStr) : null;
        Integer sessionYear = (sessionYearStr != null && !sessionYearStr.equals("null")) ? Integer.valueOf(sessionYearStr) : null;
        Integer districtCode = (districtCodeStr != null && !districtCodeStr.equals("null")) ? Integer.valueOf(districtCodeStr) : null;
        Boolean alternate = (alternateStr != null && !alternateStr.equals("null")) ? Boolean.valueOf(alternateStr) : null;

        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<actionDetails tableName=\"").append(memberTable).append("\" action=\"").append(changeType.name()).append("\">\n");

        if (changeType == MemberChangeType.CREATE) {
            xmlBuilder.append("<memberId>").append(memberId).append("</memberId>\n")
                    .append("<sessionYear>").append(sessionYear).append("</sessionYear>\n")
                    .append("<lbdcShortName>").append(lbdcShortName).append("</lbdcShortName>\n")
                    .append("<districtCode>").append(districtCode).append("</districtCode>\n");
        } else if (changeType == MemberChangeType.UPDATE) {
            xmlBuilder.append("<id>").append(id).append("</id>\n")
                    .append(alternate != null ? "<alternate>" + alternate + "</alternate>\n" : "")
                    .append(districtCode != null ? "<districtCode>" + districtCode + "</districtCode>\n" : "");
        } else if (changeType == MemberChangeType.DELETE) {
            xmlBuilder.append("<id>").append(id).append("</id>\n");
        }

        xmlBuilder.append("</actionDetails>");
        return xmlBuilder;
    }

}

