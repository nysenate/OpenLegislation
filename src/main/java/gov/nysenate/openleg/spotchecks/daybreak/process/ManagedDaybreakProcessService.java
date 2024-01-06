package gov.nysenate.openleg.spotchecks.daybreak.process;

import gov.nysenate.openleg.common.util.OpenlegThreadFactory;
import gov.nysenate.openleg.spotchecks.daybreak.*;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakBill;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakBillId;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Service
public class ManagedDaybreakProcessService implements DaybreakProcessService{

    private static Logger logger = LoggerFactory.getLogger(ManagedDaybreakProcessService.class);

    @Autowired
    private DaybreakDao daybreakDao;

    private ThreadFactory threadFactory = new OpenlegThreadFactory("daybreak-process");

    private ArrayList<ExecutorService> threadServices = new ArrayList<>();

    /** --- Interfaced Methods --- */

    @PreDestroy
    public void destroy() {
        for (ExecutorService executorService: threadServices) {
            executorService.shutdownNow();
        }
        threadServices.clear();
    }

    @Override
    public int collate() {
        return collateDaybreakReports();
    }

    @Override
    public int ingest() {
        return processPendingFragments();
    }

    /**{@inheritDoc}*/
    @Override
    public String getIngestType() {
        return "daybreak fragment";
    }

    /**{@inheritDoc}*/
    @Override
    public String getCollateType() {
        return "daybreak report";
    }

    /**{@inheritDoc}*/
    @Override
    public int collateDaybreakReports() {
        DaybreakReportSet<DaybreakFile> reportSet;
        try {
            reportSet = daybreakDao.getIncomingReports();
        }
        catch (IOException ex) {
            logger.error("Could not retrieve incoming daybreak files");
            return 0;
        }

        var completeReports = reportSet.getCompleteReports();
        // Prints the status of all files found in the incoming directory
        if (!completeReports.isEmpty())
            logger.info(" --- Complete reports ---");
        for (DaybreakReport<DaybreakFile> daybreakReport: completeReports.values()) {
            logger.info("Report " + daybreakReport.getReportDate());
            daybreakReport.getReportDocs().values().forEach(daybreakFile -> logger.info('\t' + daybreakFile.getFileName()));
        }
        var partialReports = reportSet.getPartialReports();
        if (!partialReports.isEmpty())
            logger.info(" --- Partial reports --- ");
        for (DaybreakReport<DaybreakFile> daybreakReport: partialReports.values()) {
            logger.info("Report " + daybreakReport.getReportDate());
            daybreakReport.getReportDocs().values().forEach(daybreakFile -> logger.info('\t' + daybreakFile.getFileName()));
        }
        if (!reportSet.getDuplicateDocuments().isEmpty())
            logger.info(" --- Duplicate files --- ");
        reportSet.getDuplicateDocuments().forEach(file -> logger.info('\t' + file.getFileName()));

        // Collates all complete reports
        completeReports.values().forEach(this::collateDaybreakReport);

        return completeReports.size();
    }

    /**{@inheritDoc}  */
    @Override
    public List<DaybreakFragment> getPendingDaybreakFragments() {
        return daybreakDao.getPendingDaybreakFragments();
    }

    /**{@inheritDoc}*/
    @Override
    public int processFragments(List<DaybreakFragment> fragments) {
        if (fragments.size() > 0) {
            ExecutorService executorService = Executors.newFixedThreadPool(4, threadFactory);
            threadServices.add(executorService);
            logger.info("Processing " + fragments.size() + " daybreak fragments");
            for (DaybreakFragment daybreakFragment : fragments) {
                executorService.submit(() -> processFragment(daybreakFragment));
            }
            executorService.shutdown();
            try {
                // Allow maximum of 30 minutes before un-blocking
                executorService.awaitTermination(30, TimeUnit.MINUTES);
                threadServices.remove(executorService);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return fragments.size();
    }

    /**{@inheritDoc}*/
    @Override
    public int processPendingFragments() {
        List<DaybreakFragment> daybreakFragments = getPendingDaybreakFragments();
        int processedCount = processFragments(daybreakFragments);
        // Set associated reports as processed
        daybreakFragments.stream()
                .map(DaybreakFragment::getReportDate)
                .distinct()
                .forEach(daybreakDao::setProcessed);

        return processedCount;
    }

    /**{@inheritDoc}*/
    @Override
    public void updatePendingProcessing(DaybreakBillId fragmentId, boolean pendingProcessing) {
        if(pendingProcessing){
            daybreakDao.setPendingProcessing(fragmentId);
        }
        else{
            daybreakDao.setProcessed(fragmentId);
        }
    }
    
    

    /** --- Helper methods --- */

    /**
     * Goes through each file in a daybreak report, stores it, archives it and extracts
     *  any daybreak fragments or page file entries from the file.
     * All fragments and entries are then stored
     * @param daybreakReport
     */
    private void collateDaybreakReport(DaybreakReport<DaybreakFile> daybreakReport){
        List<DaybreakFragment> daybreakFragments = new ArrayList<>();
        List<PageFileEntry> pageFileEntries = new ArrayList<>();

        logger.info("Collating " + daybreakReport.getReportDate());

        for (DaybreakFile daybreakFile : daybreakReport.getReportDocs().values()) {
            // Add each file reference to the store
            daybreakDao.updateDaybreakFile(daybreakFile);

            logger.debug("Parsing " + daybreakFile.getFileName());
            // Get daybreak fragments or page file entries from the daybreak file depending on the type
            try {
                if(daybreakFile.getDaybreakDocType() == DaybreakDocType.PAGE_FILE){
                    pageFileEntries.addAll(DaybreakPageFileParser.extractPageFileEntries(daybreakFile));
                }
                else{
                    daybreakFragments.addAll(DaybreakFileParser.extractDaybreakFragments(daybreakFile));
                }
            }
            catch(IOException ex){
                logger.error("Could not parse daybreak file " + daybreakFile.getFileName());
            }
        }

        // Add a new report entry
        daybreakDao.updateDaybreakReport(daybreakReport.getReportDate());
        // Add all fragments and entries to the store
        logger.info("Saving daybreak fragments");
        daybreakFragments.parallelStream().forEach(daybreakDao::updateDaybreakFragment);
        logger.info("Saving page file entries");
        pageFileEntries.parallelStream().forEach(daybreakDao::updatePageFileEntry);

        // Archive the report files
        daybreakReport.getReportDocs().values().forEach(daybreakFile ->{
            try {
                daybreakDao.archiveDaybreakFile(daybreakFile);
            }
            catch (IOException ex){
                logger.error("An error occurred while archiving " + daybreakFile.getFileName());
            }
        });
    }

    /**
     * Processes an individual daybreak fragment and updates teh persistence layer
     * @param daybreakFragment
     */
    private void processFragment(DaybreakFragment daybreakFragment){
        // This method is called by the ExecutorService which does not pass exceptions to the calling code.
        // Log them here so we have visibility into any errors occurring.
        try {
            // Parse the fragment into a bill
            DaybreakBill daybreakBill = DaybreakFragmentParser.extractDaybreakBill(daybreakFragment);
            // Update the persistence layer
            daybreakDao.updateDaybreakBill(daybreakBill);
            // Set the fragment as processed
            daybreakDao.setProcessed(daybreakFragment.getDaybreakBillId());
        } catch(Exception ex) {
            logger.error("An error has occured while processing a daybreak fragment.", ex);
        }
    }
}
