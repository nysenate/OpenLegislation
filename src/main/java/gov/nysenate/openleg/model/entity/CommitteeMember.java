package gov.nysenate.openleg.model.entity;

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
            .compare(this.getSequenceNo(), o.getSequenceNo())
            .result();
    }

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
