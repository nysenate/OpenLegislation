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
import java.util.regex.Pattern;

import static gov.nysenate.openleg.common.util.stringDiffer.Operation.*;

/*
 * Functions for diff, match and patch.
 * Computes the difference between two texts to create a patch.
 * Applies the patch onto another text, allowing for errors.
 *
 * @author fraser@google.com (Neil Fraser)
 */

/**
 * This class has been trimmed down greatly by removing Patch methods, which we do not use.
 */
public class StringDiffer {
    /**
     * Number of seconds to map a diff before giving up (0 for infinity).
     */
    private static final float Diff_Timeout = 1.0f;
    /**
     * Cost of an empty edit operation in terms of edit characters.
     */
    private static final short Diff_EditCost = 4;

    /**
     * Internal class for returning results from diff_linesToChars().
     * Other less paranoid languages just use a three-element array.
     */
    private record LinesToCharsResult(String chars1, String chars2, List<String> lineArray) {}

    /**
     * Find the differences between two texts.
     * Run a faster, slightly less optimal diff.
     * This method allows the 'checklines' of diffMain() to be optional.
     * Most of the time checklines is wanted, so default to true.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @return Linked List of Diff objects.
     */
    public LinkedList<Diff> diffMain(String text1, String text2) {
        return diffMain(text1, text2, true);
    }

