package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.dao.scraping.LRSScraper;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.StringDiffer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kyle on 1/29/15.
 */
@Repository
public class BillTextScraper extends LRSScraper {

    private static final Logger logger = Logger.getLogger(BillTextScraper.class);
    protected static final String allBills = "http://public.leginfo.state.ny.us/navigate.cgi?NVDTO:=";
    protected URL billURLBase;

    private File billTextDirectory;

    @PostConstruct
    public void init() throws IOException {
        billURLBase = new URL(allBills);
        this.billTextDirectory = environment.getBillTextDirectory();
    }

    @Override
    public void scrape() throws IOException {
        String text = "";
        String billType = "A", billNo = "100", sessionYear = "2015";

        logger.info("FETCHING landing page.");
        String path = "http://public.leginfo.state.ny.us/navigate.cgi?NVDTO" +
                ":=&QUERYDATA=" + billType + billNo + "&QUERYTYPE=BILLNO&SESSYR=" + sessionYear + "&CBTEXT=Y";
        //String newPath = ("http://public.leginfo.state.ny.us/navigate.cgi?NVDTO:=&QUERYDATA=S1&QUERYTYPE=BILLNO&SESSYR=2015&CBTEXT=Y");

        try {
            Document doc = Jsoup.connect(path).timeout(10000).get();
            Elements preTags = doc.getElementsByTag("pre");
            StringBuilder stringBuilder = new StringBuilder();

            for (Element pre : preTags)
                processNode(pre, stringBuilder);

            text = stringBuilder.toString();
            Elements printNoEle = doc.getElementsByClass("nv_bot_info").get(0).getElementsByTag("strong");
            String printNo = printNoEle.get(0).text();
            makeFile(text, printNo, sessionYear);
            //scrapeAllTypes(text, billType);
        } catch (Exception e) {
            logger.info("Could not establish connection to LRS");
            e.printStackTrace();
        }
    }

    public void makeFile(String text, String printNo, String sessionYear) throws Exception{
        //DateUtils.LRS_WEBSITE_DATETIME_FORMAT);
        //LocalDateTime date = LocalDateTime.now();
        String filename = dateFormat.format(LocalDateTime.now()) + ".bill_" + printNo +  ".txt";
        File outfile = new File(billTextDirectory, filename);
        FileUtils.write(outfile, text);
    }


    /**
     *
     * @param ele
     * @param stringBuilder
     */
    void processNode(Element ele, StringBuilder stringBuilder) {
        for (Node t : ele.childNodes()) {
            if (t instanceof Element) {
                Element e = (Element) t;
                if (e.tag().getName().equals("u")) {
                    stringBuilder.append(e.text().toUpperCase());
                } else {
                    processNode(e, stringBuilder);
                }
            } else if (t instanceof TextNode) {
                stringBuilder.append(((TextNode) t).text());
            }
        }
    }
}