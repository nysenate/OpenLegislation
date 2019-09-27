package gov.nysenate.openleg.service.spotcheck.senatesite.law;

import gov.nysenate.openleg.client.view.law.LawDocView;
import gov.nysenate.openleg.model.law.*;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpFragment;
import gov.nysenate.openleg.model.spotcheck.senatesite.law.SenateSiteLawChapter;
import gov.nysenate.openleg.model.spotcheck.senatesite.law.SenateSiteLawDoc;
import gov.nysenate.openleg.service.law.data.LawDataService;
import gov.nysenate.openleg.service.law.data.LawDocumentNotFoundEx;
import gov.nysenate.openleg.service.law.data.LawTreeNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckUtils;
import gov.nysenate.openleg.service.spotcheck.senatesite.base.BaseSenateSiteReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

@Service
public class SenateSiteLawReportService extends BaseSenateSiteReportService<LawSpotCheckId> {

    private final SenateSiteLawJsonParser jsonParser;
    private final LawDataService lawDataService;
    private final SpotCheckUtils spotCheckUtils;
    private final SenateSiteLawDocCheckService lawCheckService;
    private final SenateSiteLawTreeCheckService lawTreeCheckService;
    private final SenateSiteLawTreeNodeCheckService lawTreeNodeCheckService;

    @Autowired
    public SenateSiteLawReportService(SenateSiteLawJsonParser jsonParser,
                                      LawDataService lawDataService,
                                      SpotCheckUtils spotCheckUtils,
                                      SenateSiteLawDocCheckService lawCheckService,
                                      SenateSiteLawTreeCheckService lawTreeCheckService,
                                      SenateSiteLawTreeNodeCheckService lawTreeNodeCheckService) {
        this.jsonParser = jsonParser;
        this.lawDataService = lawDataService;
        this.spotCheckUtils = spotCheckUtils;
        this.lawCheckService = lawCheckService;
        this.lawTreeCheckService = lawTreeCheckService;
        this.lawTreeNodeCheckService = lawTreeNodeCheckService;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.SENATE_SITE_LAW;
    }

    @Override
    protected void checkDump(SenateSiteDump dump, SpotCheckReport<LawSpotCheckId> report) {
        List<String> refLawIds = new ArrayList<>();
        for (SenateSiteDumpFragment fragment : dump.getDumpFragments()) {
            SenateSiteLawChapter refLawChapter = jsonParser.parseLawDumpFragment(fragment);
            refLawIds.add(refLawChapter.getLawId());

            LawTree lawTree = null;
            boolean lawTreeMismatch = true;

            // Perform an observation on the overall law tree
            try {
                lawTree = lawDataService.getLawTree(refLawChapter.getLawId());
                SpotCheckObservation<LawSpotCheckId> fullTreeObs =
                        lawTreeCheckService.check(lawTree, refLawChapter.getLawTree());
                lawTreeMismatch = fullTreeObs.hasMismatch(LAW_TREE);
                report.addObservation(fullTreeObs);
            } catch (LawTreeNotFoundEx ex){
                report.addObservedDataMissingObs(LawSpotCheckId.lawTreeId(refLawChapter.getLawId()));
            }

            // Observe each document
            for (SenateSiteLawDoc refDoc : refLawChapter.getDocuments()) {
                final String docId = refDoc.getStatuteId();
                try {
                    String olDocId = docId;
                    if (StringUtils.equals(docId, refLawChapter.getLawId())) {
                        olDocId = Optional.ofNullable(lawTree)
                                .map(LawTree::getRootNode)
                                .map(LawTreeNode::getDocumentId)
                                .orElse(docId);
                    }
                    LawDocument lawDoc = lawDataService.getLawDocument(olDocId, LocalDate.now());
                    LawDocView lawDocView = new LawDocView(lawDoc);
                    SpotCheckObservation<LawSpotCheckId> docObs = lawCheckService.check(lawDocView, refDoc);
                    report.addObservation(docObs);
                    // Only check the law tree related fields if the overall law tree is correct.
                    // This is to prevent redundant mismatches when the law tree is not correct.
                    if (!lawTreeMismatch && lawTree != null) {
                        Optional<LawTreeNode> treeNodeOpt = lawTree.find(olDocId);
                        if (treeNodeOpt.isPresent()) {
                            SpotCheckObservation<LawSpotCheckId> docTreeObs =
                                    lawTreeNodeCheckService.check(treeNodeOpt.get(), refDoc);
                            report.addObservation(docTreeObs);
                        } else {
                            docObs.addMismatch(new SpotCheckMismatch(
                                    LAW_TREE_NODE_NOT_FOUND, "", refDoc.getLocationId()
                            ));
                        }
                    }
                    report.addObservation(docObs);
                } catch (LawDocumentNotFoundEx ex) {
                    report.addObservedDataMissingObs(LawSpotCheckId.statuteId(refDoc.getStatuteId()));
                }
            }
        }
        // Do not do a full law id check if only a single law chapter was included in the dump
        if (dump.getDumpId().getFragmentCount() > 1) {
            checkLawIds(refLawIds, report);
        }
    }

    private void checkLawIds(List<String> refLawIds, SpotCheckReport<LawSpotCheckId> report) {
        SpotCheckObservation<LawSpotCheckId> obs = new SpotCheckObservation<>(
                report.getReportId().getReferenceId(),
                LawSpotCheckId.allChaptersId()
        );
        List<String> dataLawIds = lawDataService.getLawInfos().stream()
                .map(LawInfo::getLawId)
                .collect(Collectors.toList());
        spotCheckUtils.checkCollection(dataLawIds, refLawIds, obs, LAW_IDS, String::toString, "\n", true);
        report.addObservation(obs);
    }
}
