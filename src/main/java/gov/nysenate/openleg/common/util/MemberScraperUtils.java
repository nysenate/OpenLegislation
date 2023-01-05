package gov.nysenate.openleg.common.util;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.Member;
import gov.nysenate.openleg.legislation.member.SessionMember;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

/**
 * Quick and dirty member scraping. This scraper will break with site redesigns so always test first.
 */
public class MemberScraperUtils
{
    private static final Logger logger = LoggerFactory.getLogger(MemberScraperUtils.class);

    static final String ASSEMBLY_MEMBER_DIR_URL = "http://assembly.state.ny.us/mem/";
    static final String ASSEMBLY_MEMBER_LISTING_URL = "http://assembly.state.ny.us/mem/email/";
    static final String SENATE_MEMBER_DIR_URL = "http://www.nysenate.gov/senators";

    public static List<SessionMember> getAssemblyMembers() throws IOException {
        Document directoryPage = Jsoup.connect(ASSEMBLY_MEMBER_DIR_URL).get();
        Document listingPage = Jsoup.connect(ASSEMBLY_MEMBER_LISTING_URL).get();
        Elements csvNameElements = listingPage.select(".email1 a");
        Elements districtElements = listingPage.select(".email2");
        Elements picElements = directoryPage.select(".mem-pic a img");
        Elements fullNameElements = directoryPage.select(".leader-info strong a");
        List<String> fullNames = fullNameElements.stream().map(Element::text).collect(toList());
        Pattern districtPattern = Pattern.compile("(\\d+)\\w+");
        List<Integer> districts = districtElements.stream().map(e -> {
            Matcher m = districtPattern.matcher(e.text());
            m.matches();
            return Integer.parseInt(m.group(1));
        }).collect(toList());
        List<String> lastNames = csvNameElements.stream().map(e -> e.text().split(",")[0]).collect(toList());
        List<String> imageNames = picElements.stream().map(i -> i.attr("src")).collect(toList());
        List<SessionMember> sessionMembers = new ArrayList<>();
        for (int i = 0; i < lastNames.size(); i++) {
            Member m = new Member();
            m.setNameFields(fullNames.get(i));
            m.setImgName(imageNames.get(i));
            m.setChamber(Chamber.ASSEMBLY);

            SessionMember sm = new SessionMember();
            sm.setDistrictCode(districts.get(i));
            sm.setSessionYear(SessionYear.current());
            sm.setMember(m);

            sessionMembers.add(sm);
        }

        return sessionMembers;
    }

    public static List<SessionMember> getSenateMembers() throws IOException {
        Document directoryPage = Jsoup.connect(SENATE_MEMBER_DIR_URL).get();
        Elements nodes = directoryPage.select(".view-senators .view-content .views-row");
        Elements imageElems = nodes.select(".views-field-field-profile-picture-fid img");
        Elements nameElems = nodes.select(".views-field-field-last-name-value .field-content > a");
        Elements districtElems = nodes.select(".views-field-field-senators-district-nid span");
        List<String> imageUrls = imageElems.stream()
            .map(i -> i.attr("src")).collect(toList());
        List<String> names = nameElems.stream().map(Element::text).collect(toList());
        Pattern districtPattern = Pattern.compile("District (\\d+)");
        List<Integer> districts = districtElems.stream().map(d -> {
            Matcher m = districtPattern.matcher(d.text());
            m.find();
            return Integer.parseInt(m.group(1));
        }).collect(toList());
        List<SessionMember> senators = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String[] splitName = names.get(i).split(",");
            Member m = new Member();
            m.setNameFields(splitName[1] + " " + splitName[0]);
            m.setImgName(imageUrls.get(i));
            m.setChamber(Chamber.SENATE);

            SessionMember sm = new SessionMember();
            sm.setDistrictCode(districts.get(i));
            sm.setSessionYear(SessionYear.current());
            sm.setMember(m);

            senators.add(sm);
        }
        return senators;
    }
}
