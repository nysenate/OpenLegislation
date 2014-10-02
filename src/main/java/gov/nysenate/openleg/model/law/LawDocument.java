package gov.nysenate.openleg.model.law;

import gov.nysenate.openleg.processor.law.LawBlock;

public class LawDocument extends LawDocInfo
{
    protected String text;

    /** --- Constructors --- */

    public LawDocument() {}

    public LawDocument(LawDocInfo info, String text) {
        super(info.documentId, info.lawId, info.locationId, info.title, info.docType, info.docTypeId, info.publishedDate);
        this.text = text;
    }

    public LawDocument(LawBlock lawBlock) {
        super(lawBlock);
        this.setText(lawBlock.getText().toString());
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return super.toString() + "\n" + getText();
    }

    /** --- Basic Getters/Setters --- */

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}