package gov.nysenate.openleg.model.updates;

import java.time.LocalDateTime;
import java.util.Map;

public class UpdateDigest<ContentId> extends UpdateToken<ContentId> {

    private String action;
    private String table;
    private Map<String, String> fields;

    /** --- Constructors --- */

    public UpdateDigest(UpdateToken<ContentId> token) {
        super(token.id, token.contentType, token.sourceId, token.sourceDateTime, token.processedDateTime);
    }

    public UpdateDigest(ContentId id, UpdateContentType contentType, String sourceId, LocalDateTime sourceDateTime, LocalDateTime processedDateTime) {
        super(id, contentType, sourceId, sourceDateTime, processedDateTime);
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

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
