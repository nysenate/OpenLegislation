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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static gov.nysenate.openleg.common.util.stringDiffer.Operation.*;
import static gov.nysenate.openleg.common.util.stringDiffer.StringDiffer.*;
import static org.junit.Assert.*;

/**
 * Split out and modified from the original version.
 */
@Category(UnitTest.class)
public class StringDifferTest {
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
      List<String> tmpVector = List.of("", "alpha\n", "beta\n");
      assertEquals("Shared lines.",
              new LinesToCharsResult("\u0001\u0002\u0001", "\u0002\u0001\u0002", tmpVector),
              linesToChars("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n"));

      tmpVector = List.of("", "alpha\r\n", "beta\r\n", "\r\n");
      assertEquals("Empty string and blank lines.",
              new LinesToCharsResult("", "\u0001\u0002\u0003\u0003", tmpVector),
              linesToChars("", "alpha\r\nbeta\r\n\r\n\r\n"));

      tmpVector = List.of("", "a", "b");
      assertEquals(new LinesToCharsResult("\u0001", "\u0002", tmpVector),
              linesToChars("a", "b"));

      // More than 256 to reveal any 8-bit limitations.
      int n = 300;
      tmpVector = new ArrayList<>();
      StringBuilder lineList = new StringBuilder();
      StringBuilder charList = new StringBuilder();
      for (int i = 1; i < n + 1; i++) {
         tmpVector.add(i + "\n");
         lineList.append(i).append("\n");
         charList.append((char) i);
      }
      assertEquals("Test initialization fail #1.", n, tmpVector.size());
      assertEquals("Test initialization fail #2.", n, charList.length());
      tmpVector.add(0, "");
      assertEquals("More than 256.",
              new LinesToCharsResult(charList.toString(), "", tmpVector),
              linesToChars(lineList.toString(), ""));
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
        // Test that we didn't take forever (be forgiving).
        // Theoretically this test could fail very occasionally if the
        // OS task swaps or locks up for a second at the wrong moment.
        assertTrue("Timeout max.", diffTimeout * 1000 * 2 > endTime - startTime);

        // Test the linemode speedup.
        // Must be long to pass the 100 char cutoff.
        int arrayLength = 13;
        String[] numAr = new String[arrayLength];
        Arrays.fill(numAr, "1234567890");
        String[] lettersAr = new String[arrayLength];
        Arrays.fill(lettersAr, "abcdefghij");
        List<String> numList = List.of(numAr);
        List<String> letterList = List.of(lettersAr);
        a = String.join("\n", numList);
        b = String.join("\n", letterList);
        assertEquals("Simple line-mode.", diffMain(a, b, true, 0), diffMain(a, b, false, 0));

        a = String.join("", numList);
        b = String.join("", letterList);
        assertEquals("Single line-mode.", diffMain(a, b, true, 0), diffMain(a, b, false, 0));
    }

    @Test
    public void getCleanedDiffsTest() {
       try {
          getCleanedDiffs(null, null);
          fail("Null inputs.");
       } catch (IllegalArgumentException ignored) {
          // Error expected.
       }
       assertTrue(getCleanedDiffs("", "").isEmpty());
    }

    private static LinkedList<Diff> diffList(Diff... diffs) {
        return new LinkedList<>(List.of(diffs));
    }
}
