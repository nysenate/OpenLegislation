package gov.nysenate.openleg.scripts.admin;

import gov.nysenate.openleg.scripts.BaseScript;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.util.Config;

import java.io.*;
import java.text.SimpleDateFormat;
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
        return options;
    }

    @Override
    protected void execute(CommandLine opts) throws Exception
    {
        lock();

        boolean debug = opts.hasOption("debug");
        boolean saveEmails = opts.hasOption("save-emails");

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.ssl.protocols", "SSLv3");
        Session session = Session.getDefaultInstance(props, null);
        //session.setDebug(true);
        Store store = session.getStore("imaps");
        Config config = Application.getConfig();
//        logger.info(config.getValue("checkmail.host"));
//        logger.info(config.getValue("checkmail.user"));
//        logger.info(config.getValue("checkmail.pass"));
        store.connect(
            config.getValue("checkmail.host"),
            config.getValue("checkmail.user"),
            config.getValue("checkmail.pass")
        );

        String receivingFolder = config.getValue("checkmail.receiving");
        String processedFolder = config.getValue("checkmail.processed");

        Folder source = navigateToFolder(receivingFolder, store);
        Folder destination = navigateToFolder(processedFolder, store);
        source.open(Folder.READ_WRITE);

        Date prefixDate = null;
        boolean runSpotCheck;
        boolean senateHigh, senateLow, assemblyHigh, assemblyLow, pageFile;
        senateHigh = senateLow = assemblyHigh = assemblyLow = pageFile = false;
        Map<String, Message> validMessages = new HashMap<String, Message>();   // Stores filename, message

        for(Message message : source.getMessages()) {
            String filename;
            if (message.getSubject().contains("Sen Act Title Sum Spon Law 4001-9999")) {
                filename = ".senate.high.html";
                senateHigh = true;
            } else if (message.getSubject().contains("Sen Act Title Sum Spon Law 1-4000")) {
                filename = ".senate.low.html";
                senateLow = true;
            } else if (message.getSubject().contains("Asm Act Title Sum Spon Law 4001-99999")) {
                filename = ".assembly.high.html";
                assemblyHigh = true;
            } else if (message.getSubject().contains("Asm Act Title Sum Spon Law 1-4000")) {
                filename = ".assembly.low.html";
                assemblyLow = true;
            } else if (message.getSubject().contains("Job ABPSDD - LBDC all Bills")) {
                filename = ".page_file.txt";
                pageFile = true;
            } else {
                logger.error("Unknown subject line: " + message.getSubject());
                continue;
            }
            Date messageDate = message.getSentDate();
            if(prefixDate==null || prefixDate.before(messageDate)){
                prefixDate = messageDate;
            }
            validMessages.put(filename, message);
        }

        String prefix = new SimpleDateFormat("yyyyMMdd").format(prefixDate);

        runSpotCheck = senateHigh && senateLow && assemblyHigh && assemblyLow && pageFile;

        if (runSpotCheck) {
            // Download the messages
            for(Map.Entry<String, Message> messageEntry : validMessages.entrySet()) {
                String filename = prefix + messageEntry.getKey();
                Message message = messageEntry.getValue();

                if (message.isMimeType("multipart/*")) {
                    Multipart content = (Multipart) message.getContent();
                    for (int i = 0; i < content.getCount(); i++) {
                        Part part = content.getBodyPart(i);
                        if (Part.ATTACHMENT.equals(part.getDisposition())) {
                            logger.info("Saving " + part.getFileName() + " to " + filename);
                            String attachment = IOUtils.toString(part.getInputStream());
                            String lrsFileDir = config.getValue("checkmail.lrsFileDir");
                            FileUtils.write(new File(lrsFileDir, filename), attachment);
                        }
                    }
                }

                if(!saveEmails) {
                    source.copyMessages(new Message[]{message}, destination);
                    message.setFlag(Flags.Flag.DELETED, true);
                }
            }

            if(!saveEmails) {
                // Finalize the message deletion from the source folder
                source.expunge();
            }

            if(!debug) {
                // Run the new report and regenerate our errors.
                opts.getArgList().add(0, prefix);
                new SpotCheck().execute(opts);
                new CreateErrors().execute(opts);
            }
        }
        else{
            if(senateHigh || senateLow || assemblyHigh || assemblyLow || pageFile)
                logger.info("Spot check email(s) detected, one or more MISSING");
            else
                logger.info("No relevant emails detected");
        }

        //unlock();
    }

    private Folder navigateToFolder(String path, Store store) throws Exception{
        String[] splitPath = path.split("/");
        Folder folder = store.getFolder(splitPath[0]);
        for(int i=1; i<splitPath.length; i++)
            folder = folder.getFolder(splitPath[i]);
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
