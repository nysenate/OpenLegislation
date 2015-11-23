package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SpotCheckMismatchIgnore {

    IGNORE_PERMANTENTLY(0),
    IGNORE_UNTIL_RESOLVED(1)
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
