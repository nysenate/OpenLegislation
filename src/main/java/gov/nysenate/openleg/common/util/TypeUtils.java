package gov.nysenate.openleg.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class TypeUtils {
    private TypeUtils() {}

    public static Type[] getGenericTypes(Object caller) {
        return ((ParameterizedType) caller.getClass().getGenericSuperclass())
                .getActualTypeArguments();
    }
}
