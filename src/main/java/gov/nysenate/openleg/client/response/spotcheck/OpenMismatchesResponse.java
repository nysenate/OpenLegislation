package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.PaginationResponse;
import gov.nysenate.openleg.client.view.spotcheck.ObservationView;
import gov.nysenate.openleg.client.view.spotcheck.OpenMismatchSummaryView;
import gov.nysenate.openleg.client.view.spotcheck.SpotCheckSummaryView;
import gov.nysenate.openleg.model.spotcheck.OpenMismatchQuery;
import gov.nysenate.openleg.model.spotcheck.OpenMismatchSummary;
import gov.nysenate.openleg.model.spotcheck.RefTypeMismatchSummary;
import gov.nysenate.openleg.model.spotcheck.SpotCheckOpenMismatches;

import java.util.TreeMap;

public class OpenMismatchesResponse<ContentKey> extends PaginationResponse {

    protected TreeMap<ContentKey, ObservationView<ContentKey>> observations;

    protected OpenMismatchSummaryView summary;

    private OpenMismatchQuery query;

    public OpenMismatchesResponse(SpotCheckOpenMismatches<ContentKey> openMismatches,
                                  OpenMismatchSummary summary,
                                  OpenMismatchQuery query) {
        super(openMismatches.getTotalCurrentMismatches(), query.getLimitOffset());
        this.success = true;
        this.observations = new TreeMap<>();
        openMismatches.getObservations().forEach((k, v) -> observations.put(k, new ObservationView<>(v)));
        this.query = query;
        this.summary = new OpenMismatchSummaryView(summary);
    }

    public TreeMap<ContentKey, ObservationView<ContentKey>> getObservations() {
        return observations;
    }

    public OpenMismatchQuery getQuery() {
        return query;
    }

    public OpenMismatchSummaryView getSummary() {
        return summary;
    }
}
