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
    private static final Pattern FOR_ARTICLE = Pattern.compile("([IVX]+)\\\\n\\s+([A-Za-z ]+)\\\\n\\s+Sec\\.\\\\n(.*)");

    // The title of article x section y can be found at sectionTitles.get(AxSy).
    private Map<String, String> allSectionTitles = new HashMap<>();

    public ConstitutionBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
    }

    @Override
    protected void addRootDocument(LawDocument rootDoc, boolean isNewDoc) {
        super.addRootDocument(rootDoc, isNewDoc);
        String[] articles = rootDoc.getText().split("\\s*ARTICLE ");
        for (int i = 1; i < articles.length; i++) {
            // Article info. Split to remove notes.
            Matcher articleMatch = FOR_ARTICLE.matcher(articles[i].split("\\*")[0]);
            if (!articleMatch.find())
                continue;
            LawDocInfo articleInfo = new LawDocInfo(CONS_STR + "A" + i,
                    CONS_STR, "A" + i, articleMatch.group(2),
                    LawDocumentType.ARTICLE, articleMatch.group(1),
                    rootDoc.getPublishedDate());
            LawDocument currDoc = new LawDocument(articleInfo, "ARTICLE " + articles[i]);
            super.addDocument(currDoc, isNewDoc);

            // Section info.
            String[] sectionTitles = articleMatch.group(3).split("\\.\\\\n");
            for (String sectionTitle : sectionTitles) {
                String[] parts = sectionTitle.split("\\. ", 2);
                allSectionTitles.put(currDoc.getLocationId() + "S" + parts[0].trim().toUpperCase(), parts[1] + ".");
            }
        }
    }

    @Override
    protected String determineHierarchy(LawBlock block) {
        return block.getDocumentId().replaceAll("A\\d+", "");
    }

    @Override
    protected void addChildNode(LawTreeNode node) {
        if (node.getDocType() == LawDocumentType.ARTICLE)
            rootNode.addChild(node);
        if (node.getDocType() != LawDocumentType.SECTION)
            return;

        String articleStr = node.getDocumentId().replaceAll("S\\d+", "");
        Optional<LawTreeNode> nodeArticle = rootNode.findNode(articleStr, false);
        if (nodeArticle.isPresent())
           nodeArticle.get().addChild(node);
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
