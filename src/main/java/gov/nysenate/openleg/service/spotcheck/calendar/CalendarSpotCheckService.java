package gov.nysenate.openleg.service.spotcheck.calendar;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
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
            compareSupplementalMap(observation, content, reference);
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

    private void compareSupplementalMap(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference) {
        compareSupplementalVersions(observation, content, reference);
        compareSupplementals(observation, content, reference);
    }

    private void compareSupplementalVersions(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference) {
        Set<Version> contentVersions = content.getSupplementalMap().keySet();
        Set<Version> referenceVersions = reference.getSupplementalMap().keySet();
        if (!Sets.symmetricDifference(contentVersions, referenceVersions).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.CALENDAR_SUPP_VERSION,
                                                          StringUtils.join(referenceVersions), StringUtils.join(contentVersions)));
        }
    }

    private void compareSupplementals(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference) {
        // Converting to Set should be ok, no duplicate supplementals expected since each has a unique Version.
        Set<CalendarSupplemental> contentSupplementals = Sets.newHashSet(content.getSupplementalMap().values());
        Set<CalendarSupplemental> referenceSupplementals = Sets.newHashSet(reference.getSupplementalMap().values());

        Set<CalendarSupplemental> differenceSet = Sets.symmetricDifference(contentSupplementals, referenceSupplementals).immutableCopy();
        for (CalendarSupplemental diff : differenceSet) {
            CalendarSupplemental contentSuppDiff = content.getSupplementalMap().keySet().contains(diff.getVersion()) ? content.getSupplemental(diff.getVersion()) : null;
            CalendarSupplemental referenceSuppDiff = reference.getSupplementalMap().keySet().contains(diff.getVersion()) ? reference.getSupplemental(diff.getVersion()) : null;

            if (contentSuppDiff == null || referenceSuppDiff == null) {
                observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.CALENDAR_SUPP_ENTRY,
                                                              referenceSuppDiff == null ? "" : referenceSuppDiff.toString(),
                                                              contentSuppDiff == null ? "" : contentSuppDiff.toString()));
            } else {
                compareSectionTypes(observation, contentSuppDiff, referenceSuppDiff);
                compareSuppEntries(observation, contentSuppDiff, referenceSuppDiff);
            }
        }
    }

    private void compareSectionTypes(SpotCheckObservation<CalendarId> observation, CalendarSupplemental contentSuppDiff, CalendarSupplemental referenceSuppDiff) {
        Set<CalendarSectionType> contentSectionTypes = contentSuppDiff.getSectionEntries().keySet();
        Set<CalendarSectionType> referenceSectionTypes = referenceSuppDiff.getSectionEntries().keySet();

        if (!Sets.symmetricDifference(contentSectionTypes, referenceSectionTypes).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.CALENDAR_SECTION_TYPE, StringUtils.join(referenceSectionTypes, "\n"),
                    StringUtils.join(contentSectionTypes, "\n")));
        }
    }

    private void compareSuppEntries(SpotCheckObservation<CalendarId> observation, CalendarSupplemental contentSuppDiff, CalendarSupplemental referenceSuppDiff) {
        Set<CalendarSupplementalEntry> contentSuppEntries = Sets.newHashSet(contentSuppDiff.getAllEntries());
        Set<CalendarSupplementalEntry> referenceSuppEntries = Sets.newHashSet(referenceSuppDiff.getAllEntries());

        if (!Sets.symmetricDifference(contentSuppEntries, referenceSuppEntries).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.CALENDAR_SUPP_ENTRY, StringUtils.join(referenceSuppEntries.toString(), "\n"),
                    StringUtils.join(contentSuppEntries.toString(), "\n")));
        }
    }

    private void compareActiveLists(SpotCheckObservation<CalendarId> observation, Calendar content, Calendar reference) {
        // Safe to convert to Set since each contain link to their TreeMap key value. (assuming they have been created correctly)
        Set<CalendarActiveList> contentActiveLists = Sets.newHashSet(content.getActiveListMap().values());
        Set<CalendarActiveList> referenceActiveLists = Sets.newHashSet(reference.getActiveListMap().values());

        Set<CalendarActiveList> activeListDiffs = Sets.symmetricDifference(contentActiveLists, referenceActiveLists).immutableCopy();
        for (CalendarActiveList activeListDiff : activeListDiffs) {
            // Examine in more detail all Active List differences. So we can find differences between individual active list entries.
            CalendarActiveList contentDiff = getActiveListIfExists(content, activeListDiff.getSequenceNo());
            CalendarActiveList referenceDiff = getActiveListIfExists(reference, activeListDiff.getSequenceNo());

            if (contentDiff == null || referenceDiff == null) {
                observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.CALENDAR_ACTIVE_LIST,
                                                              referenceDiff == null ? "" : referenceDiff.toString(),
                                                              contentDiff == null ? "" : contentDiff.toString()));
            } else {
                compareActiveListInfo(observation, contentDiff, referenceDiff);
                compareActiveListEntries(observation, contentDiff, referenceDiff);
            }
        }
    }

    /**
     * Get the active list with given sequence number from a calendar. return null if does not exist.
     */
    private CalendarActiveList getActiveListIfExists(Calendar calendar, int sequenceNo) {
        return calendar.getActiveListMap().keySet().contains(sequenceNo) ? calendar.getActiveList(sequenceNo) : null;
    }

    private void compareActiveListInfo(SpotCheckObservation<CalendarId> observation, CalendarActiveList contentDiff, CalendarActiveList referenceDiff) {
        Set<String> contentDiffInfo = new HashSet<>();
        contentDiffInfo.add(contentDiff.toString());
        Set<String> referenceDiffInfo = new HashSet<>();
        referenceDiffInfo.add(referenceDiff.toString());

        if (!Sets.symmetricDifference(contentDiffInfo, referenceDiffInfo).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.CALENDAR_ACTIVE_LIST,
                    StringUtils.join(referenceDiffInfo, "\n"), StringUtils.join(contentDiffInfo, "\n")));
        }
    }

    private void compareActiveListEntries(SpotCheckObservation<CalendarId> observation, CalendarActiveList contentDiff, CalendarActiveList referenceDiff) {
        // compare entries
        Set<CalendarActiveListEntry> contentDiffEntries = Sets.newHashSet(contentDiff.getEntries());
        Set<CalendarActiveListEntry> referenceDiffEntries = Sets.newHashSet(referenceDiff.getEntries());
        if (!Sets.symmetricDifference(contentDiffEntries, referenceDiffEntries).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.CALENDAR_ACTIVE_LIST_ENTRY,
                    StringUtils.join(referenceDiffEntries, "\n"), StringUtils.join(contentDiffEntries, "\n")));
        }
    }
}
