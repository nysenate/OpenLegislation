/*
 * Diff Match and Patch
 * Copyright 2018 The diff-match-patch Authors.
 * https://github.com/google/diff-match-patch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gov.nysenate.openleg.common.util.stringDiffer;

import java.util.*;

import static gov.nysenate.openleg.common.util.stringDiffer.DiffStringUtils.commonPrefixLength;
import static gov.nysenate.openleg.common.util.stringDiffer.DiffStringUtils.commonSuffixLength;
import static gov.nysenate.openleg.common.util.stringDiffer.DiffUtils.*;
import static gov.nysenate.openleg.common.util.stringDiffer.Operation.*;

/**
 * Contains methods to get and display the differences between two Strings.
 * Heavily modified from the original version.
 */
public final class StringDiffer {
    private StringDiffer() {}

    /**
     * Internal class for returning results from linesToChars().
     * Other less paranoid languages just use a three-element array.
     */
    record LinesToCharsResult(String chars1, String chars2, List<String> lineArray) {}

    public static List<Diff> getCleanedDiffs(String text1, String text2) {
        LinkedList<Diff> diffs = diffMain(text1, text2, true, 1.0f);
        cleanupEfficiency(diffs);
        cleanupSemantic(diffs);
        cleanupMerge(diffs);
        return diffs;
    }

    static List<Diff> diffMain(String text1, String text2, boolean checklines) {
        return diffMain(text1, text2, checklines, 1.0f);
    }

    /**
     * Find the differences between two texts.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff.
     * @param diffTimeout the amount of time, in seconds, the diff should take to complete.
     * @return Linked List of Diff objects.
     */
    static LinkedList<Diff> diffMain(String text1, String text2,
                               boolean checklines, float diffTimeout) {
        // Set a deadline by which time the diff must be complete.
        long deadline;
        if (diffTimeout <= 0) {
            deadline = Long.MAX_VALUE;
        } else {
            deadline = System.currentTimeMillis() + (long) (diffTimeout * 1000);
        }
        return internalDiffMain(text1, text2, checklines, deadline);
    }

    /**
     * Find the differences between two texts.  Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff.
     * @param deadline Time when the diff should be complete by.  Used
     *     internally for recursive calls.  Users should set DiffTimeout instead.
     * @return Linked List of Diff objects.
     */
    static LinkedList<Diff> internalDiffMain(String text1, String text2,
                                                     boolean checklines, long deadline) {
        // Check for null inputs.
        if (text1 == null || text2 == null) {
            throw new IllegalArgumentException("Null inputs. (internalDiffMain)");
        }

        // Check for equality (speedup).
        LinkedList<Diff> diffs;
        if (text1.equals(text2)) {
            diffs = new LinkedList<>();
            if (text1.length() != 0) {
                diffs.add(new Diff(EQUAL, text1));
            }
            return diffs;
        }

        // Trim off common prefix (speedup).
        int commonLength = commonPrefixLength(text1, text2);
        String commonPrefix = text1.substring(0, commonLength);
        text1 = text1.substring(commonLength);
        text2 = text2.substring(commonLength);

        // Trim off common suffix (speedup).
        commonLength = commonSuffixLength(text1, text2);
        String commonSuffix = text1.substring(text1.length() - commonLength);
        text1 = text1.substring(0, text1.length() - commonLength);
        text2 = text2.substring(0, text2.length() - commonLength);

        // Compute the diff on the middle block.
        diffs = diffCompute(text1, text2, checklines, deadline);

        // Restore the prefix and suffix.
        if (!commonPrefix.isEmpty()) {
            diffs.addFirst(new Diff(EQUAL, commonPrefix));
        }
        if (!commonSuffix.isEmpty()) {
            diffs.addLast(new Diff(EQUAL, commonSuffix));
        }
        cleanupMerge(diffs);
        return diffs;
    }

