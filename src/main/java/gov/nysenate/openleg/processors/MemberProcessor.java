package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.common.util.XmlHelper;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.OpenLegCacheManager;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.*;
import gov.nysenate.openleg.legislation.member.dao.MemberChangeType;
import gov.nysenate.openleg.legislation.member.dao.MemberDao;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;

// TODO: problem is, need order. Take in as List, perhaps?
@Service
public class MemberProcessor implements LegDataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MemberProcessor.class);
    private static final String header = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="%s" action="%s">
            """;
    private final MemberDao memberDao;
    private final XmlHelper xmlHelper;

    @Autowired
    public MemberProcessor(MemberDao memberDao, XmlHelper xmlHelper) {
        this.memberDao = memberDao;
        this.xmlHelper = xmlHelper;
    }

    public static String getXmlString(MemberChangeType changeType, Map<String, String> modelMap,
                                      MemberType memberTable, Set<String> changedAttributes) {
        var xmlBuilder = new StringBuilder(header.formatted(memberTable.name(), changeType.name()));
        for (String key : modelMap.keySet()) {
            final String label = changedAttributes.contains(key) ? " \"UPDATE\"" : "";
            xmlBuilder.append("\t<%s%s>%s</%s>\n".formatted(key, label, modelMap.get(key), key));
        }
        return xmlBuilder.append("</actionDetails>\n").toString();
    }

    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.MEMBER;
    }

    @Override
    public void process(LegDataFragment fragment) {
        try {
            process(fragment.getParentLegDataFile().getFile().toPath());
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        OpenLegCacheManager.clearCaches(Set.of(CacheType.SHORTNAME, CacheType.SESSION_MEMBER, CacheType.FULL_MEMBER),
                true);
    }

    public int process(Path path) throws IOException, SAXException {
        try {
            File file = path.toFile();
            Document document = xmlHelper.parse(file);
            Node rootNode = document.getDocumentElement();
            MemberType memberType = MemberType.valueOf(xmlHelper.getString("@tableName", rootNode).toUpperCase());
            MemberChangeType action = MemberChangeType.valueOf(xmlHelper.getString("@action", rootNode));

            switch (memberType) {
                case PERSON -> {
                    return handlePerson(action, rootNode);
                }
                case MEMBER -> {
                    return handleMember(action, rootNode);
                }
                case SESSION_MEMBER -> {
                    return handleSessionMember(action, rootNode);
                }
                default -> throw new IllegalArgumentException("Unrecognized table enum");
            }

        } catch (NullPointerException | XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private int handlePerson(MemberChangeType action, Node rootNode) throws IllegalArgumentException, XPathExpressionException {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final String firstName = xmlHelper.getString("firstName", rootNode);
        final String middleName = xmlHelper.getString("middleName", rootNode);
        final String lastName = xmlHelper.getString("lastName", rootNode);
        final String suffix = xmlHelper.getString("suffix", rootNode);
        final String email = xmlHelper.getString("email", rootNode);
        final String imgName = xmlHelper.getString("imgName", rootNode);

        if (MemberChangeType.CREATE == action && (firstName == null || lastName == null)) {
            logger.error("Missing required attribute FirstName or LastName");
            throw new IllegalArgumentException("Missing required attribute FirstName or LastName");
        }
        var personName = new PersonName("", firstName, middleName, lastName, suffix);
        var person = new Person(id, personName, email, imgName);

        return memberDao.handlePersonChange(action, person);

    }

    private int handleMember(MemberChangeType action, Node rootNode) throws XPathExpressionException {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final int personId = xmlHelper.getInteger("personId", rootNode);
        final String chamber = xmlHelper.getString("chamber", rootNode);
        final Boolean incumbent = xmlHelper.getBoolean("incumbent", rootNode);

        var member = new Member(personId, id, Chamber.valueOf(chamber.toUpperCase()), incumbent);
        return memberDao.handleMemberChange(action, member);
    }

    private int handleSessionMember(MemberChangeType action, Node rootNode) throws XPathExpressionException {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final int memberId = xmlHelper.getInteger("memberId", rootNode);
        final String lbdcShortName = xmlHelper.getString("lbdcShortName", rootNode);
        final int districtCode = xmlHelper.getInteger("districtCode", rootNode);
        final Boolean alternate = xmlHelper.getBoolean("alternate", rootNode);
        final int sessionYear = xmlHelper.getInteger("sessionYear", rootNode);

        var sessionMember = new SessionMember(id, memberId, lbdcShortName, new SessionYear(sessionYear), districtCode, alternate);
        return memberDao.handleSessionMemberChange(action, sessionMember);
    }
}

