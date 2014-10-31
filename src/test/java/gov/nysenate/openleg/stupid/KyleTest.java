package gov.nysenate.openleg.stupid;

import com.google.common.base.Utf8;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kyle on 10/8/14.
 */
public class KyleTest {

    @Test
    public void test() throws Exception {


        File input = new File("/home/kyle/Test");

        Document doc = Jsoup.parse(input, "UTF-8");

        Elements center = doc.getElementsByTag("center");
        String text = center.first().text();
        //Pattern day = Pattern.compile("(MONDAY, | TUESDAY, | WEDNESDAY, | THURSDAY, | FRIDAY, | SATURDAY, | SUNDAY,) (JANUARY | FEBRUARY | MARCH | APRIL | MAY | JUNE |JULY | AUGUST | SEPTEMBER | OCTOBER | NOVEMBER | DECEMBER ) ");//[0-9]{1,2}, [0-9]{4}$");
        Pattern date = Pattern.compile("(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY), \\w{1,10} \\d{1,2}, \\d{4}");
        Pattern calendarNo = Pattern.compile("(NO.) \\d+");
        Matcher m = date.matcher(text);
        //Matcher mCalendar = calendarNo.matcher(text);

        if (m.find()) {
            System.out.println(m.group());
            m.usePattern(calendarNo);
            if (m.find()){
                System.out.println(m.group());
            }
        }


    }
}