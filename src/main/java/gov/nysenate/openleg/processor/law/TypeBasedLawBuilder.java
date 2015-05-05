package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.law.LawTreeNode;
import gov.nysenate.openleg.model.law.LawVersionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeBasedLawBuilder extends AbstractLawBuilder implements LawBuilder
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
    protected String locateDocument(LawBlock block) {
        return null;
    }

    @Override
    protected void addChildNode(LawTreeNode node) {

    }

    @Override
    protected boolean isNodeListEmpty() {
        return false;
    }

    @Override
    protected void clearParents() {

    }
}