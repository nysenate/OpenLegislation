package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckOpenMismatches;

import java.util.TreeMap;

public class OpenMismatchesView<ContentKey> implements ViewObject {

    protected TreeMap<ContentKey, ObservationView<ContentKey>> observations;

    public OpenMismatchesView(SpotCheckOpenMismatches<ContentKey> openMismatches) {
        if (openMismatches != null) {
            this.observations = new TreeMap<>();
            openMismatches.getObservations().forEach((k, v) -> observations.put(k, new ObservationView<>(v)));
        }
    }

    @Override
    public String getViewType() {
        return "spotcheck open mismatches";
    }

    public TreeMap<ContentKey, ObservationView<ContentKey>> getObservations() {
        return observations;
    }
}
