package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.processor.base.ProcessService;

import java.util.List;

public interface SpotcheckRunService<ContentId> extends ProcessService {


    /**
     * Performs the entire process required to generate a new spotcheck report.
     * First, the reference source is checked for new references. If new references are available, then they are saved
     * locally, parsed into reference data and put into the data store.  Finally, the reference data is used to generate
     * and save a new spotcheck report.
     *  Returns a reference to the generated report if it was successful, null if not
     *
     *  @return SpotCheckReport<ContentId>
     */
    default List<SpotCheckReport<ContentId>> runSpotcheck() {
        collate();
        return generateReports();
    }

    /**
     * Generates new spotcheck reports from reference data.
     * This includes checking for the most recent processed unchecked reference data, checking the reference data against
     *  Openleg data, and then saving the resulting report.
     *  Returns a list of reports that were generated
     *
     *  @return SpotCheckReport<ContentId>
     */
    List<SpotCheckReport<ContentId>> generateReports();

    @Override
    default int ingest() {
        return generateReports().size();
    }
}
