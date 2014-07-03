package gov.nysenate.openleg.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TranscriptLine {

    private static final String invalidCharactersRegex = "[^a-zA-Z0-9]+";
    private static final int PAGE_NUM_INDEX = 10;
    private static final int PAGE_NUM_MAX = 27;
    private final String line;

    public TranscriptLine(String line) {
        this.line = line;
    }

    public String fullText() {
        return line;
    }

    /**
     * Transcript number is usually right aligned at the top of each page.
     * However, sometimes it's left aligned on the next line instead.
     * e.g. 082895.v1, 011299.v1
     * @return <code>true</code> if line contains the transcript number;
     *         <code>false</code> otherwise.
     */
    public boolean isTranscriptNumber() {
        String trim = line.replaceAll(invalidCharactersRegex,"").trim();
        if (!isNumber(trim)) {
            return false;
        }
        int startIndex = line.indexOf(trim);
        if (startIndex > PAGE_NUM_INDEX || Integer.valueOf(trim) > PAGE_NUM_MAX) {
            return true;
        }
        return false;
    }

    public boolean hasLineNumber() {
        // split on two spaces so time typo's don't get treated as line numbers.
        return isNumber(line.trim().split("  ")[0]);
    }

    public String removeLineNumber() {
        if (!isTranscriptNumber()) {
            if (hasLineNumber()) {
                if (line.trim().length() < 2)
                    return line.trim().substring(1);

                return line.trim().substring(2);
            }
        }
        return line;
    }

    public boolean isLocation() {
        if (line.contains("ALBANY") && line.contains("NEW") && line.contains("YORK"))
            return true;

        return false;
    }

    public boolean isDate() {
        String date = removeLineNumber().trim().replace(", ", " ").replace(",", " ");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public String getDateString() {
        return removeLineNumber().trim().replace(" , ", " ").replace(", ", " ").replace(",", " ").replace(".", "");
    }

    public boolean isTime() {
        String date = getTimeString();

        SimpleDateFormat sdf = new SimpleDateFormat("hhmma");
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public String getTimeString() {
        String date = removeLineNumber().trim().replace(":", "").replace(".", "").replace(" ", "");

        if (date.length() == 5)
            date = "0" + date;

        if (date.contains("Noon"))
            date = date.replace("Noon", "pm");

        return date;
    }

    public boolean isSession() {
        if (line.contains("SESSION"))
            return true;

        return false;
    }

    private boolean isNumber(String text) {
        try {
            Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isEmpty() {
        return line.replaceAll(invalidCharactersRegex,"").isEmpty();
    }

    public boolean isStenographer() {
        return line.contains("Candyco Transcription Service, Inc.") || line.contains("(518) 371-8910");
    }

    public String removeInvalidCharacters() {
        return fullText().replaceAll(invalidCharactersRegex, "");
    }
}
