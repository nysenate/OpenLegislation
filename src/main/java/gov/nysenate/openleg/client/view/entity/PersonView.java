package gov.nysenate.openleg.client.view.entity;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.entity.Person;

public class PersonView implements ViewObject
{
    protected String fullName;
    protected String prefix;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String suffix;

    public PersonView(Person person) {
        if (person != null) {
            this.fullName = person.getFullName();
            this.prefix = person.getPrefix();
            this.firstName = person.getFirstName();
            this.middleName = person.getMiddleName();
            this.lastName = person.getLastName();
            this.suffix = person.getSuffix();
        }
    }

    public String getFullName() {
        return fullName;
    }

    public String getPrefix() {
        return prefix;
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

    public String getSuffix() {
        return suffix;
    }

    @Override
    public String getViewType() {
        return "person";
    }
}
