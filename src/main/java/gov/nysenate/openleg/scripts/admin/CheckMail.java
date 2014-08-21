package gov.nysenate.openleg.scripts.admin;

import gov.nysenate.openleg.scripts.BaseScript;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.util.Config;

import java.io.*;
import java.util.*;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class CheckMail extends BaseScript
{
    private static final Logger logger = Logger.getLogger(CheckMail.class);

    private static final String lockFilePath = "/tmp/openleg.lock";

    public static void main(String[] args) throws Exception
    {
        new CheckMail().run(args);
    }

    @Override
    protected Options getOptions() {
        Options options = super.getOptions();
        options.addOption("d","debug", false, "Runs the CheckMail script but does not continue with a spot check");
        options.addOption("s", "save-emails", false, "Prevents expungement of daybreak emails, allowing them to be detected again");
        options.addOption("a", "run-all", false, "Causes all detected daybreak reports to be spot checked, as opposed to just the most recent report");
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception
    {
        lock(); // Prevents another instance of this script running until unlock

        final boolean debug = opts.hasOption("debug");
        final boolean saveEmails = opts.hasOption("save-emails");
        final boolean runAll = opts.hasOption("run-all");

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.ssl.protocols", "SSLv3");
        Session session = Session.getDefaultInstance(props, null);
        //session.setDebug(true);
        Store store = session.getStore("imaps");
        Config config = Application.getConfig();

        store.connect(
            config.getValue("checkmail.host"),
            config.getValue("checkmail.user"),
            config.getValue("checkmail.pass")
        );

        String receivingFolder = config.getValue("checkmail.receiving");
        String processedFolder = config.getValue("checkmail.processed");

        Folder sourceFolder = navigateToFolder(receivingFolder, store);
        Folder archiveFolder = navigateToFolder(processedFolder, store);
        sourceFolder.open(Folder.READ_WRITE);

        // stores all daybreak emails in a set for each report
        DaybreakSetContainer daybreakSetContainer = new DaybreakSetContainer();

        // Add all emails
        for(Message message : sourceFolder.getMessages()) {
            daybreakSetContainer.addMessage(message);
        }

        List<DaybreakSet> completeSets = daybreakSetContainer.getCompleteSets();
        if(completeSets.size()>0) {
            logger.info("Detected " + completeSets.size() + " complete daybreak reports");

            for(DaybreakSet daybreakSet : completeSets){

                logger.info("Saving report " + daybreakSet.getPrefix() + ":");
                for(DaybreakDocType messageType : DaybreakDocType.values()){
                    Message message = daybreakSet.getMessage(messageType);
                    String filename = daybreakSet.getPrefix() + messageType.getLocalFileExt();

                    if (message.isMimeType("multipart/*")) {
                        Multipart content = (Multipart) message.getContent();
                        for (int i = 0; i < content.getCount(); i++) {
                            Part part = content.getBodyPart(i);
                            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                logger.info("\tSaving " + part.getFileName() + " to " + filename);
                                String attachment = IOUtils.toString(part.getInputStream());
                                String lrsFileDir = config.getValue("checkmail.lrsFileDir");
                                FileUtils.write(new File(lrsFileDir, filename), attachment);
                            }
                        }
                    }

                    if(!saveEmails) {   //Copy the message to the archiving folder and mark it for deletion
                        sourceFolder.copyMessages(new Message[]{message}, archiveFolder);
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }
            }

            if(!saveEmails) {   // Finalize the message deletion from the source folder
                sourceFolder.expunge();
            }
        }

        List<DaybreakSet> partialReports = daybreakSetContainer.getPartialSets();
        if(partialReports.size()>0){
            logger.info("Detected " + partialReports.size() + " incomplete daybreak reports");
            for(DaybreakSet partialSet : partialReports){
                logger.info("Partial report " + partialSet.getPrefix() + " contains:");
                for(DaybreakDocType messageType : DaybreakDocType.values()){
                    Message message = partialSet.getMessage(messageType);
                    if(message!=null){
                        logger.info("\t" + message.getSubject());
                    }
                }
            }
        }

        if(!debug && completeSets.size()>0) {
            if(!runAll) {   // Run just the newest report
                Collections.sort(completeSets, Collections.reverseOrder());
                DaybreakSet newestSet = completeSets.get(0);
                opts.getArgList().add(0, newestSet.getPrefix());
                new SpotCheck().execute(opts);
                new CreateErrors().execute(opts);
            }
            else{   // Run all reports in chronological order
                Collections.sort(completeSets);
                for(DaybreakSet daybreakSet : completeSets){
                    opts.getArgList().add(0, daybreakSet.getPrefix());
                    new SpotCheck().execute(opts);
                    new CreateErrors().execute(opts);
                }
            }
        }
    }

    private Folder navigateToFolder(String path, Store store) throws Exception{
        String[] splitPath = path.split("/");
        Folder folder = store.getFolder(splitPath[0]);
        for(int i=1; i<splitPath.length; i++) {
            folder = folder.getFolder(splitPath[i]);
        }
        return folder;
    }

    private void lock() throws IOException{
        File lockFile = new File(lockFilePath);
        if(lockFile.exists()){
            logger.error("Instance of CheckMail already running: halting execution.");
            System.exit(1);
        }
        logger.info("Creating lockfile: " + lockFilePath);
        boolean fileCreated = lockFile.createNewFile();
        if(!fileCreated){
            logger.error("Error creating lock file");
            throw new IOException("Could not create lock file");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run(){
                try {
                    unlock();
                } catch (Exception e) {
                    logger.error("Error deleting lock file", e);
                }
            }
        });
    }

    private void unlock() throws IOException{
        boolean deleteResult = new File(lockFilePath).delete();
        if(!deleteResult){
            throw new IOException("Lock file was not deleted");
        }
        logger.info("Lockfile " + lockFilePath + " destroyed");
    }

}
