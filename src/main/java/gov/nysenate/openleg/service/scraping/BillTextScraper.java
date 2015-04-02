package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.dao.scraping.LRSScraper;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillId;
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
import java.util.List;
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
    private File billMemoDirectory;
    private String textFileName = null, memoFileName = null, billType, billNo, sessionYear;// memo = "Y";
    private boolean memo = false;
    private BillId billId;
    private File textFile, memoFile;

    @PostConstruct
    public void init() throws IOException {
        billURLBase = new URL(allBills);
        this.billTextDirectory = environment.getBillTextDirectory();
        this.billMemoDirectory = environment.getBillMemoDirectory();
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public List<File> scrape() throws IOException {
        return null;
    }

    @Override
    public List<File> scrape(String billType, String billNo, SessionYear sessionYear) throws IOException {
        String text = "";
        String memoText = "";
        ArrayList<File> list = new ArrayList<File>();

        String path = null;

        billId = new BillId(billType+billNo, sessionYear);

        logger.info("FETCHING landing page.");
         path = "http://public.leginfo.state.ny.us/navigate.cgi?NVDTO" +
                 ":=&QUERYDATA=" + billType + billNo + "&QUERYTYPE=BILLNO&SESSYR=" + sessionYear + "&CBTEXT=Y" +
                 "&CBSPONMEMO=Y";
        try {
            Document doc = Jsoup.connect(path).timeout(10000).get();
            Elements preTags = doc.getElementsByTag("pre");
            StringBuilder textBuilder = new StringBuilder();

            /* Used for getting only text
            for (Element pre : preTags)
                processNode(pre, textBuilder);
            */

            textBuilder.append(billType+billNo + "\n" + sessionYear + "\n");
            for (int i = 0; i <preTags.size()-1; i++)                    //gets all bill text
                processNode(preTags.get(i), textBuilder);

            text = textBuilder.toString();

            //gets print number to use in filename
            Elements printNoEle = doc.getElementsByClass("nv_bot_info").get(0).getElementsByTag("strong");
            String printNo = printNoEle.get(0).text();
            makeFile(text, printNo, false);
            list.add(textFile);

            if (billType.equalsIgnoreCase("A") | billType.equalsIgnoreCase("S")) {
                StringBuilder memoBuilder = new StringBuilder();
                //memoBuilder.append(billType+billNo + "\n" + sessionYear + "\n");
                memoBuilder.append(billType + billNo + "\n" + sessionYear + "\n");
                processNode(preTags.get(preTags.size() - 1), memoBuilder);    //gets memo text

                memoText = memoBuilder.toString();


                makeFile(memoText, printNo, true);
                list.add(memoFile);

            }
        } catch (Exception e) {
            logger.info("Could not establish connection to LRS Website");
            e.printStackTrace();
        }
        return list;
    }

    public void makeFile(String text, String printNo, boolean memo) throws Exception{
        if (memo == false) {
            textFileName = dateFormat.format(LocalDateTime.now()) + ".bill_" + printNo + ".txt";
            textFile = new File(billTextDirectory, textFileName);
            FileUtils.write(textFile, text);
        }else if (memo == true){
            memoFileName = dateFormat.format(LocalDateTime.now()) + ".bill_" + printNo + "_memo.txt";
            memoFile = new File(billMemoDirectory, memoFileName);
            FileUtils.write(memoFile, text);
        }
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

    public String getMemoFileName(){
        return memoFileName;
    }
    public String getTextFileName(){
        return textFileName;
    }
    public File getBillTextDirectory(){
        return billTextDirectory;
    }
    public File getBillMemoDirectory(){
        return billMemoDirectory;
    }
    public BillId getBillId(){
        return billId;
    }

    public String getBillType(){
        return billType;
    }
    public String getBillNo(){
        return billNo;
    }
    public String getSessionYear(){
        return sessionYear;
    }
}