package gov.nysenate.openleg.model;


public class Person {
    private String position = "";

    private String fullname = "";

    private String id = "";

    private String branch = "";

    private String contactInfo = "";

    private String guid = "";

    public Person () {

    }

    public Person (String fullname) {
        this.fullname = fullname;
    }

    public Person (String fullname, String position) {
        this.fullname = fullname;
        this.position = position;

        this.id = fullname + '-' + position;
    }

    public String getPosition() {
        return position;
    }

    public String getFullname() {
        return fullname;
    }

    public String getId() {
        return id;
    }

    public String getBranch() {
        return branch;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getGuid() {
        return guid;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Person)
        {
            Person person = (Person)obj;
            return this.fullname.equals(person.getFullname());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.fullname;
    }
}