    /**
     * Find the differences between two texts.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff.
     * @return Linked List of Diff objects.
     */
    public LinkedList<Diff> diffMain(String text1, String text2,
                                     boolean checklines) {
        long deadline = System.currentTimeMillis() + (long) (Diff_Timeout * 1000);
        return diffMain(text1, text2, checklines, deadline);
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
    private LinkedList<Diff> diffMain(String text1, String text2,
                                      boolean checklines, long deadline) {
        // Check for null inputs.
        if (text1 == null || text2 == null) {
            throw new IllegalArgumentException("Null inputs. (diffMain)");
        }

        // Check for equality (speedup).
        LinkedList<Diff> diffs;
        if (text1.equals(text2)) {
            diffs = new LinkedList<>();
            if (!text1.isEmpty()) {
                diffs.add(new Diff(EQUAL, text1));
            }
            return diffs;
        }

        // Trim off common prefix (speedup).
        int commonLength = diff_commonPrefix(text1, text2);
        String commonPrefix = text1.substring(0, commonLength);
        text1 = text1.substring(commonLength);
        text2 = text2.substring(commonLength);

        // Trim off common suffix (speedup).
        commonLength = diff_commonSuffix(text1, text2);
        String commonSuffix = text1.substring(text1.length() - commonLength);
        text1 = text1.substring(0, text1.length() - commonLength);
        text2 = text2.substring(0, text2.length() - commonLength);

        // Compute the diff on the middle block.
        diffs = diff_compute(text1, text2, checklines, deadline);

        // Restore the prefix and suffix.
        if (!commonPrefix.isEmpty()) {
            diffs.addFirst(new Diff(EQUAL, commonPrefix));
        }
        if (!commonSuffix.isEmpty()) {
            diffs.addLast(new Diff(EQUAL, commonSuffix));
        }

        diff_cleanupMerge(diffs);
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
    private LinkedList<Diff> diff_compute(String text1, String text2,
                                          boolean checklines, long deadline) {
        LinkedList<Diff> diffs = new LinkedList<>();

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
            Operation op = (text1.length() > text2.length()) ? DELETE : INSERT;
            diffs.add(new Diff(op, longText.substring(0, i)));
            diffs.add(new Diff(EQUAL, shortText));
            diffs.add(new Diff(op, longText.substring(i + shortText.length())));
            return diffs;
        }

        if (shortText.length() == 1) {
            // Single character string.
            // After the previous speedup, the character can't be an equality.
            diffs.add(new Diff(DELETE, text1));
            diffs.add(new Diff(INSERT, text2));
            return diffs;
        }

        // Check to see if the problem can be split in two.
        String[] hm = diff_halfMatch(text1, text2);
        if (hm != null) {
            // A half-match was found, sort out the return data.
            String text1_a = hm[0];
            String text1_b = hm[1];
            String text2_a = hm[2];
            String text2_b = hm[3];
            String mid_common = hm[4];
            // Send both pairs off for separate processing.
            LinkedList<Diff> diffs_a = diffMain(text1_a, text2_a,
                    checklines, deadline);
            LinkedList<Diff> diffs_b = diffMain(text1_b, text2_b,
                    checklines, deadline);
            // Merge the results.
            diffs = diffs_a;
            diffs.add(new Diff(Operation.EQUAL, mid_common));
            diffs.addAll(diffs_b);
            return diffs;
        }

        if (checklines && text1.length() > 100 && text2.length() > 100) {
            return diff_lineMode(text1, text2, deadline);
        }

        return diff_bisect(text1, text2, deadline);
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
    private LinkedList<Diff> diff_lineMode(String text1, String text2,
                                           long deadline) {
        // Scan the text on a line-by-line basis first.
        LinesToCharsResult a = diff_linesToChars(text1, text2);
        text1 = a.chars1;
        text2 = a.chars2;
        List<String> lineArray = a.lineArray;

        LinkedList<Diff> diffs = diffMain(text1, text2, false, deadline);

        // Convert the diff back to original text.
        diff_charsToLines(diffs, lineArray);
        // Eliminate freak matches (e.g. blank lines)
        diff_cleanupSemantic(diffs);

        // Rediff any replacement blocks, this time character-by-character.
        // Add a dummy entry at the end.
        diffs.add(new Diff(Operation.EQUAL, ""));
        int count_delete = 0;
        int count_insert = 0;
        StringBuilder text_delete = new StringBuilder();
        StringBuilder text_insert = new StringBuilder();
        ListIterator<Diff> pointer = diffs.listIterator();
        while (pointer.hasNext()) {
            Diff thisDiff = pointer.next();
            switch (thisDiff.operation) {
                case INSERT -> {
                    count_insert++;
                    text_insert.append(thisDiff.text);
                }
                case DELETE -> {
                    count_delete++;
                    text_delete.append(thisDiff.text);
                }
                case EQUAL -> {
                    // Upon reaching an equality, check for prior redundancies.
                    if (count_delete >= 1 && count_insert >= 1) {
                        // Delete the offending records and add the merged ones.
                        pointer.previous();
                        for (int j = 0; j < count_delete + count_insert; j++) {
                            pointer.previous();
                            pointer.remove();
                        }
                        for (Diff subDiff : diffMain(text_delete.toString(), text_insert.toString(),
                                false, deadline)) {
                            pointer.add(subDiff);
                        }
                    }
                    count_insert = 0;
                    count_delete = 0;
                    text_delete = new StringBuilder();
                    text_insert = new StringBuilder();
                }
            }
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
    protected LinkedList<Diff> diff_bisect(String text1, String text2, long deadline) {
        // Cache the text lengths to prevent multiple calls.
        final int text1_length = text1.length();
        final int text2_length = text2.length();
        final int max_d = (text1_length + text2_length + 1) / 2;
        final int v_offset = max_d;
        final int v_length = 2 * max_d;
        final int[] v1 = new int[v_length];
        final int[] v2 = new int[v_length];
        Arrays.fill(v1, -1);
        Arrays.fill(v2, -1);
        v1[v_offset + 1] = 0;
        v2[v_offset + 1] = 0;
        final int delta = text1_length - text2_length;
        // If the total number of characters is odd, then the front path will
        // collide with the reverse path.
        final boolean front = (delta % 2 != 0);
        // Offsets for start and end of k loop.
        // Prevents mapping of space beyond the grid.
        int k1start = 0;
        int k1end = 0;
        int k2start = 0;
        int k2end = 0;
        for (int d = 0; d < max_d; d++) {
            // Bail out if deadline is reached.
            if (System.currentTimeMillis() > deadline) {
                break;
            }

            // Walk the front path one step.
            for (int k1 = -d + k1start; k1 <= d - k1end; k1 += 2) {
                int k1_offset = v_offset + k1;
                int x1;
                if (k1 == -d || (k1 != d && v1[k1_offset - 1] < v1[k1_offset + 1])) {
                    x1 = v1[k1_offset + 1];
                } else {
                    x1 = v1[k1_offset - 1] + 1;
                }
                int y1 = x1 - k1;
                while (x1 < text1_length && y1 < text2_length
                        && text1.charAt(x1) == text2.charAt(y1)) {
                    x1++;
                    y1++;
                }
                v1[k1_offset] = x1;
                if (x1 > text1_length) {
                    // Ran off the right of the graph.
                    k1end += 2;
                } else if (y1 > text2_length) {
                    // Ran off the bottom of the graph.
                    k1start += 2;
                } else if (front) {
                    int k2_offset = v_offset + delta - k1;
                    if (k2_offset >= 0 && k2_offset < v_length && v2[k2_offset] != -1) {
                        // Mirror x2 onto top-left coordinate system.
                        int x2 = text1_length - v2[k2_offset];
                        if (x1 >= x2) {
                            // Overlap detected.
                            return diff_bisectSplit(text1, text2, x1, y1, deadline);
                        }
                    }
                }
            }

            // Walk the reverse path one step.
            for (int k2 = -d + k2start; k2 <= d - k2end; k2 += 2) {
                int k2_offset = v_offset + k2;
                int x2;
                if (k2 == -d || (k2 != d && v2[k2_offset - 1] < v2[k2_offset + 1])) {
                    x2 = v2[k2_offset + 1];
                } else {
                    x2 = v2[k2_offset - 1] + 1;
                }
                int y2 = x2 - k2;
                while (x2 < text1_length && y2 < text2_length
                        && text1.charAt(text1_length - x2 - 1)
                        == text2.charAt(text2_length - y2 - 1)) {
                    x2++;
                    y2++;
                }
                v2[k2_offset] = x2;
                if (x2 > text1_length) {
                    // Ran off the left of the graph.
                    k2end += 2;
                } else if (y2 > text2_length) {
                    // Ran off the top of the graph.
                    k2start += 2;
                } else if (!front) {
                    int k1_offset = v_offset + delta - k2;
                    if (k1_offset >= 0 && k1_offset < v_length && v1[k1_offset] != -1) {
                        int x1 = v1[k1_offset];
                        int y1 = v_offset + x1 - k1_offset;
                        // Mirror x2 onto top-left coordinate system.
                        x2 = text1_length - x2;
                        if (x1 >= x2) {
                            // Overlap detected.
                            return diff_bisectSplit(text1, text2, x1, y1, deadline);
                        }
                    }
                }
            }
        }
        // Diff took too long and hit the deadline or
        // number of diffs equals number of characters, no commonality at all.
        return new LinkedList<>(List.of(new Diff(DELETE, text1), new Diff(INSERT, text2)));
    }

    /**
     * Given the location of the 'middle snake', split the diff in two parts
     * and recurse.
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param x Index of split point in text1.
     * @param y Index of split point in text2.
     * @param deadline Time at which to bail if not yet complete.
     * @return LinkedList of Diff objects.
     */
    private LinkedList<Diff> diff_bisectSplit(String text1, String text2,
                                              int x, int y, long deadline) {
        String text1a = text1.substring(0, x);
        String text2a = text2.substring(0, y);
        String text1b = text1.substring(x);
        String text2b = text2.substring(y);

        // Compute both diffs serially.
        LinkedList<Diff> diffsa = diffMain(text1a, text2a, false, deadline);
        LinkedList<Diff> diffsb = diffMain(text1b, text2b, false, deadline);

        diffsa.addAll(diffsb);
        return diffsa;
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
    protected LinesToCharsResult diff_linesToChars(String text1, String text2) {
        List<String> lineArray = new ArrayList<>();
        Map<String, Integer> lineHash = new HashMap<>();
        // e.g. lineArray[4] == "Hello\n"
        // e.g. lineHash.get("Hello\n") == 4

        // "\x00" is a valid character, but various debuggers don't like it.
        // So we'll insert a junk entry to avoid generating a null character.
        lineArray.add("");

        // Allocate 2/3rds of the space for text1, the rest for text2.
        String chars1 = diff_linesToCharsMunge(text1, lineArray, lineHash, 40000);
        String chars2 = diff_linesToCharsMunge(text2, lineArray, lineHash, 65535);
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
    private String diff_linesToCharsMunge(String text, List<String> lineArray,
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
    private static void diff_charsToLines(List<Diff> diffs,
                                     List<String> lineArray) {
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
     * Determine the common prefix of two strings
     * @param text1 First string.
     * @param text2 Second string.
     * @return The number of characters common to the start of each string.
     */
    private static int diff_commonPrefix(String text1, String text2) {
        // Performance analysis: https://neil.fraser.name/news/2007/10/09/
        int n = Math.min(text1.length(), text2.length());
        for (int i = 0; i < n; i++) {
            if (text1.charAt(i) != text2.charAt(i)) {
                return i;
            }
        }
        return n;
    }

    /**
     * Determine the common suffix of two strings
     * @param text1 First string.
     * @param text2 Second string.
     * @return The number of characters common to the end of each string.
     */
    private static int diff_commonSuffix(String text1, String text2) {
        // Performance analysis: https://neil.fraser.name/news/2007/10/09/
        int text1_length = text1.length();
        int text2_length = text2.length();
        int n = Math.min(text1_length, text2_length);
        for (int i = 1; i <= n; i++) {
            if (text1.charAt(text1_length - i) != text2.charAt(text2_length - i)) {
                return i - 1;
            }
        }
        return n;
    }

    /**
     * Determine if the suffix of one string is the prefix of another.
     * @param text1 First string.
     * @param text2 Second string.
     * @return The number of characters common to the end of the first
     *     string and the start of the second string.
     */
    protected int diff_commonOverlap(String text1, String text2) {
        // Cache the text lengths to prevent multiple calls.
        int text1_length = text1.length();
        int text2_length = text2.length();
        // Eliminate the null case.
        if (text1_length == 0 || text2_length == 0) {
            return 0;
        }
        // Truncate the longer string.
        if (text1_length > text2_length) {
            text1 = text1.substring(text1_length - text2_length);
        } else if (text1_length < text2_length) {
            text2 = text2.substring(0, text1_length);
        }
        int text_length = Math.min(text1_length, text2_length);
        // Quick check for the worst case.
        if (text1.equals(text2)) {
            return text_length;
        }

        // Start by looking for a single character match
        // and increase length until no match is found.
        // Performance analysis: https://neil.fraser.name/news/2010/11/04/
        int best = 0;
        int length = 1;
        while (true) {
            String pattern = text1.substring(text_length - length);
            int found = text2.indexOf(pattern);
            if (found == -1) {
                return best;
            }
            length += found;
            if (found == 0 || text1.substring(text_length - length).equals(
                    text2.substring(0, length))) {
                best = length;
                length++;
            }
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
    protected String[] diff_halfMatch(String text1, String text2) {
        String longText = text1.length() > text2.length() ? text1 : text2;
        String shortText = text1.length() > text2.length() ? text2 : text1;
        if (longText.length() < 4 || shortText.length() * 2 < longText.length()) {
            return null;  // Pointless.
        }

        // First check if the second quarter is the seed for a half-match.
        String[] hm1 = diff_halfMatchI(longText, shortText,
                (longText.length() + 3) / 4);
        // Check again based on the third quarter.
        String[] hm2 = diff_halfMatchI(longText, shortText,
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
            //return new String[]{hm[0], hm[1], hm[2], hm[3], hm[4]};
        } else {
            return new String[]{hm[2], hm[3], hm[0], hm[1], hm[4]};
        }
    }

    /**
     * Does a substring of shorttext exist within longtext such that the
     * substring is at least half the length of longtext?
     * @param longtext Longer string.
     * @param shorttext Shorter string.
     * @param i Start index of quarter length substring within longtext.
     * @return Five element String array, containing the prefix of longtext, the
     *     suffix of longtext, the prefix of shorttext, the suffix of shorttext
     *     and the common middle.  Or null if there was no match.
     */
    private String[] diff_halfMatchI(String longtext, String shorttext, int i) {
        // Start with a 1/4 length substring at position i as a seed.
        String seed = longtext.substring(i, i + longtext.length() / 4);
        int j = -1;
        String best_common = "";
        String bestLongTextA = "", bestLongtextB = "";
        String bestShortTextA = "", bestShortTextB = "";
        while ((j = shorttext.indexOf(seed, j + 1)) != -1) {
            int prefixLength = diff_commonPrefix(longtext.substring(i),
                    shorttext.substring(j));
            int suffixLength = diff_commonSuffix(longtext.substring(0, i),
                    shorttext.substring(0, j));
            if (best_common.length() < suffixLength + prefixLength) {
                best_common = shorttext.substring(j - suffixLength, j)
                        + shorttext.substring(j, j + prefixLength);
                bestLongTextA = longtext.substring(0, i - suffixLength);
                bestLongtextB = longtext.substring(i + prefixLength);
                bestShortTextA = shorttext.substring(0, j - suffixLength);
                bestShortTextB = shorttext.substring(j + prefixLength);
            }
        }
        if (best_common.length() * 2 >= longtext.length()) {
            return new String[]{bestLongTextA, bestLongtextB,
                    bestShortTextA, bestShortTextB, best_common};
        } else {
            return null;
        }
    }

    /**
     * Reduce the number of edits by eliminating semantically trivial equalities.
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupSemantic(LinkedList<Diff> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        Deque<Diff> equalities = new ArrayDeque<>();  // Double-ended queue of qualities.
        String lastEquality = null; // Always equal to equalities.peek().text
        ListIterator<Diff> pointer = diffs.listIterator();
        // Number of characters that changed prior to the equality.
        int length_insertions1 = 0;
        int length_deletions1 = 0;
        // Number of characters that changed after the equality.
        int length_insertions2 = 0;
        int length_deletions2 = 0;
        Diff thisDiff = pointer.next();
        while (thisDiff != null) {
            if (thisDiff.operation == Operation.EQUAL) {
                // Equality found.
                equalities.push(thisDiff);
                length_insertions1 = length_insertions2;
                length_deletions1 = length_deletions2;
                length_insertions2 = 0;
                length_deletions2 = 0;
                lastEquality = thisDiff.text;
            } else {
                // An insertion or deletion.
                if (thisDiff.operation == Operation.INSERT) {
                    length_insertions2 += thisDiff.text.length();
                } else {
                    length_deletions2 += thisDiff.text.length();
                }
                // Eliminate an equality that is smaller or equal to the edits on both
                // sides of it.
                if (lastEquality != null && (lastEquality.length()
                        <= Math.max(length_insertions1, length_deletions1))
                        && (lastEquality.length()
                        <= Math.max(length_insertions2, length_deletions2))) {
                    //System.out.println("Splitting: '" + lastEquality + "'");
                    // Walk back to offending equality.
                    while (thisDiff != equalities.peek()) {
                        thisDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Diff(Operation.DELETE, lastEquality));
                    // Insert a corresponding an insert.
                    pointer.add(new Diff(Operation.INSERT, lastEquality));

                    equalities.pop();  // Throw away the equality we just deleted.
                    if (!equalities.isEmpty()) {
                        // Throw away the previous equality (it needs to be reevaluated).
                        equalities.pop();
                    }
                    if (equalities.isEmpty()) {
                        // There are no previous equalities, walk back to the start.
                        while (pointer.hasPrevious()) {
                            pointer.previous();
                        }
                    } else {
                        // There is a safe equality we can fall back to.
                        thisDiff = equalities.peek();
                        while (thisDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }
                    }

                    length_insertions1 = 0;  // Reset the counters.
                    length_insertions2 = 0;
                    length_deletions1 = 0;
                    length_deletions2 = 0;
                    lastEquality = null;
                    changes = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        // Normalize the diff.
        if (changes) {
            diff_cleanupMerge(diffs);
        }
        diff_cleanupSemanticLossless(diffs);

        // Find any overlaps between deletions and insertions.
        // e.g: <del>abcxxx</del><ins>xxxdef</ins>
        //   -> <del>abc</del>xxx<ins>def</ins>
        // e.g: <del>xxxabc</del><ins>defxxx</ins>
        //   -> <ins>def</ins>xxx<del>abc</del>
        // Only extract an overlap if it is as big as the edit ahead or behind it.
        pointer = diffs.listIterator();
        Diff prevDiff = null;
        thisDiff = null;
        if (pointer.hasNext()) {
            prevDiff = pointer.next();
            if (pointer.hasNext()) {
                thisDiff = pointer.next();
            }
        }
        while (thisDiff != null) {
            if (prevDiff.operation == Operation.DELETE &&
                    thisDiff.operation == Operation.INSERT) {
                String deletion = prevDiff.text;
                String insertion = thisDiff.text;
                int overlap_length1 = this.diff_commonOverlap(deletion, insertion);
                int overlap_length2 = this.diff_commonOverlap(insertion, deletion);
                if (overlap_length1 >= overlap_length2) {
                    if (overlap_length1 >= deletion.length() / 2.0 ||
                            overlap_length1 >= insertion.length() / 2.0) {
                        // Overlap found. Insert an equality and trim the surrounding edits.
                        pointer.previous();
                        pointer.add(new Diff(Operation.EQUAL,
                                insertion.substring(0, overlap_length1)));
                        prevDiff.text =
                                deletion.substring(0, deletion.length() - overlap_length1);
                        thisDiff.text = insertion.substring(overlap_length1);
                        // pointer.add inserts the element before the cursor, so there is
                        // no need to step past the new element.
                    }
                } else {
                    if (overlap_length2 >= deletion.length() / 2.0 ||
                            overlap_length2 >= insertion.length() / 2.0) {
                        // Reverse overlap found.
                        // Insert an equality and swap and trim the surrounding edits.
                        pointer.previous();
                        pointer.add(new Diff(Operation.EQUAL,
                                deletion.substring(0, overlap_length2)));
                        prevDiff.operation = Operation.INSERT;
                        prevDiff.text =
                                insertion.substring(0, insertion.length() - overlap_length2);
                        thisDiff.operation = Operation.DELETE;
                        thisDiff.text = deletion.substring(overlap_length2);
                        // pointer.add inserts the element before the cursor, so there is
                        // no need to step past the new element.
                    }
                }
                thisDiff = pointer.hasNext() ? pointer.next() : null;
            }
            prevDiff = thisDiff;
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
    }

    /**
     * Look for single edits surrounded on both sides by equalities
     * which can be shifted sideways to align the edit to a word boundary.
     * e.g: The c<ins>at c</ins>ame. -> The <ins>cat </ins>came.
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupSemanticLossless(LinkedList<Diff> diffs) {
        String equality1, edit, equality2;
        String commonString;
        int commonOffset;
        int score, bestScore;
        String bestEquality1, bestEdit, bestEquality2;
        // Create a new iterator at the start.
        ListIterator<Diff> pointer = diffs.listIterator();
        Diff prevDiff = pointer.hasNext() ? pointer.next() : null;
        Diff thisDiff = pointer.hasNext() ? pointer.next() : null;
        Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
        // Intentionally ignore the first and last element (don't need checking).
        while (nextDiff != null) {
            if (prevDiff.operation == Operation.EQUAL &&
                    nextDiff.operation == Operation.EQUAL) {
                // This is a single edit surrounded by equalities.
                equality1 = prevDiff.text;
                edit = thisDiff.text;
                equality2 = nextDiff.text;

                // First, shift the edit as far left as possible.
                commonOffset = diff_commonSuffix(equality1, edit);
                if (commonOffset != 0) {
                    commonString = edit.substring(edit.length() - commonOffset);
                    equality1 = equality1.substring(0, equality1.length() - commonOffset);
                    edit = commonString + edit.substring(0, edit.length() - commonOffset);
                    equality2 = commonString + equality2;
                }

                // Second, step character by character right, looking for the best fit.
                bestEquality1 = equality1;
                bestEdit = edit;
                bestEquality2 = equality2;
                bestScore = diff_cleanupSemanticScore(equality1, edit)
                        + diff_cleanupSemanticScore(edit, equality2);
                while (edit.length() != 0 && equality2.length() != 0
                        && edit.charAt(0) == equality2.charAt(0)) {
                    equality1 += edit.charAt(0);
                    edit = edit.substring(1) + equality2.charAt(0);
                    equality2 = equality2.substring(1);
                    score = diff_cleanupSemanticScore(equality1, edit)
                            + diff_cleanupSemanticScore(edit, equality2);
                    // The >= encourages trailing rather than leading whitespace on edits.
                    if (score >= bestScore) {
                        bestScore = score;
                        bestEquality1 = equality1;
                        bestEdit = edit;
                        bestEquality2 = equality2;
                    }
                }

                if (!prevDiff.text.equals(bestEquality1)) {
                    // We have an improvement, save it back to the diff.
                    if (bestEquality1.length() != 0) {
                        prevDiff.text = bestEquality1;
                    } else {
                        pointer.previous(); // Walk past nextDiff.
                        pointer.previous(); // Walk past thisDiff.
                        pointer.previous(); // Walk past prevDiff.
                        pointer.remove(); // Delete prevDiff.
                        pointer.next(); // Walk past thisDiff.
                        pointer.next(); // Walk past nextDiff.
                    }
                    thisDiff.text = bestEdit;
                    if (bestEquality2.length() != 0) {
                        nextDiff.text = bestEquality2;
                    } else {
                        pointer.remove(); // Delete nextDiff.
                        nextDiff = thisDiff;
                        thisDiff = prevDiff;
                    }
                }
            }
            prevDiff = thisDiff;
            thisDiff = nextDiff;
            nextDiff = pointer.hasNext() ? pointer.next() : null;
        }
    }

    /**
     * Given two strings, compute a score representing whether the internal
     * boundary falls on logical boundaries.
     * Scores range from 6 (best) to 0 (worst).
     * @param one First string.
     * @param two Second string.
     * @return The score.
     */
    private int diff_cleanupSemanticScore(String one, String two) {
        if (one.length() == 0 || two.length() == 0) {
            // Edges are the best.
            return 6;
        }

        // Each port of this function behaves slightly differently due to
        // subtle differences in each language's definition of things like
        // 'whitespace'.  Since this function's purpose is largely cosmetic,
        // the choice has been made to use each language's native features
        // rather than force total conformity.
        char char1 = one.charAt(one.length() - 1);
        char char2 = two.charAt(0);
        boolean nonAlphaNumeric1 = !Character.isLetterOrDigit(char1);
        boolean nonAlphaNumeric2 = !Character.isLetterOrDigit(char2);
        boolean whitespace1 = nonAlphaNumeric1 && Character.isWhitespace(char1);
        boolean whitespace2 = nonAlphaNumeric2 && Character.isWhitespace(char2);
        boolean lineBreak1 = whitespace1
                && Character.getType(char1) == Character.CONTROL;
        boolean lineBreak2 = whitespace2
                && Character.getType(char2) == Character.CONTROL;
        boolean blankLine1 = lineBreak1 && BLANKLINEEND.matcher(one).find();
        boolean blankLine2 = lineBreak2 && BLANKLINESTART.matcher(two).find();

        if (blankLine1 || blankLine2) {
            return 5;
        } else if (lineBreak1 || lineBreak2) {
            return 4;
        } else if (nonAlphaNumeric1 && !whitespace1 && whitespace2) {
            return 3;
        } else if (whitespace1 || whitespace2) {
            return 2;
        } else if (nonAlphaNumeric1 || nonAlphaNumeric2) {
            return 1;
        }
        return 0;
    }

    // Define some regex patterns for matching boundaries.
    private static final Pattern BLANKLINEEND
            = Pattern.compile("\\n\\r?\\n\\Z", Pattern.DOTALL);
    private static final Pattern BLANKLINESTART
            = Pattern.compile("\\A\\r?\\n\\r?\\n", Pattern.DOTALL);

    /**
     * Reduce the number of edits by eliminating operationally trivial equalities.
     * @param diffs LinkedList of Diff objects.
     */
    public void diff_cleanupEfficiency(LinkedList<Diff> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        Deque<Diff> equalities = new ArrayDeque<>();  // Double-ended queue of equalities.
        String lastEquality = null; // Always equal to equalities.peek().text
        ListIterator<Diff> pointer = diffs.listIterator();
        // Is there an insertion operation before the last equality.
        boolean pre_ins = false;
        // Is there a deletion operation before the last equality.
        boolean pre_del = false;
        // Is there an insertion operation after the last equality.
        boolean post_ins = false;
        // Is there a deletion operation after the last equality.
        boolean post_del = false;
        Diff thisDiff = pointer.next();
        Diff safeDiff = thisDiff;  // The last Diff that is known to be unsplittable.
        while (thisDiff != null) {
            if (thisDiff.operation == Operation.EQUAL) {
                // Equality found.
                if (thisDiff.text.length() < Diff_EditCost && (post_ins || post_del)) {
                    // Candidate found.
                    equalities.push(thisDiff);
                    pre_ins = post_ins;
                    pre_del = post_del;
                    lastEquality = thisDiff.text;
                } else {
                    // Not a candidate, and can never become one.
                    equalities.clear();
                    lastEquality = null;
                    safeDiff = thisDiff;
                }
                post_ins = false;
                post_del = false;
            } else {
                // An insertion or deletion.
                if (thisDiff.operation == Operation.DELETE) {
                    post_del = true;
                } else {
                    post_ins = true;
                }
                /*
                 * Five types to be split:
                 * <ins>A</ins><del>B</del>XY<ins>C</ins><del>D</del>
                 * <ins>A</ins>X<ins>C</ins><del>D</del>
                 * <ins>A</ins><del>B</del>X<ins>C</ins>
                 * <ins>A</del>X<ins>C</ins><del>D</del>
                 * <ins>A</ins><del>B</del>X<del>C</del>
                 */
                if (lastEquality != null
                        && ((pre_ins && pre_del && post_ins && post_del)
                        || ((lastEquality.length() < Diff_EditCost / 2)
                        && ((pre_ins ? 1 : 0) + (pre_del ? 1 : 0)
                        + (post_ins ? 1 : 0) + (post_del ? 1 : 0)) == 3))) {
                    //System.out.println("Splitting: '" + lastEquality + "'");
                    // Walk back to offending equality.
                    while (thisDiff != equalities.peek()) {
                        thisDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with delete.
                    pointer.set(new Diff(DELETE, lastEquality));
                    // Insert a corresponding an insert.
                    thisDiff = new Diff(INSERT, lastEquality);
                    pointer.add(thisDiff);

                    equalities.pop();  // Throw away the equality we just deleted.
                    lastEquality = null;
                    if (pre_ins && pre_del) {
                        // No changes made which could affect previous entry, keep going.
                        post_ins = true;
                        post_del = true;
                        equalities.clear();
                        safeDiff = thisDiff;
                    } else {
                        if (!equalities.isEmpty()) {
                            // Throw away the previous equality (it needs to be reevaluated).
                            equalities.pop();
                        }
                        if (equalities.isEmpty()) {
                            // There are no previous questionable equalities,
                            // walk back to the last known safe diff.
                            thisDiff = safeDiff;
                        } else {
                            // There is an equality we can fall back to.
                            thisDiff = equalities.peek();
                        }
                        while (thisDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }
                        post_ins = false;
                        post_del = false;
                    }
                    changes = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        if (changes) {
            diff_cleanupMerge(diffs);
        }
    }

    /**
     * Reorder and merge like edit sections.  Merge equalities.
     * Any edit section can move as long as it doesn't cross an equality.
     * @param diffs LinkedList of Diff objects.
     */
    public static void diff_cleanupMerge(LinkedList<Diff> diffs) {
        diffs.add(new Diff(Operation.EQUAL, ""));  // Add a dummy entry at the end.
        ListIterator<Diff> pointer = diffs.listIterator();
        int count_delete = 0;
        int count_insert = 0;
        String text_delete = "";
        String text_insert = "";
        Diff thisDiff = pointer.next();
        Diff prevEqual = null;
        int commonlength;
        while (thisDiff != null) {
            switch (thisDiff.operation) {
                case INSERT -> {
                    count_insert++;
                    text_insert += thisDiff.text;
                    prevEqual = null;
                }
                case DELETE -> {
                    count_delete++;
                    text_delete += thisDiff.text;
                    prevEqual = null;
                }
                case EQUAL -> {
                    if (count_delete + count_insert > 1) {
                        boolean both_types = count_delete != 0 && count_insert != 0;
                        // Delete the offending records.
                        pointer.previous();  // Reverse direction.
                        while (count_delete-- > 0) {
                            pointer.previous();
                            pointer.remove();
                        }
                        while (count_insert-- > 0) {
                            pointer.previous();
                            pointer.remove();
                        }
                        if (both_types) {
                            // Factor out any common prefixies.
                            commonlength = diff_commonPrefix(text_insert, text_delete);
                            if (commonlength != 0) {
                                if (pointer.hasPrevious()) {
                                    thisDiff = pointer.previous();
                                    assert thisDiff.operation == Operation.EQUAL
                                            : "Previous diff should have been an equality.";
                                    thisDiff.text += text_insert.substring(0, commonlength);
                                    pointer.next();
                                } else {
                                    pointer.add(new Diff(Operation.EQUAL,
                                            text_insert.substring(0, commonlength)));
                                }
                                text_insert = text_insert.substring(commonlength);
                                text_delete = text_delete.substring(commonlength);
                            }
                            // Factor out any common suffixies.
                            commonlength = diff_commonSuffix(text_insert, text_delete);
                            if (commonlength != 0) {
                                thisDiff = pointer.next();
                                thisDiff.text = text_insert.substring(text_insert.length()
                                        - commonlength) + thisDiff.text;
                                text_insert = text_insert.substring(0, text_insert.length()
                                        - commonlength);
                                text_delete = text_delete.substring(0, text_delete.length()
                                        - commonlength);
                                pointer.previous();
                            }
                        }
                        // Insert the merged records.
                        if (text_delete.length() != 0) {
                            pointer.add(new Diff(Operation.DELETE, text_delete));
                        }
                        if (text_insert.length() != 0) {
                            pointer.add(new Diff(Operation.INSERT, text_insert));
                        }
                        // Step forward to the equality.
                        thisDiff = pointer.hasNext() ? pointer.next() : null;
                    } else if (prevEqual != null) {
                        // Merge this equality with the previous one.
                        prevEqual.text += thisDiff.text;
                        pointer.remove();
                        thisDiff = pointer.previous();
                        pointer.next();  // Forward direction
                    }
                    count_insert = 0;
                    count_delete = 0;
                    text_delete = "";
                    text_insert = "";
                    prevEqual = thisDiff;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }
        if (diffs.getLast().text.length() == 0) {
            diffs.removeLast();  // Remove the dummy entry at the end.
        }

        /*
         * Second pass: look for single edits surrounded on both sides by equalities
         * which can be shifted sideways to eliminate an equality.
         * e.g: A<ins>BA</ins>C -> <ins>AB</ins>AC
         */
        boolean changes = false;
        // Create a new iterator at the start.
        // (As opposed to walking the current one back.)
        pointer = diffs.listIterator();
        Diff prevDiff = pointer.hasNext() ? pointer.next() : null;
        thisDiff = pointer.hasNext() ? pointer.next() : null;
        Diff nextDiff = pointer.hasNext() ? pointer.next() : null;
        // Intentionally ignore the first and last element (don't need checking).
        while (nextDiff != null) {
            if (prevDiff.operation == Operation.EQUAL &&
                    nextDiff.operation == Operation.EQUAL) {
                // This is a single edit surrounded by equalities.
                if (thisDiff.text.endsWith(prevDiff.text)) {
                    // Shift the edit over the previous equality.
                    thisDiff.text = prevDiff.text
                            + thisDiff.text.substring(0, thisDiff.text.length()
                            - prevDiff.text.length());
                    nextDiff.text = prevDiff.text + nextDiff.text;
                    pointer.previous(); // Walk past nextDiff.
                    pointer.previous(); // Walk past thisDiff.
                    pointer.previous(); // Walk past prevDiff.
                    pointer.remove(); // Delete prevDiff.
                    pointer.next(); // Walk past thisDiff.
                    thisDiff = pointer.next(); // Walk past nextDiff.
                    nextDiff = pointer.hasNext() ? pointer.next() : null;
                    changes = true;
                } else if (thisDiff.text.startsWith(nextDiff.text)) {
                    // Shift the edit over the next equality.
                    prevDiff.text += nextDiff.text;
                    thisDiff.text = thisDiff.text.substring(nextDiff.text.length())
                            + nextDiff.text;
                    pointer.remove(); // Delete nextDiff.
                    nextDiff = pointer.hasNext() ? pointer.next() : null;
                    changes = true;
                }
            }
            prevDiff = thisDiff;
            thisDiff = nextDiff;
            nextDiff = pointer.hasNext() ? pointer.next() : null;
        }
        // If shifts were made, the diff needs reordering and another shift sweep.
        if (changes) {
            diff_cleanupMerge(diffs);
        }
    }

    /**
     * Convert a Diff list into a pretty HTML report.
     * @param diffs List of Diff objects.
     * @return HTML representation.
     */
    public static String diff_prettyHtml(List<Diff> diffs) {
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
