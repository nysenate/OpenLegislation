package gov.nysenate.openleg.model.updates;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

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

    public static UpdateContentType getValue(String name) {
        return valueOf(StringUtils.upperCase(name));
    }
}
