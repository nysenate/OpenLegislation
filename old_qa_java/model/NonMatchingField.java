package gov.nysenate.openleg.qa.model;


public class NonMatchingField {
    String field;
    String openField;
    String lbdcField;

    public NonMatchingField() {

    }

    public NonMatchingField(FieldName fieldName, String openField, String lbdcField) {
        this.field = fieldName.text();
        this.openField = openField;
        this.lbdcField = lbdcField;
    }

    public String getField() {
        return field;
    }

    public String getOpenField() {
        return openField;
    }

    public String getLbdcField() {
        return lbdcField;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setOpenField(String openField) {
        this.openField = openField;
    }

    public void setLbdcField(String lbdcField) {
        this.lbdcField = lbdcField;
    }

    @Override
    public String toString() {
        return "";
    }
}
