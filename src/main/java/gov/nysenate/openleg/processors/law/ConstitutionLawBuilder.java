package gov.nysenate.openleg.processors.law;

import gov.nysenate.openleg.legislation.law.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for the special case of the Constitution.
 */
public class ConstitutionLawBuilder extends IdBasedLawBuilder implements LawBuilder {
    private static final Pattern ARTICLE_NUM_PATTERN = Pattern.compile(CONS_STR + "A(\\d+)S.*"),
    FOR_ARTICLE = Pattern.compile("(?<numerals>[IVX]+)\\\\n\\s+(?<title>[A-Za-z ]+)\\\\n\\s+Sec\\.\\\\n(?<text>.*)");

    // Maps location IDs to titles.
    private final Map<String, String> titles = new HashMap<>();

    public ConstitutionLawBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
        // Replenish titles.
        if (previousTree != null) {
            for (LawTreeNode node : previousTree.getRootNode().getAllNodes())
                titles.put(node.getLocationId(), node.getLawDocInfo().getTitle());
        }
    }

    @Override
    protected void addChildNode(LawTreeNode node) {
        if (node.getDocType().isSection()) {
            String articleStr = CONS_STR + node.getLocationId().split("S")[0];
            Optional<LawTreeNode> nodeArticle = rootNode.findNode(articleStr, false);
            nodeArticle.ifPresent(getNode -> getNode.addChild(node));
        }
        else if (node.getDocType() != LawDocumentType.CHAPTER)
            rootNode.addChild(node);
        else
            parentNodes.push(node);
    }

    @Override
    protected void setLawDocTitle(LawDocument lawDoc) {
        switch (lawDoc.getDocType()) {
            case ARTICLE, SECTION:
                lawDoc.setTitle(titles.get(lawDoc.getLocationId()));
                break;
            case CHAPTER:
                parseTitles(lawDoc);
            default:
                super.setLawDocTitle(lawDoc);
        }
    }

    @Override
    public void rebuildTree(String masterDoc) {
        StringBuilder correctedMasterDoc = new StringBuilder();
        String currArticle = "";
        for (String docId : StringUtils.split(masterDoc, "\\n")) {
            Matcher m = ARTICLE_NUM_PATTERN.matcher(docId);
            if (m.find()) {
                String matchedArticleNum = m.group(1);
                // Adds in articles where they are supposed to be in the tree.
                if (!matchedArticleNum.equals(currArticle)) {
                    correctedMasterDoc.append(CONS_STR).append("A").append(matchedArticleNum).append("\\n");
                    currArticle = matchedArticleNum;
                }
            }
            correctedMasterDoc.append(docId).append("\\n");
        }
        super.rebuildTree(correctedMasterDoc.toString());
    }

    /**
     * Parses out the titles of articles and sections in the Constitution and stores them.
     * Also adds dummy articles to simplify processing.
     * @param rootDoc chapter to parse.
     */
    private void parseTitles(final LawDocument rootDoc) {
        // Creating empty space in sequence for Preamble later if initial.
        if (rootNode.getChildNodeList().isEmpty())
            sequenceNo++;

        String[] articles = rootDoc.getText().split("\\s*" + LawDocumentType.ARTICLE.name() + " ");
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
            }
            else
                super.addDocument(currDoc);
            lawDocMap.put(currDoc.getDocumentId(), currDoc);

            // Section info.
            String[] sectionTitlesArray = articleMatch.group("text").split("\\.\\)?\\\\n");
            for (String sectionTitle : sectionTitlesArray)
                processSection(sectionTitle, currDoc);
        }
    }

    /**
     * Processes a single section.
     * @param sectionTitle of section to be processed.
     * @param currDoc containing other information about this section.
     */
    private void processSection(String sectionTitle, LawDocument currDoc) {
        String[] parts = sectionTitle.split("\\. ", 2);
        String locId = currDoc.getLocationId() + "S" + parts[0].trim().toUpperCase();
        String title = parts[1];
        titles.put(locId, title.trim());
        // If the document was already processed, update its title.
        Optional<LawTreeNode> existingNode = rootNode.findNode(CONS_STR + locId, false);
        if (existingNode.isPresent() && !title.equals(existingNode.get().getLawDocInfo().getTitle())) {
            existingNode.get().getLawDocInfo().setTitle(title);
            lawDocMap.put(CONS_STR + locId, new LawDocument(existingNode.get().getLawDocInfo(), LawProcessor.ONLY_TITLE_UPDATE));
        }
    }
}
