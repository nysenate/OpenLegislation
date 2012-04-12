package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.util.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Collate {
    private static Logger logger = Logger.getLogger(Collate.class);

    public static void main(String[] args) {
        CommandLine opts = null;
        try {
            Options options = new Options()
                .addOption("s", "source", true, "Source data directory")
                .addOption("d", "dest", true, "Destination data directory")
                .addOption("h", "help", false, "Print this message");
            opts = new PosixParser().parse(options, args);
            if(opts.hasOption("-h")) {
                new HelpFormatter().printHelp("posix", options );
                System.exit(0);
            }
        } catch (ParseException e) {
            logger.fatal("Error parsing arguments: ", e);
            System.exit(0);
        }

        String source = opts.getOptionValue("source", Config.get("data"));
        String dest = opts.getOptionValue("dest", Config.get("work", source));

        File sourceDirectory = new File(source).getAbsoluteFile();
        File destDirectory = new File(dest).getAbsoluteFile();
        File bills = new File(destDirectory, "bills");
        File agendas = new File(destDirectory, "agendas");
        File calendars = new File(destDirectory, "calendars");
        File annotations = new File(destDirectory, "annotations");
        File transcripts = new File(destDirectory, "transcripts");

        try {
            FileUtils.forceMkdir(bills);
            FileUtils.forceMkdir(agendas);
            FileUtils.forceMkdir(calendars);
            FileUtils.forceMkdir(annotations);
            FileUtils.forceMkdir(transcripts);
        } catch (IOException e){
            logger.fatal("Could not create destination folders.", e);
            System.exit(0);
        }

        for (File file : FileUtils.listFiles(sourceDirectory, null, false)) {
            logger.debug("Processing: "+file);

            int inc = 1;
            String in = null;
            BufferedReader br = null;

            try {
                br = new BufferedReader(new StringReader(FileUtils.readFileToString(file, "UTF-8")));
                // Sort the different file parts to their destination folders
                in = br.readLine();

                if (in==null) {
                    br.close();
                    logger.error("Moving empty file: "+file);
                    FileUtils.moveFileToDirectory(file, destDirectory, false);

                } else if (!in.startsWith("<?xml")) {
                    br.close();
                    logger.info("Moving transcript: "+file);
                    FileUtils.moveFileToDirectory(file, transcripts, false);

                } else {
                    do {
                        if(in.matches("<sencalendar.+")) {
                            File calendar = new File(calendars, file.getName()+"-calendar-"+inc+".xml");
                            logger.info("Extracting calendar: "+calendar);
                            write(getXml("</sencalendar.+", in, br), calendar);
                            inc++;
                        }
                        else if(in.matches("<senagenda.+")) {
                            File agenda = new File(agendas, file.getName()+"-agenda-"+inc+".xml");
                            logger.info("Extracting agenda: "+agenda);
                            write(getXml("</senagenda.+", in, br), agenda);
                            inc++;
                        }
                        else if(in.matches("<senannotated.+")) {
                            File annotation = new File(annotations, file.getName()+"-annotation-"+inc+".xml");
                            logger.info("Extracting annotation: "+annotation);
                            write(getXml("</senannotated.+", in, br), annotation);
                            inc++;
                        }
                    } while((in = br.readLine()) != null);

                    br.close();
                    logger.info("Moving bill: "+file);
                    FileUtils.moveFileToDirectory(file, bills, false);
                }

            } catch (FileNotFoundException e) {
                logger.error("File not Found", e);
            } catch (IOException e) {
                logger.error("IO Error", e);
            }
        }

    }

    private static void write(String data, File file) throws IOException {
        // TODO: Figure out all this matcher magic. How does it work?
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("<\\!\\[CDATA\\[(.*?)\\]\\]>").matcher(data);
        while(m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0).replaceAll("&newl;", "").replaceAll("\\\\n","\n")));
        }
        m.appendTail(sb);
        data = sb.toString().replaceAll("&newl;", "\n").replaceAll("(?!\n)\\p{Cntrl}","").replaceAll("(?!\\.{2})[ ]{2,}"," ");
        FileUtils.write(file, data);
    }

    private static String getXml(String escape, String line, BufferedReader br) throws IOException {
        StringBuffer sb = new StringBuffer(
            "<?xml version='1.0' encoding='UTF-8'?>&newl;" +
            "<SENATEDATA>&newl;" +
             line+"&newl;");

        String in;
        while((in  = br.readLine()) != null) {
            sb.append(in.replaceAll("\\xb9","&sect;") + "&newl;");
            if(in.matches(escape))
                break;
        }

        return sb.append("</SENATEDATA>").toString();
    }
}
