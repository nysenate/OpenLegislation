package gov.nysenate.openleg.model.bill;

import java.io.Serializable;

public class ProgramInfo implements Serializable {

    private static final long serialVersionUID = -6700923863890785276L;

    /** Program info text */
    private String info;

    /** Some strange number associated with the program info TODO figure out what it means */
    private int number;

    public ProgramInfo(String info, int number) {
        this.info = info;
        this.number = number;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
