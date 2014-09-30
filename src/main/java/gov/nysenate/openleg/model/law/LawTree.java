package gov.nysenate.openleg.model.law;

public class LawTree
{
    protected LawTreeNode rootNode;

    /** --- Constructors --- */

    public LawTree(LawTreeNode rootNode) {
        this.rootNode = rootNode;
    }

    /** --- Methods --- */

    public LawTreeNode getRootNode() {
        return rootNode;
    }
}
