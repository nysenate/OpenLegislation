package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for the special cases of the Constitution, Senate Rules, and Assembly Rules.
 */
public class ConstitutionBuilder extends AbstractLawBuilder implements LawBuilder {
    private static final String CONS_STR = "CNS";
    // The title of article x section y can be found at sectionTitles.get(AxSy).
    private Map<String, String> allSectionTitles = new HashMap<>();
    private static final String ARTICLE_TITLE = "\\s*ARTICLE [IVX]+\\\\n\\s*";
    private static final Pattern SECTION_TITLES = Pattern.compile("\\s*(\\d+[-[a-z]]?)\\. ([^\\d]*)");

    public ConstitutionBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
    }

    @Override
    protected void addRootDocument(LawDocument rootDoc, boolean isNewDoc) {
        super.addRootDocument(rootDoc, isNewDoc);
        String[] articles = rootDoc.getText().split(ARTICLE_TITLE);
        // TODO: The -1 adjustment is due to a note with a star at the end of this document.
        for (int i = 1; i < articles.length; i++) {
            // Article info.
            String[] titleAndSections = articles[i].split("\\s*Sec\\.");
            articles[i] = titleAndSections[1];
            LawDocInfo articleInfo = new LawDocInfo(CONS_STR + "A" + i,
                    CONS_STR, "A" + i, titleAndSections[0],
                    LawDocumentType.ARTICLE, "A" + i, rootDoc.getPublishedDate());
            LawDocument currDoc = new LawDocument(articleInfo, "IDK"+i);
            super.addDocument(currDoc, isNewDoc);
            // Section info.
            Matcher sections = SECTION_TITLES.matcher(articles[i]);
            while (sections.find()) {
                allSectionTitles.put(currDoc.getLocationId() + "S" +
                        sections.group(1), sections.group(2));
            }
        }
    }

    @Override
    protected String determineHierarchy(LawBlock block) {
        return block.getDocumentId().replaceAll("A\\d+", "");
    }

    @Override
    protected void addChildNode(LawTreeNode node) {
        if (node.getDocType() == LawDocumentType.CHAPTER)
            return;
        if (node.getDocType() == LawDocumentType.ARTICLE)
            rootNode.addChild(node);
        else {
            String articleStr = node.getDocumentId().replaceAll("S\\d+", "");
            Optional<LawTreeNode> nodeArticle = rootNode.findNode(articleStr, false);
            if (nodeArticle.isPresent())
                nodeArticle.get().addChild(node);
        }
    }

    @Override
    protected boolean isNodeListEmpty() {
        return false;
    }
    @Override
    protected void clearParents() {}

    @Override
    protected boolean isLikelySectionDoc(LawDocument lawDoc) {
        return lawDoc.getLocationId().replaceAll("A\\d+", "").matches("S.+");
    }

    @Override
    protected void setLawDocTitle(LawDocument lawDoc) {
        if (lawDoc.getDocType() != LawDocumentType.SECTION)
            super.setLawDocTitle(lawDoc);
        else
            lawDoc.setTitle(allSectionTitles.get(lawDoc.getLocationId()));
    }
}
