package gov.nysenate.openleg.model.entity;

import com.google.common.collect.ComparisonChain;

import java.util.Objects;
import java.util.Optional;

public class Person implements Comparable<Person>
{
    /** The unique id used to globally identify the person.
     *  This value should only be set after retrieval from the persistence layer. */
    private Integer personId;

    /** The prefix (Mr, Mrs, Senator, etc) */
    private String prefix = "";

    /** The full name of the person. */
    private String fullName = "";

    /** The first name of the person. */
    private String firstName = "";

    /** The middle name of the person. */
    private String middleName = "";

    /** The last name of the person. */
    private String lastName = "";

    /** The suffix of the person (Jr, Sr, etc) */
    private String suffix = "";

    /** The email address of the person. */
    private String email = "";

    /** The name of the image for this person. */
    private String imgName = "";

    /** True if this person has been manually verified */
    protected boolean verified;

    /** --- Constructors --- */

    public Person () {}

    public Person(Integer personId) {
        this.personId = personId;
    }

    public Person (String fullName) {
        this.fullName = fullName;
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
        this.verified = other.verified;
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

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}