package gov.nysenate.openleg.client.response.spotcheck;

import gov.nysenate.openleg.client.response.base.PaginationResponse;
import gov.nysenate.openleg.client.view.spotcheck.ObservationView;
import gov.nysenate.openleg.client.view.spotcheck.MismatchSummaryView;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.spotcheck.DeNormSpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.MismatchQuery;
import gov.nysenate.openleg.model.spotcheck.MismatchSummary;

import java.util.TreeMap;

public class OpenMismatchesResponse<ContentKey> extends PaginationResponse {

    protected TreeMap<ContentKey, ObservationView<ContentKey>> observations;

    protected MismatchSummaryView summary;

    private MismatchQuery query;

    public OpenMismatchesResponse(PaginatedList<DeNormSpotCheckMismatch> openMismatches,
                                  MismatchSummary summary,
                                  MismatchQuery query) {
        super(openMismatches.getTotal(), openMismatches.getLimOff());
        this.success = true;
        this.observations = new TreeMap<>();
//        openMismatches.getMismatches().forEach((k, v) -> observations.put(k, new ObservationView<>(v)));
        this.query = query;
//        this.summary = new MismatchSummaryView(summary);
    }

    public TreeMap<ContentKey, ObservationView<ContentKey>> getObservations() {
        return observations;
    }

    public MismatchQuery getQuery() {
        return query;
    }

    public MismatchSummaryView getSummary() {
        return summary;
    }
}
