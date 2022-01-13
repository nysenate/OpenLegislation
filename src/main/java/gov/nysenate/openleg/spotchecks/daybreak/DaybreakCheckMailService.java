package gov.nysenate.openleg.spotchecks.daybreak;

import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.spotchecks.base.BaseCheckMailService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@Service
public class DaybreakCheckMailService extends BaseCheckMailService {
    private static final Logger logger = LoggerFactory.getLogger(DaybreakCheckMailService.class);

    private File daybreakStagingDir;

    @PostConstruct
    public void init() {
        daybreakStagingDir = new File(environment.getStagingDir(), "daybreak");
    }

    @Override
    public int saveReports(Folder sourceFolder, Folder archiveFolder) throws MessagingException, IOException {
        logger.info("Checking for daybreak emails...");
        DaybreakReportSet<DaybreakMessage> reports = getReports(sourceFolder);
        Map<LocalDate, DaybreakReport<DaybreakMessage>> completeReports = reports.getCompleteReports();
        Map<LocalDate, DaybreakReport<DaybreakMessage>> partialReports = reports.getPartialReports();
        if (completeReports.size() > 0) {
            logger.info("{} complete daybreak reports found.  Saving...", completeReports.size());
            // If a full set of daybreak emails is detected, the daybreak file attachments are saved as daybreak files.
            saveCompleteReports(completeReports, sourceFolder, archiveFolder);
            logger.info("Daybreak files saved.");
        }
        if (partialReports.size() > 0)
            logger.info("{} partial daybreak reports found.", partialReports.size());
        else if (completeReports.size() == 0)
            logger.info("No daybreak reports found.");
        return completeReports.size();
    }

    @Override
    protected void saveMessage(Message message, File file) throws MessagingException, IOException {
        boolean isMimeType = false;
        try {
            isMimeType = message.isMimeType("multipart/*");
        }
        catch (NullPointerException ex) {
            logger.error("Problem parsing mime type in message: {}", message);
        }
        if (!isMimeType)
            return;
        Multipart content = (Multipart) message.getContent();
        int partCount = content.getCount();
        logger.info("Saving {} parts of a daybreak message.", partCount);
        for (int i = 0; i < partCount; i++) {
            Part part = content.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                logger.info("\tSaving " + part.getFileName() + " to " + file.getAbsolutePath());
                String attachment = IOUtils.toString(part.getInputStream(), Charset.defaultCharset());
                FileIOUtils.write(file, attachment);
            }
        }
    }

    /**
     * Extracts all valid daybreak messages from the given source folder,
     * and places each of them in a report within the report set.
     * @param sourceFolder to check for messages.
     * @return set of Daybreak email messages.
     */
    private DaybreakReportSet<DaybreakMessage> getReports(Folder sourceFolder) throws MessagingException {
        DaybreakReportSet<DaybreakMessage> reports = new DaybreakReportSet<>();
        var messages = sourceFolder.getMessages();
        for (Message message : messages) {
            if (DaybreakDocType.getMessageDocType(message.getSubject()) != null)
                reports.insertDaybreakDocument(new DaybreakMessage(message));
            else
                logger.warn("Email could not be identified as a daybreak email: " + message.getSubject());
            // We could continue processing if the Thread has been interrupted, but it would take too long.
            if (Thread.currentThread().isInterrupted())
                return new DaybreakReportSet<>();
        }
        return reports;
    }

    /**
     * Stages DaybreakMessages for processing, and moves daybreak emails to the processed folder.
     * @param completeReports maps dates to full DaybreakReports.
     * @param sourceFolder to delete Messages in.
     * @param processedFolder to move Messages to.
     */
    private void saveCompleteReports(Map<LocalDate, DaybreakReport<DaybreakMessage>> completeReports,
                                     Folder sourceFolder, Folder processedFolder)
            throws MessagingException, IOException {
        var toArchive = new ArrayList<Message>();
        for (DaybreakReport<DaybreakMessage> report : completeReports.values()) {
            String prefix = report.getReportDate().format(DateTimeFormatter.ofPattern(DaybreakFile.reportDateMatchPattern));
            for (DaybreakMessage daybreakMessage : report.getReportDocs().values()) {
                if (Thread.currentThread().isInterrupted())
                    return;
                String filename = prefix + daybreakMessage.getDaybreakDocType().getLocalFileExt();
                Message message = daybreakMessage.getMessage();
                saveMessage(message, new File(daybreakStagingDir, filename));
                toArchive.add(message);
            }
        }
        moveToArchive(sourceFolder, processedFolder, toArchive.toArray(new Message[0]));
    }
}
