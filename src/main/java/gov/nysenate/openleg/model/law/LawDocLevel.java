package gov.nysenate.openleg.model.law;

public enum LawDocLevel
{
    // The chapter is the main container for a body of law.

    CHAPTER,

    // These top level groups are not strictly ordered as shown here.
    // E.g. some laws will group articles into titles whereas others may
    // group titles into articles.

    ARTICLE,
    TITLE,
    SUBTITLE,
    PART,
    SUB_PART,

    // The section is the primary level in which law data is contained within.

    SECTION,

    // Granularity beyond this point is not supported by the data source
    // and will have to be derived through some form of language parsing.

    SUBDIVISION,
    PARAGRAPH,
    SUB_PARAGRAPH,
    CLAUSE,
    ITEM
}