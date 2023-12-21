package gov.nysenate.openleg.spotchecks.sensite.law;

import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawTreeNode;
import gov.nysenate.openleg.spotchecks.base.SpotCheckService;
import gov.nysenate.openleg.spotchecks.base.SpotCheckUtils;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import gov.nysenate.openleg.spotchecks.sensite.bill.LawSpotCheckId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.*;

/**
 * Checks the consistency of a {@link SenateSiteLawDoc} against its corresponding {@link LawTreeNode}
 */
@Service
public class SenateSiteLawTreeNodeCheckService implements SpotCheckService<LawSpotCheckId, LawTreeNode, SenateSiteLawDoc> {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteLawTreeNodeCheckService.class);

    private final SpotCheckUtils spotCheckUtils;

    private static final String urlPrefix = "/legislation/laws/";

    public SenateSiteLawTreeNodeCheckService(SpotCheckUtils spotCheckUtils) {
        this.spotCheckUtils = spotCheckUtils;
    }

    @Override
    public SpotCheckObservation<LawSpotCheckId> check(LawTreeNode content, SenateSiteLawDoc reference) {
        SpotCheckObservation<LawSpotCheckId> obs = new SpotCheckObservation<>(
                        reference.getReferenceId(),
                        LawSpotCheckId.statuteId(reference.getStatuteId())
        );
        checkNextSiblingUrl(content, reference, obs);
        checkPrevSiblingUrl(content, reference, obs);
        checkParentLocIds(content, reference, obs);
        checkParentId(content, reference, obs);
        checkRepealed(content, reference, obs);
        checkRepealedDate(content, reference, obs);
        checkSequenceNo(content, reference, obs);
        checkFromSection(content, reference, obs);
        checkToSection(content, reference, obs);
        return obs;
    }

    private void checkNextSiblingUrl(LawTreeNode content, SenateSiteLawDoc ref, SpotCheckObservation<LawSpotCheckId> obs) {
        String contentNextUrl = getSiblingUrl(content, true);
        spotCheckUtils.checkString(contentNextUrl, ref.getNextSiblingUrl(), obs, LAW_DOC_NEXT_SIBLING_URL);
    }

    private void checkPrevSiblingUrl(LawTreeNode content, SenateSiteLawDoc ref, SpotCheckObservation<LawSpotCheckId> obs) {
        String contentPrevUrl = getSiblingUrl(content, false);
        spotCheckUtils.checkString(contentPrevUrl, ref.getPrevSiblingUrl(), obs, LAW_DOC_PREV_SIBLING_URL);
    }

    private String getSiblingUrl(LawTreeNode node, boolean next) {
        Optional<LawTreeNode> siblingOpt = next ? node.getNextSibling() : node.getPrevSibling();
        return siblingOpt
                .map(siblingNode -> urlPrefix + siblingNode.getLawId() + "/" + siblingNode.getLocationId())
                .orElse(node.isRootNode()
                        ? getAdjacentChapterUrl(node.getLawId(), next)
                        : null);
    }

    private String getAdjacentChapterUrl(String lawId, boolean next) {
        LawChapterCode code;
        try {
            code = LawChapterCode.valueOf(lawId);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        // Get an array of codes of the same type
        final ArrayList<LawChapterCode> typeCodes = Arrays.stream(LawChapterCode.values())
                .filter(c -> c.getType() == code.getType())
                .sorted(Comparator.comparing(LawChapterCode::getChapterName))
                .collect(Collectors.toCollection(ArrayList::new));
        int codeIndex = typeCodes.indexOf(code);
        int adjIndex = codeIndex + (next ? 1 : -1);
        if (adjIndex >= 0 && adjIndex < typeCodes.size()) {
            LawChapterCode adjCode = typeCodes.get(adjIndex);
            return urlPrefix + adjCode.name();
        }
        return null;
    }

    private void checkParentLocIds(LawTreeNode content, SenateSiteLawDoc ref, SpotCheckObservation<LawSpotCheckId> obs) {
        // Don't perform check for root and direct children, because they don't have this field on NYSenate.gov...
        if (content.isRootNode() || content.getParent() != null && content.getParent().isRootNode()) {
            return;
        }
        List<String> contentParentLocIds = content.getAllParents().stream()
                .map(LawTreeNode::getLocationId).toList();
        spotCheckUtils.checkCollection(
                contentParentLocIds, ref.getParentLocationIds(), obs, LAW_DOC_PARENT_LOC_IDS, Function.identity(), "\n");
    }

    private void checkParentId(LawTreeNode content, SenateSiteLawDoc ref, SpotCheckObservation<LawSpotCheckId> obs) {
        LawChapterCode chapterCode = null;
        try {
            chapterCode = LawChapterCode.valueOf(content.getLawId());
        } catch (IllegalArgumentException ignored) {}

        LawTreeNode parent = content.getParent();

        final String contentParentId;
        if (content.isRootNode() || parent == null) {
            // If current node is root, use law type as parent
            contentParentId = Optional.ofNullable(chapterCode)
                    .map(LawChapterCode::getType)
                    .map(Enum::name)
                    .map(name -> name.replaceAll("[^a-zA-Z]+", ""))
                    .orElse(null);
        } else if (parent.isRootNode()) {
            // If parent is the root use the chapter code
            contentParentId = String.valueOf(chapterCode);
        } else {
            contentParentId = parent.getDocumentId();
        }

        spotCheckUtils.checkString(contentParentId, ref.getParentStatuteId(), obs, LAW_DOC_PARENT_ID);
    }

    private void checkRepealed(LawTreeNode content, SenateSiteLawDoc ref, SpotCheckObservation<LawSpotCheckId> obs) {
        boolean contentRepealed = content.getRepealedDate() != null;
        spotCheckUtils.checkBoolean(contentRepealed, ref.isRepealed(), "Repealed", obs, LAW_DOC_REPEALED);

    }

    private void checkRepealedDate(LawTreeNode content, SenateSiteLawDoc ref, SpotCheckObservation<LawSpotCheckId> obs) {
        LocalDateTime contentRepealedDate = Optional.ofNullable(content.getRepealedDate())
                .map(LocalDate::atStartOfDay)
                .orElse(null);
        spotCheckUtils.checkObject(contentRepealedDate, ref.getRepealedDate(), obs, LAW_DOC_REPEALED_DATE);
    }

    private void checkSequenceNo(LawTreeNode content, SenateSiteLawDoc ref, SpotCheckObservation<LawSpotCheckId> obs) {
        spotCheckUtils.checkObject(content.getSequenceNo(), ref.getSequenceNo(), obs, LAW_DOC_SEQUENCE_NO);
    }

    private void checkFromSection(LawTreeNode content, SenateSiteLawDoc ref, SpotCheckObservation<LawSpotCheckId> obs) {
        String contentFromSection = content.getFromSection().map(LawTreeNode::getLocationId).orElse(null);
        spotCheckUtils.checkString(contentFromSection, ref.getFromSection(), obs, LAW_DOC_FROM_SECTION);
    }

    private void checkToSection(LawTreeNode content, SenateSiteLawDoc ref, SpotCheckObservation<LawSpotCheckId> obs) {
        String contentToSection = content.getToSection().map(LawTreeNode::getLocationId).orElse(null);
        spotCheckUtils.checkString(contentToSection, ref.getToSection(), obs, LAW_DOC_TO_SECTION);
    }
}

