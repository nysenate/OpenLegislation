package gov.nysenate.openleg.spotchecks.sensite.law;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.api.legislation.law.view.LawTreeView;
import gov.nysenate.openleg.spotchecks.sensite.bill.LawSpotCheckId;
import gov.nysenate.openleg.legislation.law.LawTree;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import gov.nysenate.openleg.spotchecks.base.SpotCheckService;
import gov.nysenate.openleg.spotchecks.base.SpotCheckUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.LAW_TREE;

/**
 * Check a law tree for mismatches
 */
@Service
public class SenateSiteLawTreeCheckService implements SpotCheckService<LawSpotCheckId, LawTree, SenateSiteLawTree> {

    private final SpotCheckUtils spotCheckUtils;

    public SenateSiteLawTreeCheckService(SpotCheckUtils spotCheckUtils) {
        this.spotCheckUtils = spotCheckUtils;
    }

    @Override
    public SpotCheckObservation<LawSpotCheckId> check(LawTree content, SenateSiteLawTree reference) {
        final LawSpotCheckId id = LawSpotCheckId.lawTreeId(reference.getChapterId());
        LawTreeView lawTreeView = new LawTreeView(content, null, null);
        SenateSiteLawTree dataLawTree = new SenateSiteLawTree(lawTreeView);
        SpotCheckObservation<LawSpotCheckId> lawTreeObs = new SpotCheckObservation<>(reference.getReferenceId(), id);
        spotCheckUtils.checkObject(dataLawTree, reference, this::getLawTreeString, lawTreeObs, LAW_TREE);
        return lawTreeObs;
    }

    String getLawTreeObsId(String lawId) {
        return lawId + " - LAW TREE";
    }

    private String getLawTreeString(SenateSiteLawTree lawTree) {
        StringBuilder sBuilder = new StringBuilder();
        Optional.ofNullable(lawTree.getRootNode()).ifPresent(root -> writeLawTreeNode(root, 0, sBuilder));
        ImmutableList<SenateSiteLawTreeNode> orphans = lawTree.getOrphanNodes();
        if (!orphans.isEmpty()) {
            sBuilder.append("\nOrphaned Nodes:\n");
            orphans.forEach(o -> writeLawTreeNode(o, 0, sBuilder));
        }
        return sBuilder.toString();
    }

    private void writeLawTreeNode(SenateSiteLawTreeNode node, int level, StringBuilder sBuilder) {
        for (int i = 0; i < level; i++) {
            sBuilder.append("  ");
        }
        String docId = node.getDocId();
        if (level == 0 && docId != null) {
            // Restrict root nodes to 3 characters
            // Openleg root nodes have additional info, but NYSenate.gov roots only have 3 letters.
            sBuilder.append(docId, 0, Integer.min(3, docId.length()));
        } else {
            sBuilder.append(docId);
        }
        sBuilder.append("\n");
        node.getChildren().forEach(child -> writeLawTreeNode(child, level + 1, sBuilder));
    }
}
