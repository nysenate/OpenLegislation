package gov.nysenate.openleg.service.spotcheck.calendar;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class CalendarCheckService implements SpotCheckService<CalendarEntryListId, Calendar, Calendar> {

    private static final SpotCheckRefType REF_TYPE = SpotCheckRefType.LBDC_CALENDAR_ALERT;

    public SpotCheckObservation<CalendarEntryListId> check(Calendar content, Calendar reference) {
        throw new NotImplementedException(":P");
    }

    /**
     * Compares the content of openleg and alert calendars.
     *
     * @param content   Openleg Calendar object, may be null if openleg is missing the calendar.
     * @param reference Alert Calendar object, should not be null.
     * @return List of all observations from checking this calendar. Returns empty observations if no mismatches are found.
     */
    public List<SpotCheckObservation<CalendarEntryListId>> checkAll(Calendar content, Calendar reference) {
        if (content == null) {
            return createObsMissingObservations(reference);
        }

        List<SpotCheckObservation<CalendarEntryListId>> observations = new ArrayList<>();
        observations.addAll(checkSupplementals(content, reference));
        observations.addAll(checkActiveLists(content, reference));
        return observations;
    }

    // Returns an observation containing a OBSERVE_DATA_MISSING mismatch for each supplemental and active list in calendar.
    private List<SpotCheckObservation<CalendarEntryListId>> createObsMissingObservations(Calendar reference) {
        List<SpotCheckObservation<CalendarEntryListId>> observations = new ArrayList<>();
        SpotCheckReferenceId spotcheckRefId = new SpotCheckReferenceId(REF_TYPE, reference.getPublishedDateTime());
        for (CalendarSupplemental sup : reference.getSupplementalMap().values()) {
            SpotCheckObservation<CalendarEntryListId> ob = createSupplementalObsMissingObservation(spotcheckRefId, sup);
            observations.add(ob);
        }
        for (CalendarActiveList al : reference.getActiveListMap().values()) {
            SpotCheckObservation<CalendarEntryListId> ob = createActiveListObsMissingObservation(spotcheckRefId, al);
            observations.add(ob);
        }
        return observations;
    }

    private SpotCheckObservation<CalendarEntryListId> createSupplementalObsMissingObservation(SpotCheckReferenceId spotcheckRefId, CalendarSupplemental sup) {
        SpotCheckObservation<CalendarEntryListId> ob = createSupplementalObservation(spotcheckRefId, sup);
        ob.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", sup.getCalendarSupplementalId().toString()));
        return ob;
    }

    private SpotCheckObservation<CalendarEntryListId> createSupplementalRefMissingObservation(SpotCheckReferenceId spotcheckRefId, CalendarSupplemental sup) {
        SpotCheckObservation<CalendarEntryListId> ob = createSupplementalObservation(spotcheckRefId, sup);
        ob.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.REFERENCE_DATA_MISSING, sup.getCalendarSupplementalId().toString(), ""));
        return ob;
    }

    private SpotCheckObservation<CalendarEntryListId> createActiveListObsMissingObservation(SpotCheckReferenceId spotcheckRefId, CalendarActiveList al) {
        SpotCheckObservation<CalendarEntryListId> ob = createActiveListObservation(spotcheckRefId, al);
        ob.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", al.getCalendarActiveListId().toString()));
        return ob;
    }

    private SpotCheckObservation<CalendarEntryListId> createActiveListRefMissingObservation(SpotCheckReferenceId spotcheckRefId, CalendarActiveList al) {
        SpotCheckObservation<CalendarEntryListId> ob = createActiveListObservation(spotcheckRefId, al);
        ob.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.REFERENCE_DATA_MISSING, al.getCalendarActiveListId().toString(), ""));
        return ob;
    }

    private SpotCheckObservation<CalendarEntryListId> createSupplementalObservation(SpotCheckReferenceId refId, CalendarSupplemental supplemental) {
        return new SpotCheckObservation<>(refId, new CalendarEntryListId(supplemental.getCalendarSupplementalId()));
    }

    private SpotCheckObservation<CalendarEntryListId> createActiveListObservation(SpotCheckReferenceId refId, CalendarActiveList activeList) {
        return new SpotCheckObservation<>(refId, new CalendarEntryListId(activeList.getCalendarActiveListId()));
    }

    @SuppressWarnings("Duplicates")
    private List<SpotCheckObservation<CalendarEntryListId>> checkSupplementals(Calendar content, Calendar reference) {
        List<SpotCheckObservation<CalendarEntryListId>> observations = new ArrayList<>();
        ImmutableSet<Version> contentVersions = ImmutableSet.copyOf(content.getSupplementalMap().keySet());
        ImmutableSet<Version> referenceVersions = ImmutableSet.copyOf(reference.getSupplementalMap().keySet());
        ImmutableSet<Version> allVersions = Stream.concat(contentVersions.stream(), referenceVersions.stream())
                .collect(ImmutableSet.toImmutableSet());
        SpotCheckReferenceId spotcheckRefId = new SpotCheckReferenceId(REF_TYPE, reference.getPublishedDateTime());

        // Check supplementals for observation/reference missing mismatches.
        ImmutableSet<Version> versionDifferences = Sets.symmetricDifference(contentVersions, referenceVersions).immutableCopy();
        for (Version v : versionDifferences) {
            if (referenceVersions.contains(v)) {
                // this version is missing from content, create obs missing mismatch.
                observations.add(createSupplementalObsMissingObservation(spotcheckRefId, reference.getSupplemental(v)));
            } else if (contentVersions.contains(v)) {
                // This version is missing from reference, create a ref missing mismatch.
                observations.add(createSupplementalRefMissingObservation(spotcheckRefId, content.getSupplemental(v)));
            }
        }

        // Find Versions in both calendars.
        ImmutableSet<Version> commonVersions = allVersions.stream()
                .filter(v -> !versionDifferences.contains(v))
                .collect(ImmutableSet.toImmutableSet());

        // Check fields of common supplementals.
        for (Version v : commonVersions) {
            CalendarSupplemental contentSup = content.getSupplemental(v);
            CalendarSupplemental referenceSup = reference.getSupplemental(v);

            // Doesn't matter which supplemental is used to create the observation, CalNo, CalYear, and Version should always be equal.
            SpotCheckObservation<CalendarEntryListId> ob = createSupplementalObservation(spotcheckRefId, referenceSup);
            observations.add(ob);
            checkSupplementalFields(ob, contentSup, referenceSup);

        }
        return observations;
    }

    private void checkSupplementalFields(SpotCheckObservation<CalendarEntryListId> observation, CalendarSupplemental contentSup, CalendarSupplemental referenceSup) {
        checkForSupplementalCalDateMismatch(observation, contentSup, referenceSup);
        checkForSuppEntryMismatch(observation, contentSup, referenceSup);
        checkForTypeMismatch(observation, contentSup, referenceSup);
    }

    private void checkForSupplementalCalDateMismatch(SpotCheckObservation<CalendarEntryListId> observation, CalendarSupplemental contentSuppDiff,
                                                     CalendarSupplemental referenceSuppDiff) {
        String contentDate = contentSuppDiff.getCalDate() == null ? "" : contentSuppDiff.getCalDate().toString();
        String referenceDate = referenceSuppDiff.getCalDate() == null ? "" : referenceSuppDiff.getCalDate().toString();
        if (!StringUtils.equals(contentDate, referenceDate)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.FLOOR_CAL_DATE, contentDate, referenceDate));
        }
    }

    private void checkForSuppEntryMismatch(SpotCheckObservation<CalendarEntryListId> observation, CalendarSupplemental contentSuppDiff,
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

    private void checkForTypeMismatch(SpotCheckObservation<CalendarEntryListId> observation, CalendarSupplemental contentSup, CalendarSupplemental referenceSup) {
        Set<CalendarSectionType> contentSectionTypes = contentSup.getSectionEntries().keySet();
        Set<CalendarSectionType> referenceSectionTypes = referenceSup.getSectionEntries().keySet();

        if (!Sets.symmetricDifference(contentSectionTypes, referenceSectionTypes).isEmpty()) {
            observation.addMismatch(new SpotCheckMismatch(
                    SpotCheckMismatchType.FLOOR_SECTION_TYPE, StringUtils.join(contentSectionTypes, "\n"), StringUtils.join(referenceSectionTypes, "\n")
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

    @SuppressWarnings("Duplicates")
    private List<SpotCheckObservation<CalendarEntryListId>> checkActiveLists(Calendar content, Calendar reference) {
        List<SpotCheckObservation<CalendarEntryListId>> observations = new ArrayList<>();
        ImmutableSet<Integer> contentSequenceNums = ImmutableSet.copyOf(content.getActiveListMap().keySet());
        ImmutableSet<Integer> referenceSequenceNums = ImmutableSet.copyOf(reference.getActiveListMap().keySet());
        ImmutableSet<Integer> allSequenceNums = Stream.concat(contentSequenceNums.stream(), referenceSequenceNums.stream())
                .collect(ImmutableSet.toImmutableSet());
        SpotCheckReferenceId spotcheckRefId = new SpotCheckReferenceId(REF_TYPE, reference.getPublishedDateTime());

        // Check Active Lists for observation/reference missing mismatches.
        ImmutableSet<Integer> sequenceNoDifferences = Sets.symmetricDifference(contentSequenceNums, referenceSequenceNums).immutableCopy();
        for (Integer seqNo : sequenceNoDifferences) {
            if (referenceSequenceNums.contains(seqNo)) {
                // This sequenceNo is missing from content, create obs missing mismatch
                observations.add(createActiveListObsMissingObservation(spotcheckRefId, reference.getActiveList(seqNo)));
            }
            else if (contentSequenceNums.contains(seqNo)) {
                // This sequenceNo is missing from reference, create ref missing mismatch.
                observations.add(createActiveListRefMissingObservation(spotcheckRefId, content.getActiveList(seqNo)));
            }
        }

        // Find sequenceNo's in both calendars.
        ImmutableSet<Integer> commonSequenceNums = allSequenceNums.stream()
                .filter(n -> !sequenceNoDifferences.contains(n))
                .collect(ImmutableSet.toImmutableSet());

        // Check fields of common active lists.
        for (Integer seqNo : commonSequenceNums) {
            CalendarActiveList contentActiveList = content.getActiveList(seqNo);
            CalendarActiveList referenceActiveList = reference.getActiveList(seqNo);

            // Doesn't matter which supplemental is used to create the observation, CalNo, CalYear, and SequenceNo should always be equal.
            SpotCheckObservation<CalendarEntryListId> ob = createActiveListObservation(spotcheckRefId, referenceActiveList);
            observations.add(ob);
            checkForActiveListCalDateMismatch(ob, contentActiveList, referenceActiveList);
            checkForActiveListEntryMismatch(ob, contentActiveList, referenceActiveList);
        }

        return observations;
    }

    private void checkForActiveListCalDateMismatch(SpotCheckObservation<CalendarEntryListId> observation, CalendarActiveList contentDiff,
                                                   CalendarActiveList referenceDiff) {
        String contentCalDate = contentDiff.getCalDate() == null ? "" : contentDiff.getCalDate().toString();
        String referenceCalDate = referenceDiff.getCalDate() == null ? "" : referenceDiff.getCalDate().toString();
        if (!StringUtils.equals(contentCalDate, referenceCalDate)) {
            observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.ACTIVE_LIST_CAL_DATE, contentCalDate, referenceCalDate));
        }
    }

    private void checkForActiveListEntryMismatch(SpotCheckObservation<CalendarEntryListId> observation, CalendarActiveList contentDiff,
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
