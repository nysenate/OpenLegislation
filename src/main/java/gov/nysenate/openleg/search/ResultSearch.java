package gov.nysenate.openleg.search;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.util.TextFormatter;
import gov.nysenate.openleg.util.Timer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

public class ResultSearch implements Iterator<Result>, Iterable<Result> {
    private static Logger logger = Logger.getLogger(ResultSearch.class);

    private static final int SIZE = 500;
    private static final int PAGE = 0;
    private static final String FORMAT = "json";
    private static final String SORT_BY = "oid";
    private static final boolean REVERSE = false;

    //current position in senateResponse.getResuls()
    private int pos;
    //current size of senateResponse.getResults()
    private int size;

    //maximum number of objects to return per query
    public int max;
    //current page of query
    public int page;

    //true when search has been completed
    private boolean exhausted = false;

    public String format;
    public String sortBy;
    public boolean reverse;
    public String query;

    public SenateResponse senateResponse;

    private final SearchEngine searchEngine;

    public ResultSearch() {
        this(SIZE, PAGE, FORMAT, SORT_BY, REVERSE);
    }

    public ResultSearch(int max, int page, String format, String sortBy, boolean reverse) {
        this.max = max;
        this.page = page;
        this.format = format;
        this.sortBy = sortBy;
        this.reverse = reverse;
        searchEngine = SearchEngine.getInstance();
    }

    public ResultSearch query(String query) {
        this.query = query;
        reset();
        return this;
    }

    @Override
    public boolean hasNext() {
        if(pos == size) {
            if(!exhausted) {
                doQuery();
            }
        }
        return pos < size;
    }

    @Override
    public Result next() {
        if(pos >= size) {
            throw new NoSuchElementException();
        }
        else {
            return senateResponse.getResults().get(pos++);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void reset() {
        page = 0;
        pos = 0;
        size = 0;
        exhausted = false;
        senateResponse = null;
    }

    private void doQuery() {
        if(exhausted){
            return;
        }

        Timer t = new Timer();
        t.start();
        try {
            senateResponse = searchEngine.search(query, format, (max * page++), max, sortBy, reverse);
        } catch (ParseException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.warn(TextFormatter.append("Query: ", query," (page ", page, ") took ", t.stop()));

        if(senateResponse == null) {
            exhausted = true;
            return;
        }

        ApiHelper.buildSearchResultList(senateResponse);

        ArrayList<Result> results = senateResponse.getResults();

        if(results.size() != max)
            exhausted = true;

        size = results.size();
        pos = 0;

    }

    @Override
    public Iterator<Result> iterator() {
        return this;
    }
}