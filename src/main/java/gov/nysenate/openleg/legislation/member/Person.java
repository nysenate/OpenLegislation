package gov.nysenate.openleg.legislation.member;

import com.google.common.collect.ComparisonChain;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record Person(Integer personId, PersonName name, String email, String imgName)
        implements Comparable<Person> {
    public Person(Integer personId, PersonName name, String email, String imgName) {
        this.personId = personId;
        this.name = name;
        this.email = email;
        this.imgName = StringUtils.isEmpty(imgName) ? "no_image.jpg" : imgName;
    }
    /**
     * A consistent naming convention for image names.
     * This should be used when naming the image for all new legislators.
     * For newer images, this will likely be the same as <code>getImageName</code>, but it may
     * not be the same for older images which had a different naming conventions.
     */
    public String getSuggestedImageFileName() {
        String temp = getPersonId() + "_" + getFirstName() + "_" + getLastName() + ".jpg";
        return RegexUtils.removeAccentedCharacters(temp);
    }

    /** --- Overrides --- */

    // TODO: are these overrides needed?
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Person other = (Person) obj;
        return Objects.equals(this.personId, other.personId) &&
               Objects.equals(this.name, other.name) &&
               Objects.equals(this.email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }

    @Override
    public int compareTo(Person o) {
        return ComparisonChain.start().compare(this.name, o.name)
                .compare(this.personId, o.personId).result();
    }
}
