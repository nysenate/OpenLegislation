package gov.nysenate.openleg.api.legislation.member.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.member.Person;

public class PersonView implements ViewObject {
    protected int personId;
    protected String fullName;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String email;
    protected String prefix;
    protected String suffix;
    // The makeshift member logic has been removed so there are never unverified members anymore.
    // Leaving 'verified' here for API backwards compatibility.
    protected boolean verified = true;
    protected String imgName;

    public PersonView(Person person) {
        if (person == null)
            return;
        this.personId = person.personId();
        this.email = person.email();
        this.imgName = person.imgName();
        var name = person.name();
        if (name == null)
            return;
        this.fullName = name.fullName();
        this.firstName = name.firstName();
        this.middleName = name.middleName();
        this.lastName = name.lastName();
        this.prefix = name.prefix();
        this.suffix = name.suffix();
    }

    public int getPersonId() {
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

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getImgName() {
        return imgName;
    }

    @Override
    public String getViewType() {
        return "person";
    }
}
