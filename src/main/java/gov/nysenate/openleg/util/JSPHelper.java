package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class JSPHelper
{
    public static String getPersonLink(String person, String base)
    {
        return "<a href=\""+base+"/search/?term=sponsor:"+person+"\" class=\"sublink\">"+person+"</a>";
    }

    public static String getPersonLink(Person person, String base)
    {
        return getPersonLink(person.getFullname(), base);
    }

    public static String getSponsorLinks(String[] sponsors, String base)
    {
        return getSponsorLinks(Arrays.asList(sponsors), base);
    }

    public static String getSponsorLinks(List<String> sponsors, String base)
    {
        ArrayList<String> links = new ArrayList<String>();
        for (String person : sponsors) {
            links.add(getPersonLink(person, base));
        }
        return StringUtils.join(links, ", ");
    }

    public static String getSponsorLinks(Bill bill, String base)
    {
        Person sponsor = bill.getSponsor();
        ArrayList<String> links = new ArrayList<String>();
        links.add(JSPHelper.getPersonLink(sponsor, base));
        for (Person otherSponsor : bill.getOtherSponsors()) {
            links.add(JSPHelper.getPersonLink(otherSponsor, base));
        }

        return StringUtils.join(links, ", ");
    }

    public static String getCoSponsorLinks(Bill bill, String base)
    {
        ArrayList<String> links = new ArrayList<String>();
        for (Person sponsor : bill.getCoSponsors()) {
            if (!bill.getOtherSponsors().contains(sponsor)) {
                links.add(JSPHelper.getPersonLink(sponsor, base));
            }
        }

        return StringUtils.join(links, ", ");
    }

    public static String getMultiSponsorLinks(Bill bill, String base)
    {
        ArrayList<String> links = new ArrayList<String>();
        for (Person sponsor : bill.getMultiSponsors()) {
            if (!bill.getOtherSponsors().contains(sponsor)) {
                links.add(JSPHelper.getPersonLink(sponsor, base));
            }
        }
        return StringUtils.join(links, ", ");
    }
}
