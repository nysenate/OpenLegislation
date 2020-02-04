package gov.nysenate.openleg.model.spotcheck.senatesite.law;

import gov.nysenate.openleg.client.view.law.LawDocWithRefsView;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Object analogous to a NYSenate.gov statute node.
 */
public class SenateSiteLawDoc {

    private LocalDateTime referenceDateTime;

    private String statuteId;
    private String title;
    private LocalDateTime activeDate;
    private String chapter;
    private String docLevelId;
    private String docType;
    private String fromSection;
    private String toSection;
    private String lawId;
    private String lawName;
    private String lawType;
    private String locationId;
    private String nextSiblingUrl;
    private String prevSiblingUrl;
    private List<String> parentLocationIds;
    private String parentStatuteId;
    private boolean repealed;
    private LocalDateTime repealedDate;
    private int sequenceNo;
    private String text;

    private SenateSiteLawDoc() {}

    public static Builder builder() {
        return new Builder();
    }

    public static SenateSiteLawDoc partialFromRef(LawDocWithRefsView lawDocView) {
        if (lawDocView == null) {
            return null;
        }
        return builder()
                .setStatuteId(lawDocView.getLawId() + lawDocView.getLocationId())
                .setTitle(lawDocView.getTitle())
                .setActiveDate(Optional.ofNullable(lawDocView.getActiveDate())
                        .map(LocalDate::atStartOfDay).orElse(null))
                .setDocLevelId(lawDocView.getDocLevelId())
                .setDocType(lawDocView.getDocType())
                .setLawId(lawDocView.getLawId())
                .setLawName(lawDocView.getLawName())
                .setLawType(lawDocView.getDocType())
                .setLocationId(lawDocView.getLocationId())
                .setParentStatuteId(Optional.ofNullable(lawDocView.getParents())
                        .filter(parents -> !parents.isEmpty())
                        .map(parents -> parents.get(parents.size() - 1))
                        .map(parent -> parent.getLawId() + parent.getLocationId())
                        .orElse(null)
                )
                .build();
    }

    public static class Builder {
        private SenateSiteLawDoc lawDoc;

        private Builder() {
            this.lawDoc = new SenateSiteLawDoc();
        }

        public SenateSiteLawDoc build() {
            return this.lawDoc;
        }

        public Builder setReferenceDateTime(LocalDateTime referenceDateTime) {
            this.lawDoc.referenceDateTime = referenceDateTime;
            return this;
        }

        public Builder setText(String text) {
            this.lawDoc.text = text;
            return this;
        }

        public Builder setStatuteId(String docId) {
            this.lawDoc.statuteId = docId;
            return this;
        }

        public Builder setTitle(String title) {
            this.lawDoc.title = title;
            return this;
        }

        public Builder setActiveDate(LocalDateTime activeDate) {
            this.lawDoc.activeDate = activeDate;
            return this;
        }

        public Builder setChapter(String chapter) {
            this.lawDoc.chapter = chapter;
            return this;
        }

        public Builder setDocLevelId(String docLevelId) {
            this.lawDoc.docLevelId = docLevelId;
            return this;
        }

        public Builder setDocType(String docType) {
            this.lawDoc.docType = docType;
            return this;
        }

        public Builder setFromSection(String fromSection) {
            this.lawDoc.fromSection = fromSection;
            return this;
        }

        public Builder setLawId(String lawId) {
            this.lawDoc.lawId = lawId;
            return this;
        }

        public Builder setLawName(String lawName) {
            this.lawDoc.lawName = lawName;
            return this;
        }

        public Builder setLawType(String lawType) {
            this.lawDoc.lawType = lawType;
            return this;
        }

        public Builder setLocationId(String locationId) {
            this.lawDoc.locationId = locationId;
            return this;
        }

        public Builder setNextSiblingUrl(String nextSiblingUrl) {
            this.lawDoc.nextSiblingUrl = nextSiblingUrl;
            return this;
        }

        public Builder setPrevSiblingUrl(String prevSiblingUrl) {
            this.lawDoc.prevSiblingUrl = prevSiblingUrl;
            return this;
        }

        public Builder setParentLocationIds(List<String> parentLocationIds) {
            this.lawDoc.parentLocationIds = parentLocationIds;
            return this;
        }

        public Builder setParentStatuteId(String parentStatuteId) {
            this.lawDoc.parentStatuteId = parentStatuteId;
            return this;
        }

        public Builder setRepealed(boolean repealed) {
            this.lawDoc.repealed = repealed;
            return this;
        }

        public Builder setRepealedDate(LocalDateTime repealedDate) {
            this.lawDoc.repealedDate = repealedDate;
            return this;
        }

        public Builder setSequenceNo(int sequenceNo) {
            this.lawDoc.sequenceNo = sequenceNo;
            return this;
        }

        public Builder setToSection(String toSection) {
            this.lawDoc.toSection = toSection;
            return this;
        }
    }

    public SpotCheckReferenceId getReferenceId() {
        return new SpotCheckReferenceId(SpotCheckRefType.SENATE_SITE_LAW, referenceDateTime);
    }

    public String getStatuteId() {
        return statuteId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getActiveDate() {
        return activeDate;
    }

    public String getChapter() {
        return chapter;
    }

    public String getDocLevelId() {
        return docLevelId;
    }

    public String getDocType() {
        return docType;
    }

    public String getFromSection() {
        return fromSection;
    }

    public String getLawId() {
        return lawId;
    }

    public String getLawName() {
        return lawName;
    }

    public String getLawType() {
        return lawType;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getNextSiblingUrl() {
        return nextSiblingUrl;
    }

    public String getPrevSiblingUrl() {
        return prevSiblingUrl;
    }

    public List<String> getParentLocationIds() {
        return parentLocationIds;
    }

    public String getParentStatuteId() {
        return parentStatuteId;
    }

    public boolean isRepealed() {
        return repealed;
    }

    public LocalDateTime getRepealedDate() {
        return repealedDate;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getToSection() {
        return toSection;
    }

    public LocalDateTime getReferenceDateTime() {
        return referenceDateTime;
    }

    public String getText() {
        return text;
    }
}
