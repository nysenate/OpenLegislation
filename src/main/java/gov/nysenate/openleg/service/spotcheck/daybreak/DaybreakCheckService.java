package gov.nysenate.openleg.service.spotcheck.daybreak;

import com.google.common.base.Strings;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakBill;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckService;
import gov.nysenate.openleg.util.BillTextUtils;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.bill.BillTextFormat.PLAIN;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;
import static java.util.stream.Collectors.*;

@Service("daybreak")
public class DaybreakCheckService extends BaseSpotCheckService<BaseBillId, Bill, DaybreakBill> {
    private static final Logger logger = LoggerFactory.getLogger(DaybreakCheckService.class);

    /* --- Implemented Methods --- */

    /**
     * {@inheritDoc}
     */
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

    /* --- Methods --- */

    /**
     * Check that the active version matches and also that only amendments before and including the active version
     * are published.
     */
    private void checkActiveVersions(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        Version daybreakActiveVersion = daybreakBill.getActiveVersion();
        if (!daybreakActiveVersion.equals(bill.getActiveVersion())) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_ACTIVE_AMENDMENT, ((bill.getActiveVersion() != null) ? bill.getActiveVersion().name() : "NULL"), daybreakActiveVersion.name()
            ));
        }

        String daybreakPubVersionsStr = publishedVersionsString(daybreakBill);
        String billPubVersionsStr = publishedVersionsString(bill);
        if (!daybreakPubVersionsStr.equals(billPubVersionsStr)) {
            obsrv.addMismatch(new SpotCheckMismatch(BILL_AMENDMENT_PUBLISH, billPubVersionsStr, daybreakPubVersionsStr));
        }
    }

    /**
     * Checks the full text page counts for each amendment version. The page count for the daybreak comes from
     * the page file.
     */
    private void checkFullTextPageCounts(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        Map<Version, Integer> billPageCounts = new HashMap<>();
        Map<Version, Integer> daybreakPageCounts = new HashMap<>();
        daybreakBill.getAmendments().forEach((k, v) -> daybreakPageCounts.put(k, v.getPageCount()));
        // Just count the pages for versions that also appear in the daybreak
        bill.getAmendmentMap().forEach((k, v) -> {
            if (daybreakPageCounts.containsKey(k)) {
                billPageCounts.put(k, BillTextUtils.getBillPages(v.getFullText(PLAIN)).size());
            }
        });
        if (!daybreakPageCounts.equals(billPageCounts)) {
            SpotCheckMismatch pageCountMismatch = new SpotCheckMismatch(BILL_FULLTEXT_PAGE_COUNT, billPageCounts.toString(), daybreakPageCounts.toString()
            );
            obsrv.addMismatch(pageCountMismatch);
        }
    }

    /**
     * Compare the actions in the daybreak with the bill's actions.
     */
    private void checkBillActions(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
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
    private void checkMultiSponsors(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        SessionYear session = bill.getSession();
        Chamber chamber = bill.getChamber();

        Optional<BillAmendment> activeAmendmentOpt =
                bill.hasActiveAmendment() ? Optional.of(bill.getActiveAmendment()) : Optional.empty();

        List<String> contentMultiSponsors = activeAmendmentOpt
                .map(BillAmendment::getMultiSponsors)
                .orElse(Collections.emptyList())
                .stream()
                .map(muSpon -> getPrimaryShortname(session, chamber, muSpon.getLbdcShortName()))
                .sorted()
                .collect(Collectors.toList());

        List<String> refMultiSponsors = daybreakBill.getMultiSponsors().stream()
                .map(this::cleanDaybreakShortname)
                .map(muSpon -> getPrimaryShortname(session, chamber, muSpon))
                .sorted()
                .collect(toList());

        // Only check for mismatch if a daybreak multisponsor is set. Sometimes the daybreaks omit the multisponsor.
        if (!refMultiSponsors.isEmpty()) {
            checkCollection(contentMultiSponsors, refMultiSponsors, obsrv, BILL_MULTISPONSOR, Function.identity(), "\n");
        }
    }

    /**
     * Check the active bill amendment's cosponsor list. Order and case do not matter.
     */
    private void checkCoSponsors(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        SessionYear session = bill.getSession();
        Chamber chamber = bill.getChamber();

        Optional<BillAmendment> activeAmendmentOpt =
                bill.hasActiveAmendment() ? Optional.of(bill.getActiveAmendment()) : Optional.empty();

        List<String> contentCoSponsors = activeAmendmentOpt
                .map(BillAmendment::getCoSponsors)
                .orElse(Collections.emptyList())
                .stream()
                .map(coSpon -> getPrimaryShortname(session, chamber, coSpon.getLbdcShortName()))
                .sorted()
                .collect(Collectors.toList());

        List<String> refCoSponsors = daybreakBill.getCosponsors().stream()
                .map(this::cleanDaybreakShortname)
                .map(coSpon -> getPrimaryShortname(session, chamber, coSpon))
                .sorted()
                .collect(toList());

        // Only check for mismatch if a daybreak cosponsor is set. Sometimes the daybreaks omit the cosponsor.
        if (!refCoSponsors.isEmpty()) {
            checkCollection(contentCoSponsors, refCoSponsors, obsrv, BILL_COSPONSOR, Function.identity(), "\n");
        }
    }

    /**
     * Clean up a shortname from daybreak report.
     * Converts to uppercase, and removes parenthesis.
     */
    private String cleanDaybreakShortname(String name) {
        name.replaceAll("[\\(\\)]+", "");
        name = StringUtils.upperCase(name);
        return name;
    }

    /**
     * Check the BillSponsor by comparing the string representation. The toString method for BillSponsor should
     * produce the same formatting as the sponsor string found in the DaybreakBill.
     */
    private void checkBillSponsor(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
        SessionYear session = bill.getSession();
        Chamber chamber = bill.getChamber();
        String contentSponsor = Optional.ofNullable(bill.getSponsor())
                .map(s -> getPrimaryShortname(session, chamber, s.toString()))
                .orElse(null);
        String refSponsor = getPrimaryShortname(session, chamber, daybreakBill.getSponsor());
        checkString(contentSponsor, refSponsor, obsrv, BILL_SPONSOR);
    }

    /**
     * The daybreaks concatenate the law code and the summary. Since it's not trivial to parse this out, we simply
     * concatenate our own law and summary and just compare the strings.
     */
    private void checkBillLawAndSummary(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
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
    private void checkBillTitle(Bill bill, DaybreakBill daybreakBill, SpotCheckObservation<BaseBillId> obsrv) {
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
    private boolean stringEquals(String a, String b, boolean ignoreCase, boolean normalizeSpaces) {
        // Convert null values to empty strings.
        a = (a == null) ? "" : a;
        b = (b == null) ? "" : b;
        // Remove excess spaces if requested
        if (normalizeSpaces) {
            a = a.replaceAll("\\s+", " ");
            b = b.replaceAll("\\s+", " ");
        }
        return (ignoreCase) ? StringUtils.equalsIgnoreCase(a, b) : StringUtils.equals(a, b);
    }

    /**
     * Convert a BillSponsor to its string representation.
     */
    private String sponsorString(BillSponsor billSponsor) {
        return (billSponsor != null) ? billSponsor.toString() : "NULL";
    }

    /**
     * Convert the actions list into a string with each line e.g SENATE - 12/16/13 PRINT NUMBER 1234A\n.
     */
    private String actionsListString(List<BillAction> actions) {
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
     * e.g. 'ORIGINAL A B C' if the base and amendments A, B, and C are all published.
     */
    private String publishedVersionsString(Bill bill) {
        Set<Version> publishedVersionSet = bill.getAmendPublishStatusMap().entrySet().stream()
                .filter(entry -> entry.getValue().isPublished())
                .map(Map.Entry::getKey)
                .collect(toSet());
        return publishedVersionsString(publishedVersionSet);
    }

    /**
     * Get a string representing the published versions of the given {@link DaybreakBill}
     */
    private String publishedVersionsString(DaybreakBill bill) {
        if (bill.getActiveVersion() == null) {
            return "";
        }
        Set<Version> publishedVersions = new HashSet<>();
        // Add all versions up to the active version
        for (int i = 0; i < Version.values().length && i <= bill.getActiveVersion().ordinal(); i++) {
            publishedVersions.add(Version.values()[i]);
        }
        // Add any versions with page file entries
        publishedVersions.addAll(bill.getAmendments().keySet());
        return publishedVersionsString(publishedVersions);
    }

    /**
     * Generate a string that represents the given set of versions.
     */
    private String publishedVersionsString(Set<Version> publishedVersions) {
        return publishedVersions.stream()
                .sorted()
                .map(Version::name)
                .collect(joining(" "));
    }
}