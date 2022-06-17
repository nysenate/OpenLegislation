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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static gov.nysenate.openleg.common.util.stringDiffer.Operation.*;
import static gov.nysenate.openleg.common.util.stringDiffer.StringDiffer.*;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class StringDifferTest {
   @Test
   public void testDiffCommonPrefix() {
      assertEquals("Null case.", 0, commonPrefixLength("abc", "xyz"));
      assertEquals("Non-null case.", 4, commonPrefixLength("1234abcdef", "1234xyz"));
      assertEquals("Whole case.", 4, commonPrefixLength("1234", "1234xyz"));
   }

   @Test
   public void testDiffCommonSuffix() {
      assertEquals("Null case.", 0, commonSuffixLength("abc", "xyz"));
      assertEquals("Non-null case.", 4, commonSuffixLength("abcdef1234", "xyz1234"));
      assertEquals("Whole case.", 4, commonSuffixLength("1234", "xyz1234"));
   }

   @Test
   public void testDiffCommonOverlap() {
      // Detect any suffix/prefix overlap.
      assertEquals("Null case.", 0, commonOverlapLength("", "abcd"));
      assertEquals("Whole case.", 3, commonOverlapLength("abc", "abcd"));
      assertEquals("No overlap.", 0, commonOverlapLength("123456", "abcd"));
      assertEquals("Overlap.", 3, commonOverlapLength("123456xxx", "xxxabcd"));

      // Some overly clever languages (C#) may treat ligatures as equal to their
      // component letters.  E.g. U+FB01 == 'fi'
      assertEquals("Unicode.", 0, commonOverlapLength("fi", "\ufb01i"));
   }

   @Test
   public void testDiffHalfmatch() {
      // Detect a halfmatch.
      assertNull("No match #1.", halfMatch("1234567890", "abcdef", false));
      assertNull("No match #2.", halfMatch("12345", "23", false));
      assertArrayEquals("Single Match #1.",
              new String[]{"12", "90", "a", "z", "345678"},
              halfMatch("1234567890", "a345678z", false));
      assertArrayEquals("Single Match #2.",
              new String[]{"a", "z", "12", "90", "345678"},
              halfMatch("a345678z", "1234567890", false));
      assertArrayEquals("Single Match #3.",
              new String[]{"abc", "z", "1234", "0", "56789"},
              halfMatch("abc56789z", "1234567890", false));
      assertArrayEquals("Single Match #4.",
              new String[]{"a", "xyz", "1", "7890", "23456"},
              halfMatch("a23456xyz", "1234567890", false));
      assertArrayEquals("Multiple Matches #1.",
              new String[]{"12123", "123121", "a", "z", "1234123451234"},
              halfMatch("121231234123451234123121", "a1234123451234z", false));
      assertArrayEquals("Multiple Matches #2.",
              new String[]{"", "-=-=-=-=-=", "x", "", "x-=-=-=-=-=-=-="},
              halfMatch("x-=-=-=-=-=-=-=-=-=-=-=-=", "xx-=-=-=-=-=-=-=", false));
      assertArrayEquals("Multiple Matches #3.",
              new String[]{"-=-=-=-=-=", "", "", "y", "-=-=-=-=-=-=-=y"},
              halfMatch("-=-=-=-=-=-=-=-=-=-=-=-=y", "-=-=-=-=-=-=-=yy", false));

      // Optimal diff would be -q+x=H-i+e=lloHe+Hu=llo-Hew+y not -qHillo+x=HelloHe-w+Hulloy
      assertArrayEquals("Non-optimal halfmatch.",
              new String[]{"qHillo", "w", "x", "Hulloy", "HelloHe"},
              halfMatch("qHilloHelloHew", "xHelloHeHulloy", false));
      assertNull("Optimal no halfmatch.",
              halfMatch("qHilloHelloHew", "xHelloHeHulloy", true));
   }

   @Test
   public void testDiffLinesToChars() {
      List<String> tmpVector = new ArrayList<>();
      tmpVector.add("");
      tmpVector.add("alpha\n");
      tmpVector.add("beta\n");
      assertEquals("Shared lines.",
              new LinesToCharsResult("\u0001\u0002\u0001", "\u0002\u0001\u0002", tmpVector),
              linesToChars("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n"));

      tmpVector.clear();
      tmpVector.add("");
      tmpVector.add("alpha\r\n");
      tmpVector.add("beta\r\n");
      tmpVector.add("\r\n");
      assertEquals("Empty string and blank lines.",
              new LinesToCharsResult("", "\u0001\u0002\u0003\u0003", tmpVector),
              linesToChars("", "alpha\r\nbeta\r\n\r\n\r\n"));

      tmpVector.clear();
      tmpVector.add("");
      tmpVector.add("a");
      tmpVector.add("b");
      assertEquals(new LinesToCharsResult("\u0001", "\u0002", tmpVector),
              linesToChars("a", "b"));

      // More than 256 to reveal any 8-bit limitations.
      int n = 300;
      tmpVector.clear();
      StringBuilder lineList = new StringBuilder();
      StringBuilder charList = new StringBuilder();
      for (int i = 1; i < n + 1; i++) {
         tmpVector.add(i + "\n");
         lineList.append(i).append("\n");
         charList.append((char) i);
      }
      assertEquals("Test initialization fail #1.", n, tmpVector.size());
      String lines = lineList.toString();
      String chars = charList.toString();
      assertEquals("Test initialization fail #2.", n, chars.length());
      tmpVector.add(0, "");
      assertEquals("More than 256.",
              new LinesToCharsResult(chars, "", tmpVector),
              linesToChars(lines, ""));
   }

     @Test
     public void testDiffCharsToLines() {
        // First check that Diff equality works.
        assertEquals("Equality.", new Diff(EQUAL, "a"), new Diff(EQUAL, "a"));

        // Convert chars up to lines.
        LinkedList<Diff> diffs = diffList(new Diff(EQUAL, "\u0001\u0002\u0001"),
                new Diff(INSERT, "\u0002\u0001\u0002"));
        List<String> tmpVector = new ArrayList<>();
        tmpVector.add("");
        tmpVector.add("alpha\n");
        tmpVector.add("beta\n");
        charsToLines(diffs, tmpVector);
        assertEquals("Shared lines.",
                diffList(new Diff(EQUAL, "alpha\nbeta\nalpha\n"),
                        new Diff(INSERT, "beta\nalpha\nbeta\n")), diffs);

        // More than 256 to reveal any 8-bit limitations.
        int n = 300;
        tmpVector.clear();
        StringBuilder lineList = new StringBuilder();
        StringBuilder charList = new StringBuilder();
        for (int i = 1; i < n + 1; i++) {
            tmpVector.add(i + "\n");
            lineList.append(i).append("\n");
            charList.append((char) i);
        }
        assertEquals("Test initialization fail #3.", n, tmpVector.size());
        String lines = lineList.toString();
        String chars = charList.toString();
        assertEquals("Test initialization fail #4.", n, chars.length());
        tmpVector.add(0, "");
        diffs = diffList(new Diff(DELETE, chars));
        charsToLines(diffs, tmpVector);
        assertEquals("More than 256.", diffList(new Diff(DELETE, lines)), diffs);

        // More than 65536 to verify any 16-bit limitation.
        lineList = new StringBuilder();
        for (int i = 0; i < 66000; i++) {
            lineList.append(i).append("\n");
        }
        chars = lineList.toString();
        LinesToCharsResult results = linesToChars(chars, "");
        diffs = diffList(new Diff(INSERT, results.chars1()));
        charsToLines(diffs, results.lineArray());
        assertEquals("More than 65536.", chars, diffs.getFirst().text);
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

     @Test
     public void testDiffPrettyHtml() {
        List<Diff> diffs = diffList(new Diff(EQUAL, "a\n"), new Diff(DELETE, "<B>b</B>"), new Diff(INSERT, "c&d"));
        assertEquals("prettyHtml:", "<span>a&para;<br></span><del style=\"background:#ffe6e6;\">&lt;B&gt;b&lt;/B&gt;</del><ins style=\"background:#e6ffe6;\">c&amp;d</ins>", prettyHtml(diffs));
    }

     @Test
     public void testDiffBisect() {
        // Normal.
        String a = "cat";
        String b = "map";
        // Since the resulting diff hasn't been normalized, it would be ok if
        // the insertion and deletion pairs are swapped.
        // If the order changes, tweak this test as required.
        List<Diff> diffs = diffList(new Diff(DELETE, "c"), new Diff(INSERT, "m"), new Diff(EQUAL, "a"), new Diff(DELETE, "t"), new Diff(INSERT, "p"));
        assertEquals("Normal.", diffs, diffBisect(a, b, Long.MAX_VALUE));

        // Timeout.
        diffs = diffList(new Diff(DELETE, "cat"), new Diff(INSERT, "map"));
        assertEquals("Timeout.", diffs, diffBisect(a, b, 0));
    }

     @Test
     public void testDiffMain() {
        // Perform a trivial diff.
        List<Diff> diffs = diffList();
        assertEquals("Null case.", diffs, diffMain("", "", false));

        diffs = diffList(new Diff(EQUAL, "abc"));
        assertEquals("Equality.", diffs, diffMain("abc", "abc", false));

        diffs = diffList(new Diff(EQUAL, "ab"), new Diff(INSERT, "123"), new Diff(EQUAL, "c"));
        assertEquals("Simple insertion.", diffs, diffMain("abc", "ab123c", false));

        diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "123"), new Diff(EQUAL, "bc"));
        assertEquals("Simple deletion.", diffs, diffMain("a123bc", "abc", false));

        diffs = diffList(new Diff(EQUAL, "a"), new Diff(INSERT, "123"), new Diff(EQUAL, "b"), new Diff(INSERT, "456"), new Diff(EQUAL, "c"));
        assertEquals("Two insertions.", diffs, diffMain("abc", "a123b456c", false));

        diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "123"), new Diff(EQUAL, "b"), new Diff(DELETE, "456"), new Diff(EQUAL, "c"));
        assertEquals("Two deletions.", diffs, diffMain("a123b456c", "abc", false));

        // Perform a real diff.
        // Switch off the timeout.
        diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "b"));
        assertEquals("Simple case #1.", diffs, diffMain("a", "b", false, 0));

        diffs = diffList(new Diff(DELETE, "Apple"), new Diff(INSERT, "Banana"), new Diff(EQUAL, "s are a"), new Diff(INSERT, "lso"), new Diff(EQUAL, " fruit."));
        assertEquals("Simple case #2.", diffs, diffMain("Apples are a fruit.", "Bananas are also fruit.", false, 0));

        diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "\u0680"), new Diff(EQUAL, "x"), new Diff(DELETE, "\t"), new Diff(INSERT, "\000"));
        assertEquals("Simple case #3.", diffs, diffMain("ax\t", "\u0680x\000", false, 0));

        diffs = diffList(new Diff(DELETE, "1"), new Diff(EQUAL, "a"), new Diff(DELETE, "y"), new Diff(EQUAL, "b"), new Diff(DELETE, "2"), new Diff(INSERT, "xab"));
        assertEquals("Overlap #1.", diffs, diffMain("1ayb2", "abxab", false, 0));

        diffs = diffList(new Diff(INSERT, "xaxcx"), new Diff(EQUAL, "abc"), new Diff(DELETE, "y"));
        assertEquals("Overlap #2.", diffs, diffMain("abcy", "xaxcxabc", false, 0));

        diffs = diffList(new Diff(DELETE, "ABCD"), new Diff(EQUAL, "a"), new Diff(DELETE, "="), new Diff(INSERT, "-"), new Diff(EQUAL, "bcd"), new Diff(DELETE, "="), new Diff(INSERT, "-"), new Diff(EQUAL, "efghijklmnopqrs"), new Diff(DELETE, "EFGHIJKLMNOefg"));
        assertEquals("Overlap #3.", diffs, diffMain("ABCDa=bcd=efghijklmnopqrsEFGHIJKLMNOefg", "a-bcd-efghijklmnopqrs", false, 0));

        diffs = diffList(new Diff(INSERT, " "), new Diff(EQUAL, "a"), new Diff(INSERT, "nd"), new Diff(EQUAL, " [[Pennsylvania]]"), new Diff(DELETE, " and [[New"));
        assertEquals("Large equality.", diffs, diffMain("a [[Pennsylvania]] and [[New", " and [[Pennsylvania]]", false, 0));

        // Long Strings ensure a timeout.
        String a = "`Twas brillig, and the slithy toves\nDid gyre and gimble in the wabe:\nAll mimsy were the borogoves,\nAnd the mome raths outgrabe.\n".repeat(10);
        String b = "I am the very model of a modern major general,\nI've information vegetable, animal, and mineral,\nI know the kings of England, and I quote the fights historical,\nFrom Marathon to Waterloo, in order categorical.\n".repeat(10);

        final float diffTimeout = 0.1f;
        long startTime = System.currentTimeMillis();
        diffMain(a, b, true, diffTimeout);
        long endTime = System.currentTimeMillis();
        // Test that we took at least the timeout period.
        assertTrue("Timeout min.", diffTimeout * 1000 <= endTime - startTime);
        // Test that we didn't take forever (be forgiving).
        // Theoretically this test could fail very occasionally if the
        // OS task swaps or locks up for a second at the wrong moment.
        assertTrue("Timeout max.", diffTimeout * 1000 * 2 > endTime - startTime);

        // Test the linemode speedup.
        // Must be long to pass the 100 char cutoff.
        final String nums = "1234567890";
        final String letters = "abcdefghij";
        List<String> numList = new ArrayList<>(13);
        List<String> letterList = new ArrayList<>(13);
        for (int i = 0; i < 13; i++) {
            numList.add(nums);
            letterList.add(letters);
        }
        a = String.join("\n", numList);
        b = String.join("\n", letterList);
        assertEquals("Simple line-mode.", diffMain(a, b, true, 0), diffMain(a, b, false, 0));

        a = String.join("", numList);
        b = String.join("", letterList);
        assertEquals("Single line-mode.", diffMain(a, b, true, 0), diffMain(a, b, false, 0));

        // Test null inputs.
        try {
            getCleanedDiffs(null, null);
            fail("Null inputs.");
        } catch (IllegalArgumentException ignored) {
            // Error expected.
        }
    }

    private static LinkedList<Diff> diffList(Diff... diffs) {
        return new LinkedList<>(List.of(diffs));
    }
}
