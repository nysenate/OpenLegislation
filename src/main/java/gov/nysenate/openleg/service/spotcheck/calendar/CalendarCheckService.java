package gov.nysenate.openleg.service.spotcheck.calendar;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CalendarCheckService implements SpotCheckService<CalendarId, Calendar, Calendar> {

    @Override
    public SpotCheckObservation<CalendarId> check(Calendar content) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<CalendarId> check(Calendar content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        throw new NotImplementedException(":P");
    }

    @Override
    public SpotCheckObservation<CalendarId> check(Calendar content, Calendar reference) {
        SpotCheckObservation<CalendarId> observation = initializeObservation(reference);
        if (calendarsAreEqual(content, reference)) {
            return observation;
        } else {
            compareSupplementals(observation, content, reference);
            compareActiveLists(observation, content, reference);
            return observation;
        }
    }

    private SpotCheckObservation<CalendarId> initializeObservation(Calendar reference) {
        SpotCheckReferenceId referenceId = new SpotCheckReferenceId(
                SpotCheckRefType.LBDC_CALENDAR_ALERT, reference.getPublishedDateTime());
        return new SpotCheckObservation<>(referenceId, reference.getId());
    }

    /**
     * Compare Calendar equality, ignoring published date.
     * @return <code>true</code> if calendar's id's, supplemental's, and active list's are equal. <code>false</code> otherwise.
     */
    private boolean calendarsAreEqual(Calendar content, Calendar other) {
        return Objects.equals(content.getId(), other.getId()) &&
               Objects.equals(content.getSupplementalMap(), other.getSupplementalMap()) &&
               Objects.equals(content.getActiveListMap(), other.getActiveListMap());
    }

    private void compareSupplementals(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference) {
        Set<CalendarSupplemental> contentSupplementals = ImmutableSet.copyOf(content.getSupplementalMap().values());
        Set<CalendarSupplemental> referenceSupplementals = ImmutableSet.copyOf(reference.getSupplementalMap().values());

        Set<CalendarSupplemental> differenceSet = Sets.symmetricDifference(contentSupplementals, referenceSupplementals).immutableCopy();
        for (CalendarSupplemental diff : differenceSet) {
            recordMismatch(observation, content, reference, diff);
        }
    }

    private void recordMismatch(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference, CalendarSupplemental diff) {
        CalendarSupplemental contentSuppDiff = getMatchingSupplementalIfExists(content, diff);
        CalendarSupplemental referenceSuppDiff = getMatchingSupplementalIfExists(reference, diff);

        // They should never both be null, if they are, no mismatch to record.
        if (contentSuppDiff == null && referenceSuppDiff == null) {
            return;
        }

        if (contentSuppDiff == null) {
            recordObservationDataMismatch(observation, referenceSuppDiff);
        }
        else if (referenceSuppDiff == null) {
            recordReferenceDataMismatch(observation, contentSuppDiff);
        }
        else {
            checkForSupplementalCalDateMismatch(observation, contentSuppDiff, referenceSuppDiff);

            Set<CalendarSectionType> contentSectionTypes = contentSuppDiff.getSectionEntries().keySet();
            Set<CalendarSectionType> referenceSectionTypes = referenceSuppDiff.getSectionEntries().keySet();

            checkForTypeMismatch(observation, contentSectionTypes, referenceSectionTypes);
            checkForSuppEntryMismatch(observation, contentSuppDiff, referenceSuppDiff);
        }
    }

    private void recordObservationDataMismatch(SpotCheckObservation<CalendarId> observation, CalendarSupplemental reference) {
        observation.addMismatch(new SpotCheckMismatch(
                SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", getVersionString(reference)));
    }

    private void recordReferenceDataMismatch(SpotCheckObservation<CalendarId> observation, CalendarSupplemental content) {
        observation.addMismatch(new SpotCheckMismatch(
                SpotCheckMismatchType.REFERENCE_DATA_MISSING, getVersionString(content), ""));
    }

    /**
     * Converts a supplemental's {@link Version} into a more informative string for mismatches.
     * <p>Returns:</p>
     * <ul>
     * <li>Empty string if the supplemental is null</li>
     * <li>"BASE" if the Version = Version.BASE</li>
     * </ul>
     * This is different from the Version.toString() behavior of returning an empty string for the Default/Base version.
     */
    private String getVersionString(CalendarSupplemental contentSuppDiff) {
        String versionString = "";
        if (contentSuppDiff != null) {
            Version v = contentSuppDiff.getVersion();
            versionString = v.equals(Version.DEFAULT) ? "BASE" : v.toString();
        }
        return versionString;
    }

    private void checkForSupplementalCalDateMismatch(SpotCheckObservation<CalendarId> observation, CalendarSupplemental contentSuppDiff,
                                                     CalendarSupplemental referenceSuppDiff) {
        String contentDate = contentSuppDiff.getCalDate() == null ? "" : contentSuppDiff.getCalDate().toString();
        String referenceDate = referenceSuppDiff.getCalDate() == null ? "" : referenceSuppDiff.getCalDate().toString();
        if (!StringUtils.equals(contentDate, referenceDate)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.SUPPLEMENTAL_CAL_DATE, contentDate, referenceDate));
        }

    }

    private void checkForTypeMismatch(SpotCheckObservation<CalendarId> observation, Set<CalendarSectionType> contentSectionTypes,
                                      Set<CalendarSectionType> referenceSectionTypes) {
        if (!Sets.symmetricDifference(contentSectionTypes, referenceSectionTypes).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.SUPPLEMENTAL_SECTION_TYPE, StringUtils.join(contentSectionTypes, "\n"), StringUtils.join(referenceSectionTypes, "\n")
            ));
        }
    }

    private void checkForSuppEntryMismatch(SpotCheckObservation<CalendarId> observation, CalendarSupplemental contentSuppDiff,
                                           CalendarSupplemental referenceSuppDiff) {
        Map<String, CalendarSupplementalEntry> contentEntryMap = getStringToEntryMap(contentSuppDiff);
        Map<String, CalendarSupplementalEntry> referenceEntryMap = getStringToEntryMap(referenceSuppDiff);

        Set<String> entryDiffs = Sets.symmetricDifference(contentEntryMap.keySet(), referenceEntryMap.keySet());
        for (String diff : entryDiffs) {
            CalendarSupplementalEntry contentDiff = contentEntryMap.get(diff);
            CalendarSupplementalEntry referenceDiff = referenceEntryMap.get(diff);

            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.SUPPLEMENTAL_ENTRY,
                    contentDiff == null ? "" : contentDiff.toString(), referenceDiff == null ? "" : referenceDiff.toString()
            ));
        }
    }

    private Map<String, CalendarSupplementalEntry> getStringToEntryMap(CalendarSupplemental supplemental) {
        Map<String, CalendarSupplementalEntry> stringToEntryMap = new HashMap<>();
        for (CalendarSupplementalEntry entry : supplemental.getAllEntries()) {
            stringToEntryMap.put(entry.toString(), entry);
        }
        return stringToEntryMap;
    }

    /**
     * Returns the matching supplemental from a calendar, null if it no match exists.
     */
    private CalendarSupplemental getMatchingSupplementalIfExists(Calendar calendar, CalendarSupplemental diff) {
        return calendar.getSupplementalMap().keySet().contains(diff.getVersion()) ? calendar.getSupplemental(diff.getVersion()) : null;
    }

    /**
     * Does NOT do a full comparison of active lists.
     * We only compare the most recent (by release date time) versions against each other.
     * This is done because we don't get sequence num info in alert emails. Active lists in alert emails
     * can either be new supplementals(with an incremented sequence num) or an update to a previous supplemental,
     * but we can't tell which.
     *
     * @param observation
     * @param content
     * @param reference
     */
    private void compareActiveLists(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference) {
        TreeMap<Integer, CalendarActiveList> contentActiveListMap = content.getActiveListMap();
        TreeMap<Integer, CalendarActiveList> referenceActiveListMap = reference.getActiveListMap();

        if (contentActiveListMap.size() == 0 && referenceActiveListMap.size() == 0) {
            return; // No mismatches.
        }

        if (contentActiveListMap.size() == 0) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", referenceActiveListMap.get(referenceActiveListMap.size() - 1).getSequenceNo()));
        }
        else if (referenceActiveListMap.size() == 0) {
             observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.REFERENCE_DATA_MISSING, contentActiveListMap.get(contentActiveListMap.size() - 1).getSequenceNo(), ""));
        }
        else {
            CalendarActiveList contentMostRecent = getMostRecentActiveList(content);
            CalendarActiveList referenceMostRecent = getMostRecentActiveList(reference);

            checkForActiveListCalDateMismatch(observation, contentMostRecent, referenceMostRecent);
            checkForActiveListEntryMismatch(observation, contentMostRecent, referenceMostRecent);
        }
    }

    private CalendarActiveList getMostRecentActiveList(Calendar calendar) {
        int seqNo = 0;
        LocalDateTime releaseDateTime = DateUtils.LONG_AGO.atStartOfDay();
        for (CalendarActiveList activeList : calendar.getActiveListMap().values()) {
            if (activeList.getReleaseDateTime().isAfter(releaseDateTime)) {
                seqNo = activeList.getSequenceNo();
                releaseDateTime = activeList.getReleaseDateTime();
            }
        }
        return calendar.getActiveList(seqNo);
    }

    private void checkForActiveListCalDateMismatch(SpotCheckObservation<CalendarId> observation, CalendarActiveList contentDiff,
                                                   CalendarActiveList referenceDiff) {
        String contentCalDate = contentDiff.getCalDate() == null ? "" : contentDiff.getCalDate().toString();
        String referenceCalDate = referenceDiff.getCalDate() == null ? "" : referenceDiff.getCalDate().toString();
        if (!StringUtils.equals(contentCalDate, referenceCalDate)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_CAL_DATE, contentCalDate, referenceCalDate));
        }
    }

    private void checkForActiveListEntryMismatch(SpotCheckObservation<CalendarId> observation, CalendarActiveList contentDiff,
                                                 CalendarActiveList referenceDiff) {
        Set<CalendarEntry> contentDiffEntries = Sets.newHashSet(contentDiff.getEntries());
        Set<CalendarEntry> referenceDiffEntries = Sets.newHashSet(referenceDiff.getEntries());
        if (!Sets.symmetricDifference(contentDiffEntries, referenceDiffEntries).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.ACTIVE_LIST_ENTRY,
                    StringUtils.join(contentDiffEntries, "\n"), StringUtils.join(referenceDiffEntries, "\n")));
        }
    }

}
