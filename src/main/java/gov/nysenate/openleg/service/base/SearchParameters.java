package gov.nysenate.openleg.service.base;

public interface SearchParameters {

    /**
     * A validation function that returns false if the given parameters are not valid
     * @return
     */
    public boolean isValid();

    /**
     * Returns the number of set parameters
     * @return
     */
    public int paramCount();

    /**
     * Prints the parameters
     * @return
     */
    @Override
    public String toString();
}
