package gov.nysenate.openleg.model.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.util.Comparator;

public class CommitteeMember implements Serializable, Comparable<CommitteeMember>
{
    private static final long serialVersionUID = -3988868068553499472L;

    /** A number that indicates the position this member should appear in listings. */
    protected int sequenceNo;

    /** The member. */
    protected SessionMember member;

    /** The title of the member, e.g Chairperson, Vice-Chair. */
    protected CommitteeMemberTitle title;

    /** True if the member is part of the current majority */
    // TODO Add party data to members before expressing this in the view
    protected boolean majority;

    /** --- Overrides --- */

    @Override
    public int compareTo(CommitteeMember o) {
        return ComparisonChain.start()
                .compare(this.sequenceNo, o.sequenceNo)
                .compare(this.member, o.member)
                .compare(this.title, o.title)
                .compareTrueFirst(this.majority, o.majority)
                .result();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommitteeMember)) return false;
        CommitteeMember that = (CommitteeMember) o;
        return sequenceNo == that.sequenceNo &&
                majority == that.majority &&
                Objects.equal(member, that.member) &&
                title == that.title;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sequenceNo, member, title, majority);
    }

    /** --- Constructors --- */

    public CommitteeMember() {}

    public CommitteeMember(CommitteeMember other) {
        this.sequenceNo = other.sequenceNo;
        this.member = new SessionMember(other.member);
        this.title = other.title;
        this.majority = other.majority;
    }

    /** --- Functional Getters/Setters --- */

    public static Comparator<CommitteeMember> getComparator() {
        return (l,r) -> l.compareTo(r);
    }

    /** --- Basic Getters/Setters --- */

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public SessionMember getMember() {
        return member;
    }

    public void setMember(SessionMember member) {
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
