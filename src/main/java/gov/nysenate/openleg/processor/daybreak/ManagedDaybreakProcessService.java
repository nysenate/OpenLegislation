package gov.nysenate.openleg.processor.daybreak;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.daybreak.DaybreakDao;
import gov.nysenate.openleg.model.daybreak.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManagedDaybreakProcessService implements DaybreakProcessService{

    private static Logger logger = LoggerFactory.getLogger(ManagedDaybreakProcessService.class);

    @Autowired
    private DaybreakDao daybreakDao;

    /** --- Interfaced Methods --- */

    /**{@InheritDoc}*/
    @Override
    public int collateDaybreakReports() {
        DaybreakReportSet<DaybreakFile> reportSet;
        try {
            reportSet = daybreakDao.getIncomingReports();
        }
        catch (IOException ex){
            logger.error("Could not retrieve incoming daybreak files");
            return 0;
        }

        // Prints the status of all files found in the incoming directory
        logger.info(" --- Complete reports ---");
        for (DaybreakReport<DaybreakFile> daybreakReport: reportSet.getCompleteReports().values()) {
            logger.info("Report " + daybreakReport.getReportDate());
            daybreakReport.getReportDocs().values().forEach(daybreakFile -> logger.info('\t' + daybreakFile.getFileName()));
        }
        logger.info(" --- Partial reports --- ");
        for (DaybreakReport<DaybreakFile> daybreakReport: reportSet.getPartialReports().values()) {
            logger.info("Report " + daybreakReport.getReportDate());
            daybreakReport.getReportDocs().values().forEach(daybreakFile -> logger.info('\t' + daybreakFile.getFileName()));
        }
        logger.info(" --- Duplicate files --- ");
        reportSet.getDuplicateDocuments().forEach(file -> logger.info('\t' + file.getFileName()));

        // Collates all complete reports
        reportSet.getCompleteReports().values().forEach(this::collateDaybreakReport);

        return reportSet.getCompleteReports().values().size();
    }

    /**{@InheritDoc}*/
    @Override
    public List<DaybreakFragment> getPendingDaybreakFragments() {
        return daybreakDao.getPendingDaybreakFragments();
    }

    /**{@InheritDoc}*/
    @Override
    public void processFragments(List<DaybreakFragment> fragments) {
        logger.info("Processing " + fragments.size() + " daybreak fragments");
        for(DaybreakFragment daybreakFragment : fragments){
            // Parse the fragment into a bill
            DaybreakBill daybreakBill = DaybreakFragmentParser.extractDaybreakBill(daybreakFragment);
            // Update the persistence layer
            daybreakDao.updateDaybreakBill(daybreakBill);
            // Set the fragment as processed
            daybreakDao.setProcessed(daybreakFragment.getDaybreakBillId());
        }
    }

    /**{@InheritDoc}*/
    @Override
    public void processPendingFragments() {
        processFragments(getPendingDaybreakFragments());
    }

    /**{@InheritDoc}*/
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

        for (DaybreakFile daybreakFile : daybreakReport.getReportDocs().values()) {
            // Add each file reference to the store and archive the actual file
            daybreakDao.updateDaybreakFile(daybreakFile);
            try {
                daybreakDao.archiveDaybreakFile(daybreakFile);
            }
            catch (IOException ex){
                logger.error("An error occurred while archiving " + daybreakFile.getFileName());
            }

            logger.debug("Parsing " + daybreakFile.getFileName());
            // Get daybreak fragments or page file entries from the daybreak file depending on the type
            try {
                if(daybreakFile.getDayBreakDocType()==DaybreakDocType.PAGE_FILE){
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

        // Add all fragments and entries to the store
        logger.debug("Inserting daybreak fragments");
        daybreakFragments.forEach(daybreakDao::updateDaybreakFragment);
        logger.debug("Inserting page file entries");
        pageFileEntries.forEach(daybreakDao::updatePageFileEntry);
    }
}
