package gov.nysenate.openleg.service.base;

public interface CachingService
{
    public void setupCaches();

    public void evictCaches();
}
