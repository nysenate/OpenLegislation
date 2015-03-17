package gov.nysenate.openleg.model.updates;

import com.google.common.collect.ImmutableSet;

public enum UpdateContentType {
    AGENDA,
    BILL,
    CALENDAR,
    LAW,
    ;

    private static final ImmutableSet<UpdateContentType> allTypes = ImmutableSet.copyOf(UpdateContentType.values());

    public static ImmutableSet<UpdateContentType> getAllTypes() {
        return allTypes;
    }
}
