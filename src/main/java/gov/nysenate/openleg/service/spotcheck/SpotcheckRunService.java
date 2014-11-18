package gov.nysenate.openleg.service.spotcheck;

public interface SpotcheckRunService {

    /**
     * Performs the entire process required to generate a new spotcheck report.
     * This includes checking for reference data, processing the reference data, checking the reference data against
     *  Openleg data, and generating and saving the resulting report.
     */
    public void runSpotcheck();
}
