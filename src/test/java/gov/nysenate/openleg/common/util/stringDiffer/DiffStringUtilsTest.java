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

import static gov.nysenate.openleg.common.util.stringDiffer.DiffStringUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * Split out and modified from the original version.
 */
@Category(UnitTest.class)
public class DiffStringUtilsTest {
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
}
