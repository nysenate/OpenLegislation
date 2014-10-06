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

    /**
     * Constructs using the data within the LawBlock. Extracted fields such as title, document type,
     * and other such fields not present in the LawBlock need to be set afterwards.
     *
     * @param lawBlock LawBlock
     */
    public LawDocument(LawBlock lawBlock) {
        super(lawBlock);
        this.setText(lawBlock.getText().toString());
    }

    /** --- Overrides --- */

    public String toDocString() {
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