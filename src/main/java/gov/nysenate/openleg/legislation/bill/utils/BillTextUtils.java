package gov.nysenate.openleg.legislation.bill.utils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import gov.nysenate.openleg.legislation.bill.BillAmendment;
import gov.nysenate.openleg.legislation.bill.BillTextFormat;
import gov.nysenate.openleg.legislation.committee.Chamber;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillTextUtils
{
    private static final Pattern BILL_TEXT_PAGE_START_PATTERN =
        Pattern.compile("^(\\s+\\w.\\s\\d+(--\\w)?)?\\s{10,}(\\d+)(\\s{10,}(\\w.\\s\\d+(--\\w)?)?(\\d+-\\d+-\\d(--\\w)?)?)?$");
    private static final Integer MAX_LINES_RES_PAGE = 60;

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
        int numPages = (int) Math.ceil((double) lines.size() / MAX_LINES_RES_PAGE);
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
            Matcher billTextPageMatcher = BILL_TEXT_PAGE_START_PATTERN.matcher(lines[i]);
            if (billTextPageMatcher.find()) {
                return Integer.parseInt(billTextPageMatcher.group(3));
            }
        }
        // Since there are no page indicators, just assume its a single page bill
        return 1;
    }

    /**
     * Return this amendment's plain text with line numbers stripped if they exist.
     *
     * Resolutions do not have line numbers.
     *
     * @param amendment
     */
    public static String getPlainTextWithoutLineNumbers(BillAmendment amendment) {
        String text = amendment.getFullText(BillTextFormat.PLAIN);
        if (!amendment.isResolution()) {
            List<String> lines = Splitter.on("\n").splitToList(text);
            StringBuilder textWithoutLineNums = new StringBuilder();
            lines.forEach(line -> {
                if (line.length() > 7) {
                    textWithoutLineNums.append(line.substring(7)).append("\n");
                }
                else {
                    textWithoutLineNums.append(line).append("\n");
                }
            });
            text = textWithoutLineNums.toString();
        }
        return text;
    }

    /**
     * Checks if the given line matches the new page pattern.
     */
    public static boolean isFirstLineOfNextPage(String line, int lineNum) {
        Matcher billTextPageMatcher = BILL_TEXT_PAGE_START_PATTERN.matcher(line);
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
    public static String convertHtmlToPlainText(String htmlText)    {

        Document doc = Jsoup.parse(htmlText);
        if (doc.select("pre").size() == 0) {
            return htmlText;
        }
        Elements preTags = doc.select("pre");
        return convertHtmlToPlainText(preTags);
    }

    public static String convertHtmlToPlainText(Element element) {
        return convertHtmlToPlainText(new Elements(element));
    }

    public static String convertHtmlToPlainText(Collection<Element> elements) {
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
    private static final Pattern billHeaderPattern = Pattern.compile("^(?<startingNewlines>\\s*)" +
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
        Matcher matcher = billHeaderPattern.matcher(text);
        if (matcher.find()) {
            StringBuilder replacement = new StringBuilder()
                    .append(matcher.group("startingNewlines"))
                    .append(StringUtils.repeat(' ', 15))
                    .append("S T A T E   O F   N E W   Y O R K\n")
                    .append(matcher.group("divider"));
            switch (matcher.group("chamber")) {
                case inSenate -> replacement.append(StringUtils.repeat(' ', 36))
                        .append("I N  S E N A T E");
                case inAssembly -> replacement.append(StringUtils.repeat(' ', 34))
                        .append("I N  A S S E M B L Y");
                case inBoth -> replacement.append(StringUtils.repeat(' ', 30))
                        .append("S E N A T E - A S S E M B L Y");
                default -> throw new IllegalStateException("Unknown chamber value: " + matcher.group("chamber"));
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
}
