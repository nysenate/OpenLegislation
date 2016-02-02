package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SpotCheckMismatchIgnore {

    NOT_IGNORED(-1),
    IGNORE_PERMANENTLY(0),
    IGNORE_UNTIL_RESOLVED(1),
    IGNORE_ONCE(2)
    ;

    private int code;

    SpotCheckMismatchIgnore(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private static final ImmutableMap<Integer, SpotCheckMismatchIgnore> codeMap = ImmutableMap.copyOf(
            Arrays.stream(SpotCheckMismatchIgnore.values())
                    .collect(Collectors.toMap(SpotCheckMismatchIgnore::getCode, Function.identity()))
    );

    public static SpotCheckMismatchIgnore getIgnoreByCode(Integer code) {
        return codeMap.get(code);
    }
}
