package gov.nysenate.openleg.spotchecks.daybreak;

import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.spotchecks.base.BaseCheckMailService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DaybreakFile.reportDateMatchPattern);
    private File daybreakStagingDir;

    @PostConstruct
    private void init() {
        daybreakStagingDir = new File(environment.getStagingDir(), "daybreak");
    }

    @Override
    protected void saveMessage(Message message, File file) throws MessagingException, IOException {
        if (!message.isMimeType("multipart/*"))
            return;
        Multipart content = (Multipart) message.getContent();
        int partCount = content.getCount();
        for (int i = 0; i < partCount; i++) {
            Part part = content.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                logger.info("\tSaving " + part.getFileName() + " to " + file.getAbsolutePath());
                String attachment = IOUtils.toString(part.getInputStream(), Charset.defaultCharset());
                FileIOUtils.write(file, attachment);
            }
        }
    }

    @Override
    public int saveReports() throws MessagingException, IOException {
        DaybreakReportSet<DaybreakMessage> reports = getReports(mailUtils.getIncomingMessages());
        Map<LocalDate, DaybreakReport<DaybreakMessage>> completeReports = reports.getCompleteReports();
        Map<LocalDate, DaybreakReport<DaybreakMessage>> partialReports = reports.getPartialReports();
        if (completeReports.size() > 0) {
            logger.info("{} complete daybreak report{} found.  Saving...", completeReports.size(),
                    completeReports.size() == 1 ? "" : "s");
            // If a full set of daybreak emails is detected, the daybreak file attachments are saved as daybreak files.
            saveCompleteReports(completeReports.values());
            logger.info("Daybreak files saved.");
        }
        if (partialReports.size() > 0) {
            logger.info("{} partial daybreak report{} found.", partialReports.size(),
                    partialReports.size() == 1 ? "" : "s");
            savePartialReports(partialReports.values());
        }
        return completeReports.size();
    }

    /**
     * Extracts all valid daybreak messages from the given source Message list,
     * and places each of them in a report within the report set.
     * @return set of Daybreak email messages.
     */
    private static DaybreakReportSet<DaybreakMessage> getReports(Message[] messages) throws MessagingException {
        var reports = new DaybreakReportSet<DaybreakMessage>();
        for (var message : messages) {
            if (DaybreakDocType.getMessageDocType(message.getSubject()) != null)
                reports.insertDaybreakDocument(new DaybreakMessage(message));
        }
        return reports;
    }

    /**
     * Stages DaybreakMessages for processing, and moves daybreak emails to the processed folder.
     * @param completeReports maps dates to full DaybreakReports.
     */
    private void saveCompleteReports(Iterable<DaybreakReport<DaybreakMessage>> completeReports)
            throws MessagingException, IOException {
        var toArchive = new ArrayList<Message>();
        for (var report : completeReports) {
            String prefix = report.getReportDate().format(formatter);
            for (DaybreakMessage daybreakMessage : report.getReportDocs().values()) {
                String filename = prefix + daybreakMessage.getDaybreakDocType().getLocalFileExt();
                Message message = daybreakMessage.getMessage();
                saveMessage(message, new File(daybreakStagingDir, filename));
                toArchive.add(message);
            }
        }
        mailUtils.moveMessages(toArchive, true);
    }

    /**
     * If partial reports are over 10 days old, they are definitely
     * out of date, and the complete report isn't coming.
     * So, we should move them out of the incoming folder.
     * @param partialReports to check the dates of.
     */
    private void savePartialReports(Iterable<DaybreakReport<DaybreakMessage>> partialReports)
            throws MessagingException {
        var oldReports = new ArrayList<Message>();
        for (var report : partialReports) {
            if (report.getReportDate().isBefore(LocalDate.now().minusDays(10))) {
                for (DaybreakMessage daybreakMessage : report.getReportDocs().values())
                    oldReports.add(daybreakMessage.getMessage());
            }
        }
        if (!oldReports.isEmpty())
            logger.info("Archiving {} old partial daybreak report{}.", oldReports.size(),
                    oldReports.size() == 1 ? "" : "s");
        mailUtils.moveMessages(oldReports, false);
    }
}
