package gov.nysenate.openleg.client.view.oldapi;

import gov.nysenate.openleg.model.bill.BillId;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OldBillInfoView implements Serializable {

    private static final long serialVersionUID = 6709730808085480253L;

    protected static final Pattern billIdPattern = Pattern.compile("([A-z]\\d+[A-z]?)-(\\d{4})");

    String actClause;
    String active;
    List<String> amendments;
    String frozen;
    String lawSection;
    List<OldSponsorView> otherSponsors;
    String sameAs;
    String senateBillNo;
    OldSponsorView sponsor;
    String summary;
    String title;
    String uniBill;
    int year;

    public BillId getBillId() {
        Matcher matcher = billIdPattern.matcher(senateBillNo);
        if (matcher.matches()) {
            return new BillId(matcher.group(1), Integer.parseInt(matcher.group(2)));
        }
        return null;
    }

    /** --- Getters / Setters --- */

    public String getActClause() {
        return actClause;
    }

    public void setActClause(String actClause) {
        this.actClause = actClause;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public List<String> getAmendments() {
        return amendments;
    }

    public void setAmendments(List<String> amendments) {
        this.amendments = amendments;
    }

    public String getFrozen() {
        return frozen;
    }

    public void setFrozen(String frozen) {
        this.frozen = frozen;
    }

    public String getLawSection() {
        return lawSection;
    }

    public void setLawSection(String lawSection) {
        this.lawSection = lawSection;
    }

    public List<OldSponsorView> getOtherSponsors() {
        return otherSponsors;
    }

    public void setOtherSponsors(List<OldSponsorView> otherSponsors) {
        this.otherSponsors = otherSponsors;
    }

    public String getSameAs() {
        return sameAs;
    }

    public void setSameAs(String sameAs) {
        this.sameAs = sameAs;
    }

    public String getSenateBillNo() {
        return senateBillNo;
    }

    public void setSenateBillNo(String senateBillNo) {
        this.senateBillNo = senateBillNo;
    }

    public OldSponsorView getSponsor() {
        return sponsor;
    }

    public void setSponsor(OldSponsorView sponsor) {
        this.sponsor = sponsor;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUniBill() {
        return uniBill;
    }

    public void setUniBill(String uniBill) {
        this.uniBill = uniBill;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
