package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.api.QueryBuilder;
import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.qa.model.ProblemBill;
import gov.nysenate.openleg.search.SenateObjectSearch;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

public class ReportBuilder {
    private static Logger logger = Logger.getLogger(ReportBuilder.class);

    final static double MS_IN_DAY = 86400000.0;
    final static int MAX_RESULTS = 500;

    SenateObjectSearch<Bill> longSearch;
    long newestMod ;

    public ReportBuilder() {
        longSearch = new SenateObjectSearch<Bill>();
        newestMod = 0L;
    }

    public HashMap<String, ProblemBill> getBillReportSet(String year)
            throws ParseException, IOException {
        // add ReportBills to map, keeping track of missing fields
        // intentionally leaving memos out for the time being
        HashMap<String, ProblemBill> billReportMap = new HashMap<String, ProblemBill>();
        //addBillListToReport("memo", year, billReportMap);
        addBillListToReport("full", year, billReportMap);
        addBillListToReport("sponsor", year, billReportMap);
        addBillListToReport("summary", year, billReportMap);
        addBillListToReport("title", year, billReportMap);
        addBillListToReport("actions", year, billReportMap);

        return billReportMap;
    }

    /**
     * makes a query to lucene with getResultList to find bills that don't have
     * a valid parameter <field>
     * 
     * @param field
     * @param year
     * @param billReportMap
     */
    public void addBillListToReport(String field, String year,
            HashMap<String, ProblemBill> problemBillMap) throws ParseException,
            IOException {

        QueryBuilder builder = null;
        try {
            builder = QueryBuilder.build().otype("bill").andNot().range(field, "A*", "Z*")
                    .andNot().keyValue(field, "Z*")
                    .and().oid("(S* OR A*)")
                    .and().keyValue("year", year);
        } catch (QueryBuilderException e) {
            logger.error(e);
        }

        if(builder == null) return;

        longSearch.query(builder.query());

        for(Bill bill:longSearch) {
            logger.warn(TextFormatter.append("found ", bill.getSenateBillNo()," missing ",field));

            ProblemBill problemBill = null;
            if ((problemBill = problemBillMap.get(bill.getSenateBillNo())) != null) {
                problemBill.addMissingField(field);

            } else {
                problemBill = new ProblemBill(bill.getSenateBillNo(), bill.getModified());
                problemBill.addMissingField(field);
                problemBillMap.put(bill.getSenateBillNo(), problemBill);
            }
        }
    }

    public String formatJson(String jsonData) {
        jsonData = jsonData.substring(jsonData.indexOf(":") + 1);
        jsonData = jsonData.substring(0, jsonData.lastIndexOf("}"));
        return jsonData;
    }
}
