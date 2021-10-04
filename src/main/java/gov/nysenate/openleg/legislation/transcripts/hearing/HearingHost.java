package gov.nysenate.openleg.legislation.transcripts.hearing;

import gov.nysenate.openleg.legislation.committee.Chamber;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contains data about what group is hosting a public hearing.
 */
// TODO: Convert to record in Java 16.
public class HearingHost {
    private static final String IRRELEVANT_TEXT = "^(\\s?(ON|FOR|THE|AND|,))+|((,|THE|AND|;)\\s?)+$";
    private final Chamber chamber;
    private final HearingHostType type;
    private final String name;

    public HearingHost(Chamber chamber, HearingHostType type, String name) {
        this.chamber = chamber;
        this.type = type;
        this.name = standardizeName(name);
    }

    public static List<HearingHost> getHosts(String typeStr, String rawName, Chamber chamber) {
        var hosts = new ArrayList<HearingHost>();
        // Denotes a joint host.
        if (chamber == null) {
            hosts.addAll(getHosts(typeStr, rawName, Chamber.SENATE));
            hosts.addAll(getHosts(typeStr, rawName, Chamber.ASSEMBLY));
            return hosts;
        }
        var type = HearingHostType.toType(typeStr);
        var standardizedText = standardizeName(rawName);
        // When there is a list of committees, sometimes only the first is marked as a committee,
        // and the names are seperated by semicolons.
        String[] hostTexts = standardizedText.split(";");
        for (var hostText : hostTexts) {
            var host = new HearingHost(chamber, type, hostText);
            hosts.add(host);
        }
        return hosts;
    }

    /**
     * We want HearingHosts that refer to the same committee, task force, etc. to be equal.
     * But, different hearings will format the names slightly differently.
     * @param name to remove unnecessary data from.
     * @return the standard form of the name;
     */
    private static String standardizeName(String name) {
        return name.toUpperCase().replaceAll("\\s+", " ").replaceAll(", AND| &", " AND")
                .replaceAll(IRRELEVANT_TEXT, "").trim();
    }

    /** --- Basic Getters/Setters --- */

    public Chamber getChamber() {
        return chamber;
    }

    public HearingHostType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HearingHost that = (HearingHost) o;
        return chamber == that.chamber && type == that.type && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chamber, type, name);
    }

    @Override
    public String toString() {
        return "HearingHost{chamber=" + chamber + ", type=" + type +
                ", name='" + name + '\'' + '}';
    }
}
