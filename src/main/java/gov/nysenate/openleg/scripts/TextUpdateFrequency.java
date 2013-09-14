package gov.nysenate.openleg.scripts;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class TextUpdateFrequency
{
    public static void main(String[] args) throws IOException, ParseException
    {
        Pattern linePattern = Pattern.compile(".*?Processing (SOBI.D[0-9]{6}.T[0-9]{6}.TXT)-bill-[0-9].sobi:[0-9]+:([0-9]{4})([A-Z][0-9]{5})([A-Z]?) ?T");
        // 16:25:45,621  INFO BillProcessor:140 - Processing SOBI.D121003.T104603.TXT-bill-1.sobi:0:2011S07852 T
        SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ArrayList<String> lines = new ArrayList<String>();
        for (String line : FileUtils.readLines(new File("/data/openleg/lbdc_test/text_updates.txt"))) {
            Matcher lineMatcher = linePattern.matcher(line);
            if (!lineMatcher.find()) {
                System.err.println(line);
                continue;
            }

            if (lineMatcher.group(2).equals("2013") && lineMatcher.group(3).startsWith("S")) {
                Date date = sobiDateFormat.parse(lineMatcher.group(1));
                lines.add("INSERT INTO textupdates VALUES (\""+lineMatcher.group(3)+"\",\""+lineMatcher.group(4)+"\",\""+outputFormat.format(date)+"\");");
            }
        }
        FileUtils.writeLines(new File("/data/openleg/lbdc_test/textupdates_inserts.sql"), lines);
    }
}
