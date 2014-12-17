package gov.nysenate.openleg.service.spotcheck;

public interface SpotcheckRunService {


    /**
     * Performs the entire process required to generate a new spotcheck report.
     * First, the reference source is checked for new references. If new references are available, then they are saved
     * locally, parsed into reference data and put into the data store.  Finally, the reference data is used to generate
     * and save a new spotcheck report.
     */
    public void runSpotcheck();

    /**
     * Generates a new spotcheck report from reference data.
     * This includes checking for the most recentprocessed unchecked reference data, checking the reference data against
     *  Openleg data, and then saving the resulting report.
     */
    public void generateReport();
}
