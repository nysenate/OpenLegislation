package gov.nysenate.openleg.dao.scraping;

import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public class CalendarScraper extends LRSScraper{
    private static final Logger logger = Logger.getLogger(LRSScraper.class);
    protected static final String allCalendars = "http://leginfo.state.ny.us/ASMSEN/menugetl.cgi?COMMONQUERY=CALENDAR";
    private File outfile = null;


    private File calendarDirectory;

    @PostConstruct
    public void init() throws IOException{
        this.calendarDirectory = new File(environment.getScrapedStagingDir(), "calendar");
        try {
            FileUtils.forceMkdir(calendarDirectory);
        } catch (IOException ex) {
            logger.error("could not create assembly agenda scraped staging dir " + calendarDirectory.getPath());
        }
    }


    //Active list sequence number get from parsing the page here with all the calendars
    //ToDo Scraping doesn't handle going through the supplemental calendar intermediary page
    @Override
    protected int doScrape() throws IOException{
        logger.info("SCRETCHING landing page.");
        Document doc = getJsoupDocument(allCalendars);

        System.out.println(doc.text());

        Element image = doc.select("frame").get(1);
        String url = image.absUrl("src");

        System.out.println("THIS IS THE URL: :::::::::::::::::   " + url);

        logger.info("Searching for link to bottom half");
        Document calendarPage = getJsoupDocument(url);
        logger.info("Fetching bottom half");

        System.out.println(calendarPage.text());
        Elements rows = calendarPage.select("tr");
        rows.remove(0);
        for (Element row : rows) {

            Elements td = row.getElementsByTag("td");
            int activeCount = 0;
            if (!td.get(0).text().equals("\u00a0")){      //&nbsp
                System.out.println(td.get(0).text());
                Element link = td.get(0).select("a").first();
                String absHref = link.attr("abs:href");
                System.out.println(absHref);
                URL contentURL = new URL(absHref);
                String filename = null;
                String activeInfo = null;
                if (td.get(1).text().startsWith("Active List")) {       //Create the file for parsing
                    System.out.println();
                    System.out.println();
                    LocalDateTime listDate =  LocalDateTime. parse(td.get(2).text(), DateUtils.LRS_WEBSITE_DATETIME_FORMAT);
                    System.out.println("PARSE DATE with formatter:::: " + listDate);
                    filename = dateFormat.format(LocalDateTime.now()) + "." +
                            td.get(0).text().trim().replace(".", "").replace(" ", "_").toLowerCase() +
                            "_active_list_" + listDate + ".html";
                    activeInfo = "<h1>Active List</h1><h1>" + td.get(2).text() + "</h1><h1>" + activeCount +"</h1>\n";
                    activeCount++; //Oldest (highest) active list is the number 0
                }else if (td.get(1).text().startsWith("Debate List")) {
                    filename = dateFormat.format(LocalDateTime.now()) + "." +
                            td.get(0).text().trim()
                                    .replace(".", "")
                                    .replace(" ", "_")
                                    .toLowerCase() +
                            "_debate_List" + ".html";
                }else{
                    filename = dateFormat.format(LocalDateTime.now()) + "." +
                            td.get(0).text().trim()
                                    .replace(".", "")
                                    .replace(" ", "_")
                                    .replace("\u00a0", "")
                                    .toLowerCase() +
                            ".html";
                }
                outfile = new File(calendarDirectory, filename);
                logger.info("Fetching " + td.get(1).text().trim());

                String contents = activeInfo + getUrlContents(contentURL);
                logger.info("Writing content to " + outfile);
                FileUtils.write(outfile, contents);
            }
        }
        ArrayList<File> list = new ArrayList<File>();
        list.add(outfile);
        return 1;
    }


}