package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportSummary;

import java.util.TreeMap;

public class ReportDetailView<ContentKey> extends ReportInfoView {
    public ReportDetailView(SpotCheckReportSummary summary) {
        super(summary);
    }
//    protected TreeMap<ContentKey, ObservationView<ContentKey>> observations;
//
//    public ReportDetailView(SpotCheckReport<ContentKey> report) {
//        super(report);
//        if (report != null) {
//            this.observations = new TreeMap<>();
//            report.getObservations().forEach((k, v) -> observations.put(k, new ObservationView<>(v)));
//        }
//    }
//
//    public TreeMap<ContentKey, ObservationView<ContentKey>> getObservations() {
//        return observations;
//    }

    @Override
    public String getViewType() {
        return "report-details";
    }
}
