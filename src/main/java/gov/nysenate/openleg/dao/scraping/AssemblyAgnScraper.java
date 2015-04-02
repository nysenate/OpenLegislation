package gov.nysenate.openleg.dao.scraping;

import gov.nysenate.openleg.model.base.SessionYear;
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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by kyle on 11/10/14.
 */
@Repository
public class AssemblyAgnScraper extends LRSScraper {
    private static final Logger logger = Logger.getLogger(LRSScraper.class);
    String assemblyAgendas = "http://public.leginfo.state.ny.us/menugetf.cgi?COMMONQUERY=ASMAGEN";
    String senateAgendas = "http://public.leginfo.state.ny.us/menugetf.cgi?COMMONQUERY=SENAGEN";

    protected URL agendaURL;
    private File assemblyAgendaDirectory;
    private File outfile = null;

    @PostConstruct
    public void init() throws IOException {
        agendaURL = new URL(assemblyAgendas);
        this.assemblyAgendaDirectory = environment.getAssemblyAgendaDirectory();
    }

    @Override
    public List<File> scrape() throws IOException {
        System.out.println("ASSEMBLY AGENDA DIRECTORY ::::::: " + environment.getAssemblyAgendaDirectory());
        logger.info("SCRETCHING landing page.");
        Document doc = Jsoup.connect(agendaURL.toString()).timeout(10000).get();

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
            if (link.text().equalsIgnoreCase("All Committee Agendas")){ //possibility for error in generating filename
                String absHref = link.attr("abs:href");
                System.out.println(absHref);
                URL contentURL = new URL(absHref);

                String filename = dateFormat.format(LocalDateTime.now()) + ".all_assembly_agendas.html";
                outfile = new File(assemblyAgendaDirectory, filename);
                logger.info("Fetching all committee agendas");
                String contents = IOUtils.toString(contentURL);
                logger.info("Writing content to "+filename);
                FileUtils.write(outfile, contents);
            }
        }
        ArrayList<File> list = new ArrayList<>();
        list.add(outfile);
        return list;
    }

    @Override
    public List<File> scrape(String billType, String billNo, SessionYear sessionYear) throws IOException {
        return null;
    }
}