package gov.nysenate.openleg.service.spotcheck.daybreak;

import gov.nysenate.openleg.model.spotcheck.daybreak.*;
import gov.nysenate.openleg.service.spotcheck.base.BaseCheckMailService;
import gov.nysenate.openleg.service.spotcheck.base.CheckMailService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DaybreakCheckMailService extends BaseCheckMailService implements CheckMailService
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakCheckMailService.class);

    private File daybreakStagingDir;

    @PostConstruct
    public void init() {
        daybreakStagingDir = new File(environment.getStagingDir(), "daybreak");
    }

    /**
     * Checks an email server for incoming daybreak emails.
     * If a full set of daybreak emails is detected, the daybreak file attachments are saved as daybreak files
     * @return int - the number of reports saved
     */
    public int checkMail() {
        Store store = null;
        int savedReports = 0;
        try {
            logger.info("checking for daybreak emails...");
            store = mailUtils.getCheckMailStore();

            Folder sourceFolder = mailUtils.navigateToFolder(environment.getEmailReceivingFolder(), store);
            Folder archiveFolder = mailUtils.navigateToFolder(environment.getEmailProcessedFolder(), store);
            sourceFolder.open(Folder.READ_WRITE);

            DaybreakReportSet<DaybreakMessage> reports = getReports(sourceFolder);
            Map<LocalDate, DaybreakReport<DaybreakMessage>> completeReports = reports.getCompleteReports();
            Map<LocalDate, DaybreakReport<DaybreakMessage>> partialReports = reports.getPartialReports();
            if (completeReports.size() > 0) {
                logger.info("{} complete daybreak reports found.  Saving...", completeReports.size());
                saveCompleteReports(completeReports, sourceFolder, archiveFolder);
                logger.info("Daybreak files saved.");
                savedReports = completeReports.size();
            }
            if (partialReports.size() > 0) {
                logger.info("{} partial daybreak reports found.", partialReports.size());
            }
            if (completeReports.size() == 0 && partialReports.size() == 0) {
                logger.info("No daybreak reports found");
            }
        } catch (MessagingException | IOException ex) {
            logger.error("CheckMail Error\n{}", ExceptionUtils.getStackTrace(ex));
        } finally {
            try {
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException ignored) {}
        }
        return savedReports;
    }

    /**
     * --- Internal Methods ---
     */

    /**
     * Extracts all valid daybreak messages from the given source folder
     *  and places each of them in a report within the report set
     *
     * @param sourceFolder
     * @return
     * @throws MessagingException
     */
    private DaybreakReportSet<DaybreakMessage> getReports(Folder sourceFolder) throws MessagingException {
        DaybreakReportSet<DaybreakMessage> reports = new DaybreakReportSet<>();
        for (Message message : sourceFolder.getMessages()) {
            if (DaybreakDocType.getMessageDocType(message.getSubject()) != null) {
                reports.insertDaybreakDocument(new DaybreakMessage(message));
            }
        }
        return reports;
    }

    private void saveCompleteReports(Map<LocalDate, DaybreakReport<DaybreakMessage>> completeReports,
                                     Folder sourceFolder, Folder archiveFolder)
            throws MessagingException, IOException {
        List<Message> toArchive = new ArrayList<>();
        for (DaybreakReport<DaybreakMessage> report : completeReports.values()) {
            String prefix = report.getReportDate().format(DateTimeFormatter.ofPattern(DaybreakFile.reportDateMatchPattern));
            for (DaybreakMessage daybreakMessage : report.getReportDocs().values()) {
                String filename = prefix + daybreakMessage.getDaybreakDocType().getLocalFileExt();
                Message message = daybreakMessage.getMessage();

                saveMessageAttachment(message, new File(daybreakStagingDir, filename));
                toArchive.add(message);
            }
        }
        moveToArchive(sourceFolder, archiveFolder, toArchive.toArray(new Message[toArchive.size()]));
    }
}
