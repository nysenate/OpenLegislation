package gov.nysenate.openleg.util;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.SenateResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

public class ResultIterator implements Iterator<Result>, Iterable<Result> {
    private static Logger logger = Logger.getLogger(ResultIterator.class);

    private static final int SIZE = 500;
    private static final int PAGE = 1;
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

    public String sortBy;
    public boolean reverse;
    public String query;

    public SenateResponse senateResponse;

    public ResultIterator(String query) {
        this(query, SIZE, PAGE, SORT_BY, REVERSE);
    }

    public ResultIterator(String query, int max, int page, String sortBy, boolean reverse) {
        this.query = query;
        this.max = max;
        this.page = page;
        this.sortBy = sortBy;
        this.reverse = reverse;
        reset();
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
        page = 1;
        pos = 0;
        size = 0;
        exhausted = false;
        senateResponse = null;
    }

    private void doQuery() {
        if(exhausted){
            return;
        }

        try {
            int start = (page-1)*max;
            senateResponse = Application.getLucene().search(query, start, max, sortBy, reverse);
            page++;
        }
        catch (IOException e) {
            logger.error(e);
        }

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