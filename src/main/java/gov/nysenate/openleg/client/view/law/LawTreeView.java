package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTree;

import java.util.Map;

public class LawTreeView implements ViewObject
{
    protected LawVersionView lawVersion;
    protected LawInfoView info;
    protected LawNodeView documents;

    public LawTreeView(LawTree lawTree) {
        if (lawTree != null) {
            lawVersion = new LawVersionView(lawTree.getLawVersionId());
            info = new LawInfoView(lawTree.getLawInfo());
            documents = new LawNodeView(lawTree.getRootNode());
        }
    }

    public LawTreeView(LawTree lawTree, Map<String, LawDocument> docMap) {
        if (lawTree != null) {
            lawVersion = new LawVersionView(lawTree.getLawVersionId());
            info = new LawInfoView(lawTree.getLawInfo());
            documents = new LawNodeView(lawTree.getRootNode(), docMap);
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
