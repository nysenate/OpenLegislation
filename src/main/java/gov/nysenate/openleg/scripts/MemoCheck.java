package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.SOBIBlock;
import gov.nysenate.openleg.processors.BillProcessor;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MemoCheck extends BaseScript
{
    private final Logger logger = Logger.getLogger(MemoCheck.class);

    protected String get_diff(String a, String b) throws IOException, InterruptedException
    {
        Runtime run = Runtime.getRuntime();
        FileUtils.write(new File("/data/openleg/a"), a);
        FileUtils.write(new File("/data/openleg/b"), b);
        Process pr = run.exec("wdiff /data/openleg/a /data/openleg/b -3");
        pr.waitFor();
        return IOUtils.toString(pr.getInputStream());
    }
    @Override
    protected void execute(CommandLine opts) throws Exception
    {
        HashMap<String, Integer> errors = new HashMap<String, Integer>();
        errors.put("missing",0);
        errors.put("mismatch", 0);
        File blockFile = new File("/data/openleg/PALMER.SEN.ALL.MEMO2011.TXT");
        BillProcessor bp = new BillProcessor();
        Storage storage = Application.getStorage();
        List<SOBIBlock> blocks = bp.getBlocks(blockFile);
        for (SOBIBlock block : blocks) {
            String billNo = block.getPrintNo()+block.getAmendment()+"-"+block.getYear();
            Bill jsonBill = storage.getBill(block.getPrintNo()+block.getAmendment(), block.getYear());
            Bill lbdcBill = new Bill(billNo, block.getYear());
            bp.applyText(new String(block.getData().getBytes(), "utf-8"), lbdcBill, new Date());
            String jsonMemo = StringUtils.normalizeSpace(jsonBill.getMemo().replaceAll("-\n+ *", "").replaceAll("\n *", " ").replaceAll(" *([:,]) *", "$1").replaceAll(" *([()!\\\"]) *", " $1 ").replaceAll("([A-Za-z])- ?([A-Za-z])","$1$2").replaceAll("-", "").trim()).toLowerCase();
            String lbdcMemo = StringUtils.normalizeSpace(lbdcBill.getMemo().replaceAll("�", "§").replaceAll("-\n+ *", "").replaceAll("\n *", " ").replaceAll(" *([:,]) *", "$1").replaceAll(" *([()!\\\"]) *", " $1 ").replaceAll("([A-Za-z])- ?([A-Za-z])","$1$2").replaceAll("-", "").trim()).toLowerCase();

            if(jsonMemo.isEmpty()) {
                logger.error(billNo+": MISSING");
                errors.put("missing", errors.get("missing")+1);
            }
            else if (!jsonMemo.equals(lbdcMemo)) {
                logger.error(billNo+": MISMATCH");
                errors.put("mismatch", errors.get("mismatch")+1);
                System.out.println(get_diff(jsonMemo, lbdcMemo));
//                System.in.read();
            }
        }
        System.out.println(errors);
        System.out.println("Total bills "+blocks.size());
    }

    public static void main(String[] args) throws Exception
    {
        new MemoCheck().run(args);
    }

}
