package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.common.util.XmlHelper;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.OpenLegCacheManager;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;
import gov.nysenate.openleg.legislation.member.dao.MemberChangeType;
import gov.nysenate.openleg.legislation.member.dao.MemberDao;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import org.apache.commons.lang3.StringUtils;
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

@Service
public class MemberProcessor implements LegDataProcessor {
    private static final String header = """
            <?xml version="1.0" encoding="UTF-8"?>
            <actionDetails tableName="%s" action="%s">
            """;
    private final MemberDao memberDao;
    private final XmlHelper xmlHelper;
    private static final Logger logger = LoggerFactory.getLogger(MemberProcessor.class);

    @Autowired
    public MemberProcessor(MemberDao memberDao, XmlHelper xmlHelper) {
        this.memberDao = memberDao;
        this.xmlHelper = xmlHelper;
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

    public int handlePerson(MemberChangeType action, Node rootNode) throws IllegalArgumentException {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final String firstName = xmlHelper.getStringSafe("firstName", rootNode);
        final String lastName = xmlHelper.getStringSafe("lastName", rootNode);
        final String email = xmlHelper.getStringSafe("email", rootNode);
        final String middleName = StringUtils.defaultIfEmpty(xmlHelper.getStringSafe("middleName", rootNode),"");
        final String suffix = StringUtils.defaultIfEmpty(xmlHelper.getStringSafe("suffix", rootNode),"");
        final String imgName = xmlHelper.getStringSafe("imgName", rootNode);

        if (MemberChangeType.CREATE == action && (firstName == null || lastName == null)) {
            logger.error("Missing required attribute FirstName or LastName");
            throw new IllegalArgumentException("Missing required attribute FirstName or LastName");
        }
        Person person = new Person(id,
                new PersonName("", "", firstName, middleName, lastName, suffix ), email, imgName);

        return memberDao.handlePersonChange(action, person);

    }

    public int handleMember(MemberChangeType action, Node rootNode) {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final Integer personId = xmlHelper.getIntegerSafe("personId", rootNode);
        final String chamber = xmlHelper.getStringSafe("chamber", rootNode);
        final Boolean incumbent = xmlHelper.getBooleanSafe("incumbent", rootNode);

        Member member = new Member(personId, id, Chamber.valueOf(chamber.toUpperCase()), incumbent);

        if ( MemberChangeType.CREATE == action && (personId == null || chamber == null)) {
            logger.error("Missing required attribute PersonId | Chamber");
        }
        return  memberDao.handleMemberChange(action, member);
    }

    public int handleSessionMember(MemberChangeType action, Node rootNode) {
        final Integer id = xmlHelper.getIntegerSafe("id", rootNode);
        final Integer memberId = xmlHelper.getIntegerSafe("memberId", rootNode);
        final String lbdcShortName = xmlHelper.getStringSafe("lbdcShortName", rootNode);
        final Integer districtCode = xmlHelper.getIntegerSafe("districtCode", rootNode);
        final Boolean alternate = xmlHelper.getBooleanSafe("alternate", rootNode);
        final Integer sessionYear = xmlHelper.getIntegerSafe("sessionYear", rootNode);

        if( MemberChangeType.CREATE == action && (memberId == null || lbdcShortName == null || sessionYear == -1 || districtCode == -1)) {
            logger.error("Missing required attributes: MemberId, lbdcShortName, sessionYear, or districtCode");
            throw new MemberNotFoundEx();
        }
        SessionMember sessionMember = new SessionMember(id, memberId, lbdcShortName, new SessionYear(sessionYear), districtCode, alternate);
        return memberDao.handleSessionMemberChange(action, sessionMember);
    }

    public int process(Path path) throws IOException, SAXException {
        try {
            File file = path.toFile();
            Document document = xmlHelper.parse(file);
            Node rootNode = document.getDocumentElement();
            final String tableName = xmlHelper.getString("@tableName", rootNode);
            MemberType memberType = MemberType.getMemberType(tableName);
            MemberChangeType action = MemberChangeType.valueOf(xmlHelper.getString("@action", rootNode));

            switch (Objects.requireNonNull(memberType)) {
                case PERSON -> {
                    return handlePerson(action, rootNode);
                }
                case MEMBER -> {
                    return handleMember(action, rootNode);
                }
                case SESSION_MEMBER -> {
                    return handleSessionMember(action, rootNode);
                }
                default -> logger.error("Unhandled action: {}", action);
            }

        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
        return 0;
    }

    public StringBuilder getMemberXmlBuilder(MemberChangeType changeType, HashMap<String, String> modelMap, MemberType memberTable, HashSet<String> changedAttributes) {
        var xmlBuilder = new StringBuilder(header.formatted(memberTable.name(), changeType.name()));
        List<String> mappingNames = new ArrayList<>();
        if(changeType != MemberChangeType.CREATE) {
            mappingNames.add("id");
        }
        mappingNames.addAll(List.of("personId", "chamber", "incumbent"));
        return xmlBuilder.append(getXmlFragment(mappingNames, modelMap, changedAttributes)).append("</actionDetails>\n");
    }

    public StringBuilder getPersonXmlBuilder(MemberChangeType changeType, HashMap<String, String> modelMap, MemberType memberTable, HashSet<String> changedAttributes) {
        var xmlBuilder = new StringBuilder(header.formatted(memberTable.name(), changeType.name()));
        List<String> mappingNames = new ArrayList<>();
        if(changeType != MemberChangeType.CREATE) {
            mappingNames.add("id");
        }
        mappingNames.addAll(List.of("firstName", "lastName","middleName", "suffix", "email", "imgName"));
        return xmlBuilder.append(getXmlFragment(mappingNames, modelMap, changedAttributes)).append("</actionDetails>\n");

    }

    public StringBuilder getSessionXmlBuilder(MemberChangeType changeType, HashMap<String, String> modelMap, MemberType memberTable, HashSet<String> changedAttributes) {
        var xmlBuilder = new StringBuilder(header.formatted(memberTable.name(), changeType.name()));
        List<String> mappingNames = new ArrayList<>();
        if(changeType != MemberChangeType.CREATE) {
            mappingNames.add("id");
        }
        mappingNames.addAll(List.of("memberId", "sessionYear", "lbdcShortName", "districtCode", "alternate"));
        return xmlBuilder.append(getXmlFragment(mappingNames, modelMap, changedAttributes)).append("</actionDetails>\n");
    }

    private static String getXmlFragment(List<String> keys, Map<String, String> modelMap, HashSet<String> changedAttributes) {
        var tempStringBuilder = new StringBuilder();
        for (String key : keys) {
            if(changedAttributes.contains(key)) {
                tempStringBuilder.append(String.format("<%s action=\"UPDATE\">%s</%s>\n", key, modelMap.get(key), key));
            }
            else{
                tempStringBuilder.append("<%s>%s</%s>\n".formatted(key, modelMap.get(key), key));
            }

        }
        return tempStringBuilder.toString();
    }

}

