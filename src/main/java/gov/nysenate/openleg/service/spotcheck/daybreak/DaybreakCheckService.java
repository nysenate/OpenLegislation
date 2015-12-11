package gov.nysenate.openleg.service.spotcheck.daybreak;

import com.google.common.base.Strings;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.dao.bill.reference.daybreak.DaybreakDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillSponsor;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakBill;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.base.SpotcheckMismatchEvent;
import gov.nysenate.openleg.util.BillTextUtils;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

@Service("daybreak")
public class DaybreakCheckService implements SpotCheckService<BaseBillId, Bill, DaybreakBill>
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakCheckService.class);

    @Autowired
    protected DaybreakDao daybreakDao;

    @Autowired
    protected EventBus eventBus;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    /** --- Implemented Methods --- */

    /** {@inheritDoc}
     *  Just use the latest daybreak files we have. */
    @Override
    public SpotCheckObservation<BaseBillId> check(Bill bill) throws ReferenceDataNotFoundEx {
        return check(bill, DateUtils.LONG_AGO.atStartOfDay(), LocalDateTime.now());
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckObservation<BaseBillId> check(Bill bill, LocalDateTime start, LocalDateTime end)
                                                  throws ReferenceDataNotFoundEx {
        if (bill == null) {
            throw new IllegalArgumentException("Supplied bill cannot be null");
        }
        Range<LocalDate> dateRange = Range.closed(start.toLocalDate(), end.toLocalDate());
        try {
            DaybreakBill daybreakBill = daybreakDao.getCurrentDaybreakBill(bill.getBaseBillId(), dateRange);
            return check(bill, daybreakBill);
        }
        catch (DataAccessException ex) {
            throw new ReferenceDataNotFoundEx();
        }
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckObservation<BaseBillId> check(Bill bill, DaybreakBill daybreakBill) {
        if (daybreakBill == null) {
            throw new IllegalArgumentException("DaybreakBill cannot be null when performing spot check");
        }
        BaseBillId baseBillId = bill.getBaseBillId();
        SpotCheckReferenceId referenceId = daybreakBill.getReferenceId();
        final SpotCheckObservation<BaseBillId> observation = new SpotCheckObservation<>(referenceId, baseBillId);
        // Perform the checks
        checkBillTitle(bill, daybreakBill, observation);
        checkBillLawAndSummary(bill, daybreakBill, observation);
        checkBillSponsor(bill, daybreakBill, observation);
        checkCoSponsors(bill, daybreakBill, observation);
        checkMultiSponsors(bill, daybreakBill, observation);
        checkBillActions(bill, daybreakBill, observation);
        checkFullTextPageCounts(bill, daybreakBill, observation);
        checkActiveVersions(bill, daybreakBill, observation);
        // Some friendly logging
        int mismatchCount = observation.getMismatches().size();
        if (mismatchCount > 0) {
            logger.info("Bill {} | {} mismatch(es). | {}", baseBillId, mismatchCount, observation.getMismatchTypes(false));
        }
        return observation;
    }

    /** --- Methods --- */

    /**
     * Check that the active version matches and also that only amendments before and including the active version
     * are published.
     */
    protected void checkActiveVersions(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        Version daybreakActiveVersion = daybreakBill.getActiveVersion();
        if (!daybreakActiveVersion.equals(bill.getActiveVersion())) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTIVE_AMENDMENT, ((bill.getActiveVersion() != null) ? bill.getActiveVersion().name() : "NULL"), daybreakActiveVersion.name()
            ));
        }

        String daybreakPubVersionsStr = publishedVersionsString(daybreakActiveVersion);
        String billPubVersionsStr = publishedVersionsString(bill);
        if (!daybreakPubVersionsStr.equals(billPubVersionsStr)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_AMENDMENT_PUBLISH, billPubVersionsStr, daybreakPubVersionsStr));
        }
    }

    /**
     * Checks the full text page counts for each amendment version. The page count for the daybreak comes from
     * the page file.
     */
    protected void checkFullTextPageCounts(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        Map<Version, Integer> billPageCounts = new HashMap<>();
        Map<Version, Integer> daybreakPageCounts = new HashMap<>();
        daybreakBill.getAmendments().forEach((k, v) -> daybreakPageCounts.put(k, v.getPageCount()));
        // Just count the pages for versions that also appear in the daybreak
        bill.getAmendmentMap().forEach((k,v) -> {
            if (daybreakPageCounts.containsKey(k)) {
                billPageCounts.put(k, BillTextUtils.getPageCount(v.getFullText()));
            }
        });
        if (!daybreakPageCounts.equals(billPageCounts)) {
            SpotCheckMismatch pageCountMismatch = new SpotCheckMismatch(BILL_FULLTEXT_PAGE_COUNT, billPageCounts.toString(), daybreakPageCounts.toString()
            );
            obsrv.addMismatch(pageCountMismatch);
            eventBus.post(new SpotcheckMismatchEvent<>(LocalDateTime.now(), bill.getBaseBillId(), pageCountMismatch));
        }
    }

    /**
     * Compare the actions in the daybreak with the bill's actions.
     */
    protected void checkBillActions(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        if (daybreakBill.getActions() != null && !daybreakBill.getActions().equals(bill.getActions())) {
            // There are cases when the daybreak actions list stops upon substitution of the bill. Ignore those cases
            if (!daybreakBill.getActions().isEmpty()) {
                BillAction lastAction = new LinkedList<>(daybreakBill.getActions()).getLast();
                if (StringUtils.containsIgnoreCase(lastAction.getText(), "SUBSTITUTED BY") &&
                    bill.getActions().containsAll(daybreakBill.getActions())) {
                    return;
                }
            }
            String daybreakActionsStr = actionsListString(daybreakBill.getActions());
            String billActionsStr = actionsListString(bill.getActions());
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTION, billActionsStr, daybreakActionsStr));
        }
    }

    /**
     * Check the active bill amendment's multisponsor list. Order and case do not matter.
     */
    protected void checkMultiSponsors(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        List<SessionMember> billMuSponsors = bill.hasActiveAmendment() ? bill.getActiveAmendment().getMultiSponsors()
                                                                : new ArrayList<>();
        Set<String> daybreakMuSponsorSet =
            daybreakBill.getMultiSponsors().stream()
                .map(c -> StringUtils.upperCase(c.replaceAll("[\\(\\)]+", "")))  // Upper case and remove any parenthesis
                .collect(toSet());
        // The bill multi sponsor set will just have the short names as-is (they should already be uppercased)
        Set<String> billMuSponsorSet = billMuSponsors.stream().map(SessionMember::getLbdcShortName).collect(toSet());
        // Only check for mismatch if a daybreak multisponsor is set. Sometimes the daybreaks omit the multisponsor.
        if (!daybreakMuSponsorSet.isEmpty() && (daybreakMuSponsorSet.size() != billMuSponsorSet.size() ||
                                               !daybreakMuSponsorSet.containsAll(billMuSponsorSet))) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_MULTISPONSOR, StringUtils.join(billMuSponsorSet, " "), StringUtils.join(daybreakMuSponsorSet, " ")
            ));
        }
    }

    /**
     * Check the active bill amendment's cosponsor list. Order and case do not matter.
     */
    protected void checkCoSponsors(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        List<SessionMember> billCoSponsors = bill.hasActiveAmendment() ? bill.getActiveAmendment().getCoSponsors()
                                                                : new ArrayList<>();
        Set<String> daybreakCoSponsorSet =
            daybreakBill.getCosponsors().stream()
                .map(c -> StringUtils.upperCase(c.replaceAll("[\\(\\)]+", "")))  // Upper case and remove any parenthesis
                .collect(toSet());
        // The bill co sponsor set will just have the short names as-is (they should already be uppercased)
        Set<String> billCoSponsorSet = billCoSponsors.stream().map(SessionMember::getLbdcShortName).collect(toSet());
        // Only check for mismatch if a daybreak cosponsor is set. Sometimes the daybreaks omit the cosponsor.
        if (!daybreakCoSponsorSet.isEmpty() && (daybreakCoSponsorSet.size() != billCoSponsorSet.size() ||
                                               !daybreakCoSponsorSet.containsAll(billCoSponsorSet))) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_COSPONSOR, StringUtils.join(billCoSponsorSet, " "), StringUtils.join(daybreakCoSponsorSet, " ")
            ));
        }
    }

    /**
     * Check the BillSponsor by comparing the string representation. The toString method for BillSponsor should
     * produce the same formatting as the sponsor string found in the DaybreakBill.
     */
    protected void checkBillSponsor(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        String billSponsorStr = sponsorString(bill.getSponsor());
        if (!stringEquals(daybreakBill.getSponsor(), billSponsorStr, true, true)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_SPONSOR, billSponsorStr, daybreakBill.getSponsor()));
        }
    }

    /**
     * The daybreaks concatenate the law code and the summary. Since it's not trivial to parse this out, we simply
     * concatenate our own law and summary and just compare the strings.
     */
    protected void checkBillLawAndSummary(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        String billLawSummary = (Strings.nullToEmpty(bill.getAmendment(bill.getActiveVersion()).getLaw()) + " " +
                                 Strings.nullToEmpty(bill.getSummary())).trim();
        billLawSummary = billLawSummary.replace('§', 'S').replace('¶', 'P');
        String dayBreakLawSummary = daybreakBill.getLawCodeAndSummary();
        if (!stringEquals(billLawSummary, dayBreakLawSummary, false, true)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_LAW_CODE_SUMMARY, billLawSummary, dayBreakLawSummary));
        }
    }

    /**
     * Compare the bill title with the daybreak title.
     */
    protected void checkBillTitle(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        if (!stringEquals(daybreakBill.getTitle(), bill.getTitle(), false, true)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_TITLE, bill.getTitle(), daybreakBill.getTitle()));
        }
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
    protected boolean stringEquals(String a, String b, boolean ignoreCase, boolean normalizeSpaces) {
        // Convert null values to empty strings.
        a = (a == null) ? "" : a;
        b = (b == null) ? "" : b;
        // Remove excess spaces if requested
        if (normalizeSpaces) {
            a = a.replaceAll("\\s+", " ");
            b = b.replaceAll("\\s+", " ");
        }
        return (ignoreCase) ? StringUtils.equalsIgnoreCase(a, b) : StringUtils.equals(a,b);
    }

    /**
     * Convert a BillSponsor to its string representation.
     */
    protected String sponsorString(BillSponsor billSponsor) {
        return (billSponsor != null) ? billSponsor.toString() : "NULL";
    }

    /**
     * Convert the actions list into a string with each line e.g SENATE - 12/16/13 PRINT NUMBER 1234A\n.
     */
    protected String actionsListString(List<BillAction> actions) {
        String actionsStr = "";
        if (actions != null) {
            actionsStr = actions.stream()
                .map(a -> a.getChamber() + " - " + DateUtils.LRS_ACTIONS_DATE.format(a.getDate()) + " " + a.getText())
                .collect(joining("\n"));
        }
        return actionsStr.toUpperCase();
    }

    /**
     * Given the bill return a single string that has the name of each published version,
     * e.g. 'DEFAULT A B C' if the base and amendments A, B, and C are all published. If
     * the amendment is missing from the bill but is in the publish map, tack on a [MISSING_DATA]
     * like B[MISSING_DATA]
     */
    protected String publishedVersionsString(Bill bill) {
        return bill.getAmendPublishStatusMap().entrySet().stream()
            .filter(e -> e.getValue().isPublished())
            .map(e -> e.getKey().name() + ((!bill.hasAmendment(e.getKey())) ? "[MISSING_DATA]" : ""))
            .collect(joining(" "));
    }

    /**
     * Given an active amendment version, return a string that has the names of every version before
     * and including the active version.
     */
    protected String publishedVersionsString(Version activeVersion) {
        return Arrays.asList(Version.values()).stream()
            .filter(v -> v.compareTo(activeVersion) <= 0).map(v -> v.name()).collect(joining(" "));
    }
}