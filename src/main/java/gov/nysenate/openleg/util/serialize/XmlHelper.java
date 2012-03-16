package gov.nysenate.openleg.util.serialize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlHelper {

    public static String fixCDATA(String data) {
        Pattern p = Pattern.compile("<\\!\\[CDATA\\[(.*?)\\]\\]>");
        Matcher m = p.matcher(data);

        StringBuffer sb = new StringBuffer();

        while(m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0).replaceAll("&newl;", "").replaceAll("\\\\n","\n")));
        }
        m.appendTail(sb);

        return sb.toString().replaceAll("&newl;", "\n");
    }

    public static void fixCalendar(File file) {
        String data = flatten(file);

        Pattern p = Pattern.compile("\\<sections\\>.+?\\<\\/sections\\>");
        Matcher m = p.matcher(data);
        if(!m.find()) {
            data = data.replace("</sections>", "");
        }

        Pattern.compile("\\<section.*?\\>.+?\\<\\/section\\>");
        m = p.matcher(data);
        if(!m.find()) {
            data = data.replace("</section>", "");
        }

        write(data.replaceAll("&newl;", "\n"), file);
    }

    public static void separateXmlFromSobi(File file) {
        String in = null;
        int inc = 1;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "ISO-8859-1"));
            in  = null;

            while((in = br.readLine()) != null) {
                if(in.matches("<sencalendar.+")) {
                    write(getXml("</sencalendar.+", in, br), new File(file.getAbsoluteFile() + "-calendar-" + inc + ".xml"));
                    inc++;
                }
                else if(in.matches("<senagenda.+")) {
                    write(getXml("</senagenda.+", in, br), new File(file.getAbsoluteFile() + "-agenda-" + inc + ".xml"));
                    inc++;
                }
                else if(in.matches("<senannotated.+")) {
                    write(getXml("</senannotated.+", in, br), new File(file.getAbsoluteFile() + "-annotation-" + inc + ".xml"));
                    inc++;
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getXml(String escape, String line, BufferedReader br) throws IOException {
        StringBuffer sb = new StringBuffer("");
        sb.append("<?xml version='1.0' encoding='UTF-8'?>&newl;");
        sb.append("<SENATEDATA>&newl;");
        sb.append(line + "&newl;");

        String in = null;

        while((in  = br.readLine()) != null) {
            sb.append(in.replaceAll("\\xb9","&sect;") + "&newl;");

            if(in.matches(escape))
                break;
        }

        sb.append("</SENATEDATA>");

        return sb.toString();
    }

    private static void write(String data, File file) {
        try {
            data = fixCDATA(data);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(data.replaceAll("(?!\n)\\p{Cntrl}","").replaceAll("(?!\\.{2})[ ]{2,}"," "));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String flatten(File file) {
        String ret = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String in = null;
            while((in = br.readLine()) != null) {
                ret += in + "&newl;";
            }
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void generateXml(String path) {
        File file = new File(path);
        if (file.isDirectory())	{
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++)
            {
                if(files[i].isFile()) {
                    XmlHelper.separateXmlFromSobi(files[i]);
                }
                else if(files[i].isDirectory()) {
                    generateXml(files[i].getAbsolutePath());
                }
            }
        }
        else {
            XmlHelper.separateXmlFromSobi(file);
        }
    }
}
