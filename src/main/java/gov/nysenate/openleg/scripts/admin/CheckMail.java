package gov.nysenate.openleg.scripts.admin;

import gov.nysenate.openleg.scripts.BaseScript;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.util.Config;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.cli.CommandLine;
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
    protected void execute(CommandLine opts) throws Exception
    {
        lock();

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

        String prefix="";
        boolean runSpotCheck = false;
        for(Message message : source.getMessages()) {
            Date sent = message.getSentDate();
            String filename;
            prefix = new SimpleDateFormat("yyyyMMdd").format(sent);
            if (message.getSubject().contains("Sen Act Title Sum Spon Law 4001-9999")) {
                filename = prefix + ".senate.high.html";
                runSpotCheck = true;
            }
            else if (message.getSubject().contains("Sen Act Title Sum Spon Law 1-4000")) {
                filename = prefix + ".senate.low.html";
                runSpotCheck = true;
            }
            else if (message.getSubject().contains("Asm Act Title Sum Spon Law 4001-99999")) {
                filename = prefix + ".assembly.high.html";
                runSpotCheck = true;
            }
            else if (message.getSubject().contains("Asm Act Title Sum Spon Law 1-4000")) {
                filename = prefix + ".assembly.low.html";
                runSpotCheck = true;
            }
            else if (message.getSubject().contains("Job ABPSDD - LBDC all Bills")) {
                filename = prefix + ".page_file.txt";
            }
            else {
                logger.error("Unknown subject line: "+message.getSubject());
                continue;
            }

            if (message.isMimeType("multipart/*")) {
                Multipart content = (Multipart) message.getContent();
                for (int i = 0; i < content.getCount(); i++) {
                    Part part = content.getBodyPart(i);
                    if (Part.ATTACHMENT.equals(part.getDisposition())) {
                        System.out.println("Saving " + part.getFileName() + " to " + filename);
                        String attachment = IOUtils.toString(part.getInputStream());
                        String lrsFileDir = config.getValue("checkmail.lrsFileDir");
                        FileUtils.write(new File(lrsFileDir, filename), attachment);
                    }
                }
            }

            source.copyMessages(new Message[]{message}, destination);
            message.setFlag(Flags.Flag.DELETED, true);
        }

        // Finalize the message deletion from the source folder
        source.expunge();

        // Run the new report and regenerate our errors.
        if (runSpotCheck) {
            opts.getArgList().add(prefix);
            new SpotCheck().execute(opts);
            new CreateErrors().execute(opts);
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

    private void lock() throws Exception{
        File lockFile = new File(lockFilePath);
        if(lockFile.exists()){
            logger.error("Instance of CheckMail already running: halting execution.");
            System.exit(1);
        }
        logger.info("Creating lockfile: " + lockFilePath);
        lockFile.createNewFile();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run(){
                try {
                    unlock();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void unlock() throws Exception{
        new File(lockFilePath).delete();
        logger.info("Lockfile " + lockFilePath + " destroyed");
    }

}
