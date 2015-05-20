package gov.nysenate.openleg.model.law;

public enum LawDocumentType
{
    // The chapter is the main container for a body of law.

    CHAPTER,

    // These top level groups are not strictly ordered as shown here.
    // E.g. some laws will group articles into titles whereas others may
    // group titles into articles.

    ARTICLE,
    SUBARTICLE,
    TITLE,
    SUBTITLE,
    PART,
    SUB_PART,
    INDEX,
    CONTENTS,

    // The section is the primary level in which law data is contained within.

    SECTION,

    // For the ones we don't know how to classify

    MISC,

    // Granularity beyond this point is not supported by the data source
    // and will have to be derived through some form of language parsing.

    SUBDIVISION,
    PARAGRAPH,
    SUB_PARAGRAPH,
    CLAUSE,
    ITEM
}