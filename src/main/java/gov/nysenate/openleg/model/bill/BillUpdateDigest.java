package gov.nysenate.openleg.model.bill;

import java.time.LocalDateTime;
import java.util.Map;

public class BillUpdateDigest extends BillUpdateToken
{
    private String action;
    private String table;
    private Map<String, String> updates;
    private String sourceDataId;
    private LocalDateTime sourceDataDateTime;

    /** --- Constructors --- */

    public BillUpdateDigest(BaseBillId billId, LocalDateTime updatedDateTime) {
        super(billId, updatedDateTime);
    }

    /** --- Basic Getters/Setters --- */

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, String> getUpdates() {
        return updates;
    }

    public void setUpdates(Map<String, String> updates) {
        this.updates = updates;
    }

    public String getSourceDataId() {
        return sourceDataId;
    }

    public void setSourceDataId(String sourceDataId) {
        this.sourceDataId = sourceDataId;
    }

    public LocalDateTime getSourceDataDateTime() {
        return sourceDataDateTime;
    }

    public void setSourceDataDateTime(LocalDateTime sourceDataDateTime) {
        this.sourceDataDateTime = sourceDataDateTime;
    }
}
