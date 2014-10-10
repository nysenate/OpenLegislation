package gov.nysenate.openleg.service.base;

import java.util.Set;

public interface SearchParameters
{
    /**
     * A validation function that returns false if the given parameters are not valid
     * @return
     */
    public boolean isValid();

    /**
     * Gets a set of parameter names that are invalid
     */
    public Set<String> getInvalidParams();

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
