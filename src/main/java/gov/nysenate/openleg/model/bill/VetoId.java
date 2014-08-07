package gov.nysenate.openleg.model.bill;

import com.google.common.collect.ComparisonChain;

import java.io.Serializable;

public class VetoId implements Serializable, Comparable<VetoId>
{
    private static final long serialVersionUID = 6631006116248868890L;

    /** The year that the veto was signed */
    private int year;

    /** The id number for the veto */
    private int vetoNumber;

    /* --- Constructors --- */

    public VetoId(int year, int vetoNumber) {
        this.year = year;
        this.vetoNumber = vetoNumber;
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VetoId vetoId = (VetoId) o;

        if (vetoNumber != vetoId.vetoNumber) return false;
        if (year != vetoId.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + vetoNumber;
        return result;
    }

    @Override
    public int compareTo(VetoId o) {
        return ComparisonChain.start()
            .compare(this.year, o.year)
            .compare(this.vetoNumber, o.vetoNumber)
            .result();
    }

    @Override
    public String toString() {
        return year + "-" + vetoNumber;
    }

    /* --- Basic Getters/Setters --- */

    public int getYear() {
        return year;
    }

    public int getVetoNumber() {
        return vetoNumber;
    }
}