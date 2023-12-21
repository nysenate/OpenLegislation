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

/**
 * The data structure representing a diff is a LinkedList of Diff objects. E.g.
 * {Diff(DELETE, "Hello"), Diff(INSERT, "Goodbye"), Diff(EQUAL, " world.")}
 * Means: delete "Hello", add "Goodbye" and keep " world."
 * Split out from original version.
 */
public enum Operation {
    DELETE, INSERT, EQUAL
}
