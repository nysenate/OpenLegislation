package gov.nysenate.openleg.service.slack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SlackAttachment {

    private String fallback;
    private String text;
    private String pretext;
    private String color;

    private String title;
    private String titleLink;

    private List<SlackField> fields;

    public SlackAttachment() {}

    public SlackAttachment(SlackAttachment other) {
        this.fallback = other.fallback;
        this.text = other.text;
        this.pretext = other.pretext;
        this.color = other.color;
        this.title = other.title;
        this.titleLink = other.titleLink;
        this.fields = other.fields != null ? other.fields.stream()
                .map(SlackField::new)
                .collect(Collectors.toList())
                : null;
    }


    public SlackAttachment addFields(SlackField field) {
        if(this.fields == null) {
            this.fields = new ArrayList<SlackField>();
        }

        this.fields.add(field);

        return this;
    }

    private boolean isHex(String pair) {
        return pair.matches("^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }

    private JsonArray prepareFields() {
        JsonArray data = new JsonArray();
        for(SlackField field: fields) {
            data.add(field.toJson());
        }

        return data;
    }

    public SlackAttachment removeFields(Integer index) {
        if(this.fields != null) {
            this.fields.remove(index);
        }

        return this;
    }

    public SlackAttachment setColor(String color) {
        if(color != null) {
            if(color.charAt(0) == '#') {
                if(!isHex(color.substring(1))) {
                    throw new IllegalArgumentException("Invalid Hex Color @ SlackAttachment");
                }
            } else if(!color.matches("^(good|warning|danger)$")) {
                throw new IllegalArgumentException("Invalid PreDefined Color @ SlackAttachment");
            }
        }

        this.color = color;

        return this;
    }

    public SlackAttachment setFallback(String fallback) {
        this.fallback = fallback;

        return this;
    }

    public SlackAttachment setFields(ArrayList<SlackField> fields) {
        this.fields = fields;

        return this;
    }

    public SlackAttachment setPretext(String pretext) {
        this.pretext = pretext;

        return this;
    }

    public SlackAttachment setText(String text) {
        this.text = text;

        return this;
    }

    public SlackAttachment setTitle(String title) {
        this.title = title;

        return this;
    }

    public SlackAttachment setTitleLink(String titleLink) {
        this.titleLink = titleLink;

        return this;
    }

    public JsonObject toJson() {
        JsonObject data = new JsonObject();

        if(fallback == null) {
            throw new IllegalArgumentException("Missing Fallback @ SlackAttachment");
        } else {
            data.addProperty("fallback", fallback);
        }

        if(text != null) {
            data.addProperty("text", text);
        }

        if(pretext != null) {
            data.addProperty("pretext", pretext);
        }

        if(color != null) {
            data.addProperty("color", color);
        }

        if (title != null) {
            data.addProperty("title", title);
        }

        if (titleLink != null) {
            data.addProperty("title_link", titleLink);
        }

        if(fields != null && fields.size() > 0) {
            data.add("fields", prepareFields());
        }

        return data;
    }

}
