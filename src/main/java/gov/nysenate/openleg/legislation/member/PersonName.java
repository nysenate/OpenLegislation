package gov.nysenate.openleg.legislation.member;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.legislation.committee.Chamber;

/**
 * Used as a field in Person, otherwise the constructor would have 8 String arguments in a row.
 */
public record PersonName(String fullName, String prefix, String firstName, String middleName,
                         String lastName, String suffix) implements Comparable<PersonName> {
    public PersonName(String fullName, Chamber mostRecentChamber, String firstName, String middleName,
                  String lastName, String suffix) {
        this(fullName, mostRecentChamber == Chamber.SENATE ? "Senator" : "Assembly Member",
                firstName, middleName, lastName, suffix);
    }

    @Override
    public int compareTo(PersonName o) {
        return ComparisonChain.start().compare(lastName, o.lastName)
                .compare(firstName, o.firstName).compare(middleName, o.middleName).result();
    }
}
