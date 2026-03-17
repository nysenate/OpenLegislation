package gov.nysenate.openleg.api.legislation.transcripts.session;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptPdfView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.transcripts.session.InvalidLinkTypeEx;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

public abstract class TranscriptBaseCtrl extends BaseCtrl {
    protected final TranscriptDataService transcriptData;
    @Autowired
    protected OpenLegEnvironment env;

    protected TranscriptBaseCtrl(TranscriptDataService transcriptData) {
        this.transcriptData = transcriptData;
    }

    private enum TranscriptLinkType {
        NONE, OPEN_LEGISLATION, PUBLIC_WEBSITE;

        public static TranscriptLinkType fromString(String string) {
            if (string.isEmpty()) {
                return NONE;
            }
            return valueOf(string.toUpperCase());
        }
    }

    protected TranscriptView getFullView(String linkTypeStr, Transcript transcript) {
        return new TranscriptView(transcript, getTranscriptText(linkTypeStr, transcript));
    }

    protected TranscriptPdfView getPdfView(String linkTypeStr, Transcript transcript) throws IOException {
        return new TranscriptPdfView(getTranscriptText(linkTypeStr, transcript),
                transcript.getId().dateTime().toLocalDate());
    }

    private String getTranscriptText(String linkTypeStr, Transcript transcript) {
        TranscriptLinkType linkType;
        try {
            linkType = TranscriptLinkType.fromString(linkTypeStr);
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidLinkTypeEx(linkTypeStr);
        }
        if (linkType == TranscriptLinkType.NONE) {
            return transcript.getPlainText();
        }
        String baseUrl = (linkType == TranscriptLinkType.OPEN_LEGISLATION) ? env.getUrl() :
                (env.getSenSiteUrl() + "/legislation");
        return transcript.getLinkedText(baseUrl + "/bills");
    }

    @ExceptionHandler(InvalidLinkTypeEx.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidLinkTypeEx(InvalidLinkTypeEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.INVALID_LINK_TYPE, ex.getLinkTypeStr());
    }
}
