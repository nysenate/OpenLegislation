package gov.nysenate.openleg.dao.scraping;

import java.net.URL;

/**
 * Indicates that an exception has occurred while trying to retrieve a web resource for scraping
 */
public class ScrapingIOException extends RuntimeException {

    public ScrapingIOException(String url, Throwable cause) {
        super("A IO exception occurred while retrieving " + url + " for scraping.", cause);
    }

    public ScrapingIOException(URL url, Throwable cause) {
        this(url.toString(), cause);
    }
}
