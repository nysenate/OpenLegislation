package gov.nysenate.openleg.legislation.member;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.common.util.RegexUtils;
import gov.nysenate.openleg.legislation.committee.Chamber;

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

    // Assumes a normal last name with no suffix. Used for testing.
    public Person(Integer personId, String fullName, String email, String pref, String imgName) {
        this.personId = personId;
        this.prefix = pref;
        String[] name_split = fullName.split(" ");
        if (name_split.length == 3) {
            this.middleName = name_split[1];
            name_split = new String[]{name_split[0], name_split[2]};
        }
        this.firstName = name_split[0];
        this.lastName = name_split[1];
        this.fullName = fullName;
        this.email = email;
        this.imgName = imgName;
    }

    public Person(Person other) {
        this.personId = other.personId;
        this.fullName = other.fullName;
        this.prefix = other.prefix;
        this.firstName = other.firstName;
        this.middleName = other.middleName;
        this.lastName = other.lastName;
        this.suffix = other.suffix;
        this.email = other.email;
        this.imgName = other.imgName;
    }

    /**
     * Updates a Person's fields to be equal to other.
     * @param other to copy from.
     */
    public void updateFromOther(Person other) {
        this.personId = other.personId;
        this.fullName = other.fullName;
        this.prefix = other.prefix;
        this.firstName = other.firstName;
        this.middleName = other.middleName;
        this.lastName = other.lastName;
        this.suffix = other.suffix;
        this.email = other.email;
        this.imgName = other.imgName;
    }

    /**
     * A consistent naming convention for image names.
     * This should be used when naming the image for all new legislators.
     * For newer images, this will likely be the same as <code>getImageName</code>, but it may
     * not be the same for older images which had a different naming conventions.
     * @return
     */
    public String getSuggestedImageFileName() {
        String temp = getPersonId() + "_" + getFirstName() + "_" + getLastName() + ".jpg";
        return RegexUtils.removeAccentedCharacters(temp);
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

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(Chamber mostRecentChamber) {
        this.prefix = mostRecentChamber == Chamber.SENATE ? "Senator" : "Assembly Member";
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
