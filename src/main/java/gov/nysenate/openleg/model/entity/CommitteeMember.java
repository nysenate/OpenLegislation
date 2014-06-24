package gov.nysenate.openleg.model.entity;

public class CommitteeMember
{
    /** A number that indicates the position this member should appear in listings. */
    protected int sequenceNo;

    /** The name of the member. */
    protected String name;

    /** The title of the member, e.g Chairperson, Vice-Chair. */
    protected CommitteeMemberTitle title;

    /** A tag for which list the member goes in, e.g. Majority, Minority. */
    protected String memberList;

    /** --- Constructors --- */

    public CommitteeMember() {}

    /** --- Basic Getters/Setters --- */

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommitteeMemberTitle getTitle() {
        return title;
    }

    public void setTitle(CommitteeMemberTitle title) {
        this.title = title;
    }

    public String getMemberList() {
        return memberList;
    }

    public void setMemberList(String memberList) {
        this.memberList = memberList;
    }
}
