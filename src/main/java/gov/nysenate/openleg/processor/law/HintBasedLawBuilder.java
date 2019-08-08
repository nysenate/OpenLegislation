package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Some laws have inconsistent document ids that result in incorrect hierarchies when using the
 * {@link IdBasedLawBuilder}. This implementation can be initialized with an expected ordering of
 * the document types so that the sub documents can be paired correctly with their parent doc.
 */
public class HintBasedLawBuilder extends IdBasedLawBuilder implements LawBuilder
{
    private static final Logger logger = LoggerFactory.getLogger(HintBasedLawBuilder.class);

    /** Ordered list of law document types used to constrain the hierarchy to certain nesting rules. */
    private LinkedList<LawDocumentType> expectedOrder;

    private Map<LawDocumentType, LawTreeNode> lastParentNodeOfType = new HashMap<>();

    /** --- Constructors --- */

    public HintBasedLawBuilder(LawVersionId lawVersionId, List<LawDocumentType> expectedOrder) {
        super(lawVersionId);
        this.expectedOrder = new LinkedList<>(expectedOrder);
    }

    public HintBasedLawBuilder(LawVersionId lawVersionId, LawTree previousTree, List<LawDocumentType> expectedOrder) {
        super(lawVersionId, previousTree);
        this.expectedOrder = new LinkedList<>(expectedOrder);
    }

    /** --- Overrides --- */

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
                !(currParent().getLocationId().replace("A", "")
                        .equals(node.getLocationId().split("\\.")[0]))) {
            logger.debug("Removing CPL section {}.", node.getLocationId());
            lawDocMap.remove(node.getDocumentId());
            return;
        }
        super.addChildNode(node);
        if (!node.isRootNode() && !node.getDocType().equals(LawDocumentType.SECTION)) {
            lastParentNodeOfType.put(node.getDocType(), node);
        }
    }
}