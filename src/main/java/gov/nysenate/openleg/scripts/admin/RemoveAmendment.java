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
import gov.nysenate.openleg.util.UnpublishListManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.ArrayList;
import java.util.List;

public class RemoveAmendment extends BaseScript
{
    public static void main(String[] args) throws Exception {
        new RemoveAmendment().run(args);
    }

    /**{@inheritDoc}*/
    @Override
    protected boolean luceneReadOnly() {
        return false;
    }

    protected Options getOptions() {
        Options options = new Options();
        options.addOption("y", true, "Year of bill session");
        options.addOption("b", true, "Bill id");
        options.addOption("t", "temporary", false, "Does not add the bill to the unpublished list if set");

        // Not currently used, Removes this bill from the list of amendments in its related bills
        // while not deleting it so it can still be referenced if needed.... Doesn't work with current lucene search implementation.
//        options.addOption("r", false, "Was this restored to a previous version?");
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception {
        Storage storage = Application.getStorage();
        String billId = opts.getOptionValue("b");
        int sessionYear = Integer.valueOf(opts.getOptionValue("y"));
        boolean wasRestored = opts.hasOption("r");

        Bill bill = storage.getBill(billId, sessionYear);
        removeAmendmentFromOtherVersions(storage, bill);

        // Deactivate ourselves.
        bill.setActive(false);
        storage.set(bill);

        if (wasRestored) {
            ChangeLogger.record(storage.key(bill), storage);
        }
        else {
            bill.setPublishDate(null); // un publish.
            ChangeLogger.delete(storage.key(bill), storage);
        }

        storage.flush();

        // push changes to lucene and varnish
        ArrayList<ServiceBase> services = new ArrayList<ServiceBase>();
        services.add(new Lucene());
        services.add(new Varnish("127.0.0.1", 80));

        DataProcessor process = new DataProcessor();
        process.push(storage, ChangeLogger.getEntries(), services);

        // Add the bill to the unpublished bill list
        if (!opts.hasOption('t')) {
            UnpublishListManager unpublishListManager = new UnpublishListManager();
            unpublishListManager.addUnpublishedBill(billId + '-' + sessionYear);
        }
    }

    private void removeAmendmentFromOtherVersions(Storage storage, Bill bill) {
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
    }
}
