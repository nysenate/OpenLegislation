package gov.nysenate.openleg.dao.scraping;

import java.net.URL;

/**
 * Indicates that an exception has occurred while trying to retrieve a web resource for scraping
 */
public class ScrapingException extends RuntimeException {

    public ScrapingException(String cause) {
        super(cause);
    }

    public ScrapingException(String url, Throwable cause) {
        super("A IO exception occurred while retrieving " + url + " for scraping.", cause);
    }

    public ScrapingException(URL url, Throwable cause) {
        this(url.toString(), cause);
    }
}
