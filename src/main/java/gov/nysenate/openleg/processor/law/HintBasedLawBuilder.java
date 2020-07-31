package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Some laws have inconsistent document ids that result in incorrect hierarchies when using the
 * {@link IdBasedLawBuilder}. This implementation can be initialized with an expected ordering of
 * the document types so that the sub documents can be paired correctly with their parent doc.
 */
public class HintBasedLawBuilder extends IdBasedLawBuilder implements LawBuilder {
    private static final Logger logger = LoggerFactory.getLogger(HintBasedLawBuilder.class);

    /** Ordered list of law document types used to constrain the hierarchy to certain nesting rules. */
    private final LinkedList<LawDocumentType> expectedOrder;

    private final Map<LawDocumentType, LawTreeNode> lastParentNodeOfType = new HashMap<>();

    /** --- Constructors --- */

    public HintBasedLawBuilder(LawVersionId lawVersionId, LawTree previousTree, List<LawDocumentType> expectedOrder) {
        super(lawVersionId, previousTree);
        this.expectedOrder = new LinkedList<>(expectedOrder);
    }

    /** --- Overrides --- */

    @SuppressWarnings("unchecked")
    @Override
    protected String determineHierarchy(LawBlock block) {
        Stack<LawTreeNode> backup = (Stack<LawTreeNode>) parentNodes.clone();
        String locationId = super.determineHierarchy(block);
        if (currParent().isRootNode()) {
            // Determine doc type
            Matcher locMatcher = locationPattern.matcher(locationId);
            if (locMatcher.matches()) {
                LawDocumentType docType = lawLevelCodes.get(locMatcher.group(1));
                if (!docType.equals(expectedOrder.getFirst())) {
                    // Possible mismatch
                    logger.info("Possible mismatch for {}", locationId);
                    for (int i = 1; i < expectedOrder.size(); i++) {
                        if (expectedOrder.get(i).equals(docType)) {
                            LawDocumentType expectedType = expectedOrder.get(i - 1);
                            if (lastParentNodeOfType.containsKey(expectedType)) {
                                LawTreeNode expectedParent = lastParentNodeOfType.get(expectedType);
                                parentNodes = backup;
                                if (parentNodes.peek().equals(expectedParent))
                                    parentNodes.pop();
                                parentNodes.push(expectedParent);
                                logger.info("Guessing actual parent is {}", expectedParent);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return locationId;
    }

    @Override
    protected void addChildNode(LawTreeNode node) {
        // CPL sections should be of the form precedingArticleNumber.anotherNumber, but some aren't and should be removed.
        if (lawInfo.getLawId().equals(LawChapterCode.CPL.name()) &&
                node.getDocType() == LawDocumentType.SECTION &&
                !(currParent().getLocationId().substring(1).equals(node.getLocationId().split("\\.")[0]))) {
            logger.debug("Removing CPL section {}.", node.getLocationId());
            lawDocMap.remove(node.getDocumentId());
            return;
        }
        super.addChildNode(node);
        if (!node.isRootNode() && node.getDocType() != LawDocumentType.SECTION)
            lastParentNodeOfType.put(node.getDocType(), node);
    }
}