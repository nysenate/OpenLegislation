package gov.nysenate.openleg.model.updates;

import java.time.LocalDateTime;
import java.util.Map;

public class UpdateDigest<ContentId> extends UpdateToken<ContentId> {

    private String action;
    private String table;
    private Map<String, String> updates;
    private String sourceDataId;
    private LocalDateTime sourceDataDateTime;

    /** --- Constructors --- */

    public UpdateDigest(ContentId id, LocalDateTime updatedDateTime) {
        super(id, updatedDateTime);
    }

    /** --- Basic Getters / Setters */

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
