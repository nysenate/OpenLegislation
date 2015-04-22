package gov.nysenate.openleg.service.spotcheck.calendar;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;

@Service
public class CalendarSpotCheckService implements SpotCheckService<CalendarId, Calendar, Calendar> {

    @Override
    public SpotCheckObservation<CalendarId> check(Calendar content) throws ReferenceDataNotFoundEx {
        return null;
    }

    @Override
    public SpotCheckObservation<CalendarId> check(Calendar content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        return null;
    }

    @Override
    public SpotCheckObservation<CalendarId> check(Calendar content, Calendar reference) {
        SpotCheckReferenceId referenceId = new SpotCheckReferenceId(
                SpotCheckRefType.LBDC_FLOOR_CALENDAR, reference.getPublishedDateTime().truncatedTo(ChronoUnit.SECONDS));
        SpotCheckObservation<CalendarId> observation = new SpotCheckObservation<>(referenceId, content.getId());

        if (calendarsEqual(content, reference)) {
            return observation;
        } else {
            compareSupplementals(observation, content, reference);
            compareActiveLists(observation, content, reference);
            return observation;
        }
    }

    /**
     * Compare Calendar equality without checking published date.
     */
    private boolean calendarsEqual(Calendar content, Calendar other) {
        return Objects.equals(content.getId(), other.getId()) &&
               Objects.equals(content.getSupplementalMap(), other.getSupplementalMap()) &&
               Objects.equals(content.getActiveListMap(), other.getActiveListMap());
    }

    private void compareSupplementals(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference) {
        Set<CalendarSupplemental> contentSupplementals = ImmutableSortedSet.copyOf(content.getSupplementalMap().values());
        Set<CalendarSupplemental> referenceSupplementals = ImmutableSortedSet.copyOf(reference.getSupplementalMap().values());

        Set<CalendarSupplemental> differenceSet = Sets.symmetricDifference(contentSupplementals, referenceSupplementals).immutableCopy();
        for (CalendarSupplemental diff : differenceSet) {
            recordMismatch(observation, content, reference, diff);
        }
    }

    private void recordMismatch(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference, CalendarSupplemental diff) {
        CalendarSupplemental contentSuppDiff = getMatchingSupplementalIfExists(content, diff);
        CalendarSupplemental referenceSuppDiff = getMatchingSupplementalIfExists(reference, diff);

        if (contentSuppDiff == null || referenceSuppDiff == null) {
            recordVersionMismatch(observation, contentSuppDiff, referenceSuppDiff);
        } else {
            checkForSupplementalCalDateMismatch(observation, contentSuppDiff, referenceSuppDiff);

            Set<CalendarSectionType> contentSectionTypes = contentSuppDiff.getSectionEntries().keySet();
            Set<CalendarSectionType> referenceSectionTypes = referenceSuppDiff.getSectionEntries().keySet();

            checkForTypeMismatch(observation, contentSectionTypes, referenceSectionTypes);
            checkForSuppEntryMismatch(observation, contentSuppDiff, referenceSuppDiff, contentSectionTypes, referenceSectionTypes);
        }
    }

