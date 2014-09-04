package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewList;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class ObservationView<ContentKey>
{
    protected LocalDateTime refDateTime;
    protected ContentKey key;
    protected LocalDateTime observedDateTime;
    protected ViewList<MismatchView> mismatches;

    public ObservationView(SpotCheckObservation<ContentKey> observation) {
        if (observation != null) {
            this.refDateTime = observation.getReferenceId().getRefActiveDateTime();
            this.key = observation.getKey();
            this.observedDateTime = observation.getObservedDateTime();
            this.mismatches = new ViewList<>(
                observation.getMismatches().values().stream()
                    .map(m -> new MismatchView(m, observation.getPriorMismatches().get(m.getMismatchType())))
                    .collect(Collectors.toList()));
        }
    }

    public LocalDateTime getRefDateTime() {
        return refDateTime;
    }

    public ContentKey getKey() {
        return key;
    }

    public LocalDateTime getObservedDateTime() {
        return observedDateTime;
    }

    public ViewList<MismatchView> getMismatches() {
        return mismatches;
    }
}
