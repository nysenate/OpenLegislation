package gov.nysenate.openleg.service.spotcheck;

import com.google.common.base.Strings;
import gov.nysenate.openleg.dao.daybreak.DaybreakDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.daybreak.DaybreakBill;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.util.BillTextUtils;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;
import static java.util.stream.Collectors.joining;

@Service("daybreak")
public class DaybreakCheckService implements SpotCheckBillService
{
    @Autowired
    protected DaybreakDao daybreakDao;

    /** --- Implemented Methods --- */

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill bill) {
        return check(bill, LocalDateTime.now());
    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill bill, LocalDateTime latestDate) {
        if (bill == null) {
            throw new IllegalArgumentException("Supplied bill cannot be null");
        }
        BaseBillId baseBillId = bill.getBaseBillId();
        // TODO: Use dao here.. Get the latest matching DaybreakBill
        DaybreakBill daybreakBill = new DaybreakBill();
        // Create the observation
        SpotCheckObservation<BaseBillId> obsrv = new SpotCheckObservation<>(daybreakBill, baseBillId);

        // Compare the titles, ignore white space differences
        if (!stringEquals(daybreakBill.getTitle(), bill.getTitle(), true, true)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_TITLE, daybreakBill.getTitle(), bill.getTitle()));
        }

        // LBDC reports the law and summary together so we can't really check them separately with accuracy,
        // we can only verify that both are correct.
        String billSummary = (Strings.nullToEmpty(bill.getLaw()) + " " +
                              Strings.nullToEmpty(bill.getSummary())).trim();
        billSummary = billSummary.replace('§', 'S').replace('¶', 'P');
        String dayBreakSummary = daybreakBill.getSummary();
        if (!stringEquals(billSummary, dayBreakSummary, false, true)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_LAW_CODE_SUMMARY, dayBreakSummary, billSummary));
        }

        // Compare the bill sponsor if it exists
        if (bill.getSponsor() != null) {
            if (!daybreakBill.getSponsor().equals(bill.getSponsor())) {
                obsrv.addMismatch(new SpotCheckMismatch(BILL_SPONSOR, daybreakBill.getSponsor().toString(),
                                                                      bill.getSponsor().toString()));
            }
        }
        else if (daybreakBill.getSponsor() != null) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_SPONSOR, daybreakBill.getSponsor().toString(), "NULL"));
        }

        // Compare cosponsors
        List<Member> billCoSponsors = bill.hasActiveAmendment() ? bill.getActiveAmendment().getCoSponsors()
                                                                : new ArrayList<>();
        if (!daybreakBill.getCosponsors().equals(billCoSponsors)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_COSPONSOR, memberListString(daybreakBill.getCosponsors()),
                                                                    memberListString(billCoSponsors)));
        }

        // Compare multisponsors
        List<Member> billMultiSponsors = bill.hasActiveAmendment() ? bill.getActiveAmendment().getMultiSponsors()
                                                                   : new ArrayList<>();
        if (!daybreakBill.getMultiSponsors().equals(billMultiSponsors)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_MULTISPONSOR, memberListString(daybreakBill.getMultiSponsors()),
                                                                       memberListString(billMultiSponsors)));
        }

        // Compare the actions lists
        String billActions = actionsListString(bill.getActions());
        String daybreakActions = actionsListString(daybreakBill.getActions());
        if (!daybreakActions.equals(billActions)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTION, daybreakActions, billActions));
        }

        // Check the page counts for each amendment version
        Map<Version, Integer> billPageCounts = new HashMap<>();
        bill.getAmendmentMap().forEach((k,v) -> billPageCounts.put(k, BillTextUtils.getPageCount(v.getFullText())));
        if (!daybreakBill.getPageCounts().equals(billPageCounts)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_FULLTEXT_PAGE_COUNT, daybreakBill.getPageCounts().toString(),
                                                                              billPageCounts.toString()));
        }

        return obsrv;
    }

    /** --- Internal --- */

    /**
     * Returns a string with any html escapes converted to unicode.
     */
    private String unescapeHTML(String text) {
        return StringEscapeUtils.unescapeHtml4(text).replace("&apos;", "'");
    }

    /**
     * Compare two strings a and b with the option to ignore case and extra whitespace.
     */
    private boolean stringEquals(String a, String b, boolean ignoreCase, boolean normalizeSpaces) {
        if (normalizeSpaces) {
            a = a.replaceAll("\\s+", " ");
            b = b.replaceAll("\\s+", " ");
        }
        return (ignoreCase) ? a.equalsIgnoreCase(b) : a.equals(b);
    }

    /**
     * Convert a member list into a string containing the short names, delimited by a space.
     */
    private String memberListString(List<Member> members) {
        String membersStr = "";
        if (members != null) {
            membersStr = members.stream().map(Member::getLbdcShortName).collect(joining(" "));
        }
        return membersStr.toUpperCase();
    }

    /**
     * Convert the actions list into a string with each line as MM/DD/YY THE_ACTION_TEXT\n.
     */
    private String actionsListString(List<BillAction> actions) {
        String actionsStr = "";
        if (actions != null) {
            actionsStr = actions.stream().map(a -> DateUtils.LRS_ACTIONS_DATE.format(a.getDate()) + " " + a.getText())
                                         .collect(joining("\n"));

        }
        return actionsStr.toUpperCase();
    }
}