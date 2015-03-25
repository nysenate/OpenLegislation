package gov.nysenate.openleg.client.view.updates;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import org.apache.commons.lang3.text.WordUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UpdateDigestView extends UpdateTokenView implements ViewObject
{
    protected String action;
    protected String scope;
    protected Map<String, String> fields;

    public UpdateDigestView(UpdateDigest<?> updateDigest, ViewObject idView) {
        super(updateDigest, idView);
        if (updateDigest != null) {
            this.action = (updateDigest.getAction() != null) ? WordUtils.capitalizeFully(updateDigest.getAction())
                                                             : updateDigest.getAction();
            this.scope = WordUtils.capitalizeFully(updateDigest.getTable().replaceAll("_", " "));
            this.fields = new HashMap<>();
            if (updateDigest.getFields() != null) {
                for (String key : updateDigest.getFields().keySet()) {
                    this.fields.put(
                        WordUtils.capitalizeFully(key, new char[]{'_'}).replace("_", " "), updateDigest.getFields().get(key));
                }
            }
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
}
