package gov.nysenate.openleg.qa.model;

import gov.nysenate.openleg.util.EasyReader;
import gov.nysenate.openleg.util.SessionYear;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Date;

public abstract class LbdcFile {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public
    @interface AssociatedFields {
        FieldName[] value();
    }

    protected EasyReader er = null;
    protected File file = null;
    protected long time = new Date().getTime();

    public LbdcFile(File file) {
        this.file  = file;
    }

    abstract public ArrayList<ProblemBill> getProblemBills(FieldName[] fieldNames);

    protected void open() {
        if(file.exists()) {
            er = new EasyReader(file).open();
        }
    }

    protected void close() {
        er.close();
    }

    // 00021 -> S21-<current session year>
    protected String getBillNumber(String billNo, boolean assembly) {
        billNo = billNo.replaceAll("^0+","");
        return (assembly? "A" : "S") + billNo + "-" + SessionYear.getSessionYear();
    }
}