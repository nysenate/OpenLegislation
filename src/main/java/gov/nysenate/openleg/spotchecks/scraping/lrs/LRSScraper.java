package gov.nysenate.openleg.spotchecks.scraping.lrs;

import gov.nysenate.openleg.common.script.BaseScript;
import gov.nysenate.openleg.common.util.FileIOUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LRSScraper extends BaseScript {
    private static final Logger logger = LogManager.getLogger(LRSScraper.class);
    private static final String allCalendars = "http://leginfo.state.ny.us/ASMSEN/menugetl.cgi?COMMONQUERY=CALENDAR";
    private static final String assemblyAgendas = "http://public.leginfo.state.ny.us/menugetf.cgi?COMMONQUERY=SENAGEN";
    private static final String senateAgendas = "http://public.leginfo.state.ny.us/menugetf.cgi?COMMONQUERY=ASMAGEN";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("'D'yyyyMMdd'.T'HHmmss");
    private static final Pattern relativeBasePattern = Pattern.compile("(http://.+/).*");
    private static final Pattern absoluteBasePattern = Pattern.compile("(http://.+?)/.*");
    private static final Pattern linkPattern = Pattern.compile("<a href=\"(.*?)\">(.+?)</a>");
    private static final Pattern bottomPattern = Pattern.compile("src=\"(frmload\\.cgi\\?BOT-(\\d+))\">");

    public static void main(String[] args) throws Exception {
        logger.info("running");
        CommandLine cmd = getCommandLine(new Options(), args);
        new LRSScraper().execute(cmd);
    }

    public void scrapeCalendars(URL landingURL, File directory, Date currentTime) throws IOException {
        logger.info("Fetching landing page.");
        String landingPage = IOUtils.toString(landingURL.openStream(), Charset.defaultCharset());
        Matcher tokenMatcher = bottomPattern.matcher(landingPage);
        logger.info("Searching for link to bottom half");
        if (tokenMatcher.find()) {
            String link = tokenMatcher.group(1);
            URL contentURL = resolveLink(landingURL, link);
            logger.info("Fetching bottom half");
            String contentPage = IOUtils.toString(contentURL.openStream(), Charset.defaultCharset())
                    .replace("\r\n", " ");
            Matcher linkMatcher = linkPattern.matcher(contentPage);
            while(linkMatcher.find()) {
                URL linkURL = resolveLink(contentURL, linkMatcher.group(1));
                String filename = dateFormat.format(currentTime)+"."+linkMatcher.group(2).trim().replace(".", "")
                        .replace(" ", "_").toLowerCase()+".html"; // add 0 for last active list and increment
                File outfile = new File(directory, filename);
                logger.info("Fetching "+linkMatcher.group(2).trim());
                String contents = IOUtils.toString(linkURL, Charset.defaultCharset());
                logger.info("Writing content to "+filename);
                FileIOUtils.write(outfile, contents);
            }
        }
    }

    public void scrapeAgendas(URL landingURL, File directory, Date currentTime) throws IOException {
        logger.info("Fetching landing page.");
        String landingPage = IOUtils.toString(landingURL.openStream(), Charset.defaultCharset());
        Matcher tokenMatcher = bottomPattern.matcher(landingPage);
        logger.info("Searching for link to bottom half");
        if (tokenMatcher.find()) {
            String link = tokenMatcher.group(1);
            URL contentURL = resolveLink(landingURL, link);
        //calen
            logger.info("Fetching bottom half");
            String contentPage = IOUtils.toString(contentURL.openStream(), Charset.defaultCharset())
                    .replace("\r\n", " ");
            Matcher linkMatcher = linkPattern.matcher(contentPage);
            logger.info("Searching for all committee agendas link");
            while (linkMatcher.find()) {
                if (linkMatcher.group(2).trim().equals("All Committee Agendas")) {
                    URL linkURL = resolveLink(contentURL, linkMatcher.group(1));
                    String filename = dateFormat.format(currentTime)+".all_agendas.html";
                    File outfile = new File(directory, filename);
                    logger.info("Fetching all committee agendas");
                    String contents = IOUtils.toString(linkURL, Charset.defaultCharset());
                    logger.info("Writing content to "+filename);
                    FileIOUtils.write(outfile, contents);
                }
            }
        } else {
            logger.error("NO MATCH on pattern: "+tokenMatcher.toString());
            logger.error(landingPage);
        }
    }

    public void execute(CommandLine opts) throws IOException {
        String[] args = opts.getArgs();
        File directory = new File(args[0]);
        Date currentTime = new Date();
        scrapeCalendars(new URL(allCalendars), new File(directory, "CALENDAR"), currentTime);
        scrapeAgendas(new URL(senateAgendas), new File(directory, "SENAGEN"), currentTime);
        scrapeAgendas(new URL(assemblyAgendas), new File(directory, "ASMAGEN"), currentTime);
    }

    public URL resolveLink(URL url, String link) throws MalformedURLException {
        Pattern basePattern = link.startsWith("/") ? absoluteBasePattern : relativeBasePattern;
        Matcher baseMatcher = basePattern.matcher(url.toString());
        if (baseMatcher.find()) {
            String base = baseMatcher.group(1);
            return new URL(base+link);
        } else {
            logger.error("Couldn't extract the link base");
            return null;
        }
    }
}
