package gov.nysenate.openleg.qa.model;

public enum FieldName {
    FULLTEXT("full text"),
    MEMO("memo"),
    SPONSOR("sponsor"),
    COSPONSORS("cosponsors"),
    SUMMARY("summary"),
    TITLE("title"),
    LAW_SECTION("law section"),
    ACTIONS("actions");

    private String text;
    private FieldName(String text) {
        this.text = text;
    }
    public String text() {
        return this.text;
    }
    public static FieldName getFieldName(String text) {
        for(FieldName fieldName:FieldName.values()) {
            if(fieldName.text().equals(text))
                return fieldName;
        }
        return null;
    }
}
