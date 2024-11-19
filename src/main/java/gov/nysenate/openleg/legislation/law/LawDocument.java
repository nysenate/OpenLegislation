package gov.nysenate.openleg.legislation.law;

import gov.nysenate.openleg.processors.law.LawBlock;

public class LawDocument extends LawDocInfo {
    protected String text;

    /** --- Constructors --- */

    public LawDocument() {}

    public LawDocument(LawDocInfo info, String text) {
        super(info);
        // TODO: probably can be fixed with better encoding
        this.text = text.replace("├Á", "§");
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

    /** --- Basic Getters/Setters --- */

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}