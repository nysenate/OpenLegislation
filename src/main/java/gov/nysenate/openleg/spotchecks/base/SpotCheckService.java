package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;

/**
 * An implementation of SpotCheckService provides the ability to compare processed
 * data stored in OpenLeg against reference data sets provided by external services such
 * as LRS (LBDC). The interface is kept generic to allow for multiple types of data
 * quality check implementations to use the same pattern.
 *
 * @param <ContentKey>  - The class that can uniquely identify an instance of ContentType (e.g. AgendaId)
 * @param <ContentType> - The class to perform data verification on (e.g. Bill, Agenda, etc).
 */
@FunctionalInterface
public interface SpotCheckService<ContentKey, ContentType, ReferenceType> {
    /**
     * Perform a check on 'content' against the supplied reference data 'reference'. A SpotCheckObservation
     * will be returned which will contain a list of any mismatches.
     *
     * @param content   ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return SpotCheckObservation<ContentKey>
     */
    SpotCheckObservation<ContentKey> check(ContentType content, ReferenceType reference);
}