package gov.nysenate.openleg.model.law;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;

public class LawInfo implements Serializable, Comparable<LawInfo>
{
    private static final long serialVersionUID = -7614328485103748745L;

    /** The three letter law id. */
    protected String lawId;

    /** The short name that can be used to refer to the law. */
    protected String name;

    /** The chapter number. */
    protected String chapterId;

    /** The type of law. */
    protected LawType type;

    /** --- Constructors --- */

    public LawInfo() {}

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "LawInfo {" + "lawId='" + lawId + '\'' + ", name='" + name + '\'' + ", chapterId='" + chapterId + '\'' +
                ", type=" + type +'}';
    }

    @Override
    public int compareTo(LawInfo o) {
        return ComparisonChain.start().compare(this.getLawId(), o.getLawId()).result();
    }

    /** --- Basic Getters/Setters --- */

    public String getLawId() {
        return lawId;
    }

    public void setLawId(String lawId) {
        this.lawId = lawId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public LawType getType() {
        return type;
    }

    public void setType(LawType type) {
        this.type = type;
    }
}