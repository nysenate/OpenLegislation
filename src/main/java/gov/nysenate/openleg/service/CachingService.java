package gov.nysenate.openleg.service;

public interface CachingService
{
    public void setupCaches();

    public void evictCaches();
}
