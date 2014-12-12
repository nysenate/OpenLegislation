package gov.nysenate.openleg.processor.base;

/**
 * An interface that describes a generic data process service that processes data in two steps:
 * collate and ingest
 */
public interface ProcessService
{
    /**
     * Perform pre-processing steps
     * @return int the number of collated items
     */
    public int collate();

    /**
     * Perform the data processing
     * @return int the number of processed items
     */
    public int ingest();

    /**
     * @return the name of the data type that is collated
     */
    public String getCollateType();

    /**
     * @return the name of the data type that is ingested
     */
    default public String getIngestType() {
        return getCollateType();
    }
}
