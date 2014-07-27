package gov.nysenate.openleg.model.entity;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;

public class CommitteeId implements Serializable, Comparable<CommitteeId>
{
    private static final long serialVersionUID = -4463517719215556844L;

    protected Chamber chamber;
    protected String name;

    /* --- Constructors --- */

    public CommitteeId(Chamber chamber, String name) {
        if(name == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }
        if(chamber == null) {
            throw new IllegalArgumentException("Chamber cannot be null!");
        }
        this.chamber = chamber;
        this.name = name;
    }

    /* --- Overrides --- */

    @Override
    public String toString() {
        return chamber.toString() + "-" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommitteeId)) return false;
        CommitteeId that = (CommitteeId) o;
        if (chamber != that.chamber) return false;
        if (!name.equals(that.name)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = chamber.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public int compareTo(CommitteeId o) {
        return ComparisonChain.start()
            .compare(this.getChamber().name(), o.getChamber().name())
            .compare(this.getName(), o.getName())
            .result();
    }

    /* --- Basic Getters/Setters --- */

    public Chamber getChamber() {
        return chamber;
    }

    public String getName() {
        return name;
    }
}
