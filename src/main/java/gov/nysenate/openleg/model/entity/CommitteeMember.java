package gov.nysenate.openleg.model.entity;

import java.util.Comparator;

public class CommitteeMember
{
    /** A number that indicates the position this member should appear in listings. */
    protected int sequenceNo;

    /** The member. */
    protected Member member;

    /** The title of the member, e.g Chairperson, Vice-Chair. */
    protected CommitteeMemberTitle title;

    /** True if the member is part of the current majority */
    protected boolean majority;

    /** --- Operators --- */

    public static final Comparator<CommitteeMember> BY_SEQUENCE_NO =
        new Comparator<CommitteeMember>() {
            @Override
            public int compare(CommitteeMember left, CommitteeMember right) {
                if(left.getSequenceNo() == right.getSequenceNo()){
                    return 0;
                }else if (left.getSequenceNo() > right.getSequenceNo()){
                    return 1;
                }else{
                    return -1;
                }
            }
        };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitteeMember that = (CommitteeMember) o;

        if (majority != that.majority) return false;
        //if (sequenceNo != that.sequenceNo) return false;
        if (!member.equals(that.member)) return false;
        if (title != that.title) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sequenceNo;
        result = 31 * result + member.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + (majority ? 1 : 0);
        return result;
    }

    /** --- Constructors --- */

    public CommitteeMember() {}

    public CommitteeMember(CommitteeMember other) {
        this.sequenceNo = other.sequenceNo;
        this.member = new Member(other.member);
        this.title = other.title;
        this.majority = other.majority;
    }

    /** --- Basic Getters/Setters --- */

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public CommitteeMemberTitle getTitle() {
        return title;
    }

    public void setTitle(CommitteeMemberTitle title) {
        this.title = title;
    }

    public boolean isMajority() {
        return majority;
    }

    public void setMajority(boolean majority) {
        this.majority = majority;
    }

}
