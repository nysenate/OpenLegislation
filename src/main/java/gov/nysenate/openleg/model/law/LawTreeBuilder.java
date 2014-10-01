package gov.nysenate.openleg.model.law;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LawTreeBuilder
{
    protected Map<String, LawTreeNode> rootNodeMap = new HashMap<>();

    protected Stack<LawTreeNode> parentNodeStack = new Stack<>();

    protected LawTreeNode currParent = null;

    /** --- Constructors --- */

    public void addNode(LawDocument lawDocument) {

    }



}
