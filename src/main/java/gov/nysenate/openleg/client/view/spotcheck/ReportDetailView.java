package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;

import java.util.HashMap;
import java.util.Map;

public class ReportDetailView<ContentKey> extends ReportInfoView<ContentKey>
{
    protected Map<ContentKey, ObservationView<ContentKey>> observations;

    public ReportDetailView(SpotCheckReport<ContentKey> report) {
        super(report);
        if (report != null) {
            this.observations = new HashMap<>();
            report.getObservations().forEach((k, v) -> observations.put(k, new ObservationView<>(v)));
        }
    }

    public Map<ContentKey, ObservationView<ContentKey>> getObservations() {
        return observations;
    }
}
