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
public interface SpotCheckService<ContentKey, ContentType>
{
    /**
     *
     *
     * @param content ContentType
     * @return SpotCheckObservation<ContentKey>
     * @throws ReferenceDataNotFoundEx
     */
    public SpotCheckObservation<ContentKey> check(ContentType content)
                                                  throws ReferenceDataNotFoundEx;

    /**
     *
     *
     * @param content ContentType
     * @param latestDate LocalDateTime
     * @return SpotCheckObservation<ContentKey>
     * @throws ReferenceDataNotFoundEx
     */
    public SpotCheckObservation<ContentKey> check(ContentType content, LocalDateTime latestDate)
                                                  throws ReferenceDataNotFoundEx;
}