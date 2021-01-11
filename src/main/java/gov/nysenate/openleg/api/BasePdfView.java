package gov.nysenate.openleg.api;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public abstract class BasePdfView {
    public static final float FONT_SIZE = 12f;
    public static final PDFont FONT = PDType1Font.COURIER;
    protected static final float TOP = 740f;
    protected static final float BOTTOM = 60f;
}
