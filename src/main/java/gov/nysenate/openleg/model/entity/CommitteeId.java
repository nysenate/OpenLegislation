package gov.nysenate.openleg.model.entity;

import java.io.Serializable;

public class CommitteeId implements Serializable{
    protected Chamber chamber;
    protected String name;

    public CommitteeId(Chamber chamber, String name){
        if(name==null){
            throw new IllegalArgumentException("Name cannot be null!");
        }
        if(chamber==null) {
            throw new IllegalArgumentException("Chamber cannot be null!");
        }
        this.chamber = chamber;
        this.name = name;
    }

    @Override
    public String toString() {
        return chamber.toString() + "-" + name;
    }

    public Chamber getChamber() {
        return chamber;
    }

    public String getName() {
        return name;
    }
}
