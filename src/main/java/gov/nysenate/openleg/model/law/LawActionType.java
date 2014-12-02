package gov.nysenate.openleg.model.law;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum LawActionType
{
    ADD("ADD"), AMEND("AMD"), REPEAL("RPLD"), RENAME("RENUM"), DESIGNATE("DESIG");

    static Map<String, LawActionType > lookupMap = Arrays.asList(LawActionType .values())
            .stream().collect(Collectors.toMap(LawActionType::getToken, Function.identity()));

    private String token;
    LawActionType(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static Optional<LawActionType> lookupAction(String action) {
        if (action == null) {
            throw new IllegalArgumentException("Supplied string cannot be null!");
        }
        return Optional.ofNullable(lookupMap.get(action.trim().toUpperCase()));
    }
}
