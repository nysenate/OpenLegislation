package gov.nysenate.openleg.api;

import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.TextFormatter;

import org.apache.log4j.Logger;

/**
 * This class attempts to make building queries
 * across the application a little easier, hiding
 * some of underlying logic
 *
 */
public class QueryBuilder {
    public static Logger logger = Logger.getLogger(QueryBuilder.class);

    public static final String OTYPE  = "otype";
    public static final String OID  = "oid";
    public static final String YEAR = "year";
    public static final String WHEN = "when";
    public static final String ACTIVE = "active";

    public static final String SEPARATOR = ":";
    public static final String TO = "TO";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";

    protected StringBuffer query;
    protected boolean operatorToggle = true;

    public QueryBuilder() {
        query = new StringBuffer();
    }

    public QueryBuilder(String key, String value) throws QueryBuilderException {
        this();
        keyValue(key, value);
    }

    public QueryBuilder(QueryBuilder queryBuilder) {
        this.query = new StringBuffer(queryBuilder.query);
        this.operatorToggle = queryBuilder.operatorToggle;
    }


    public QueryBuilder otype(String otype) throws QueryBuilderException {
        return keyValue(OTYPE, otype);
    }

    public QueryBuilder oid(String oid) throws QueryBuilderException {
        return keyValue(OID, oid);
    }

    public QueryBuilder oid(String oid, boolean quote) throws QueryBuilderException {
        return keyValue(OID, quote ? TextFormatter.append("\"", oid, "\"") : oid);
    }

    public QueryBuilder active() throws QueryBuilderException {
        return keyValue(ACTIVE,"true");
    }

    public QueryBuilder inactive() throws QueryBuilderException {
        return keyValue(ACTIVE,"false");
    }

    public QueryBuilder inSession(int year) throws QueryBuilderException {
        int session = SessionYear.getSessionYear(year);
        condition();
        return append(YEAR, SEPARATOR, "[", Integer.toString(session), " TO ", Integer.toString(session+1), "] ");
    }

    public QueryBuilder current() throws QueryBuilderException {
        return inSession(SessionYear.getSessionYear());
    }

    public QueryBuilder relatedBills(String key, String billNo) throws QueryBuilderException {
        String year = null;
        String billNoRaw = null;

        if(billNo.contains("-")) {
            String tuple[] = billNo.split("\\-");
            billNo = tuple[0];
            year = tuple[1];
        }
        else {
            year = Integer.toString(SessionYear.getSessionYear());
        }

        billNoRaw = billNo.replaceAll("[a-zA-Z]$", "");

        return keyValue(key,TextFormatter.append(
                "((",billNoRaw,"-",year," ",OR," ",range(
                        TextFormatter.append(billNoRaw,"A-",year),
                        TextFormatter.append(billNoRaw,"Z-",year)),
                        ") ",AND," ",billNoRaw,"*-",year,")"));
    }

    public QueryBuilder range(String key, String from, String to) throws QueryBuilderException {
        return keyValue(key, range(from, to));
    }

    private String range(String from, String to) throws QueryBuilderException {
        return TextFormatter.append("[",from," ",TO," ",to,"]");
    }

    public QueryBuilder keyValue(String key, String value) throws QueryBuilderException {
        condition();
        return append(key,SEPARATOR, value);
    }

    public QueryBuilder keyValue(String key, String value, String wrapper) throws QueryBuilderException {
        condition();
        return append(key,SEPARATOR, wrapper, value, wrapper);
    }

    public QueryBuilder keyValue(String key, String value, String before, String after) throws QueryBuilderException {
        condition();
        return append(key,SEPARATOR, before, value, after);
    }

    public QueryBuilder and() throws QueryBuilderException {
        if(operator())
            return append(" ",AND," ");
        return this;
    }

    public QueryBuilder or() throws QueryBuilderException {
        if(operator())
            return append(" ",OR," ");
        return this;
    }

    public QueryBuilder not() throws QueryBuilderException {
        if(operator())
            return append(" ",NOT," ");
        return this;
    }

    public QueryBuilder andNot() throws QueryBuilderException {
        if(operator())
            return append(" ",AND," ",NOT," ");
        return this;
    }

    /**
     * assumes @param strs consists of a condition
     * 		and does not end with an operator
     * @throws QueryBuilderException
     */
    public QueryBuilder insertAfter(String... strs) throws QueryBuilderException {
        condition();
        return append(strs);
    }

    public QueryBuilder append(String... strs) {
        for(String str:strs) {
            query.append(str);
        }
        return this;
    }

    public QueryBuilder insertBefore(String... strs) {
        StringBuilder temp = new StringBuilder();
        for(String str:strs) {
            temp.append(str);
        }

        if(query.length() == 0) operatorToggle = false;

        query.insert(0, temp);
        return this;
    }

    private void condition() throws QueryBuilderException {
        if(!operatorToggle)
            throw new QueryBuilderException("putting two conditions next to each other: " + query());

        operatorToggle = false;
    }

    private boolean operator() throws QueryBuilderException {
        if(query.length() == 0)
            return false;

        if(operatorToggle)
            throw new QueryBuilderException("putting two operators next to each other: " + query());

        operatorToggle = true;

        return true;
    }

    public void reset() {
        query.setLength(0);
        operatorToggle = true;
    }

    public String query() {
        return query.toString();
    }

    @Override
    public String toString() {
        return query();
    }

    public static QueryBuilder build() {
        return new QueryBuilder();
    }

    public static QueryBuilder build(String key, String value) throws QueryBuilderException {
        return new QueryBuilder(key, value);
    }

    @SuppressWarnings("serial")
    public static class QueryBuilderException extends Exception {
        public QueryBuilderException() {
            super();
        }

        public QueryBuilderException(String message) {
            super(message);
        }
    }
}
