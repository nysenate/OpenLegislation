package gov.nysenate.openleg.legislation.member;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.legislation.committee.Chamber;
import org.apache.commons.text.WordUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Person implements Comparable<Person> {
    /** The unique id used to globally identify the person.
     *  This value should only be set after retrieval from the persistence layer. */
    private Integer personId;
    private String fullName = "";
    private String firstName = "";
    private String middleName = "";
    private String lastName = "";
    private String email = "";
    private String prefix = "";
    private String suffix = "";
    private String imgName = "";

    /** --- Constructors --- */

    public Person() {}

    public Person(Integer personId) {
        this.personId = personId;
    }

    // Assumes that the last word is the last name.
    public Person(Integer personId, String fullName, String email, String pref, String imgName) {
        this.personId = personId;
        this.prefix = pref;
        var nameParts = fullName.split(" ");
        setNameFields(fullName, nameParts[nameParts.length - 1]);
        this.email = email;
        this.imgName = imgName;
    }

    public Person(Person other) {
        this.personId = other.personId;
        this.prefix = other.prefix;
        setNameFields(other.fullName, other.lastName);
        this.email = other.email;
        this.imgName = other.imgName;
    }

    /**
     * Updates a Person's fields to be equal to other.
     * @param other to copy from.
     */
    public void updateFromOther(Person other) {
        this.personId = other.getPersonId();
        this.prefix = other.getPrefix();
        setNameFields(other.fullName, other.lastName);
        this.email = other.getEmail();
        this.imgName = other.getImgName();
    }

    /**
     * Sets all the name fields at once, sans the prefix.
     * @param fullName to pull information from.
     * @param mostRecentShortname to identify the last name.
     */
    public void setNameFields(String fullName, String mostRecentShortname) {
        this.fullName = fullName;
        String[] mrsSplit = mostRecentShortname.split(" ");
        // If there is a duplicate last name, it's followed by the first letter of the first name,
        // and potentially of the middle name.
        if (mrsSplit.length > 1 && mrsSplit[mrsSplit.length - 1].matches(fullName.charAt(0) + ".?")) {
            mrsSplit[mrsSplit.length - 1] = "";
        }
        this.lastName = WordUtils.capitalizeFully(String.join(" ", List.of(mrsSplit)).trim());
        fullName = fullName.replaceFirst(lastName, "").replaceAll(" {2,}", " ").trim();

        LinkedList<String> nameParts = new LinkedList<>(List.of(fullName.split(" ")));
        if (nameParts.getLast().matches("[IV]+|Jr.?|Sr.?")) {
            this.suffix = nameParts.removeLast();
        }
        this.firstName = nameParts.removeFirst();
        this.middleName = String.join(" ", nameParts);
    }

    /**
     * A consistent naming convention for image names.
     * This should be used when naming the image for all new legislators.
     * For newer images, this will likely be the same as <code>getImageName</code>, but it may
     * not be the same for older images which had a different naming conventions.
     * @return
     */
    public String getSuggestedImageFileName() {
        return getPersonId() + "_" + getFirstName() + "_" + getLastName() + ".jpg";
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Person other = (Person) obj;
        return Objects.equals(this.personId, other.personId) &&
               Objects.equals(this.prefix, other.prefix) &&
               Objects.equals(this.fullName, other.fullName) &&
               Objects.equals(this.firstName, other.firstName) &&
               Objects.equals(this.middleName, other.middleName) &&
               Objects.equals(this.lastName, other.lastName) &&
               Objects.equals(this.suffix, other.suffix) &&
               Objects.equals(this.email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, fullName, firstName, middleName, lastName, suffix, email);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + personId +
                ", prefix='" + prefix + '\'' +
                ", fullName='" + fullName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", email='" + email + '\'' +
                ", imgName='" + imgName + '\'' +
                '}';
    }

    @Override
    public int compareTo(Person o) {
        return ComparisonChain.start()
                .compare(Optional.ofNullable(this.lastName).orElse(""), Optional.ofNullable(o.lastName).orElse(""))
                .compare(Optional.ofNullable(this.firstName).orElse(""), Optional.ofNullable(o.firstName).orElse(""))
                .compare(Optional.ofNullable(this.middleName).orElse(""), Optional.ofNullable(o.middleName).orElse(""))
                .compare(this.personId, o.personId)
                .result();
    }

    /** --- Basic Getters/Setters --- */

    public Integer getPersonId() {
        return personId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(Chamber mostRecentChamber) {
        this.prefix = mostRecentChamber == Chamber.SENATE ? "Senator" : "Assembly Member";
    }

    public String getSuffix() {
        return suffix;
    }

    /**
     * The name of the image file that represents this Person.
     * If the person does not have an image use the no_image.jpg placeholder.
     * @return
     */
    public String getImgName() {
        return imgName == null || imgName.equals("") ? "no_image.jpg" : imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
