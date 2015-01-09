package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;

public interface SpotcheckRunService {


    /**
     * Performs the entire process required to generate a new spotcheck report.
     * First, the reference source is checked for new references. If new references are available, then they are saved
     * locally, parsed into reference data and put into the data store.  Finally, the reference data is used to generate
     * and save a new spotcheck report.
     *  Returns a reference to the generated report if it was successful, null if not
     *
     *  @return SpotCheckReport<BaseBillId>
     */
    public SpotCheckReport<BaseBillId> runSpotcheck();

    /**
     * Generates a new spotcheck report from reference data.
     * This includes checking for the most recent processed unchecked reference data, checking the reference data against
     *  Openleg data, and then saving the resulting report.
     *  Returns a reference to the generated report if it was successful, null if not
     *
     *  @return SpotCheckReport<BaseBillId>
     */
    public SpotCheckReport<BaseBillId> generateReport();
}
