package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.qa.model.FieldName;
import gov.nysenate.openleg.qa.model.LbdcFile;
import gov.nysenate.openleg.qa.model.LbdcFile.AssociatedFields;
import gov.nysenate.openleg.qa.model.NonMatchingField;
import gov.nysenate.openleg.qa.model.ProblemBill;
import gov.nysenate.openleg.search.SearchEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AssociatedFields({FieldName.FULLTEXT})
public class LbdcFilePaging extends LbdcFile {

    public LbdcFilePaging(File file) {
        super(file);
    }

    @Override
    public ArrayList<ProblemBill> getProblemBills(FieldName[] fieldNames) {
        ArrayList<ProblemBill> ret = new ArrayList<ProblemBill>();

        open();

        //header
        er.readLine();

        String in = null;

        /*
         * SESSYR = 0
         * SEN_HSE = 1
         * SEN_NO = 2
         * SEN_AMD = 3
         * ASM_HSE = 4
         * ASM_NO = 5
         * ASM_AMD = 6
         * OUT_DATE = 7
         * PAGES = 8
         */
        while((in = er.readLine()) != null) {
            String tuple[] = in.replaceAll(",,", ", ,").split(",");

            /* check senate bill if exists */
            updateList(
                    tuple[2],
                    combineBillFields(tuple[1], tuple[2], tuple[3]),
                    tuple[8],
                    ret);

            /* check assembly bill if exists */
            updateList(
                    tuple[5],
                    combineBillFields(tuple[4], tuple[5], tuple[6]),
                    tuple[8],
                    ret);
        }

        close();

        return ret;
    }

    private void updateList(String rawBillNo, String formattedBillNo, String lbdcPageCount, ArrayList<ProblemBill> list) {
        if(formattedBillNo != null) {
            ProblemBill problemBill = getProblemBill(rawBillNo, formattedBillNo, lbdcPageCount);

            if(problemBill != null) {
                list.add(problemBill);
            }
        }
    }

    private ProblemBill getProblemBill(String rawBillNo, String formattedBillNo, String lbdcPageCount) {
        Bill luceneBill = SearchEngine.getInstance().getBill(formattedBillNo);

        if(luceneBill == null) {
            //TODO we don't have it
        }
        else {
            if(luceneBill.getFulltext() != null) {
                int pageNumber = getPageNumber(rawBillNo.replaceAll("^0*", ""), luceneBill.getFulltext());

                if(pageNumber != new Integer(lbdcPageCount)) {
                    ProblemBill problemBill = new ProblemBill(formattedBillNo, luceneBill.getModified());
                    problemBill.addNonMatchingField(new NonMatchingField(FieldName.FULLTEXT, pageNumber + "", lbdcPageCount + ""));
                    problemBill.setLastReported(time);
                    return problemBill;
                }
            }
        }
        return null;
    }

    private String combineBillFields(String house, String number, String amd) {
        if(house.matches("\\s*"))
            return null;
        if(amd.matches("\\s*"))
            amd = "";
        return this.getBillNumber(number + amd, (house.equals("A") ? true : false));
    }

    private int getPageNumber(String billNo, String text) {
        if(text == null)
            return -1;

        Pattern pagePattern = Pattern.compile("^\\s+(?:" +
                "\\w\\.\\s\\d+(?:--\\w)?\\s+(\\d+)(?:\\s+\\w\\.\\s\\d+(?:--\\w)?)?|" +
                "(\\d+)(\\s+(?:\\d+(?:\\-)?){3})?)$", Pattern.MULTILINE);

        Matcher m = pagePattern.matcher(text);

        MatchResult result = null;
        while(m.find()) result = m.toMatchResult();

        if(result != null) {
            //with non capturing groups when a match is found it will either be in group 1 or 2
            String ret = result.group(1) == null ? result.group(2) : result.group(1);
            if(ret.equals(billNo)) {
                return 1;
            }
            return new Integer(ret);
        }

        return 1;
    }
}
