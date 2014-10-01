package gov.nysenate.openleg.model.law;

import gov.nysenate.openleg.processor.law.LawBlock;

public class LawDocument extends LawInfo
{
    protected String text;

    /** --- Constructors --- */

    public LawDocument() {}

    public LawDocument(LawBlock lawBlock) {
        super(lawBlock);
        this.setText(lawBlock.getText().toString());
    }

    /** --- Basic Getters/Setters --- */

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}