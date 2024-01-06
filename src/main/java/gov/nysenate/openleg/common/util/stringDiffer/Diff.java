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

import java.util.Objects;

/**
 * Class representing one diff operation. Split out and simplified from the original version.
 */
public class Diff {
    protected Operation operation;
    protected String text;

    /**
     * Constructor.  Initializes the diff with the provided values.
     * @param operation One of INSERT, DELETE or EQUAL.
     * @param text The text being applied.
     */
    public Diff(Operation operation, String text) {
        // Construct a diff with the specified operation and text.
        this.operation = operation;
        this.text = text;
    }

    /**
     * Display a human-readable version of this Diff.
     * @return text version.
     */
    public String toString() {
        return "Diff(" + operation + ",\"" + text.replace('\n', '\u00b6') + "\")";
    }

    /**
     * Is this Diff equivalent to another Diff?
     * @param obj Another Diff to compare against.
     * @return true or false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Diff other)) {
            return false;
        }
        return operation == other.operation && Objects.equals(text, other.text);
    }
}
