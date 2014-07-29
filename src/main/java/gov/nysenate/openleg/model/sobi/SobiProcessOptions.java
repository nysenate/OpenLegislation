package gov.nysenate.openleg.model.sobi;

import com.google.common.base.Predicate;

import java.util.HashSet;
import java.util.Set;

public class SobiProcessOptions
{
    /**
     * This Builder class is used to construct immutable SobiProcessOption instances.
     */
    public static class Builder
    {
        /**
         * Predicate implementation that filters SobiFragments via the options set in the Builder.
         */
        public static class SobiFragmentFilter implements Predicate<SobiFragment>
        {
            private Builder builder;
            public SobiFragmentFilter(Builder processOptionsBuilder) {
                this.builder = processOptionsBuilder;
            }
            @Override
            public boolean apply(SobiFragment input) {
                if (!builder.filterTypes.isEmpty()) {
                    if (!builder.filterTypes.contains(input.getType())) {
                        return false;
                    }
                }
                return true;
            }
        }

        /** A set of fragment types to process. If the set is empty, all types should be processed. */
        private Set<SobiFragmentType> filterTypes = new HashSet<>();

        /**
         * Process fragment types contained in the given 'types' set.
         */
        public Builder allowFragmentTypes(Set<SobiFragmentType> types) {
            filterTypes.addAll(types);
            return this;
        }

        /**
         * Process fragments that match the given 'type' as well.
         */
        public Builder allowFragmentType(SobiFragmentType type) {
            filterTypes.add(type);
            return this;
        }

        /**
         * Returns a new predicate instance for use in filtering by fragment types;
         */
        public Predicate<SobiFragment> getFragmentFilter() {
            return new SobiFragmentFilter(this);
        }

        /**
         * Constructs a new instance of SobiProcessOptions via this Builder.
         */
        public SobiProcessOptions build() {
            return new SobiProcessOptions(this);
        }
    }

    /** A predicate (evaluates to boolean given an input) that filters out fragments. */
    private Predicate<SobiFragment> fragmentFilter;

    /** --- Constructors --- */

    public SobiProcessOptions(Builder processOptionsBuilder) {
        fragmentFilter = processOptionsBuilder.getFragmentFilter();
    }

    /** --- Methods --- */

    public Predicate<SobiFragment> getFragmentFilter() {
        return fragmentFilter;
    }

    public static Builder builder() {
        return new Builder();
    }
}
