package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for the special cases of the Constitution, Senate Rules, and Assembly Rules.
 */
public class ConstitutionBuilder extends AbstractLawBuilder {
    private static final String CONS_CHAPTER = CONS_STR + "AS";
    private static final Pattern TITLE_MATCHER = Pattern.compile("(?<docId>" + CONS_STR + "A\\d+)S.*");
    private static final Pattern FOR_ARTICLE = Pattern.compile("(?<numerals>[IVX]+)\\\\n\\s+(?<title>[A-Za-z ]+)\\\\n\\s+Sec\\.\\\\n(?<text>.*)");
    private static final String BAD_TITLE_IDENTIFIER = "accounts; obligations";

    // Maps locationIDs to titles.
    private Map<String, String> titles = new HashMap<>();

    public ConstitutionBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
        // Replenish titles.
        if (previousTree != null) {
            for (LawTreeNode article : previousTree.getRootNode().getChildNodeList()) {
                titles.put(article.getLocationId(), article.getLawDocInfo().getTitle());
                for (LawTreeNode section : article.getChildNodeList())
                    titles.put(section.getLocationId(), section.getLawDocInfo().getTitle());
            }
        }
    }

    @Override
    protected String determineHierarchy(LawBlock block) {
        return block.getLocationId();
    }

    @Override
    protected void addChildNode(LawTreeNode node) {
        if (node.getDocType() == LawDocumentType.SECTION) {
            String articleStr = CONS_STR + node.getLocationId().split("S")[0];
            Optional<LawTreeNode> nodeArticle = rootNode.findNode(articleStr, false);
            nodeArticle.ifPresent(getNode -> getNode.addChild(node));
        }
        else if (node.getDocType() != LawDocumentType.CHAPTER)
            rootNode.addChild(node);
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
    protected void setLawDocTitle(LawDocument lawDoc, boolean isNewDoc) {
        switch (lawDoc.getDocType()) {
            case ARTICLE:
            case SECTION:
                lawDoc.setTitle(titles.get(lawDoc.getLocationId()));
                break;
            case CHAPTER:
                parseTitles(lawDoc, isNewDoc);
            default:
                super.setLawDocTitle(lawDoc, isNewDoc);
        }
    }

    @Override
    public void rebuildTree(String masterDoc) {
        Set<String> articleSet = new LinkedHashSet<>();
        for (String docId : StringUtils.split(masterDoc, "\\n")) {
            Matcher m = TITLE_MATCHER.matcher(docId);
            // Adds in article titles.
            if (m.find())
                articleSet.add(m.group("docId") + "\\n");
        }
        // Always start with the chapter.
        StringBuilder masterBuilder = new StringBuilder(CONS_CHAPTER + "\\n");
        for (String article : articleSet)
            masterBuilder.append(article);
        super.rebuildTree(masterDoc.replace(CONS_CHAPTER + "\\n", masterBuilder.toString()));
    }

    /**
     * Parses out the titles of articles and sections in the Constitution and stores them.
     * @param rootDoc chapter to parse.
     * @param isNewDoc if the chapter is new.
     */
    private void parseTitles(final LawDocument rootDoc, boolean isNewDoc) {
        // Creating empty space in sequence for Preamble later if initial.
        if (rootNode.getChildNodeList().isEmpty())
            sequenceNo++;

        // One title does not have a period at the end.
        String[] articles = rootDoc.getText()
                .replaceFirst(BAD_TITLE_IDENTIFIER, BAD_TITLE_IDENTIFIER + ".")
                .split("\\s*" + LawDocumentType.ARTICLE.name() + " ");
        for (int i = 1; i < articles.length; i++) {
            // Article info. Split to remove notes.
            Matcher articleMatch = FOR_ARTICLE.matcher(articles[i].split("\\*")[0]);
            if (!articleMatch.find())
                continue;
            String articleTitle = articleMatch.group("title");
            titles.put("A" + i, articleTitle.trim());
            LawDocInfo articleInfo = new LawDocInfo(CONS_STR + "A" + i,
                    CONS_STR, "A" + i, articleTitle, LawDocumentType.ARTICLE,
                    articleMatch.group("numerals"), rootDoc.getPublishedDate(), false);
            LawDocument currDoc = new LawDocument(articleInfo,
                    LawDocumentType.ARTICLE.name() + " " + articles[i]);
            Optional<LawDocInfo> oldArticle = rootNode.find(currDoc.getDocumentId());
            // Checks if we're updating an existing article, or adding a new one.
            if (oldArticle.isPresent()) {
                oldArticle.get().setPublishedDate(rootDoc.getPublishedDate());
                currDoc.setTitle(articleTitle);
                lawDocMap.put(currDoc.getDocumentId(), currDoc);
            }
            else
                super.addDocument(currDoc, isNewDoc);

            // Section info.
            String[] sectionTitlesArray = articleMatch.group("text").split("\\.\\)?\\\\n");
            for (String sectionTitle : sectionTitlesArray) {
                String[] parts = sectionTitle.split("\\. ", 2);
                String locId = currDoc.getLocationId() + "S" + parts[0].trim().toUpperCase();
                String title = parts[1];
                titles.put(locId, title.trim());
                // If the document was already processed, update its title.
                Optional<LawTreeNode> existingNode = rootNode.findNode(CONS_STR + locId, false);
                if (existingNode.isPresent() && !title.equals(existingNode.get().getLawDocInfo().getTitle())) {
                    existingNode.get().getLawDocInfo().setTitle(title);
                    lawDocMap.put(CONS_STR + locId, new LawDocument(existingNode.get().getLawDocInfo(), LawProcessor.ONLY_TITLE_UPDATE));
                };
            }
        }
    }
}
