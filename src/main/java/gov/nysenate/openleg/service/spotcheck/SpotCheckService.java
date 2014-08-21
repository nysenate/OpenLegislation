package gov.nysenate.openleg.service.spotcheck;

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
     * Overload of {@link #check(Object)} with an option to specify the latest datetime for
     * which reference data should be retrieved for. Since reference data can typically
     * be stored as a snapshot, perform the check such that the reference data was active
     * prior to the 'latestDateTime'
     *
     * @param content ContentType - The content to check
     * @param latestDateTime LocalDateTime - Reference data must be active before this datetime
     * @return SpotCheckObservation<ContentKey>
     * @throws ReferenceDataNotFoundEx
     */
    public SpotCheckObservation<ContentKey> check(ContentType content, LocalDateTime latestDateTime)
                                                  throws ReferenceDataNotFoundEx;

    public SpotCheckObservation<ContentKey> check(ContentType content, ReferenceType reference);
}