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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static gov.nysenate.openleg.common.util.stringDiffer.Operation.DELETE;
import static gov.nysenate.openleg.common.util.stringDiffer.Operation.INSERT;
import static gov.nysenate.openleg.common.util.stringDiffer.StringDiffer.internalDiffMain;

/**
 * Find the 'middle snake' of a diff, split the problem in two
 * and return the recursively constructed diff.
 * See Myers 1986 paper: An O(ND) Difference Algorithm and Its Variations.
 * Split out and heavily modified from the original version.
 */
public class DiffBisect {
    private final long deadline;
    private final String text1;
    private final String text2;
    private final int text1Length;
    private final int text2Length;
    private final int max_d;
    private final int[] v1;
    private final int[] v2;
    private final int delta;
    private LinkedList<Diff> result = null;
    private final DiffBisectBoundaries frontBoundary = new DiffBisectBoundaries();
    private final DiffBisectBoundaries reverseBoundary = new DiffBisectBoundaries();

    /**
     * @param text1 Old string to be diffed.
     * @param text2 New string to be diffed.
     * @param deadline Time at which to bail if not yet complete.
     */
    public DiffBisect(String text1, String text2, long deadline) {
        this.deadline = deadline;
        this.text1 = text1;
        this.text2 = text2;
        // Cache the text lengths to prevent multiple calls.
        this.text1Length = text1.length();
        this.text2Length = text2.length();
        this.max_d = (text1Length + text2Length + 1) / 2;
        this.v1 = new int[2 * max_d];
        Arrays.fill(v1, -1);
        v1[max_d + 1] = 0;
        this.v2 = Arrays.copyOf(v1, v1.length);
        this.delta = text1Length - text2Length;
    }

    public LinkedList<Diff> mainLoop() {
        for (int d = 0; d < max_d; d++) {
            // Bail out if deadline is reached.
            if (System.currentTimeMillis() > deadline) {
                break;
            }
            bisectHelper(true, d);
            bisectHelper(false, d);
            if (result != null) {
                return result;
            }
        }
        // Diff took too long and hit the deadline or
        // number of diffs equals number of characters, no commonality at all.
        var diffs = new LinkedList<Diff>();
        diffs.add(new Diff(DELETE, text1));
        diffs.add(new Diff(INSERT, text2));
        return diffs;
    }

    private void bisectHelper(boolean isFront, int d) {
        if (result != null) {
            return;
        }
        var boundary = isFront ? frontBoundary : reverseBoundary;
        int[] vArray = isFront ? v1 : v2;
        int[] otherArray = isFront ? v2 : v1;
        for (int k = -d + boundary.getStart(); k <= d - boundary.getEnd(); k += 2) {
            int offset = max_d + k;
            int x;
            if (k == -d || (k != d && vArray[offset - 1] < vArray[offset + 1])) {
                x = vArray[offset + 1];
            } else {
                x = vArray[offset - 1] + 1;
            }
            int y = x - k;
            while (x < text1Length && y < text2Length
                    && text1.charAt(isFront ? x : text1Length - x - 1)
                    == text2.charAt(isFront ? y : text2Length - y - 1)) {
                x++;
                y++;
            }
            vArray[offset] = x;
            if (x > text1Length) {
                // Ran off the left of the graph.
                boundary.incrementEnd();
            } else if (y > text2Length) {
                // Ran off the top of the graph.
                boundary.incrementStart();
            } else if (isFront == (delta%2 != 0)) {
                int otherOffset = max_d + delta - k;
                if (hasValue(otherArray, otherOffset)) {
                    int x2 = text1Length - (isFront ? otherArray[otherOffset] : x);
                    if (!isFront) {
                        x = otherArray[otherOffset];
                        y = max_d + x - otherOffset;
                    }
                    if (x >= x2) {
                        // Overlap detected.
                        result = diffBisectSplit(text1, text2, x, y, deadline);
                        break;
                    }
                }
            }
        }
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
    private static LinkedList<Diff> diffBisectSplit(String text1, String text2,
                                              int x, int y, long deadline) {
        // Compute both diffs serially.
        LinkedList<Diff> diffs = internalDiffMain(text1.substring(0, x), text2.substring(0, y),
                false, deadline);
        List<Diff> diffsb = internalDiffMain(text1.substring(x), text2.substring(y),
                false, deadline);
        diffs.addAll(diffsb);
        return diffs;
    }

    private static boolean hasValue(int[] ar, int index) {
        return index >= 0 && index < ar.length && ar[index] != -1;
    }
}
