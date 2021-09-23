package gov.nysenate.openleg.processors.bill.sobi;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;

public class SobiProcessOptions
{
    /** This Builder class is used to construct immutable SobiProcessOption instances. */
    public static class Builder
    {
        ImmutableSet<LegDataFragmentType> allowedFragmentTypes = ImmutableSet.copyOf(LegDataFragmentType.values());

        public Builder setAllowedFragmentTypes(ImmutableSet<LegDataFragmentType> allowedFragmentTypes) {
            this.allowedFragmentTypes = allowedFragmentTypes;
            return this;
        }

        /**
         * Constructs a new instance of SobiProcessOptions via this Builder.
         */
        public SobiProcessOptions build() {
            return new SobiProcessOptions(this);
        }
    }

    /** --- Begin SobiProcessOptions --- */

    private ImmutableSet<LegDataFragmentType> allowedFragmentTypes;

    /** --- Constructors --- */

    public SobiProcessOptions(Builder processOptionsBuilder) {
        allowedFragmentTypes = processOptionsBuilder.allowedFragmentTypes;
    }

    /** --- Methods --- */

    public static Builder builder() {
        return new Builder();
    }

    /** --- Basic Getters --- */

    public ImmutableSet<LegDataFragmentType> getAllowedFragmentTypes() {
        return allowedFragmentTypes;
    }
}
