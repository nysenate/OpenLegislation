package gov.nysenate.openleg.api.legislation.transcripts.session;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptIdView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptInfoView;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TranscriptBaseCtrl extends BaseCtrl {
    protected final TranscriptDataService transcriptData;
    @Autowired
    private OpenLegEnvironment env;

    protected TranscriptBaseCtrl(TranscriptDataService transcriptData) {
        this.transcriptData = transcriptData;
    }

    protected TranscriptView getFullView(String linkTypeStr, Transcript transcript) {
        var linkType = TranscriptLinkType.fromString(linkTypeStr);
        if (linkType == TranscriptLinkType.NONE) {
            return new TranscriptView(transcript);
        }
        String baseUrl = (linkType == TranscriptLinkType.OPEN_LEGISLATION) ? env.getUrl() : env.getSenSiteUrl();
        return new TranscriptView(transcript, baseUrl + "/bills");
    }

    protected ViewObject getTranscriptView(boolean summary, boolean full, String linkType, TranscriptId result) {
        if (full) {
            return getFullView(linkType, transcriptData.getTranscript(result));
        }
        if (summary) {
            return new TranscriptInfoView(transcriptData.getTranscript(result));
        }
        return new TranscriptIdView(result);
    }
}
