package gov.nysenate.openleg.spotchecks.scraping.assembly.agenda;

import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.spotchecks.scraping.LRSScraper;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by kyle on 11/10/14.
 */
@Repository
public class AssemblyAgnScraper extends LRSScraper {
    private static final Logger logger = LoggerFactory.getLogger(LRSScraper.class);
    private static final String assemblyAgendas = "http://public.leginfo.state.ny.us/menugetf.cgi?COMMONQUERY=ASMAGEN";

    protected URL agendaURL;
    private File assemblyAgendaDirectory;
    private File outfile = null;

    @PostConstruct
    public void init() throws IOException {
        agendaURL = new URL(assemblyAgendas);
        this.assemblyAgendaDirectory = new File(environment.getScrapedStagingDir(), "ass-agenda");
        try {
            FileUtils.forceMkdir(assemblyAgendaDirectory);
        } catch (IOException ex) {
            logger.error("could not create assembly agenda scraped staging dir " + assemblyAgendaDirectory.getPath());
        }
    }

    @Override
    protected int doScrape() throws IOException {
        System.out.println("ASSEMBLY AGENDA DIRECTORY ::::::: " + assemblyAgendaDirectory);
        logger.info("SCRETCHING landing page.");
        Document doc = getJsoupDocument(agendaURL.toString());

        System.out.println(doc.text());

        Element image = doc.select("frame").get(1);
        String url = image.absUrl("src");

        System.out.println("THIS IS THE URL: :::::::::::::::::   " + url);

        logger.info("Searching for link to bottom half");
        Document agendaPage = getJsoupDocument(url);
        logger.info("Fetching bottom half");

        System.out.println(agendaPage.text());
        Elements links = agendaPage.select("a");

        for (Element link : links){
            if ("All Committee Agendas".equalsIgnoreCase(link.text())){ //possibility for error in generating filename
                String absHref = link.attr("abs:href");
                System.out.println(absHref);
                URL contentURL = new URL(absHref);

                String filename = dateFormat.format(LocalDateTime.now()) + ".all_assembly_agendas.html";
                outfile = new File(assemblyAgendaDirectory, filename);
                logger.info("Fetching all committee agendas");
                String contents = getUrlContents(contentURL);
                logger.info("Writing content to "+filename);
                FileIOUtils.write(outfile, contents);
            }
        }
        ArrayList<File> list = new ArrayList<>();
        list.add(outfile);
        return list.size();
    }

}