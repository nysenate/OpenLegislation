package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.PaginationResponse;
import gov.nysenate.openleg.client.view.spotcheck.ObservationView;
import gov.nysenate.openleg.model.spotcheck.OpenMismatchQuery;
import gov.nysenate.openleg.model.spotcheck.SpotCheckOpenMismatches;

import java.util.TreeMap;

public class OpenMismatchesResponse<ContentKey> extends PaginationResponse {

    protected TreeMap<ContentKey, ObservationView<ContentKey>> observations;

    private OpenMismatchQuery query;

    public OpenMismatchesResponse(SpotCheckOpenMismatches<ContentKey> openMismatches,
                                  OpenMismatchQuery query) {
        super(openMismatches.getTotalCurrentMismatches(), query.getLimitOffset());
        this.success = true;
        this.observations = new TreeMap<>();
        openMismatches.getObservations().forEach((k, v) -> observations.put(k, new ObservationView<>(v)));
        this.query = query;
    }

    public TreeMap<ContentKey, ObservationView<ContentKey>> getObservations() {
        return observations;
    }

    public OpenMismatchQuery getQuery() {
        return query;
    }
}
