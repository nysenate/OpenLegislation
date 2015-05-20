package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

/**
 * Constructs document hierarchies using document id prefixes.
 */
public class IdBasedLawBuilder extends AbstractLawBuilder implements LawBuilder
{
    private static final Logger logger = LoggerFactory.getLogger(IdBasedLawBuilder.class);

    /** Stack of the current parent nodes, used to determine hierarchy. */
    protected Stack<LawTreeNode> parentNodes = new Stack<>();

    /** --- Constructors --- */

    public IdBasedLawBuilder(LawVersionId lawVersionId) {
        super(lawVersionId);
    }

    public IdBasedLawBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
    }

    /**
     * We determine the position of a node based on whether any of the nodes in the parent stack has a location id
     * that prefixes the current document's location id. The specific location id (the matched prefix from the parent
     * document removed) is returned.
     *
     * For example given the locationId 'A2P1SP3', we will pop the parent stack until we find it's parent 'A2P1'
     * or reach the root node. The new portion 'SP3' would be the returned value which serves as the docTypeId.
     *
     * @param block LawBlock
     * @return String
     */
    @Override
    protected String determineHierarchy(LawBlock block) {
        String docTypeId = block.getLocationId();
        while (!currParent().isRootNode()) {
            if (StringUtils.startsWith(block.getLocationId(), currParent().getLocationId())) {
                String trimLocId = StringUtils.removeStart(block.getLocationId(), currParent().getLocationId());
                if (locationPattern.matcher(trimLocId).matches()) {
                    docTypeId = trimLocId;
                    break;
                }
            }
            parentNodes.pop();
        }
        return docTypeId;
    }

    @Override
    protected void addChildNode(LawTreeNode node) {
        if (currParent() != null) {
            currParent().addChild(node);
        }
        // Section nodes should never become parents because they are the most granular (at the moment).
        if (!node.getDocType().equals(LawDocumentType.SECTION)) {
            parentNodes.push(node);
        }
    }

    @Override
    protected boolean isNodeListEmpty() {
        return parentNodes.empty();
    }

    @Override
    protected void clearParents() {
        parentNodes.clear();
    }

    /**
     * Peek at the parent node stack which holds the current parent.
    */
    protected LawTreeNode currParent() {
       if (!parentNodes.empty()) {
           return parentNodes.peek();
       }
       else {
           return null;
       }
    }
}