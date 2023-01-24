package gov.nysenate.openleg.legislation.member;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.legislation.committee.Chamber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Person implements Comparable<Person> {
    private static final Logger logger = LoggerFactory.getLogger(Person.class);
    private static final String namePattern = "(?i)(?<firstName>%s )(?<middleName>.*)(?<lastName>%s) ?(?<suffix>([IV]+|Jr.?|Sr.?)?)";
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

    // Assumes that the last word is the last name. Used for testing.
    public Person(Integer personId, String fullName, String email, String pref, String imgName) {
        this.personId = personId;
        this.prefix = pref;
        var nameParts = fullName.split(" ");
        setNameFields(fullName, nameParts[nameParts.length - 1], "");
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
     * Sets all the name fields at once, sans the prefix.
     * @param fullName to pull information from.
     * @param mostRecentShortname to identify the last name.
     */
    public void setNameFields(String fullName, String mostRecentShortname, String altFirstName) {
        this.fullName = fullName;
        String firstNamePattern = altFirstName.isBlank() ? "[^ ]+" : altFirstName;
        String tempLastName = getLastName(fullName.charAt(0), mostRecentShortname);
        // Matches a version of the String without accents or diacritics.
        Matcher m = Pattern.compile(namePattern.formatted(firstNamePattern, tempLastName))
                .matcher(Normalizer.normalize(fullName, Normalizer.Form.NFKD)
                        .replaceAll("\\p{M}", ""));
        if (!m.matches()) {
            logger.warn("There is a problem with the name " + fullName);
            return;
        }
        this.firstName = getGroup(m, "firstName");
        this.middleName = getGroup(m, "middleName");
        this.lastName = getGroup(m, "lastName");
        this.suffix = m.group("suffix");
    }

    private String getGroup(Matcher m, String group) {
        return fullName.substring(m.start(group), m.end(group)).trim();
    }

    private static String getLastName(char firstInitial, String mostRecentShortname) {
        // If there is a duplicate last name, it's followed by the first letter of the first name,
        // and potentially of the middle name.
        Matcher m = Pattern.compile("(.*) %c.?".formatted(firstInitial))
                .matcher(mostRecentShortname);
        return m.matches() ? m.group(1) : mostRecentShortname;
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
