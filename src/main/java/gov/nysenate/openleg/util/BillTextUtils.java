package gov.nysenate.openleg.util;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import gov.nysenate.openleg.model.entity.Chamber;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillTextUtils
{

    protected static Pattern billTextPageStartPattern =
        Pattern.compile("^(\\s+\\w.\\s\\d+(--\\w)?)?\\s{10,}(\\d+)(\\s{10,}(\\w.\\s\\d+(--\\w)?)?(\\d+-\\d+-\\d(--\\w)?)?)?$");

    protected static Integer MAX_LINES_RES_PAGE = 60;

    /**
     * Uses the new page lines to generate a list of pages from the bill text.
     *
     * @param fullText String - String - Bill full text
     * @return List<List<String>>
     */
    public static List<List<String>> getBillPages(String fullText) {
        List<List<String>> pages = new ArrayList<>();
        if (StringUtils.isEmpty(fullText)) {
            return pages;
        }
        List<String> lines = Splitter.on("\n").splitToList(fullText);
        int startLine = 0;
        for (int newPageLine : getNewPageLines(lines)) {
            pages.add(lines.subList(startLine, newPageLine));
            startLine = newPageLine;
        }
        pages.add(lines.subList(startLine, lines.size()));
        return pages;
    }

    /**
     * Returns the pages for resolution full text. Since resolutions don't have the same
     * formatting cues as bills, we just cap the pages to a certain number of lines.
     * @param fullText
     * @return
     */
    public static List<List<String>> getResolutionPages(String fullText) {
        List<List<String>> pages = new ArrayList<>();
        if (StringUtils.isEmpty(fullText)) {
            return pages;
        }
        List<String> lines = Splitter.on("\n").splitToList(fullText);
        int numPages = new Double(Math.ceil((double) lines.size() / MAX_LINES_RES_PAGE)).intValue();
        for (int page = 0; page < numPages; page++) {
            int pageStart = page * MAX_LINES_RES_PAGE;
            int pageEnd = Math.min(pageStart + MAX_LINES_RES_PAGE, lines.size());
            pages.add(lines.subList(pageStart, pageEnd));
        }
        return pages;
    }

    private static List<Integer> getNewPageLines(List<String> lines) {
        List<Integer> pageLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (isFirstLineOfNextPage(lines.get(i), i)) {
                pageLines.add(i);
            }
        }
        return pageLines;
    }

    /**
     * Returns the number of pages contained within the supplied bill text.
     *
     * @param fullText String - Bill full text
     * @return int
     */
    public static int getPageCount(String fullText) {
        // Short circuit
        if (Strings.isNullOrEmpty(fullText)) return 0;
        // Iterate through the lines in reverse order (until 10 to prevent errors)
        // looking for the last page number (e.g. A. 7461--A           2 ...)
        String[] lines = fullText.split("\n");
        for (int i = lines.length - 1; i > 10; i--) {
            Matcher billTextPageMatcher = billTextPageStartPattern.matcher(lines[i]);
            if (billTextPageMatcher.find()) {
                return Integer.parseInt(billTextPageMatcher.group(3));
            }
        }
        // Since there are no page indicators, just assume its a single page bill
        return 1;
    }

    /** WIP */
    public static String formatBillText(boolean isResolution, String fullText) {
        if (fullText == null) {
            fullText = "";
        }
        if (!isResolution && StringUtils.isNotBlank(fullText)) {
            List<String> lines = Splitter.on("\n").splitToList(fullText);
            StringBuilder formattedFullText = new StringBuilder();
            lines.forEach(line -> {
                if (line.length() > 7) {
                    formattedFullText.append(line.substring(7)).append("\n");
                }
                else {
                    formattedFullText.append(line).append("\n");
                }
            });
            return formattedFullText.toString();
        }
        return fullText;
    }

    /**
     * Checks if the given line matches the new page pattern.
     */
    public static boolean isFirstLineOfNextPage(String line, int lineNum) {
        Matcher billTextPageMatcher = billTextPageStartPattern.matcher(line);
        // Ignore erroneous result in first 10 lines.
        return lineNum > 10 && billTextPageMatcher.find();
    }


    /**
     *  Extracts plain bill text from html.
     *
     *  Substitutes text sections that are denoted by markup with plain text equivalents.
     *  Further text alteration is needed depending on the type of text
     *  @see #formatHtmlExtractedBillText(String)
     *  @see #formatHtmlExtractedResoText(String)
     */
    public static String parseHTMLtext(String htmlText)    {

        Document doc = Jsoup.parse(htmlText);
        if (doc.select("pre").size() == 0) {
            return htmlText;
        }
        Elements preTags = doc.select("pre");
        return parseHTMLText(preTags);
    }

    public static String parseHTMLText(Element element) {
        return parseHTMLText(new Elements(element));
    }

    public static String parseHTMLText(Collection<Element> elements) {
        StringBuilder textBuilder = new StringBuilder();

        elements.forEach(element -> processTextNode(element, textBuilder));

        String text = textBuilder.toString();
        // Remove some undesirable characters and blank lines with spaces
        text = text.replaceAll("[\r\\uFEFF-\\uFFFF]+|(?<=\n|^) +(?=\n|$)", "");
        return text;
    }

    private static final String inSenate = "IN SENATE";
    private static final String inAssembly = "IN ASSEMBLY";
    private static final String inBoth = "SENATE - ASSEMBLY";
    private static final Pattern billHeaderPattern = Pattern.compile("^(?<startingNewlines>\n*)" +
            "[ ]{3,}STATE OF NEW YORK\n" +
            "(?<divider>(?:[ \\w.\\-]*\n){0,8})" +
            "[ ]{3,}(?<chamber>" + inSenate + "|" + inAssembly + "|" + inBoth + ")" +
            "(?:(?<prefiledWhiteSpace>\\s+)\\(Prefiled\\))?"
    );

    /**
     * Reformat plain bill text that has been extracted from html
     *
     * @param text String
     * @return String
     */
    public static String formatHtmlExtractedBillText(String text) {
        // The html has an extra space at the beginning of each line
        text = text.replaceAll("(?<=\n|^) ", "");
        Matcher matcher = billHeaderPattern.matcher(text);
        if (matcher.find()) {
            StringBuilder replacement = new StringBuilder()
                    .append(matcher.group("startingNewlines"))
                    .append(StringUtils.repeat(' ', 27))
                    .append("S T A T E   O F   N E W   Y O R K\n")
                    .append(matcher.group("divider"));
            switch (matcher.group("chamber")) {
                case inSenate:
                    replacement.append(StringUtils.repeat(' ', 35))
                            .append("I N  S E N A T E");
                    break;
                case inAssembly:
                    replacement.append(StringUtils.repeat(' ', 33))
                            .append("I N  A S S E M B L Y");
                    break;
                case inBoth:
                    replacement.append(StringUtils.repeat(' ', 29))
                            .append("S E N A T E - A S S E M B L Y");
                    break;
                default:
                    throw new IllegalStateException("Unknown chamber value: " + matcher.group("chamber"));
            }
            if (matcher.group("prefiledWhiteSpace") != null) {
                replacement.append(matcher.group("prefiledWhiteSpace"))
                        .append("(PREFILED)");
            }
            text = matcher.replaceFirst(replacement.toString());
        }

        return text;
    }

    private static final Pattern resolutionHeaderPattern = Pattern.compile(
            "^\\s+(?<chamber>Senate|Assembly) *Resolution *No *\\. *(\\d+)\\s+" +
                    "BY:[\\w '.\\-:()]+\n" +
                    "(?:\\s+(?<verb>[A-Z]{2,}ING))?",
            Pattern.CASE_INSENSITIVE);

    /**
     * Reformat plain resolution text that has been extracted from html to resemble SOBI resolution text.
     *
     * @param text String
     * @return String
     */
    public static String formatHtmlExtractedResoText(String text) {
        Matcher headerMatcher = resolutionHeaderPattern.matcher(text);
        if (headerMatcher.find()) {
            Chamber chamber = Chamber.getValue(headerMatcher.group("chamber"));

            String replacement = "\n";
            String verb = headerMatcher.group("verb");
            if (verb != null) {
                replacement += String.format("%s RESOLUTION %s",
                        verb.equalsIgnoreCase("providing") ? chamber : "LEGISLATIVE",
                        verb.toLowerCase()
                );
            }

            text = headerMatcher.replaceFirst(replacement);
        }
        return text;
    }

    /**
     * Extracts bill/memo text from an element recursively
     */
    private static void processTextNode(Element element, StringBuilder stringBuilder) {
        processTextNode(element, stringBuilder, false);
    }

    /**
     * Extracts bill/memo text from an element recursively
     */
    private static void processTextNode(Element element, StringBuilder stringBuilder, boolean insideUTag) {
        // If this element is <U>, consider it within a u tag
        insideUTag = insideUTag || "u".equalsIgnoreCase(element.tag().getName());
        for (Node node : element.childNodes()) {
            if (node instanceof Element) {
                processTextNode((Element) node, stringBuilder, insideUTag);
            } else if (node instanceof TextNode) {
                String text = ((TextNode) node).getWholeText();
                if (insideUTag) {
                    // TEXT IN <U> TAGS IS REPRESENTED IN CAPS FOR SOBI AND XML BILL TEXT
                    text = StringUtils.upperCase(text);
                }
                stringBuilder.append(text);
            }
        }
    }

    /**
     * Converts a billtext HTML String to an HTML5 String. Retains the same formatting.
     *
     * @param rawHTML String
     * @return HTML5 String
     */
    public static String toHTML5(String rawHTML) {
        return convertToHTML5(rawHTML, true);
    }

    /**
     * Converts a billtext HTML String to an HTML5 String with custom tags surrounding changed text.
     *
     * @param rawHTML String
     * @return HTML5 String
     */
    public static String toHTML5WithTags(String rawHTML) {
        final ArrayList<TextDiff> changes = toChanges(rawHTML);
        String ans = "";
        for (int i = 0; i < changes.size(); ++i) {
            switch (changes.get(i).type) {
                case 0:
                    ans = ans + changes.get(i).html;
                    break;
                case 1:
                    ans = ans + "<span class=\"ol-text-change ol-text-added\">" + changes.get(i).html + "</span>";
                    break;
                case -1:
                    ans = ans + "<span class=\"ol-text-change ol-text-removed\">" + changes.get(i).html + "</span>";
                    break;
            }
        }
        ans = convertToHTML5(ans, false);
        return ans;
    }

    /**
     * TextDiff.type can be -1 (text removed), 0 (text unchanged), 1 (text added)
     * TextDiff.text is the plain text
     * TextDiff.html is the escaped html (contains HTML escape sequences)
     */
    public static class TextDiff {
        public int type;//0 = same, 1 = add, -1 = remove
        public String text;
        public String html;
        public TextDiff(final int type, final String text, final String html) {
            this.type = type;
            this.text = new String(text);
            this.html = new String(html);
        }

        @Override
        public String toString() {//meant for debug
            return type + ": " + text + "\n" + html;
        }
    }

    /**
     * Converts ArrayList&lt;TextDiff&gt; to the final version, plain text.
     *
     * @param changes
     * @return String
     */
    public static String changesToFinalText(final ArrayList<TextDiff> changes) {
        String ans = "";
        for (int i = 0; i < changes.size(); ++i) {
            if (changes.get(i).type >= 0) ans = ans + changes.get(i).text;
        }
        return ans;
    }

    /**
     * Accepts raw HTML string and converts it into a ArrayList&lt;TextDiff&gt;.
     *
     * @param rawHTML String
     * @return ArrayList&lt;TextDiff&gt;
     */
    public static ArrayList<TextDiff> toChanges(final String rawHTML) {
        ArrayList<TextDiff> ans = new ArrayList<TextDiff>();
        String unformatted = new String(rawHTML);
        if (unformatted.contains("</STYLE>")) unformatted = unformatted.substring(unformatted.indexOf("</STYLE>") + 8);
        //unformatted = unformatted.trim();
        int lastLength;
        while (unformatted.length() > 0) {
            lastLength = unformatted.length();
            int[] toAdd = addIndex(unformatted);
            int[] toRemove = removeIndex(unformatted);
            int action = -1;//1 = add, 0 = remove
            if (toAdd[0] == -1 && toRemove[0] == -1) {//no more text added or removed
                ans.add(new TextDiff(0, unformatted, unformatted));
                unformatted = "";
            }
            else {
                if (toAdd[0] != -1 && toRemove[0] != -1) {
                    if (toAdd[0] < toRemove[0]) action = 1;
                    else action = 0;
                }
                else if (toAdd[0] != -1) action = 1;
                else action = 0;
            }
            if (action == 1) {
                ans.add(new TextDiff(0, unformatted.substring(0, toAdd[0]), unformatted.substring(0, toAdd[0])));
                ans.add(new TextDiff(1, unformatted.substring(toAdd[1], toAdd[2]), unformatted.substring(toAdd[1], toAdd[2])));
                unformatted = unformatted.substring(toAdd[3]);
            }
            else if (action == 0) {
                ans.add(new TextDiff(0, unformatted.substring(0, toRemove[0]), unformatted.substring(0, toRemove[0])));
                ans.add(new TextDiff(-1, unformatted.substring(toRemove[1], toRemove[2]), unformatted.substring(toRemove[1], toRemove[2])));
                unformatted = unformatted.substring(toRemove[3]);
            }
            if (lastLength == unformatted.length()) {//unformatted remained the same 2 loop iterations in a row
                //throw new RuntimeException("Unexpected loop break");//for testing purposes only
                //expect to just append type 0 and continue on with execution instead of throwing exception
                ans.add(new TextDiff(0, unformatted, unformatted));
                unformatted = "";
            }
        }
        for (int i = 0; i < ans.size(); ++i) {
            ans.get(i).text = jsoupParsePreserveNewline(ans.get(i).text);
            if (ans.get(i).text.equals("") && ans.get(i).html.equals("")) {
                ans.remove(i--);
            }
        }
        return ans;
    }

    /**
     * Strips/Replaces legacy HTML formatting.
     *
     * @param html String
     * @param style boolean
     * @return String
     */
    private static String convertToHTML5(final String html, final boolean style) {
        String html5 = new String(html);
        html5 = html5.replace("<!--", "");
        html5 = html5.replace("-->", "");
        if (style) {
            html5 = html5.replaceAll("<FONT SIZE=5>", "<SPAN STYLE=\"font-size: 18px;\">");
            html5 = html5.replaceAll("</FONT>", "</SPAN>");
            html5 = html5.replaceAll("(?i)<PRE WIDTH=\"[0-9]+\">", "<PRE>");
            html5 = html5.replaceAll("(?i)<BASEFONT SIZE=[0-9]+>", "<STYLE> body {font-size: 12px;}</STYLE>");
        }
        else {
            html5 = html5.replaceAll("<FONT SIZE=5>", "<SPAN class=\"ol-text-large\">");
            html5 = html5.replaceAll("</FONT>", "</SPAN>");
            html5 = html5.replaceAll("(?i)<PRE WIDTH=\"[0-9]+\">", "<PRE>");
            html5 = html5.replaceAll("(?i)<BASEFONT SIZE=[0-9]+>", "<STYLE> body {font-size: 12px;}</STYLE>");
            html5 = html5.replaceAll("(?i)<STYLE>[\\s\\S]*</STYLE>", "");//removes style tags
        }
        return html5;
    }

    /**
     * Returns the minimum of a and b, indicating which of a or b was selected, with a default preference for b.
     * Special case if both a and b are -1.
     * Expects a and b to be >= -1.
     *
     * @param a int
     * @param b int
     * @return int[2], representing [minimum of a and b, which of a and b was selected]
     */
    private static int[] specialMin(final int a, final int b) {
        if (a == -1 && b == -1) return new int[]{-1, 0};
        else if (a == -1) return new int[]{b, 2};
        else if (b == -1) return new int[]{a, 1};
        else if (a < b) return new int[]{a, 1};
        else if (b < a) return new int[]{b, 2};
        else return new int[]{b, 2};
    }

    private static final String ADDTAGOPEN = "<B><U>";
    private static final String ADDTAGCLOSE = "</U></B>";
    /**
     * Detects HTML text marked as an addition.
     *
     * @param text HTML String
     * @return int[4], where int[0] is the index before the initial tags,
     * int[1] is the index after the initial tags,
     * int[2] is the index before the final tags,
     * and int[3] is the index after the final tags.
     */
    private static int[] addIndex(final String text) {
        final int startIndex = text.indexOf(ADDTAGOPEN);
        int endIndex = text.indexOf(ADDTAGCLOSE);
        int cpos = 0;
        while (endIndex <= startIndex && endIndex != -1 && startIndex != -1) {
            endIndex = text.indexOf(ADDTAGCLOSE, cpos);
            cpos = endIndex + 8;
        }
        if (startIndex == -1 || endIndex == -1) return new int[]{-1, -1, -1, -1};
        else return new int[]{startIndex, startIndex + ADDTAGOPEN.length(), endIndex, endIndex + ADDTAGCLOSE.length()};
    }

    private static final String REMOVETAGOPEN1 = "<B><S>";
    private static final String REMOVETAGOPEN2 = "[<B><S>";
    private static final String REMOVETAGCLOSE1 = "</S></B>";
    private static final String REMOVETAGCLOSE2 = "</S></B>]";
    /**
     * Detects HTML text marked for removal.
     *
     * @param text HTML String
     * @return int[4], where int[0] is the index before the initial tags,
     * int[1] is the index after the initial tags,
     * int[2] is the index before the final tags,
     * and int[3] is the index after the final tags.
     */
    private static int[] removeIndex(final String text) {
        final int[] start = specialMin(text.indexOf(REMOVETAGOPEN1), text.indexOf(REMOVETAGOPEN2));
        int[] end = specialMin(text.indexOf(REMOVETAGCLOSE1), text.indexOf(REMOVETAGCLOSE2));
        int cpos = 0;
        while (end[0] <= start[0] && end[0] != -1 && start[0] != -1) {
            end = specialMin(text.indexOf(REMOVETAGCLOSE1, cpos), text.indexOf(REMOVETAGCLOSE2, cpos));
            cpos = end[0] + (end[1] == 2 ? REMOVETAGCLOSE2.length() : REMOVETAGCLOSE1.length());
        }
        if (start[0] == -1 || end[0] == -1) return new int[]{-1, -1, -1, -1};
        else return new int[]{start[0],
                start[0] + (start[1] == 2 ? REMOVETAGOPEN2.length() : REMOVETAGOPEN1.length()),
                end[0],
                end[0] + (end[1] == 2 ? REMOVETAGCLOSE2.length() : REMOVETAGCLOSE1.length())};
    }

    /**
     * Converts html to plaintext while attempting to preserve all line breaks.
     *
     * @param html String
     * @return String
     */
    private static String jsoupParsePreserveNewline(String html) {
        if (html==null)
            return html;
        final String leadingNewlines = leadingWhitespace(html);
        html = html.substring(leadingNewlines.length());
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return leadingNewlines + Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    /**
     * Returns the leading whitespace characters of a String.
     *
     * @param x String
     * @return String
     */
    private static String leadingWhitespace(final String x) {
        for (int i = 0; i < x.length(); ++i) {
            if (x.charAt(i) != '\n' && x.charAt(i) != ' ') {
                return x.substring(0, i);
            }
        }
        return x;
    }

}
