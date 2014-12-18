package gov.nysenate.openleg.client.view.updates;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import org.apache.commons.lang3.text.WordUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UpdateDigestView implements ViewObject
{
    protected String action;
    protected String scope;
    protected Map<String, String> fields;
    protected LocalDateTime updatedOn;
    protected String sourceId;
    protected LocalDateTime sourceDateTime;

    public UpdateDigestView(UpdateDigest<?> updateDigest) {
        if (updateDigest != null) {
            this.action = updateDigest.getAction();
            this.scope = WordUtils.capitalizeFully(updateDigest.getTable().replaceAll("_", " "));
            this.fields = new HashMap<>();
            for (String key : updateDigest.getUpdates().keySet()) {
                // Camel case the keys
                this.fields.put(
                        (key.contains("_"))
                                ? key.substring(0, 1) + WordUtils.capitalizeFully(key, new char[]{'_'}).replace("_", "").substring(1)
                                : key,
                        updateDigest.getUpdates().get(key));
            }
            this.updatedOn = updateDigest.getUpdatedDateTime();
            this.sourceId = updateDigest.getSourceDataId();
            this.sourceDateTime = updateDigest.getSourceDataDateTime();
        }
    }

    @Override
    public String getViewType() {
        return "update-digest";
    }

    public String getAction() {
        return action;
    }

    public String getScope() {
        return scope;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public String getSourceId() {
        return sourceId;
    }

    public LocalDateTime getSourceDateTime() {
        return sourceDateTime;
    }
}
