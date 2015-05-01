package gov.nysenate.openleg.dao.scraping;

import gov.nysenate.openleg.model.bill.BaseBillId;
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

/**
 * Created by kyle on 11/12/14.
 */
@Repository
public class SenateAgnScraper extends LRSScraper{
    private static final Logger logger = Logger.getLogger(LRSScraper.class);
    String senateAgendas = "http://public.leginfo.state.ny.us/menugetf.cgi?COMMONQUERY=SENAGEN";
    private File outfile;

    protected URL senateURL;
    private File senateAgendaDirectory;


    @PostConstruct
    public void init() throws IOException {
        senateURL = new URL(senateAgendas);
        this.senateAgendaDirectory = new File(environment.getScrapedStagingDir(), "sen-agenda");
        try {
            FileUtils.forceMkdir(senateAgendaDirectory);
        } catch (IOException ex) {
            logger.error("could not create assembly agenda scraped staging dir " + senateAgendaDirectory.getPath());
        }
    }

    @Override
    public int scrape() throws IOException {
        logger.info("SCRETCHING landing page.");
        Document doc = Jsoup.connect(senateURL.toString()).timeout(10000).get();

        System.out.println(doc.text());

        Element image = doc.select("frame").get(1);
        String url = image.absUrl("src");

        System.out.println("THIS IS THE URL: :::::::::::::::::   " + url);

        logger.info("Searching for link to bottom half");
        Document agendaPage = Jsoup.connect(url).timeout(10000).get();
        logger.info("Fetching bottom half");

        System.out.println(agendaPage.text());
        Elements links = agendaPage.select("a");

        for (Element link : links){
            if (link.text().equalsIgnoreCase("All Committee Agendas")){
                String absHref = link.attr("abs:href");
                System.out.println(absHref);
                URL contentURL = new URL(absHref);

                String filename = dateFormat.format(LocalDateTime.now()) + ".all_senate_agendas.html";
                outfile = new File(senateAgendaDirectory, filename);
                logger.info("Fetching all committee agendas");
                String contents = IOUtils.toString(contentURL);
                logger.info("Writing content to "+filename);
                FileUtils.write(outfile, contents);
            }
        }
        ArrayList<File> list = new ArrayList<File>();
        list.add(outfile);
        return list.size();

    }
}
