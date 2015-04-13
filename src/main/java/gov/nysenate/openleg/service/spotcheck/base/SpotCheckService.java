package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;

import java.time.LocalDateTime;

/**
 * An implementation of SpotCheckService provides the ability to compare processed
 * data stored in OpenLeg against reference data sets provided by external services such
 * as LRS (LBDC). The interface is kept generic to allow for multiple types of data
 * quality check implementations to use the same pattern.
 *
 * @param <ContentKey> - The class that can uniquely identify an instance of ContentType (e.g AgendaId)
 * @param <ContentType> - The class to perform data verification on (e.g. Bill, Agenda, etc).
 */
public interface SpotCheckService<ContentKey, ContentType, ReferenceType>
{
    /**
     * Perform a check on 'content' against the latest reference data available. If no
     * reference data can be matched for the given content, the ReferenceDataNotFoundEx
     * will be thrown. A SpotCheckObservation will be returned which will contain a
     * listing of any mismatches.
     *
     * @param content ContentType - The content to check
     * @return SpotCheckObservation<ContentKey>
     * @throws ReferenceDataNotFoundEx
     */
    public SpotCheckObservation<ContentKey> check(ContentType content)
                                                  throws ReferenceDataNotFoundEx;

    /**
     * Overload of {@link #check(Object)} with an option to specify the active date range in
     * which reference data should be retrieved for. The latest reference data within that date range
     * will be used, or a ReferenceDataNotFoundEx will be thrown if none exist.
     *
     * @param content ContentType - The content to check
     * @param start LocalDateTime - Reference data must be active after/on this datetime
     * @param end LocalDateTime - Reference data must be active before/on this datetime
     * @return SpotCheckObservation<ContentKey>
     * @throws ReferenceDataNotFoundEx
     */
    public SpotCheckObservation<ContentKey> check(ContentType content, LocalDateTime start, LocalDateTime end)
                                                  throws ReferenceDataNotFoundEx;

    /**
     * Perform a check on 'content' against the supplied reference data 'reference'. A SpotCheckObservation
     * will be returned which will contain a list of any mismatches.
     *
     * @param content ContentType - The content to check
     * @param reference ReferenceType - The reference content to use for comparison
     * @return SpotCheckObservation<ContentKey>
     */
    public SpotCheckObservation<ContentKey> check(ContentType content, ReferenceType reference);
}