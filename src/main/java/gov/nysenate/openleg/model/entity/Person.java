package gov.nysenate.openleg.model.entity;

import com.google.common.collect.ComparisonChain;

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

    public Person (String fullName) {
        this.fullName = fullName.trim();
    }

    public Person(Integer personId, String fullName, String firstName, String middleName, String
            lastName, String email, String pref, String suffix, String imgName) {
        this.personId = personId;
        this.fullName = fullName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.prefix = pref;
        this.suffix = suffix;
        this.imgName = imgName;
    }

    public Person(Person other) {
        this.personId = other.personId;
        this.prefix = other.prefix;
        this.fullName = other.fullName;
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
        this.personId = other.getPersonId();
        this.prefix = other.getPrefix();
        this.fullName = other.getFullName();
        this.firstName = other.getFirstName();
        this.middleName = other.getMiddleName();
        this.lastName = other.getLastName();
        this.suffix = other.getSuffix();
        this.email = other.getEmail();
        this.imgName = other.getImgName();
    }

    /**
     * A consistent naming convention for image names.
     *
     * This should be used when naming the image for all new legislators.
     *
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

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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
