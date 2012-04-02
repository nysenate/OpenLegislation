package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.api.QueryBuilder;
import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.ingest.hook.Hook;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateObjectSearch;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.EasyReader;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.TextFormatter;
import gov.nysenate.openleg.util.Timer;
import gov.nysenate.openleg.util.serialize.JsonSerializer;
import gov.nysenate.openleg.util.serialize.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class IngestIndexWriter {
    private final Logger logger = Logger.getLogger(IngestIndexWriter.class);
    private static final int BATCH_SIZE = 1000;

    JsonDao jsonDao;
    SearchEngine searchEngine;

    String jsonDirectory;
    String logPath;

    ArrayList<Hook<List<? extends SenateObject>>> hooks;

    Timer timer;

    public IngestIndexWriter(String jsonDirectory, String logPath,
            SearchEngine searchEngine, JsonDao jsonDao,
            ArrayList<Hook<List<? extends SenateObject>>> hooks) {
        this.searchEngine = searchEngine;
        this.jsonDao = jsonDao;

        this.jsonDirectory = jsonDirectory;
        this.logPath = logPath;

        this.hooks = hooks == null
                ? new ArrayList<Hook<List<? extends SenateObject>>>()
                        : hooks;

                timer = new Timer();
    }

    /**
     * index documents based on what is listed in log from JsonDao write function
     */
    public void indexBulk(ArrayList<Hook<List<? extends SenateObject>>> hooks) {
        indexBulk(truncateLog(), hooks);
    }

    /**
     * Index BATCH_SIZE number of documents per operation
     * @param filePaths string paths of files that must be indexed
     */
    public void indexBulk(String[] files, ArrayList<Hook<List<? extends SenateObject>>> hooks) {
        ArrayList<SenateObject> lst;
        Pattern p = Pattern.compile("\\d{4}/(\\w+)/.*$");
        Matcher m = null;

        logger.warn("Indexing " + files.length + " documents");

        int its = files.length/BATCH_SIZE;
        for(int i = 0; i <= its; i++) {
            lst = new ArrayList<SenateObject>();

            timer.start();
            for(int j = (i * BATCH_SIZE); j < (((i+1) * BATCH_SIZE)) && j < files.length; j++) {
                m = p.matcher(files[j]);
                if(m.find()) {
                    SenateObject senObj = jsonDao.load(files[j], Ingest.getIngestType(m.group(1)).clazz());

                    if(senObj != null) {
                        lst.add(senObj);
                    }
                }
            }
            logger.warn(timer.stop() + " - Read " + lst.size() + " Objects");

            indexList(lst);

            lst.clear();
        }
    }

    public void indexList(List<SenateObject> senateObjects) {
        timer.start();
        try {
            searchEngine.indexSenateObjects(
                    senateObjects,
                    new LuceneSerializer[] {
                            new XmlSerializer(),
                            new JsonSerializer()});
        } catch (IOException e) {
            logger.error(e);
        }
        logger.warn(timer.stop() + " - Indexed Objects");

        for(Hook<List<? extends SenateObject>> hook:hooks) {
            hook.call(senateObjects);
        }
    }

    private String[] truncateLog() {
        File file = new File(jsonDirectory + "/.log");

        if(!file.exists()) {
            return new String[0];
        }

        EasyReader er = new EasyReader(file).open();

        /*
         * want bills to show up first in the
         */
        TreeSet<String> set = new TreeSet<String>();

        String in = null;
        while((in = er.readLine()) != null) {
            set.add(in);
        }
        er.close();

        String[] files = new String[set.size()];

        set.toArray(files);

        file.deleteOnExit();

        return files;
    }

    /**
     * utility to reset all inactive bills
     */
    public void resetInactiveBills() {
        QueryBuilder queryBuilder = null;

        try {
            queryBuilder = QueryBuilder.build().otype("bill").and().inactive();
        } catch (QueryBuilderException e) {
            logger.error(e);
        }

        SenateObjectSearch<Bill> longSearch = new SenateObjectSearch<Bill>()
                .clazz(Bill.class)
                .query(queryBuilder.query());

        for(Bill bill:longSearch) {
            this.reindexActiveBill(bill.getSenateBillNo(), bill.getYear() + "");
        }
    }

    /**
     *  The same as markInactiveBills(year) but passes on
     *  the current session year from SessionYear.getSessionYear()
     */
    public void markInactiveBills() {
        markInactiveBills(SessionYear.getSessionYear() + "");
    }

    /**
     * scans all bills in the index looking for bills with amendments, once the latest version of
     * a bill is found it passes it to reindexAmendedVersions(bill) where old versions
     * of the bill are marked as inactive
     * 
     * @param year - the session year for bills you want to be checked
     */
    public void markInactiveBills(String year) {
        Bill prev = null;

        boolean reindex = false;

        QueryBuilder queryBuilder = null;

        try {
            queryBuilder = QueryBuilder.build().otype("bill").and().keyValue("year", year);
        } catch (QueryBuilderException e) {
            logger.error(e);
        }

        SenateObjectSearch<Bill> longSearch = new SenateObjectSearch<Bill>()
                .clazz(Bill.class)
                .query(queryBuilder.query());

        for(Bill bill:longSearch) {
            if(prev != null) {
                String billNo = bill.getSenateBillNo().split("-")[0];

                if(cleanBillNo(prev).equals(billNo.replaceAll("[A-Z]$", ""))) {
                    reindex = true;
                }
                else {
                    if(reindex) {
                        reindexAmendedVersions(prev);
                    }
                    else {
                        if(Character.isLetter(billNo.charAt(billNo.length() - 1))) {
                            reindexAmendedVersions(bill);
                        }
                    }
                    reindex = false;
                }
            }
            prev = bill;
        }
    }

    private String cleanBillNo(Bill bill) {
        return bill.getSenateBillNo().split("-")[0].replaceAll("[A-Z]$", "");
    }

    /**
     * desirable to hide old versions of an amended bill from the default search
     * this appends "active:false" as a field to any old versions of bills
     * 
     * to avoid constantly rewriting amended versions of bills this does a query
     * to lucene to check if they've already been hidden, if they haven't then
     * they are sent to reindexInactiveBill
     * 
     * @param bill
     * 
     * returns true if current bill isn't searchable, false otherwise
     */
    public boolean reindexAmendedVersions(Bill bill) {
        try {
            //oid:(S418-2009 OR [S418A-2009 TO S418Z-2009]) AND year:2009
            QueryBuilder query = QueryBuilder.build().otype("bill").and().relatedBills("oid", bill.getSenateBillNo());

            SenateResponse sr = searchEngine.search(query.query(),
                    "json", 0,100, null, false);

            //if there aren't any results this is a new bill
            if(sr.getResults().isEmpty())
                return false;

            //create a list and store bill numbers from oldest to newest
            ArrayList<String> billNumbers = new ArrayList<String>();
            for(Result result:sr.getResults()) {
                billNumbers.add(result.getOid());
            }
            if(!billNumbers.contains(bill))
                billNumbers.add(bill.getSenateBillNo());
            Collections.sort(billNumbers);

            String newest = billNumbers.get(billNumbers.size()-1);

            //if bill being stored isn't the newest we can assume
            //that the newest bill has already reindexed older bills
            if(!bill.getSenateBillNo().equals(newest))
                return true;

            billNumbers.remove(newest);
            billNumbers.remove(bill.getSenateBillNo());

            for(Result result:sr.getResults()) {
                if(billNumbers.contains(result.getOid())) {
                    if(result.isActive()) {
                        reindexInactiveBill(result.getOid(), bill.getYear()+"");
                    }
                }
            }

        } catch (IOException e) {
            logger.error(e);
        } catch (org.apache.lucene.queryParser.ParseException e) {
            logger.error(e);
        } catch (QueryBuilderException e) {
            logger.error(e);
        }
        return false;
    }

    private void reindexActiveBill(String senateBillNo, String year) {
        reindexWithActive(senateBillNo, year, true);
    }

    private void reindexInactiveBill(String senateBillNo, String year) {
        reindexWithActive(senateBillNo, year, false);
    }

    private void reindexWithActive(String senateBillNo, String year, boolean active) {
        Bill temp = jsonDao.load(senateBillNo,
                year,
                "bill",
                Bill.class);

        if(temp != null) {
            temp.setActive(active);

            logger.warn(TextFormatter.append("Reset ", temp.getSenateBillNo(), " to active:", active));
            jsonDao.write(temp);
        }
    }

    public void fixTitles() {
        fixTitles(SessionYear.getSessionYear() + "");
    }

    public void fixTitles(String year) {
        rewriteField("title", year, new FieldRewrite<Bill>() {
            @Override
            protected boolean valid(Bill from) {
                return from.getTitle() != null;
            }
            @Override
            protected Bill rewriteFields(Bill from, Bill to) {
                to.setTitle(from.getTitle());
                return to;
            }
        });
    }

    public void fixSummaries() {
        fixSummaries(SessionYear.getSessionYear() + "");
    }

    public void fixSummaries(String year) {
        rewriteField("summary", year, new FieldRewrite<Bill>() {
            @Override
            protected boolean valid(Bill from) {
                return from.getSummary() != null;
            }
            @Override
            protected Bill rewriteFields(Bill from, Bill to) {
                to.setSummary(from.getSummary());
                return to;
            }
        });
    }

    /**
     * LBDC doesn't always send data for bills that are quickly amended,
     * this scans the index for bills missing those fields and when possible
     * assigns the data from the latest version of the bill
     * 
     * applicable to summaries, titles, actions
     * 
     * only updates JSON, indexed documents will be updated on next pass
     * 
     * @param year - session to scan
     */
    public void rewriteField(String fieldName, String year, FieldRewrite<Bill> fieldRewriter) {
        QueryBuilder builder = null;

        try {
            builder = QueryBuilder.build().otype("bill")
                    .andNot().range(fieldName, "A*", "Z*")
                    .andNot().keyValue(fieldName, "A*")
                    .andNot().keyValue(fieldName, "Z*")
                    .and().oid("(A* OR S*)")
                    .and().keyValue("year", year);
        } catch (QueryBuilderException e) {
            logger.error(e);
        }

        if(builder == null) return;

        SenateObjectSearch<Bill> search = new SenateObjectSearch<Bill>()
                .clazz(Bill.class)
                .query(builder.query());

        for(Bill bill:search) {
            String newestBillNo = searchEngine.getNewestAmendment(
                    bill.getSenateBillNo()).getSenateBillNo();

            /*
             * if newestBillNo matches the bill's senateBillNo then
             * the newest bill is missing it's summary
             */
            if(newestBillNo.equalsIgnoreCase(bill.getSenateBillNo()))
                continue;

            Bill newestBill = searchEngine.getBill(newestBillNo);

            if(fieldRewriter.rewrite(newestBill, bill)) {
                logger.warn(TextFormatter.append("Updating",fieldName,"for bill ",
                        bill.getSenateBillNo()," from version ",newestBill.getSenateBillNo()));

                jsonDao.write(bill);
            }
        }
    }

    private static abstract class FieldRewrite<T> {
        protected abstract boolean valid(T from);
        protected abstract T rewriteFields(T from, T to);

        public boolean rewrite(T from, T to) {
            if(valid(from) && from != null && to != null) {
                rewriteFields(from, to);
                return true;
            }
            return false;
        }
    }
}
