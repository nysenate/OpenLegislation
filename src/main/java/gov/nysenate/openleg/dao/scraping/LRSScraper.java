package gov.nysenate.openleg.dao.scraping;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kyle on 11/3/14.
 */
public abstract class LRSScraper {
    private static final Logger logger = Logger.getLogger(LRSScraper.class);
    @Autowired
    protected Environment environment;

    protected final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("'D'yyyyMMdd'.T'HHmmss");
    protected final Pattern relativeBasePattern = Pattern.compile("(http://.+/).*");
    protected final Pattern absoluteBasePattern = Pattern.compile("(http://.+?)/.*");
    protected final Pattern linkPattern = Pattern.compile("<a href=\\\"(.*?)\\\">(.+?)</a>");
    protected final Pattern bottomPattern = Pattern.compile("src=\\\"(frmload\\.cgi\\?BOT-([0-9]+))\\\">");

    /**
     * Attempts to scrape LRS data.
     * @return the number of scraped files
     * @throws IOException
     */
    public abstract int scrape() throws IOException;

    public URL resolveLink(URL url, String link) throws MalformedURLException
    {
        Pattern basePattern = link.startsWith("/") ? absoluteBasePattern : relativeBasePattern;
        Matcher baseMatcher = basePattern.matcher(url.toString());
        if (baseMatcher.find()) {
            String base = baseMatcher.group(1);
            return new URL(base+link);
        }
        else {
            logger.error("Couldn't extract the link base");
            return null;
        }
    }
}
