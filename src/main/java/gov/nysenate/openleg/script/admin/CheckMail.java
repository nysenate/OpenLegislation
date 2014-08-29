package gov.nysenate.openleg.script.admin;

import gov.nysenate.openleg.script.BaseScript;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.mail.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Deprecated
public class CheckMail extends BaseScript
{
    private static final Logger logger = Logger.getLogger(CheckMail.class);

    public static void main(String[] args) throws Exception
    {
//        new CheckMail().run(args);
    }

    @Override
    protected void execute(CommandLine opts) throws Exception
    {
        String[] args = opts.getArgs();
        File lrsFileDir = new File(args[0]);
        String filenamePrefix = args[1];

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.ssl.protocols", "SSLv3");
        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect("webmail.senate.state.ny.us", "kim", "s3nat32011");

        Folder source = store.getFolder("OpenLegislation").getFolder("LRSAutomated");
        Folder destination = store.getFolder("OpenLegislation").getFolder("LRSProcessed");
        source.open(Folder.READ_WRITE);

        boolean runSpotCheck = false;
        for(Message message : source.getMessages()) {
            Date sent = message.getSentDate();
            String filename = new SimpleDateFormat("yyyyMMdd").format(sent);
            if (message.getSubject().contains("Sen Act Title Sum Spon Law 4001-9999")) {
                filename = filenamePrefix + ".senate.high.html";
                runSpotCheck = true;
            }
            else if (message.getSubject().contains("Sen Act Title Sum Spon Law 1-4000")) {
                filename = filenamePrefix + ".senate.low.html";
                runSpotCheck = true;
            }
            else if (message.getSubject().contains("Asm Act Title Sum Spon Law 4001-99999")) {
                filename = filenamePrefix + ".assembly.high.html";
                runSpotCheck = true;
            }
            else if (message.getSubject().contains("Asm Act Title Sum Spon Law 1-4000")) {
                filename = filenamePrefix + ".assembly.low.html";
                runSpotCheck = true;
            }
            else if (message.getSubject().contains("Job ABPSDD - LBDC all Bills")) {
                filename = filenamePrefix + ".page_file.txt";
            }
            else {
                logger.error("Unknown subject line: "+message.getSubject());
                continue;
            }

            if (message.isMimeType("multipart/*")) {
                Multipart content = (Multipart)message.getContent();
                for (int i = 0; i < content.getCount(); i++) {
                    Part part = content.getBodyPart(i);
                    if (Part.ATTACHMENT.equals(part.getDisposition())) {
                        System.out.println("Saving "+part.getFileName()+" to "+filename);
                        String attachment = IOUtils.toString(part.getInputStream());
                        FileUtils.write(new File(lrsFileDir, filename), attachment);
                    }
                }
            }

            source.copyMessages(new Message[]{message}, destination);
            message.setFlag(Flags.Flag.DELETED, true);
        }

        // Finalize the message deletion from the source folder
        source.expunge();
    }
}