    /**
     * Find the differences between two texts.  Assumes that the texts do not
     * have any common prefix or suffix.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff.
     * @param deadline Time when the diff should be complete by.
     * @return Linked List of Diff objects.
     */
    private static LinkedList<Diff> diffCompute(String text1, String text2,
                                          boolean checklines, long deadline) {
        var diffs = new LinkedList<Diff>();
        if (text1.isEmpty()) {
            // Just add some text (speedup).
            diffs.add(new Diff(INSERT, text2));
            return diffs;
        }

        if (text2.isEmpty()) {
            // Just delete some text (speedup).
            diffs.add(new Diff(DELETE, text1));
            return diffs;
        }

        String longText = text1.length() > text2.length() ? text1 : text2;
        String shortText = text1.length() > text2.length() ? text2 : text1;
        int i = longText.indexOf(shortText);
        if (i != -1) {
            // Shorter text is inside the longer text (speedup).
            Operation op = text1.length() > text2.length() ? DELETE : INSERT;
            diffs.add(new Diff(op, longText.substring(0, i)));
            diffs.add(new Diff(EQUAL, shortText));
            diffs.add(new Diff(op, longText.substring(i + shortText.length())));
            return diffs;
        }

        if (shortText.length() == 1) {
            // After the previous speedup, the character can't be an equality.
            diffs.add(new Diff(DELETE, text1));
            diffs.add(new Diff(INSERT, text2));
            return diffs;
        }

        // Check to see if the problem can be split in two.
        String[] hm = halfMatch(text1, text2, deadline == Long.MAX_VALUE);
        if (hm != null) {
            // The pairs are processed separately, then merged.
            diffs = internalDiffMain(hm[0], hm[2], checklines, deadline);
            diffs.add(new Diff(EQUAL, hm[4]));
            diffs.addAll(internalDiffMain(hm[1], hm[3], checklines, deadline));
            return diffs;
        }

        if (checklines && text1.length() > 100 && text2.length() > 100) {
            return lineModeDiffs(text1, text2, deadline);
        }

        return new DiffBisect(text1, text2, deadline).mainLoop();
    }

