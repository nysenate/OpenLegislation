package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.entity.Chamber;

import static gov.nysenate.openleg.model.entity.Chamber.ASSEMBLY;
import static gov.nysenate.openleg.model.entity.Chamber.SENATE;

public enum BillType
{
    S(SENATE, "Senate", false),
    J(SENATE, "Regular and Joint", true),
    B(SENATE, "Concurrent", true),
    R(SENATE, "Rules and Extraordinary Session", true),

    A(ASSEMBLY, "Assembly", false),
    K(ASSEMBLY, "Regular", true),
    C(ASSEMBLY, "Concurrent", true),
    E(ASSEMBLY, "Rules and Extraordinary Session", true),
    L(ASSEMBLY, "Joint", true);

    private Chamber chamber;
    private String name;
    private boolean resolution;

    BillType(Chamber chamber, String name, boolean resolution) {
        this.chamber = chamber;
        this.name = name;
        this.resolution = resolution;
    }

    public Chamber getChamber() {
        return chamber;
    }

    public String getName() {
        return name;
    }

    public boolean isResolution() {
        return resolution;
    }
}