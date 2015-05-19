package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawDocumentType;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.law.LawTreeNode;
import gov.nysenate.openleg.model.law.LawVersionId;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Matcher;

public class TypeBasedLawBuilder extends IdBasedLawBuilder implements LawBuilder
{
    private static final Logger logger = LoggerFactory.getLogger(TypeBasedLawBuilder.class);

    /** --- Constructors --- */

    public TypeBasedLawBuilder(LawVersionId lawVersionId) {
        super(lawVersionId);
    }

    public TypeBasedLawBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
    }

    /** --- Overrides --- */

    @Override
    protected String determineHierarchy(LawBlock block) {
        String locationId = block.getLocationId();
        final String originalLocationId = locationId;
        Optional<LawTreeNode> possibleParentNode =
            parentNodes.stream().filter(pn -> originalLocationId.startsWith(pn.getLocationId())).findFirst();
        if (possibleParentNode.isPresent()) {
            int index = parentNodes.search(possibleParentNode.get());
            logger.info("Found possible parent for {} at index {}", locationId, index);
        }

        // Sometimes the location ids are prefixed so try to remove that portion.
        if (!currParent().isRootNode() && locationId.startsWith(currParent().getLocationId())) {
            locationId = StringUtils.removeStart(block.getLocationId(), currParent().getLocationId());
        }
        Matcher locMatcher = locationPattern.matcher(locationId);
        if (locMatcher.matches()) {
            LawDocumentType docType = lawLevelCodes.get(locMatcher.group(1));
            if (!docType.equals(LawDocumentType.SECTION)) {
                while (!currParent().isRootNode() && currParent().getDocType().equals(docType)) {
                    parentNodes.pop();
                }
            }
        }
        else {
            logger.error("Type based hierarchy builder encountered a location id that didn't match the expected pattern. {}",
                block);
        }
        return locationId;
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
}