    /**
     * Do a quick line-level diff on both strings, then rediff the parts for
     * greater accuracy.
     * This speedup can produce non-minimal diffs.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param deadline Time when the diff should be complete by.
     * @return Linked List of Diff objects.
     */
    private static LinkedList<Diff> lineModeDiffs(String text1, String text2, long deadline) {
        // Scan the text on a line-by-line basis first.
        LinesToCharsResult a = linesToChars(text1, text2);
        LinkedList<Diff> diffs = internalDiffMain(a.chars1, a.chars2, false, deadline);
        // Convert the diff back to original text.
        charsToLines(diffs, a.lineArray);
        // Eliminate freak matches (e.g. blank lines)
        cleanupSemantic(diffs);

        // Re-diff any replacement blocks, this time character-by-character.
        // Add a dummy entry at the end.
        diffs.add(new Diff(EQUAL, ""));
        int countDelete = 0;
        int countInsert = 0;
        var textDelete = new StringBuilder();
        var textInsert = new StringBuilder();
        ListIterator<Diff> pointer = diffs.listIterator();
        Diff thisDiff = pointer.next();
        while (thisDiff != null) {
            switch (thisDiff.operation) {
                case INSERT -> {
                    countInsert++;
                    textInsert.append(thisDiff.text);
                }
                case DELETE -> {
                    countDelete++;
                    textDelete.append(thisDiff.text);
                }
                case EQUAL -> {
                    // Upon reaching an equality, check for prior redundancies.
                    if (countDelete >= 1 && countInsert >= 1) {
                        // Delete the offending records and add the merged ones.
                        pointer.previous();
                        for (int j = 0; j < countDelete + countInsert; j++) {
                            pointer.previous();
                            pointer.remove();
                        }
                        for (Diff subDiff : internalDiffMain(textDelete.toString(), textInsert.toString(),
                                false, deadline)) {
                            pointer.add(subDiff);
                        }
                    }
                    countInsert = 0;
                    countDelete = 0;
                    textDelete = new StringBuilder();
                    textInsert = new StringBuilder();
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
        diffs.removeLast();  // Remove the dummy entry at the end.

        return diffs;
    }

    /**
     * Find the 'middle snake' of a diff, split the problem in two
     * and return the recursively constructed diff.
     * See Myers 1986 paper: An O(ND) Difference Algorithm and Its Variations.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param deadline Time at which to bail if not yet complete.
     * @return LinkedList of Diff objects.
     */
    static LinkedList<Diff> diffBisect(String text1, String text2, long deadline) {
        return new DiffBisect(text1, text2, deadline).mainLoop();
    }

    /**
     * Split two texts into a list of strings.  Reduce the texts to a string of
     * hashes where each Unicode character represents one line.
     * @param text1 First string.
     * @param text2 Second string.
     * @return An object containing the encoded text1, the encoded text2 and
     *     the List of unique strings.  The zeroth element of the List of
     *     unique strings is intentionally blank.
     */
    static LinesToCharsResult linesToChars(String text1, String text2) {
        var lineArray = new ArrayList<String>();
        var lineHash = new HashMap<String, Integer>();
        // e.g. lineArray[4] == "Hello\n"
        // e.g. lineHash.get("Hello\n") == 4

        // "\x00" is a valid character, but various debuggers don't like it.
        // So we'll insert a junk entry to avoid generating a null character.
        lineArray.add("");

        // Allocate 2/3rds of the space for text1, the rest for text2.
        String chars1 = linesToCharsMunge(text1, lineArray, lineHash, 40000);
        String chars2 = linesToCharsMunge(text2, lineArray, lineHash, 65535);
        return new LinesToCharsResult(chars1, chars2, lineArray);
    }

    /**
     * Split a text into a list of strings.  Reduce the texts to a string of
     * hashes where each Unicode character represents one line.
     * @param text String to encode.
     * @param lineArray List of unique strings.
     * @param lineHash Map of strings to indices.
     * @param maxLines Maximum length of lineArray.
     * @return Encoded string.
     */
    private static String linesToCharsMunge(String text, List<String> lineArray,
                                          Map<String, Integer> lineHash, int maxLines) {
        int lineStart = 0;
        int lineEnd = -1;
        String line;
        StringBuilder chars = new StringBuilder();
        // Walk the text, pulling out a substring for each line.
        // text.split('\n') would would temporarily double our memory footprint.
        // Modifying text would create many large strings to garbage collect.
        while (lineEnd < text.length() - 1) {
            lineEnd = text.indexOf('\n', lineStart);
            if (lineEnd == -1) {
                lineEnd = text.length() - 1;
            }
            line = text.substring(lineStart, lineEnd + 1);

            if (lineHash.containsKey(line)) {
                chars.append((char) (int) lineHash.get(line));
            } else {
                if (lineArray.size() == maxLines) {
                    // Bail out at 65535 because
                    // String.valueOf((char) 65536).equals(String.valueOf(((char) 0)))
                    line = text.substring(lineStart);
                    lineEnd = text.length();
                }
                lineArray.add(line);
                lineHash.put(line, lineArray.size() - 1);
                chars.append((char) (lineArray.size() - 1));
            }
            lineStart = lineEnd + 1;
        }
        return chars.toString();
    }

    /**
     * Rehydrate the text in a diff from a string of line hashes to real lines of
     * text.
     * @param diffs List of Diff objects.
     * @param lineArray List of unique strings.
     */
    static void charsToLines(List<Diff> diffs, List<String> lineArray) {
        StringBuilder text;
        for (Diff diff : diffs) {
            text = new StringBuilder();
            for (int j = 0; j < diff.text.length(); j++) {
                text.append(lineArray.get(diff.text.charAt(j)));
            }
            diff.text = text.toString();
        }
    }

    /**
     * Do the two texts share a substring which is at least half the length of
     * the longer text?
     * This speedup can produce non-minimal diffs.
     * @param text1 First string.
     * @param text2 Second string.
     * @return Five element String array, containing the prefix of text1, the
     *     suffix of text1, the prefix of text2, the suffix of text2 and the
     *     common middle.  Or null if there was no match.
     */
    static String[] halfMatch(String text1, String text2, boolean noDeadline) {
        if (noDeadline) {
            // Don't risk returning a non-optimal diff if we have unlimited time.
            return null;
        }
        String longText = text1.length() > text2.length() ? text1 : text2;
        String shortText = text1.length() > text2.length() ? text2 : text1;
        if (longText.length() < 4 || shortText.length() * 2 < longText.length()) {
            return null;  // Pointless.
        }

        // First check if the second quarter is the seed for a half-match.
        String[] hm1 = halfMatchI(longText, shortText,
                (longText.length() + 3) / 4);
        // Check again based on the third quarter.
        String[] hm2 = halfMatchI(longText, shortText,
                (longText.length() + 1) / 2);
        String[] hm;
        if (hm1 == null && hm2 == null) {
            return null;
        } else if (hm2 == null) {
            hm = hm1;
        } else if (hm1 == null) {
            hm = hm2;
        } else {
            // Both matched.  Select the longest.
            hm = hm1[4].length() > hm2[4].length() ? hm1 : hm2;
        }

        // A half-match was found, sort out the return data.
        if (text1.length() > text2.length()) {
            return hm;
        } else {
            return new String[]{hm[2], hm[3], hm[0], hm[1], hm[4]};
        }
    }

    /**
     * Does a substring of shortText exist within longText such that the
     * substring is at least half the length of longText?
     * @param longText Longer string.
     * @param shortText Shorter string.
     * @param i Start index of quarter length substring within longText.
     * @return Five element String array, containing the prefix of longText, the
     *     suffix of longText, the prefix of shortText, the suffix of shortText
     *     and the common middle.  Or null if there was no match.
     */
    private static String[] halfMatchI(String longText, String shortText, int i) {
        // Start with a 1/4 length substring at position i as a seed.
        String seed = longText.substring(i, i + longText.length() / 4);
        int j = -1;
        String bestCommon = "";
        String bestLongTextA = "", bestLongTextB = "";
        String bestShortTextA = "", bestShortTextB = "";
        while (true) {
            j = shortText.indexOf(seed, j + 1);
            if (j == -1) {
                break;
            }
            int prefixLength = commonPrefixLength(longText.substring(i),
                    shortText.substring(j));
            int suffixLength = commonSuffixLength(longText.substring(0, i),
                    shortText.substring(0, j));
            if (bestCommon.length() < suffixLength + prefixLength) {
                bestCommon = shortText.substring(j - suffixLength, j)
                        + shortText.substring(j, j + prefixLength);
                bestLongTextA = longText.substring(0, i - suffixLength);
                bestLongTextB = longText.substring(i + prefixLength);
                bestShortTextA = shortText.substring(0, j - suffixLength);
                bestShortTextB = shortText.substring(j + prefixLength);
            }
        }
        if (bestCommon.length() * 2 >= longText.length()) {
            return new String[]{bestLongTextA, bestLongTextB,
                    bestShortTextA, bestShortTextB, bestCommon};
        } else {
            return null;
        }
    }

    /**
     * Convert a Diff list into a pretty HTML report.
     * @param diffs List of Diff objects.
     * @return HTML representation.
     */
    public static String prettyHtml(List<Diff> diffs) {
        StringBuilder html = new StringBuilder();
        for (Diff aDiff : diffs) {
            String text = aDiff.text.replace("&", "&amp;").replace("<", "&lt;")
                    .replace(">", "&gt;").replace("\n", "&para;<br>");
            switch (aDiff.operation) {
                case INSERT -> html.append("<ins style=\"background:#e6ffe6;\">").append(text)
                        .append("</ins>");
                case DELETE -> html.append("<del style=\"background:#ffe6e6;\">").append(text)
                        .append("</del>");
                case EQUAL -> html.append("<span>").append(text).append("</span>");
            }
        }
        return html.toString();
    }
}
