package gov.nysenate.openleg.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatter {
    public static String append(Object... objects) {
        StringBuilder sb = new StringBuilder();
        for(Object o:objects) {
            sb.append(o);
        }
        return sb.toString();
    }

    public static String clean(String s) {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("'", "&apos;");
        s = s.replaceAll("<","&lt;");
        s = s.replaceAll(">","&gt;");
        s = s.replaceAll("\"","&quot;");

        return s;
    }

    public static String lrsPrinter(String fulltext) {
        StringTokenizer st = new StringTokenizer(fulltext, "\n");
        StringBuffer out = new StringBuffer("");

        boolean redact = false;
        int r_start = -1;
        int r_end = -1;
        boolean cap = false;
        int capCount = 0;
        int start = -1;
        int end = -1;

        ArrayList<TextPoint> points;

        int linenum = 0;
        while(st.hasMoreTokens()) {
            String line = st.nextToken();
            linenum++;

            Pattern pagePattern = Pattern.compile("(^\\s+\\w\\.\\s\\d+(--\\w)?\\s+\\d+(\\s+\\w\\.\\s\\d+(--\\w)?)?$|^\\s+\\d+\\s+\\d+\\-\\d+\\-\\d$|^\\s+\\d{1,4}$)");
            Matcher pageMatcher = pagePattern.matcher(line);

            Pattern linePattern = Pattern.compile("^\\s{3,4}\\d{1,2}\\s*");
            Matcher lineMatcher = linePattern.matcher(line);

            points = new ArrayList<TextPoint>();

            if(lineMatcher.find()) {
                String text = line.substring(lineMatcher.end());
                String lineNo = line.substring(lineMatcher.start(), lineMatcher.end());

                char[] textChar = text.toCharArray();

                for(int i = 0; i < textChar.length; i++) {
                    if(textChar[i] == '[') {
                        redact = true;
                        r_start = i+1;
                    }
                    else if(textChar[i] == ']') {
                        r_end = i;
                        points.add(new TextPoint(r_start,r_end,false));

                        r_start = -1;
                        r_end = -1;
                        redact = false;
                    }

                    if(Character.toString(textChar[i]).matches("\\s")) {

                    }
                    else {
                        if(Character.isUpperCase(textChar[i])) {
                            if(!cap) {
                                cap = true;
                                if(i < 6) {
                                    start = 0;
                                }
                                else {
                                    start = i;
                                }
                            }
                            capCount++;
                        }
                        else if(Character.isLowerCase(textChar[i])) {
                            if(cap) {
                                if(capCount > 2) {
                                    end = i - 1;
                                    points.add(new TextPoint(start,end,true));
                                }
                                start = -1;
                                end = -1;
                                capCount = 0;
                                cap = false;
                            }
                        }
                    }
                }

                if(redact) {
                    text += "</del>";

                    if(r_start != -1) {
                        text = text.substring(0,r_start) + "<del>" + text.substring(r_start);
                    }
                    else {
                        text = "<del>" + text;
                    }
                    r_start = -1;
                    r_end = -1;
                }

                Collections.reverse(points);
                for(TextPoint tp:points) {
                    if(tp.s == -1) {
                        tp.s = 0;
                    }

                    text = text.substring(0, tp.e) + (tp.uOrDel ? ""/*"</u>"*/ : "</del>") + text.substring(tp.e);
                    text = text.substring(0,tp.s) + (tp.uOrDel ? ""/*"<u>"*/ : "<del>") + text.substring(tp.s);


                }

                out.append(lineNo + text + "\n");

                start = -1;
                end = -1;
                cap = false;
                capCount = 0;
            }
            else {
                // We need to wait till we hit the 10th line to avoid breaking on the bill header
                if(pageMatcher.find() && linenum > 10) {
                    out.append("<div style=\"page-break-after:always\"></div>"+line + "\n");
                }
                else {
                    out.append(line + "\n");
                }
            }
        }
        return out.toString();
    }

    static class TextPoint {
        public int s;
        public int e;
        public boolean uOrDel;

        public TextPoint(int s, int e, boolean uOrDel) {
            this.s = s;
            this.e = e;
            this.uOrDel = uOrDel;
        }
    }
}
