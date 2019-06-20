package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for the special cases of the Constitution, Senate Rules, and Assembly Rules.
 */
public class ConstitutionBuilder extends AbstractLawBuilder implements LawBuilder {
    protected static final String CONS_STR = "CNS";
    private static final String CONS_CHAPTER = CONS_STR + "AS";
    private static final Pattern TITLE_MATCHER = Pattern.compile("(" + CONS_STR + "A\\d+)S.*");
    private static final Pattern FOR_ARTICLE = Pattern.compile("([IVX]+)\\\\n\\s+([A-Za-z ]+)\\\\n\\s+Sec\\.\\\\n(.*)");
    private static final TreeMap<Integer, String> NUMERALS = new TreeMap<>();

    // Maps locationIDs to titles.
    private Map<String, String> titles = new HashMap<>();

    static {
        NUMERALS.put(50, "L");
        NUMERALS.put(40, "XL");
        NUMERALS.put(10, "X");
        NUMERALS.put(9, "IX");
        NUMERALS.put(5, "V");
        NUMERALS.put(4, "IV");
        NUMERALS.put(1, "I");
    }

    public ConstitutionBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
        // Replenish section titles.
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
                int articleNum = Integer.parseInt(lawDoc.getLocationId().replace("A", ""));
                lawDoc.setDocTypeId(toNumeral(articleNum));
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
                articleSet.add(m.group(1) + "\\n");
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
        boolean isInitial = rootNode.getChildNodeList().isEmpty();
        // Creating empty space in sequence for Preamble later if initial.
        if (rootNode.getChildNodeList().isEmpty())
            sequenceNo++;
        String[] articles = rootDoc.getText().split("\\s*ARTICLE ");
        for (int i = 1; i < articles.length; i++) {
            // Article info. Split to remove notes.
            Matcher articleMatch = FOR_ARTICLE.matcher(articles[i].split("\\*")[0]);
            if (!articleMatch.find())
                continue;
            String articleTitle = articleMatch.group(2);
            titles.put("A" + i, articleTitle);
            LawDocInfo articleInfo = new LawDocInfo(CONS_STR + "A" + i,
                    CONS_STR, "A" + i, articleTitle, LawDocumentType.ARTICLE,
                    articleMatch.group(1), rootDoc.getPublishedDate());
            LawDocument currDoc = new LawDocument(articleInfo, "ARTICLE " + articles[i]);
            Optional<LawDocInfo> oldArticle = rootNode.find(currDoc.getDocumentId());
            if (oldArticle.isPresent()) {
                oldArticle.get().setPublishedDate(rootDoc.getPublishedDate());
                setLawDocTitle(currDoc, isNewDoc);
                lawDocMap.put(currDoc.getDocumentId(), currDoc);
            }
            else
                super.addDocument(currDoc, isNewDoc);

            // Section info.
            String[] sectionTitlesArray = articleMatch.group(3).split("\\.\\\\n");
            for (String sectionTitle : sectionTitlesArray) {
                String[] parts = sectionTitle.split("\\. ", 2);
                String locId = currDoc.getLocationId() + "S" + parts[0].trim().toUpperCase();
                String title = parts[1] + ".";
                titles.put(locId, title);
                // If the document was already processed, update its title.
                Optional<LawTreeNode> existingNode = rootNode.findNode(CONS_STR + locId, false);
                existingNode.ifPresent(node -> node.getLawDocInfo().setTitle(title));
            }
        }
    }

    /**
     * Quickly converts a number to a Roman numeral. Used to display Articles
     * as Roman numerals, as they are in the Constitution text.
     * @param number to convert.
     * @return a Roman numeral.
     */
    private static String toNumeral(int number) {
        if (number == 0)
            return "";
        int next = NUMERALS.floorKey(number);
        return NUMERALS.get(next) + toNumeral(number-next);
    }
}