    private void recordVersionMismatch(SpotCheckObservation<CalendarId> observation, CalendarSupplemental contentSuppDiff,
                                       CalendarSupplemental referenceSuppDiff) {
        // TODO: Version.Default toString() == "" -> not going to show well in a diff.
        observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.SUPPLEMENTAL_VERSION,
                                                      referenceSuppDiff == null ? "" : referenceSuppDiff.getVersion().toString(),
                                                      contentSuppDiff == null ? "" : contentSuppDiff.getVersion().toString()));
    }

    private void checkForSupplementalCalDateMismatch(SpotCheckObservation<CalendarId> observation, CalendarSupplemental contentSuppDiff,
                                                     CalendarSupplemental referenceSuppDiff) {
        String contentDate = contentSuppDiff.getCalDate() == null ? "" : contentSuppDiff.getCalDate().toString();
        String referenceDate = referenceSuppDiff.getCalDate() == null ? "" : referenceSuppDiff.getCalDate().toString();
        if (!StringUtils.equals(contentDate, referenceDate)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.SUPPLEMENTAL_CAL_DATE, referenceDate, contentDate));
        }

    }

    private void checkForTypeMismatch(SpotCheckObservation<CalendarId> observation, Set<CalendarSectionType> contentSectionTypes,
                                      Set<CalendarSectionType> referenceSectionTypes) {
        if (!Sets.symmetricDifference(contentSectionTypes, referenceSectionTypes).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.SUPPLEMENTAL_SECTION_TYPE, StringUtils.join(referenceSectionTypes, "\n"),
                    StringUtils.join(contentSectionTypes, "\n")));
        }
    }

    private void checkForSuppEntryMismatch(SpotCheckObservation<CalendarId> observation, CalendarSupplemental contentSuppDiff,
                                           CalendarSupplemental referenceSuppDiff, Set<CalendarSectionType> contentSectionTypes,
                                           Set<CalendarSectionType> referenceSectionTypes) {
        // Look at the union to avoid double reporting section type errors.
        Set<CalendarSectionType> sectionTypesUnion = Sets.union(contentSectionTypes, referenceSectionTypes);
        for (CalendarSectionType type : sectionTypesUnion) {
            Set<CalendarSupplementalEntry> contentEntries = ImmutableSortedSet.copyOf(contentSuppDiff.getEntriesBySection(type));
            Set<CalendarSupplementalEntry> referenceEntries = ImmutableSortedSet.copyOf(referenceSuppDiff.getEntriesBySection(type));

            if (!Sets.symmetricDifference(contentEntries, referenceEntries).isEmpty()) {
                observation.addMismatch(new SpotCheckMismatch(
                        SpotCheckMismatchType.SUPPLEMENTAL_ENTRY, StringUtils.join(referenceEntries, "\n"),
                        StringUtils.join(contentEntries, "\n")));
            }
        }
    }

    /**
     * Returns the matching supplemental from a calendar, null if it no match exists.
     */
    private CalendarSupplemental getMatchingSupplementalIfExists(Calendar content, CalendarSupplemental diff) {
        return content.getSupplementalMap().keySet().contains(diff.getVersion()) ? content.getSupplemental(diff.getVersion()) : null;
    }

    private void compareActiveLists(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference) {
        Set<CalendarActiveList> contentActiveLists = Sets.newHashSet(content.getActiveListMap().values());
        Set<CalendarActiveList> referenceActiveLists = Sets.newHashSet(reference.getActiveListMap().values());

        Set<CalendarActiveList> activeListDiffs = Sets.symmetricDifference(contentActiveLists, referenceActiveLists).immutableCopy();
        for (CalendarActiveList activeListDiff : activeListDiffs) {
            CalendarActiveList contentDiff = getActiveListIfExists(content, activeListDiff.getSequenceNo());
            CalendarActiveList referenceDiff = getActiveListIfExists(reference, activeListDiff.getSequenceNo());

            if (contentDiff == null || referenceDiff == null) {
                recordSequenceNoMismatch(observation, contentDiff, referenceDiff);
            } else {
                checkForActiveListCalDateMismatch(observation, contentDiff, referenceDiff);
                checkForNotesMismatch(observation, contentDiff, referenceDiff);
                checkForActiveListEntryMismatch(observation, contentDiff, referenceDiff);
            }
        }
    }

    private void recordSequenceNoMismatch(SpotCheckObservation<CalendarId> observation, CalendarActiveList contentDiff,
                                          CalendarActiveList referenceDiff) {
        observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_SEQUENCE_NO,
                                                      referenceDiff == null ? "" : referenceDiff.getSequenceNo().toString(),
                                                      contentDiff == null ? "" : contentDiff.getSequenceNo().toString()));
    }

    private void checkForActiveListCalDateMismatch(SpotCheckObservation<CalendarId> observation, CalendarActiveList contentDiff,
                                                   CalendarActiveList referenceDiff) {
        String contentCalDate = contentDiff.getCalDate() == null ? "" : contentDiff.getCalDate().toString();
        String referenceCalDate = referenceDiff.getCalDate() == null ? "" : referenceDiff.getCalDate().toString();
        if (!StringUtils.equals(contentCalDate, referenceCalDate)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_CAL_DATE, referenceCalDate, contentCalDate));
        }
    }

    private void checkForNotesMismatch(SpotCheckObservation<CalendarId> observation, CalendarActiveList contentDiff,
                                       CalendarActiveList referenceDiff) {
        if (!StringUtils.equals(contentDiff.getNotes(), referenceDiff.getNotes())) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_NOTES,
                                                          referenceDiff.getNotes(), contentDiff.getNotes()));
        }
    }

    private void checkForActiveListEntryMismatch(SpotCheckObservation<CalendarId> observation, CalendarActiveList contentDiff,
                                                 CalendarActiveList referenceDiff) {
        Set<CalendarEntry> contentDiffEntries = Sets.newHashSet(contentDiff.getEntries());
        Set<CalendarEntry> referenceDiffEntries = Sets.newHashSet(referenceDiff.getEntries());
        if (!Sets.symmetricDifference(contentDiffEntries, referenceDiffEntries).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.ACTIVE_LIST_ENTRY,
                    StringUtils.join(referenceDiffEntries, "\n"), StringUtils.join(contentDiffEntries, "\n")));
        }
    }

    /**
     * Get the active list with given sequence number from a calendar. return null if does not exist.
     */
    private CalendarActiveList getActiveListIfExists(Calendar calendar, int sequenceNo) {
        return calendar.getActiveListMap().keySet().contains(sequenceNo) ? calendar.getActiveList(sequenceNo) : null;
    }
}
