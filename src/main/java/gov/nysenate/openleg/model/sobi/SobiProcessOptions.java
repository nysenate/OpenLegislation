package gov.nysenate.openleg.model.sobi;

import com.google.common.collect.ImmutableSet;

public class SobiProcessOptions
{
    /** This Builder class is used to construct immutable SobiProcessOption instances. */
    public static class Builder
    {
        ImmutableSet<SobiFragmentType> allowedFragmentTypes = ImmutableSet.copyOf(SobiFragmentType.values());

        public Builder setAllowedFragmentTypes(ImmutableSet<SobiFragmentType> allowedFragmentTypes) {
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

    private ImmutableSet<SobiFragmentType> allowedFragmentTypes;

    /** --- Constructors --- */

    public SobiProcessOptions(Builder processOptionsBuilder) {
        allowedFragmentTypes = processOptionsBuilder.allowedFragmentTypes;
    }

    /** --- Methods --- */

    public static Builder builder() {
        return new Builder();
    }

    /** --- Basic Getters --- */

    public ImmutableSet<SobiFragmentType> getAllowedFragmentTypes() {
        return allowedFragmentTypes;
    }
}
