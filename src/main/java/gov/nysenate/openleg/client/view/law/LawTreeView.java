package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.law.LawTreeNode;
import gov.nysenate.openleg.service.law.data.LawDocumentNotFoundEx;
import gov.nysenate.openleg.service.law.data.LawTreeNotFoundEx;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class LawTreeView implements ViewObject
{
    protected LawVersionView lawVersion;
    protected LawInfoView info;
    protected LawNodeView documents;

    public LawTreeView(LawTree lawTree, String fromLocation, Integer depth) {
        this(lawTree, fromLocation, depth, null);
    }

    public LawTreeView(LawTree lawTree, String fromLocation, Integer depth, Map<String, LawDocument> docMap) {
        if (lawTree != null) {
            lawVersion = new LawVersionView(lawTree.getLawVersionId());
            info = new LawInfoView(lawTree.getLawInfo());
            if (fromLocation != null && !fromLocation.isEmpty()) {
                Optional<LawTreeNode> fromNode = lawTree.getRootNode().findNode(info.lawId + fromLocation, false);
                if (fromNode.isPresent()) {
                    documents = new LawNodeView(fromNode.get(), depth, docMap);
                }
                else {
                    throw new LawDocumentNotFoundEx(info.lawId, LocalDate.now(),
                            "The location " + fromLocation + " does not exist for this law tree,");
                }
            }
            else {
                documents = new LawNodeView(lawTree.getRootNode(), depth, docMap);
            }
        }
    }

    @Override
    public String getViewType() {
        return "law-tree";
    }

    public LawVersionView getLawVersion() {
        return lawVersion;
    }

    public LawInfoView getInfo() {
        return info;
    }

    public LawNodeView getDocuments() {
        return documents;
    }
}
