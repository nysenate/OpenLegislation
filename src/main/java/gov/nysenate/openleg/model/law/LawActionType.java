package gov.nysenate.openleg.model.law;

import com.google.common.collect.Sets;

import java.util.*;

public enum LawActionType
{
    ADD(Sets.newHashSet("ADD")),
    AMEND(Sets.newHashSet("AMD", "AMDS")),
    REPEAL(Sets.newHashSet("RPLD", "RPL")),
    RENAME(Sets.newHashSet("REN", "REL", "RELET")),
    DESIGNATE(Sets.newHashSet("DESIG")),
    REDESIGNATE(Sets.newHashSet("REDESIG", "REDES")),
    RENUMERATE(Sets.newHashSet("RENUM")),
    REPEAL_ADD(Sets.newHashSet("RPLDADD")),
    // rename §126 to be §127 -> RENAME 126, REN_TO 127
    REN_TO(Sets.newHashSet("RENTO"));

    private static Map<String, LawActionType> lookupMap = new HashMap<>();
    static {
        Arrays.stream(values())
                .forEach(action -> action.getTokens()
                        .forEach(token -> {
                            if (token != null && !token.trim().isEmpty())
                                lookupMap.put(token.toUpperCase().trim(), action);
                        }));
    }
    private Set<String> tokens;
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
