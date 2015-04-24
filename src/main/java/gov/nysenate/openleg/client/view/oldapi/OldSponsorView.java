package gov.nysenate.openleg.client.view.oldapi;

import java.io.Serializable;

public class OldSponsorView implements Serializable {

    private static final long serialVersionUID = 833455639411330261L;

    String fullname;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
