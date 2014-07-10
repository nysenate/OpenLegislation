package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.entity.Person;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSPHelper
{
    public static String getPersonLink(String person, HttpServletRequest request)
    {
        if (person != null && person.trim().length() > 0) {
            return "<a href=\""+getLink(request, "/search/?term=sponsor:"+person)+"\" class=\"sublink\">"+person+"</a>";
        }
        else {
            return "None";
        }
    }

    /**
     * Joins the link path with the full URL of the servlet context path
     * @param request
     * @param link
     * @return
     */
    public static String getFullLink(HttpServletRequest request, String link) {
        int port = request.getServerPort();
        return request.getScheme()+"://"+request.getServerName()+(port != 80 ? ":"+port : "")+request.getContextPath()+link;
    }

    /**
     * Joins the link path with the context path of the request for an absolute link
     *
     * @param request
     * @param link
     * @return
     */
    public static String getLink(HttpServletRequest request, String link)
    {
        return request.getContextPath()+link;
    }

    public static String getLink(HttpServletRequest request, Bill bill)
    {
        return JSPHelper.getLink(request, "/bill/"+bill.getBillId());
    }

    public static String getPersonLink(Person person, HttpServletRequest request)
    {
        if (person != null) {
            return getPersonLink(person.getFullName(), request);
        }
        else {
            return "None";
        }
    }

    public static String getPersonLinks(List<String> people, HttpServletRequest request)
    {
        ArrayList<String> links = new ArrayList<String>();
        for (String person : people) {
            links.add(getPersonLink(person, request));
        }
        return StringUtils.join(links, ", ");
    }

    public static String getSponsorLinks(String[] sponsors, HttpServletRequest request)
    {
        return getSponsorLinks(Arrays.asList(sponsors), request);
    }

    public static String getSponsorLinks(List<String> sponsors, HttpServletRequest request)
    {
        ArrayList<String> links = new ArrayList<String>();
        for (String person : sponsors) {
            links.add(getPersonLink(person, request));
        }
        return StringUtils.join(links, ", ");
    }

    public static String getSponsorLinks(Bill bill, HttpServletRequest request)
    {
        //Person sponsor = bill.getSponsor();
        ArrayList<String> links = new ArrayList<String>();
        //links.add(JSPHelper.getPersonLink(sponsor, request));
        for (Person otherSponsor : bill.getAdditionalSponsors()) {
            links.add(JSPHelper.getPersonLink(otherSponsor, request));
        }

        return StringUtils.join(links, ", ");
    }

    public static String getCoSponsorLinks(Bill bill, HttpServletRequest request)
    {
        ArrayList<String> links = new ArrayList<String>();
        /** FIXME
        for (Person sponsor : bill.getCoSponsors()) {
            if (!bill.getOtherSponsors().contains(sponsor)) {
                links.add(JSPHelper.getPersonLink(sponsor, request));
            }
        }         */

        return StringUtils.join(links, ", ");
    }

    public static String getMultiSponsorLinks(Bill bill, HttpServletRequest request)
    {
        ArrayList<String> links = new ArrayList<String>();
        /** FIXME
        for (Person sponsor : bill.getMultiSponsors()) {
            if (!bill.getOtherSponsors().contains(sponsor)) {
                links.add(JSPHelper.getPersonLink(sponsor, request));
            }
        }        */
        return StringUtils.join(links, ", ");
    }
}
