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

import static gov.nysenate.openleg.common.util.stringDiffer.DiffStringUtils.*;
import static gov.nysenate.openleg.common.util.stringDiffer.Operation.*;

/**
 * Utilities to help cleanup Lists of Diffs.
 * Split out and modified from the original version.
 */
public final class DiffUtils {
    // Define some regex patterns for matching boundaries.
    private static final Pattern BLANK_LINE_END
            = Pattern.compile("\\n\\r?\\n\\Z", Pattern.DOTALL);
    private static final Pattern BLANK_LINE_START
            = Pattern.compile("\\A\\r?\\n\\r?\\n", Pattern.DOTALL);

    private DiffUtils() {}

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
                post_ins = false;
                post_del = false;
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
            cleanupMerge(diffs);
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
                    if (2 * overlap_length1 >= deletion.length() ||
                            2 * overlap_length1 >= insertion.length()) {
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
                    if (2 * overlap_length2 >= deletion.length() ||
                            2 * overlap_length2 >= insertion.length()) {
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
                            // Factor out any common prefixes.
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
                            // Factor out any common suffixes.
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
}
