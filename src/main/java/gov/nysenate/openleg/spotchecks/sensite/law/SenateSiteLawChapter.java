package gov.nysenate.openleg.spotchecks.sensite.law;

import gov.nysenate.openleg.spotchecks.model.SpotCheckReferenceId;

import java.util.List;

/**
 * A grouping of NYSenate.gov law documents into a chapter.
 */
public class SenateSiteLawChapter {

    private SpotCheckReferenceId referenceId;
    private String lawChapterCode;
    private List<SenateSiteLawDoc> documents;
    private SenateSiteLawTree lawTree;

    public SenateSiteLawChapter(String lawChapterCode,
                                List<SenateSiteLawDoc> documents,
                                SpotCheckReferenceId referenceId) {
        this.lawChapterCode = lawChapterCode;
        this.documents = documents;
        this.lawTree = new SenateSiteLawTree(lawChapterCode, documents, referenceId);
        this.referenceId = referenceId;
    }

    public SenateSiteLawTree getLawTree() {
        return lawTree;
    }

    public String getLawId() {
        return lawChapterCode;
    }

    public List<SenateSiteLawDoc> getDocuments() {
        return documents;
    }

    public SpotCheckReferenceId getReferenceId() {
        return referenceId;
    }
}
