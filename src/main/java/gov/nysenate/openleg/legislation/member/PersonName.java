package gov.nysenate.openleg.legislation.member;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.common.util.NonNullList;

import java.util.stream.Collectors;

/**
 * Used as a field in Person, otherwise the constructor would have a ton of String arguments in a row.
 */
public record PersonName(String firstName, String middleName,
                         String lastName, String suffix) implements Comparable<PersonName> {
    @Override
    public int compareTo(PersonName o) {
        return ComparisonChain.start().compare(lastName, o.lastName)
                .compare(firstName, o.firstName).compare(middleName, o.middleName).result();
    }

    public String fullName() {
        return NonNullList.of(firstName, middleName, lastName, suffix).stream()
                .collect(Collectors.joining(" "))
                .replaceAll(" {2,}", " ").trim();
    }
}
