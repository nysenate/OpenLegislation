package gov.nysenate.openleg.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class TypeUtils {
    private TypeUtils() {}

    /**
     * Turns generics into actual Type objects.
     * @param caller with said generics.
     * @return an array of generics as Type objects.
     */
    public static Type[] getGenericTypes(Object caller) {
        return ((ParameterizedType) caller.getClass().getGenericSuperclass())
                .getActualTypeArguments();
    }
}
