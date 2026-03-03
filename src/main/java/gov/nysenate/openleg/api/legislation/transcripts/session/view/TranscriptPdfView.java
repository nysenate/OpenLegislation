package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.api.legislation.transcripts.AbstractTranscriptPdfView;
import gov.nysenate.openleg.api.legislation.transcripts.session.TranscriptBaseCtrl;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.transcripts.session.InvalidLinkTypeEx;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.processors.transcripts.session.Stenographer;

import java.io.IOException;
import java.util.List;

/**
 * Pdf representation of a transcript designed to match the formatting
 * of the official transcripts.
 */
public class TranscriptPdfView extends AbstractTranscriptPdfView {
    private static final int STENOGRAPHER_LINE_NUM = 27;
    private final String stenographer;
    private final float stenographerCenter;

    public TranscriptPdfView(Transcript transcript, String linkTypeStr, OpenLegEnvironment env) throws IOException, InvalidLinkTypeEx {
        if (transcript == null)
            throw new IllegalArgumentException("Supplied transcript cannot be null when converting to pdf.");

        TranscriptBaseCtrl.TranscriptLinkType linkType;
        try {
            linkType = TranscriptBaseCtrl.TranscriptLinkType.fromString(linkTypeStr);
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidLinkTypeEx(linkTypeStr);
        }

        List<List<String>> pages;
        if (linkType == TranscriptBaseCtrl.TranscriptLinkType.NONE) {
            pages = new TranscriptPdfParser(transcript.getPlainText()).getPages();
        }
        else {
            String baseUrl = (linkType == TranscriptBaseCtrl.TranscriptLinkType.OPEN_LEGISLATION) ? env.getUrl() : (env.getSenSiteUrl() + "/legislation");
            pages = new TranscriptPdfParser(transcript.getLinkedText(baseUrl + "/bills")).getPages();
        }
        this.stenographer = Stenographer.getStenographer(transcript.getDateTime().toLocalDate());
        this.stenographerCenter = (RIGHT + LEFT - stenographer.length() * FONT_WIDTH) / 2;
        writeTranscriptPages(pages);
    }

    @Override
    protected void writePage(List<String> page) throws IOException {
        super.writePage(page);
        // The stenographer should be centered at the bottom of the page.
        float yOffset = (page.size() - STENOGRAPHER_LINE_NUM) * FONT_SIZE * getSpacing();
        newLineAtOffsetTracked(stenographerCenter, yOffset);
        contentStream.showText(stenographer);
    }

    @Override
    protected float getSpacing() {
        return 2;
    }
}
