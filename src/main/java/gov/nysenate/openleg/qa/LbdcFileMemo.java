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

@AssociatedFields({FieldName.MEMO})
public class LbdcFileMemo extends LbdcFile {

    public LbdcFileMemo(File file) {
        super(file);
    }

    @Override
    public ArrayList<ProblemBill> getProblemBills(FieldName[] fieldNames) {
        ArrayList<ProblemBill> ret = new ArrayList<ProblemBill>();

        open();

        String in = null;

        while((in = er.readLine()) != null) {
            if(!in.matches("\\s*")) {
                Bill luceneBill = SearchEngine.getInstance().getBill(getBillNumber(in, false));

                if(luceneBill == null) {
                    //TODO we don't have it
                    continue;
                }
                else {
                    if(luceneBill.getMemo() == null || luceneBill.getMemo().matches("\\s*")) {
                        ProblemBill problemBill = new ProblemBill(
                                luceneBill.getSenateBillNo(), luceneBill.getModified());
                        problemBill.addNonMatchingField(new NonMatchingField(FieldName.MEMO, null, null));
                        problemBill.setLastReported(time);

                        ret.add(problemBill);
                    }
                }
            }
        }

        close();

        return ret;
    }
}