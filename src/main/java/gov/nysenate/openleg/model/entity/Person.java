package gov.nysenate.openleg.model.entity;

import java.util.Objects;

public class Person
{
    /** The unique id used to globally identify the person.
     *  This value should only be set after retrieval from the persistence layer. */
    private Integer id;

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

    /** --- Constructors --- */

    public Person () {}

    public Person(Integer id) {
        this.id = id;
    }

    public Person (String fullName) {
        this.fullName = fullName;
    }

    public Person(Person other) {
        this.id = other.id;
        this.prefix = other.prefix;
        this.fullName = other.fullName;
        this.firstName = other.firstName;
        this.middleName = other.middleName;
        this.lastName = other.lastName;
        this.suffix = other.suffix;
        this.email = other.email;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Person other = (Person) obj;
        return Objects.equals(this.prefix, other.prefix) &&
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
                "id=" + id +
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

    /** --- Basic Getters/Setters --- */

    public int getId() {
        return id;
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

    public void setId(Integer id) {
        this.id = id;
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
}