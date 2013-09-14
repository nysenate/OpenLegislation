package gov.nysenate.openleg.model;

import java.util.HashMap;

public class Result {

    public String otype;
    public String oid;
    public String title;
    public String summary;
    public long lastModified;
    public boolean active;
    public String data;
    public IBaseObject object;
    public HashMap<String,String> fields;

    public Result(String otype, String data, String oid, long lastModified, boolean active, HashMap<String,String> fields) {
        this.otype = otype;
        this.data = data;
        this.oid  = oid;
        this.lastModified = lastModified;
        this.active = active;
        this.fields = fields;
    }

    public String getOtype() {
        return otype;
    }

    public String getOid() {
        return oid;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public long getLastModified() {
        return lastModified;
    }

    public boolean isActive() {
        return active;
    }

    public String getData() {
        return data;
    }

    public IBaseObject getObject() {
        return object;
    }

    public HashMap<String, String> getFields() {
        return fields;
    }

    public void setOtype(String otype) {
        this.otype = otype;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setObject(IBaseObject object) {
        this.object = object;
    }

    public void setFields(HashMap<String, String> fields) {
        this.fields = fields;
    }
}
