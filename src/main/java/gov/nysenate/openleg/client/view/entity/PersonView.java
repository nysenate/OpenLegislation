package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.entity.Person;

public class PersonView implements ViewObject
{
    protected int personId;
    protected String fullName;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String email;
    protected String prefix;
    protected String suffix;
    protected boolean verified;
    protected String imgName;

    public PersonView(Person person) {
        if (person != null) {
            this.personId = person.getPersonId();
            this.fullName = person.getFullName();
            this.firstName = person.getFirstName();
            this.middleName = person.getMiddleName();
            this.lastName = person.getLastName();
            this.prefix = person.getPrefix();
            this.suffix = person.getSuffix();
            this.email = person.getEmail();
            this.verified = person.isVerified();
            this.imgName = person.getImgName();
        }
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
