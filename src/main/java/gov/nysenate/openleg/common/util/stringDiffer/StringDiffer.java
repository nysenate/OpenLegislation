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
 * Class containing the diff, match and patch methods.
 * Also contains the behaviour settings.
 */
public final class StringDiffer {
    private StringDiffer() {}

    /**
     * Internal class for returning results from diff_linesToChars().
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

        return diffBisect(text1, text2, deadline);
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
        // e.g. linearray[4] == "Hello\n"
        // e.g. linehash.get("Hello\n") == 4

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
     * Determine the common prefix of two strings
     * @param text1 First string.
     * @param text2 Second string.
     * @return The number of characters common to the start of each string.
     */
    static int commonPrefixLength(String text1, String text2) {
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
    static int commonSuffixLength(String text1, String text2) {
        // Performance analysis: https://neil.fraser.name/news/2007/10/09/
        int text1Length = text1.length();
        int text2Length = text2.length();
        int n = Math.min(text1Length, text2Length);
        for (int i = 1; i <= n; i++) {
            if (text1.charAt(text1Length - i) != text2.charAt(text2Length - i)) {
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
    static int commonOverlapLength(String text1, String text2) {
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
    static String[] halfMatch(String text1, String text2, boolean noDeadline) {
        if (noDeadline) {
            // Don't risk returning a non-optimal diff if we have unlimited time.
            return null;
        }
        String longtext = text1.length() > text2.length() ? text1 : text2;
        String shorttext = text1.length() > text2.length() ? text2 : text1;
        if (longtext.length() < 4 || shorttext.length() * 2 < longtext.length()) {
            return null;  // Pointless.
        }

        // First check if the second quarter is the seed for a half-match.
        String[] hm1 = halfMatchI(longtext, shorttext,
                (longtext.length() + 3) / 4);
        // Check again based on the third quarter.
        String[] hm2 = halfMatchI(longtext, shorttext,
                (longtext.length() + 1) / 2);
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
        while ((j = shortText.indexOf(seed, j + 1)) != -1) {
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
     * Reduce the number of edits by eliminating semantically trivial equalities.
     * @param diffs LinkedList of Diff objects.
     */
    static void cleanupSemantic(LinkedList<Diff> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        Deque<Diff> equalities = new ArrayDeque<Diff>();  // Double-ended queue of qualities.
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
            if (thisDiff.operation == EQUAL) {
                // Equality found.
                equalities.push(thisDiff);
                length_insertions1 = length_insertions2;
                length_deletions1 = length_deletions2;
                length_insertions2 = 0;
                length_deletions2 = 0;
                lastEquality = thisDiff.text;
            } else {
                // An insertion or deletion.
                if (thisDiff.operation == INSERT) {
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
                    pointer.set(new Diff(DELETE, lastEquality));
                    // Insert a corresponding an insert.
                    pointer.add(new Diff(INSERT, lastEquality));

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
            cleanupMerge(diffs);
        }
        cleanupSemanticLossless(diffs);

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
            if (prevDiff.operation == DELETE &&
                    thisDiff.operation == INSERT) {
                String deletion = prevDiff.text;
                String insertion = thisDiff.text;
                int overlap_length1 = commonOverlapLength(deletion, insertion);
                int overlap_length2 = commonOverlapLength(insertion, deletion);
                if (overlap_length1 >= overlap_length2) {
                    if (overlap_length1 >= deletion.length() / 2.0 ||
                            overlap_length1 >= insertion.length() / 2.0) {
                        // Overlap found. Insert an equality and trim the surrounding edits.
                        pointer.previous();
                        pointer.add(new Diff(EQUAL,
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
                        pointer.add(new Diff(EQUAL,
                                deletion.substring(0, overlap_length2)));
                        prevDiff.operation = INSERT;
                        prevDiff.text =
                                insertion.substring(0, insertion.length() - overlap_length2);
                        thisDiff.operation = DELETE;
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
    static void cleanupSemanticLossless(List<Diff> diffs) {
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
            if (prevDiff.operation == EQUAL &&
                    nextDiff.operation == EQUAL) {
                // This is a single edit surrounded by equalities.
                equality1 = prevDiff.text;
                edit = thisDiff.text;
                equality2 = nextDiff.text;

                // First, shift the edit as far left as possible.
                commonOffset = commonSuffixLength(equality1, edit);
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
                bestScore = cleanupSemanticScore(equality1, edit)
                        + cleanupSemanticScore(edit, equality2);
                while (edit.length() != 0 && equality2.length() != 0
                        && edit.charAt(0) == equality2.charAt(0)) {
                    equality1 += edit.charAt(0);
                    edit = edit.substring(1) + equality2.charAt(0);
                    equality2 = equality2.substring(1);
                    score = cleanupSemanticScore(equality1, edit)
                            + cleanupSemanticScore(edit, equality2);
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
    private static int cleanupSemanticScore(String one, String two) {
        if (one.isEmpty() || two.isEmpty()) {
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
        boolean blankLine1 = lineBreak1 && BLANK_LINE_END.matcher(one).find();
        boolean blankLine2 = lineBreak2 && BLANK_LINE_START.matcher(two).find();

        if (blankLine1 || blankLine2) {
            // Five points for blank lines.
            return 5;
        } else if (lineBreak1 || lineBreak2) {
            // Four points for line breaks.
            return 4;
        } else if (nonAlphaNumeric1 && !whitespace1 && whitespace2) {
            // Three points for end of sentences.
            return 3;
        } else if (whitespace1 || whitespace2) {
            // Two points for whitespace.
            return 2;
        } else if (nonAlphaNumeric1 || nonAlphaNumeric2) {
            // One point for non-alphanumeric.
            return 1;
        }
        return 0;
    }

    // Define some regex patterns for matching boundaries.
    private static final Pattern BLANK_LINE_END
            = Pattern.compile("\\n\\r?\\n\\Z", Pattern.DOTALL);
    private static final Pattern BLANK_LINE_START
            = Pattern.compile("\\A\\r?\\n\\r?\\n", Pattern.DOTALL);

    static void cleanupEfficiency(LinkedList<Diff> diffs) {
        cleanupEfficiency(diffs, (short) 4);
    }

    /**
     * Reduce the number of edits by eliminating operationally trivial equalities.
     * @param diffs LinkedList of Diff objects.
     */
    static void cleanupEfficiency(LinkedList<Diff> diffs, short diffEditCost) {
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
            if (thisDiff.operation == EQUAL) {
                // Equality found.
                if (thisDiff.text.length() < diffEditCost && (post_ins || post_del)) {
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
                post_ins = post_del = false;
            } else {
                // An insertion or deletion.
                if (thisDiff.operation == DELETE) {
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
                        || ((lastEquality.length() < diffEditCost / 2)
                        && ((pre_ins ? 1 : 0) + (pre_del ? 1 : 0)
                        + (post_ins ? 1 : 0) + (post_del ? 1 : 0)) == 3))) {
                    //System.out.println("Splitting: '" + lastEquality + "'");
                    // Walk back to offending equality.
                    while (thisDiff != equalities.peek()) {
                        thisDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Diff(DELETE, lastEquality));
                    // Insert a corresponding an insert.
                    pointer.add(thisDiff = new Diff(INSERT, lastEquality));

                    equalities.pop();  // Throw away the equality we just deleted.
                    lastEquality = null;
                    if (pre_ins && pre_del) {
                        // No changes made which could affect previous entry, keep going.
                        post_ins = post_del = true;
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
                        post_ins = post_del = false;
                    }

                    changes = true;
                }
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        if (changes) {
            cleanupMerge(diffs);
        }
    }

    /**
     * Reorder and merge like edit sections.  Merge equalities.
     * Any edit section can move as long as it doesn't cross an equality.
     * @param diffs LinkedList of Diff objects.
     */
    static void cleanupMerge(LinkedList<Diff> diffs) {
        diffs.add(new Diff(EQUAL, ""));  // Add a dummy entry at the end.
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
                            commonlength = commonPrefixLength(text_insert, text_delete);
                            if (commonlength != 0) {
                                if (pointer.hasPrevious()) {
                                    thisDiff = pointer.previous();
                                    assert thisDiff.operation == EQUAL
                                            : "Previous diff should have been an equality.";
                                    thisDiff.text += text_insert.substring(0, commonlength);
                                    pointer.next();
                                } else {
                                    pointer.add(new Diff(EQUAL,
                                            text_insert.substring(0, commonlength)));
                                }
                                text_insert = text_insert.substring(commonlength);
                                text_delete = text_delete.substring(commonlength);
                            }
                            // Factor out any common suffixies.
                            commonlength = commonSuffixLength(text_insert, text_delete);
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
                            pointer.add(new Diff(DELETE, text_delete));
                        }
                        if (text_insert.length() != 0) {
                            pointer.add(new Diff(INSERT, text_insert));
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
            if (prevDiff.operation == EQUAL && nextDiff.operation == EQUAL) {
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
            cleanupMerge(diffs);
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
