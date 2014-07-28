package gov.nysenate.openleg.scripts.admin;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.processors.DataProcessor;
import gov.nysenate.openleg.scripts.BaseScript;
import gov.nysenate.openleg.services.Lucene;
import gov.nysenate.openleg.services.ServiceBase;
import gov.nysenate.openleg.services.Varnish;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.ChangeLogger;
import gov.nysenate.openleg.util.Storage;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.ArrayList;
import java.util.List;

public class RemoveAmendment extends BaseScript
{
    public static void main(String[] args) throws Exception {
        new RemoveAmendment().run(args);
    }

    protected Options getOptions() {
        Options options = new Options();
        options.addOption("y", true, "Year of bill session");
        options.addOption("b", true, "Bill id");
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {
        Storage storage = Application.getStorage();
        String billId = opts.getOptionValue("b");
        int sessionYear = Integer.valueOf(opts.getOptionValue("y"));

        Bill bill = storage.getBill(billId, sessionYear);
        List<String> amendments = bill.getAmendments();
        if (amendments.size() > 0) {
            String newActiveBill = amendments.get(amendments.size() - 1);

            // Remove all references to the unpublished bill from other versions.
            for (String versionKey : bill.getAmendments()) {
                Bill billVersion = storage.getBill(versionKey);
                billVersion.removeAmendment(bill.getBillId());
                if (bill.isActive() && versionKey.equals(newActiveBill)) {
                    billVersion.setActive(true);
                }
                storage.set(billVersion);
                ChangeLogger.record(storage.key(billVersion), storage);
            }
        }
        // Deactivate ourselves.
        bill.setActive(false);
        storage.set(bill);
        if (!bill.isBrandNew()) {
            ChangeLogger.delete(storage.key(bill), storage);
        }

        // push changes to lucene and varnish
        ArrayList<ServiceBase> services = new ArrayList<ServiceBase>();
        services.add(new Lucene());
        services.add(new Varnish("127.0.0.1", 80));

        DataProcessor process = new DataProcessor();
        process.push(Application.getStorage(), ChangeLogger.getEntries(), services);
    }
}
