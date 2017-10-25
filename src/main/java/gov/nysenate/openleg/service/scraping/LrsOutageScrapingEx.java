package gov.nysenate.openleg.service.scraping;

public class LrsOutageScrapingEx extends Exception {

    Object contentId;

    public LrsOutageScrapingEx(Object contentId) {
        super("The requested content: " + contentId +
                " is not parsable due to an lrs outage (received error response)");
    }

    public Object getContentId() {
        return contentId;
    }
}
