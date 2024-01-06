/*
 * Diff Match and Patch -- Test harness
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

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.LinkedList;
import java.util.List;

import static gov.nysenate.openleg.common.util.stringDiffer.DiffUtils.*;
import static gov.nysenate.openleg.common.util.stringDiffer.Operation.*;
import static org.junit.Assert.*;

/**
 * Split out and modified from the original version.
 */
@Category(UnitTest.class)
public class DiffUtilsTest {

    @Test
    public void basicDiffTest() {
        var diff = new Diff(EQUAL, "test");
        assertEquals(diff, diff);
        assertNotEquals(null, diff);
        String diffStr = diff.toString();
        assertTrue(diffStr.contains(diff.operation.toString()) && diffStr.contains(diff.text));
    }

    @Test
    public void testDiffCleanupMerge() {
        // Cleanup a messy diff.
        diffCleanupMergeCompare("Null case.", diffList());
        LinkedList<Diff> diffs;

        diffCleanupMergeCompare("No change case.",
                diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "b"), new Diff(INSERT, "c")),
                new Diff(EQUAL, "a"), new Diff(DELETE, "b"), new Diff(INSERT, "c"));

        diffCleanupMergeCompare("Merge equalities.",
                diffList(new Diff(EQUAL, "a"), new Diff(EQUAL, "b"), new Diff(EQUAL, "c")),
                new Diff(EQUAL, "abc"));

        diffCleanupMergeCompare("Merge deletions.",
                diffList(new Diff(DELETE, "a"), new Diff(DELETE, "b"), new Diff(DELETE, "c")),
                new Diff(DELETE, "abc"));

        diffCleanupMergeCompare("Merge insertions.",
                diffList(new Diff(INSERT, "a"), new Diff(INSERT, "b"), new Diff(INSERT, "c")),
                new Diff(INSERT, "abc"));

        diffCleanupMergeCompare("Merge interweave.",
                diffList(new Diff(DELETE, "a"), new Diff(INSERT, "b"), new Diff(DELETE, "c"),
                        new Diff(INSERT, "d"), new Diff(EQUAL, "e"), new Diff(EQUAL, "f")),
                new Diff(DELETE, "ac"), new Diff(INSERT, "bd"), new Diff(EQUAL, "ef"));

        diffCleanupMergeCompare("Prefix and suffix detection.",
                diffList(new Diff(DELETE, "a"), new Diff(INSERT, "abc"), new Diff(DELETE, "dc")),
                new Diff(EQUAL, "a"), new Diff(DELETE, "d"), new Diff(INSERT, "b"),
                new Diff(EQUAL, "c"));

        diffCleanupMergeCompare("Prefix and suffix detection with equalities.",
                diffList(new Diff(EQUAL, "x"), new Diff(DELETE, "a"), new Diff(INSERT, "abc"),
                        new Diff(DELETE, "dc"), new Diff(EQUAL, "y")),
                new Diff(EQUAL, "xa"), new Diff(DELETE, "d"), new Diff(INSERT, "b"), new Diff(EQUAL, "cy"));


        diffs = diffList(new Diff(EQUAL, "a"), new Diff(INSERT, "ba"), new Diff(EQUAL, "c"));
        cleanupMerge(diffs);
        assertEquals("Slide edit left.", diffList(new Diff(INSERT, "ab"), new Diff(EQUAL, "ac")),
                diffs);

        diffs = diffList(new Diff(EQUAL, "c"), new Diff(INSERT, "ab"), new Diff(EQUAL, "a"));
        cleanupMerge(diffs);
        assertEquals("Slide edit right.", diffList(new Diff(EQUAL, "ca"), new Diff(INSERT, "ba")),
                diffs);

        diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "b"), new Diff(EQUAL, "c"),
                new Diff(DELETE, "ac"), new Diff(EQUAL, "x"));
        cleanupMerge(diffs);
        assertEquals("Slide edit left recursive.", diffList(new Diff(DELETE, "abc"),
                new Diff(EQUAL, "acx")), diffs);

        diffs = diffList(new Diff(EQUAL, "x"), new Diff(DELETE, "ca"), new Diff(EQUAL, "c"),
                new Diff(DELETE, "b"), new Diff(EQUAL, "a"));
        cleanupMerge(diffs);
        assertEquals("Slide edit right recursive.", diffList(new Diff(EQUAL, "xca"),
                new Diff(DELETE, "cba")), diffs);

        diffs = diffList(new Diff(DELETE, "b"), new Diff(INSERT, "ab"), new Diff(EQUAL, "c"));
        cleanupMerge(diffs);
        assertEquals("Empty merge.", diffList(new Diff(INSERT, "a"), new Diff(EQUAL, "bc")),
                diffs);

        diffs = diffList(new Diff(EQUAL, ""), new Diff(INSERT, "a"), new Diff(EQUAL, "b"));
        cleanupMerge(diffs);
        assertEquals("Empty equality.", diffList(new Diff(INSERT, "a"), new Diff(EQUAL, "b")),
                diffs);
    }

    private static void diffCleanupMergeCompare(String message, LinkedList<Diff> input, Diff... expected) {
        cleanupMerge(input);
        assertEquals(message, diffList(expected), input);
    }

    @Test
    public void testDiffCleanupSemanticLossless() {
        // Slide diffs to match logical boundaries.
        List<Diff> diffs = diffList();
        cleanupSemanticLossless(diffs);
        assertEquals("Null case.", diffList(), diffs);

        diffs = diffList(new Diff(EQUAL, "AAA\r\n\r\nBBB"), new Diff(INSERT, "\r\nDDD\r\n\r\nBBB"), new Diff(EQUAL, "\r\nEEE"));
        cleanupSemanticLossless(diffs);
        assertEquals("Blank lines.", diffList(new Diff(EQUAL, "AAA\r\n\r\n"), new Diff(INSERT, "BBB\r\nDDD\r\n\r\n"), new Diff(EQUAL, "BBB\r\nEEE")), diffs);

        diffs = diffList(new Diff(EQUAL, "AAA\r\nBBB"), new Diff(INSERT, " DDD\r\nBBB"), new Diff(EQUAL, " EEE"));
        cleanupSemanticLossless(diffs);
        assertEquals("Line boundaries.", diffList(new Diff(EQUAL, "AAA\r\n"), new Diff(INSERT, "BBB DDD\r\n"), new Diff(EQUAL, "BBB EEE")), diffs);

        diffs = diffList(new Diff(EQUAL, "The c"), new Diff(INSERT, "ow and the c"), new Diff(EQUAL, "at."));
        cleanupSemanticLossless(diffs);
        assertEquals("Word boundaries.", diffList(new Diff(EQUAL, "The "), new Diff(INSERT, "cow and the "), new Diff(EQUAL, "cat.")), diffs);

        diffs = diffList(new Diff(EQUAL, "The-c"), new Diff(INSERT, "ow-and-the-c"), new Diff(EQUAL, "at."));
        cleanupSemanticLossless(diffs);
        assertEquals("Alphanumeric boundaries.", diffList(new Diff(EQUAL, "The-"), new Diff(INSERT, "cow-and-the-"), new Diff(EQUAL, "cat.")), diffs);

        diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "a"), new Diff(EQUAL, "ax"));
        cleanupSemanticLossless(diffs);
        assertEquals("Hitting the start.", diffList(new Diff(DELETE, "a"), new Diff(EQUAL, "aax")), diffs);

        diffs = diffList(new Diff(EQUAL, "xa"), new Diff(DELETE, "a"), new Diff(EQUAL, "a"));
        cleanupSemanticLossless(diffs);
        assertEquals("Hitting the end.", diffList(new Diff(EQUAL, "xaa"), new Diff(DELETE, "a")), diffs);

        diffs = diffList(new Diff(EQUAL, "The xxx. The "), new Diff(INSERT, "zzz. The "), new Diff(EQUAL, "yyy."));
        cleanupSemanticLossless(diffs);
        assertEquals("Sentence boundaries.", diffList(new Diff(EQUAL, "The xxx."), new Diff(INSERT, " The zzz."), new Diff(EQUAL, " The yyy.")), diffs);
    }

    @Test
    public void testDiffCleanupSemantic() {
        // Cleanup semantically trivial equalities.
        LinkedList<Diff> diffs = diffList();
        cleanupSemantic(diffs);
        assertEquals("Null case.", diffList(), diffs);

        diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "cd"), new Diff(EQUAL, "12"), new Diff(DELETE, "e"));
        cleanupSemantic(diffs);
        assertEquals("No elimination #1.", diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "cd"), new Diff(EQUAL, "12"), new Diff(DELETE, "e")), diffs);

        diffs = diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "ABC"), new Diff(EQUAL, "1234"), new Diff(DELETE, "wxyz"));
        cleanupSemantic(diffs);
        assertEquals("No elimination #2.", diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "ABC"), new Diff(EQUAL, "1234"), new Diff(DELETE, "wxyz")), diffs);

        diffs = diffList(new Diff(DELETE, "a"), new Diff(EQUAL, "b"), new Diff(DELETE, "c"));
        cleanupSemantic(diffs);
        assertEquals("Simple elimination.", diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "b")), diffs);

        diffs = diffList(new Diff(DELETE, "ab"), new Diff(EQUAL, "cd"), new Diff(DELETE, "e"), new Diff(EQUAL, "f"), new Diff(INSERT, "g"));
        cleanupSemantic(diffs);
        assertEquals("Backpass elimination.", diffList(new Diff(DELETE, "abcdef"), new Diff(INSERT, "cdfg")), diffs);

        diffs = diffList(new Diff(INSERT, "1"), new Diff(EQUAL, "A"), new Diff(DELETE, "B"), new Diff(INSERT, "2"), new Diff(EQUAL, "_"), new Diff(INSERT, "1"), new Diff(EQUAL, "A"), new Diff(DELETE, "B"), new Diff(INSERT, "2"));
        cleanupSemantic(diffs);
        assertEquals("Multiple elimination.", diffList(new Diff(DELETE, "AB_AB"), new Diff(INSERT, "1A2_1A2")), diffs);

        diffs = diffList(new Diff(EQUAL, "The c"), new Diff(DELETE, "ow and the c"), new Diff(EQUAL, "at."));
        cleanupSemantic(diffs);
        assertEquals("Word boundaries.", diffList(new Diff(EQUAL, "The "), new Diff(DELETE, "cow and the "), new Diff(EQUAL, "cat.")), diffs);

        diffs = diffList(new Diff(DELETE, "abcxx"), new Diff(INSERT, "xxdef"));
        cleanupSemantic(diffs);
        assertEquals("No overlap elimination.", diffList(new Diff(DELETE, "abcxx"), new Diff(INSERT, "xxdef")), diffs);

        diffs = diffList(new Diff(DELETE, "abcxxx"), new Diff(INSERT, "xxxdef"));
        cleanupSemantic(diffs);
        assertEquals("Overlap elimination.", diffList(new Diff(DELETE, "abc"), new Diff(EQUAL, "xxx"), new Diff(INSERT, "def")), diffs);

        diffs = diffList(new Diff(DELETE, "xxxabc"), new Diff(INSERT, "defxxx"));
        cleanupSemantic(diffs);
        assertEquals("Reverse overlap elimination.", diffList(new Diff(INSERT, "def"), new Diff(EQUAL, "xxx"), new Diff(DELETE, "abc")), diffs);

        diffs = diffList(new Diff(DELETE, "abcd1212"), new Diff(INSERT, "1212efghi"), new Diff(EQUAL, "----"), new Diff(DELETE, "A3"), new Diff(INSERT, "3BC"));
        cleanupSemantic(diffs);
        assertEquals("Two overlap eliminations.", diffList(new Diff(DELETE, "abcd"), new Diff(EQUAL, "1212"), new Diff(INSERT, "efghi"), new Diff(EQUAL, "----"), new Diff(DELETE, "A"), new Diff(EQUAL, "3"), new Diff(INSERT, "BC")), diffs);
    }

    @Test
    public void testDiffCleanupEfficiency() {
        // Cleanup operationally trivial equalities.
        LinkedList<Diff> diffs = diffList();
        cleanupEfficiency(diffs);
        assertEquals("cleanupEfficiency: Null case.", diffList(), diffs);

        diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "wxyz"), new Diff(DELETE, "cd"), new Diff(INSERT, "34"));
        cleanupEfficiency(diffs);
        assertEquals("cleanupEfficiency: No elimination.", diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "wxyz"), new Diff(DELETE, "cd"), new Diff(INSERT, "34")), diffs);

        diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "xyz"), new Diff(DELETE, "cd"), new Diff(INSERT, "34"));
        cleanupEfficiency(diffs);
        assertEquals("cleanupEfficiency: Four-edit elimination.", diffList(new Diff(DELETE, "abxyzcd"), new Diff(INSERT, "12xyz34")), diffs);

        diffs = diffList(new Diff(INSERT, "12"), new Diff(EQUAL, "x"), new Diff(DELETE, "cd"), new Diff(INSERT, "34"));
        cleanupEfficiency(diffs);
        assertEquals("cleanupEfficiency: Three-edit elimination.", diffList(new Diff(DELETE, "xcd"), new Diff(INSERT, "12x34")), diffs);

        diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "xy"), new Diff(INSERT, "34"), new Diff(EQUAL, "z"), new Diff(DELETE, "cd"), new Diff(INSERT, "56"));
        cleanupEfficiency(diffs);
        assertEquals("cleanupEfficiency: Backpass elimination.", diffList(new Diff(DELETE, "abxyzcd"), new Diff(INSERT, "12xy34z56")), diffs);

        diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "wxyz"), new Diff(DELETE, "cd"), new Diff(INSERT, "34"));
        cleanupEfficiency(diffs, (short) 5);
        assertEquals("cleanupEfficiency: High cost elimination.", diffList(new Diff(DELETE, "abwxyzcd"), new Diff(INSERT, "12wxyz34")), diffs);
    }

    private static LinkedList<Diff> diffList(Diff... diffs) {
        return new LinkedList<>(List.of(diffs));
    }
}
