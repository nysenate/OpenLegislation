package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.PaginationResponse;
import gov.nysenate.openleg.client.view.spotcheck.OpenMismatchesView;
import gov.nysenate.openleg.model.spotcheck.OpenMismatchQuery;

public class OpenMismatchesResponse<ContentKey> extends PaginationResponse {

    private OpenMismatchesView<ContentKey> observations;

    private OpenMismatchQuery query;

    public OpenMismatchesResponse(OpenMismatchesView<ContentKey> observations,
                                  OpenMismatchQuery query, int total) {
        super(total, query.getLimitOffset());
        this.observations = observations;
        this.query = query;
    }

    public OpenMismatchesView<ContentKey> getObservations() {
        return observations;
    }

    public OpenMismatchQuery getQuery() {
        return query;
    }
}
