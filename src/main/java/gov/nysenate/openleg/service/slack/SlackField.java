package gov.nysenate.openleg.service.slack;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SlackField {

    private List<String> allowMarkdown = null;
    private boolean shorten = false;
    private String title = null;
    private String value = null;

    public SlackField() {}

    public SlackField(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public SlackField(SlackField other) {
        this.title = other.title;
        this.value = other.value;
        this.shorten = other.shorten;
        this.allowMarkdown = other.allowMarkdown != null ? new ArrayList<>(other.allowMarkdown) : null;
    }

    public void addAllowedMarkdown(String field) {
        if(this.allowMarkdown == null) {
            this.allowMarkdown = new ArrayList<String>();
        }

        if(field.matches("^(pretext|text|title|fields|fallback)$")) {
            this.allowMarkdown.add(field);
        } else {
            throw new IllegalArgumentException(field + " is not allowed. Allowed: pretext, text, title, fields and fallback");
        }
    }

    public boolean isShorten() {
        return shorten;
    }

    private JsonArray prepareMarkdown() {
        JsonArray data = new JsonArray();
        for(String item: this.allowMarkdown) {
            data.add(new JsonPrimitive(item));
        }

        return data;
    }

    public void setAllowedMarkdown(ArrayList<String> allowMarkdown) {
        if(allowMarkdown != null) {
            this.allowMarkdown = allowMarkdown;
        }
    }

    public void setShorten(boolean shorten) {
        this.shorten = shorten;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JsonObject toJson() {
        JsonObject data = new JsonObject();
        data.addProperty("title", title);
        data.addProperty("value", value);
        data.addProperty("short", shorten);
        if(allowMarkdown != null && allowMarkdown.size() > 0) {
            data.add("mrkdwn_in", prepareMarkdown());
        }

        return data;
    }

}
