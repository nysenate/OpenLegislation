package gov.nysenate.openleg.legislation.law;

import java.util.*;

public enum LawActionType
{
    ADD(Set.of("ADD")),
    AMEND(Set.of("AMD", "AMDS")),
    REPEAL(Set.of("RPLD", "RPL")),
    RELETTER(Set.of("REL", "RELET")),
    DESIGNATE(Set.of("DESIG")),
    REDESIGNATE(Set.of("REDESIG", "REDES")),
    RENUMBER(Set.of("REN", "RENUM")),
    REPEAL_ADD(Set.of("RPLDADD")),
    // renumber §126 to be §127 -> RENUMBER 126, REN_TO 127
    REN_TO(Set.of("RENTO"));

    private static final Map<String, LawActionType> lookupMap = new HashMap<>();
    static {
        Arrays.stream(values())
                .forEach(action -> action.getTokens()
                        .forEach(token -> {
                            if (token != null && !token.trim().isEmpty())
                                lookupMap.put(token.toUpperCase().trim(), action);
                        }));
    }
    private final Set<String> tokens;
    LawActionType(Set<String> tokens) {
        this.tokens = tokens;
    }

    public Set<String> getTokens() {
        return tokens;
    }

    public static Optional<LawActionType> lookupAction(String action) {
        if (action == null)
            throw new IllegalArgumentException("Supplied string cannot be null!");
        return Optional.ofNullable(lookupMap.get(action.trim().toUpperCase()));
    }

    /**
     * Compares the current enum to another LawAction in String form.
     * @param other to compare.
     * @return if they are equal.
     */
    public boolean compareToString(String other) {
        Optional<LawActionType> o = lookupAction(other);
        return o.isPresent() && o.get() == this;
    }
}
