package gov.nysenate.openleg.legislation.member;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.legislation.committee.Chamber;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Person implements Comparable<Person>
{
    /** The unique id used to globally identify the person.
     *  This value should only be set after retrieval from the persistence layer. */
    private Integer personId;

    /** The full name of the person. */
    private String fullName = "";

    /** The first name of the person. */
    private String firstName = "";

    /** The middle name of the person. */
    private String middleName = "";

    /** The last name of the person. */
    private String lastName = "";

    /** The email address of the person. */
    private String email = "";

    /** The prefix (Mr, Mrs, Senator, etc) */
    private String prefix = "";

    /** The suffix of the person (Jr, Sr, etc) */
    private String suffix = "";

    /** The name of the image for this person. */
    private String imgName = "";

    /** --- Constructors --- */

    public Person () {}

    public Person(Integer personId) {
        this.personId = personId;
    }

    public Person(String fullName) {
        setNameFields(fullName);
    }

    public Person(Integer personId, String fullName, String email, String pref, String imgName) {
        this.personId = personId;
        this.prefix = pref;
        setNameFields(fullName);
        this.email = email;
        this.imgName = imgName;
    }

    public Person(Person other) {
        this.personId = other.personId;
        this.prefix = other.prefix;
        setNameFields(other.fullName);
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
        setNameFields(other.fullName);
        this.email = other.getEmail();
        this.imgName = other.getImgName();
    }

    public void setNameFields(String fullName) {
        this.fullName = fullName;
        LinkedList<String> nameParts = new LinkedList<>(List.of(fullName.split(" ")));
        if (nameParts.getLast().matches("[IV]+|Jr.?|Sr.?")) {
            this.suffix = nameParts.removeLast();
        }
        if (nameParts.size() == 3) {
            this.middleName = nameParts.remove(1);
        }
        this.firstName = nameParts.removeFirst();
        this.lastName = nameParts.removeLast();
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
     *
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
