package gov.nysenate.openleg.service.spotcheck.senatesite.law;

import gov.nysenate.openleg.client.view.law.LawDocView;
import gov.nysenate.openleg.model.law.LawChapterCode;
import gov.nysenate.openleg.model.law.LawSpotCheckId;
import gov.nysenate.openleg.model.law.LawType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.senatesite.law.SenateSiteLawDoc;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

@Service
public class SenateSiteLawDocCheckService implements SpotCheckService<LawSpotCheckId, LawDocView, SenateSiteLawDoc> {

    private final SpotCheckUtils spotCheckUtils;

    public SenateSiteLawDocCheckService(SpotCheckUtils spotCheckUtils) {
        this.spotCheckUtils = spotCheckUtils;
    }

    @Override
    public SpotCheckObservation<LawSpotCheckId> check(LawDocView content, SenateSiteLawDoc reference) {
        SpotCheckObservation<LawSpotCheckId> obs = new SpotCheckObservation<>(reference.getReferenceId(),
                LawSpotCheckId.lawDocId(content.getLawId(), content.getLocationId()));
        checkTitle(content, reference, obs);
        checkActiveDate(content, reference, obs);
        checkDocLevelId(content, reference, obs);
        checkDocType(content, reference, obs);
        checkLawId(content, reference, obs);
        checkLawName(content, reference, obs);
        checkLawType(content, reference, obs);
        checkLocationId(content, reference, obs);
        checkText(content, reference, obs);
        return obs;
    }


    private void checkTitle(LawDocView content, SenateSiteLawDoc reference, SpotCheckObservation<LawSpotCheckId> obs) {
        spotCheckUtils.checkString(content.getTitle(), reference.getTitle(), obs, LAW_DOC_TITLE);
    }

    private void checkActiveDate(LawDocView content, SenateSiteLawDoc reference, SpotCheckObservation<LawSpotCheckId> obs) {
        spotCheckUtils.checkObject(content.getActiveDate().atStartOfDay(), reference.getActiveDate(),
                obs, LAW_DOC_ACTIVE_DATE);
    }

    private void checkDocLevelId(LawDocView content, SenateSiteLawDoc reference, SpotCheckObservation<LawSpotCheckId> obs) {
        spotCheckUtils.checkString(content.getDocTypeId(), reference.getDocLevelId(), obs, LAW_DOC_DOC_LEVEL_ID);
    }

    private void checkDocType(LawDocView content, SenateSiteLawDoc reference, SpotCheckObservation<LawSpotCheckId> obs) {
        spotCheckUtils.checkString(content.getDocType(), reference.getDocType(), obs, LAW_DOC_DOC_TYPE);
    }

    private void checkLawId(LawDocView content, SenateSiteLawDoc reference, SpotCheckObservation<LawSpotCheckId> obs) {
        spotCheckUtils.checkString(content.getLawId(), reference.getLawId(), obs, LAW_DOC_LAW_ID);
    }

    private void checkLawName(LawDocView content, SenateSiteLawDoc reference, SpotCheckObservation<LawSpotCheckId> obs) {
        spotCheckUtils.checkString(content.getLawName(), reference.getLawName(), obs, LAW_DOC_LAW_NAME);
    }

    private void checkLawType(LawDocView content, SenateSiteLawDoc reference, SpotCheckObservation<LawSpotCheckId> obs) {
        String contentLawType = Optional.ofNullable(content.getLawId())
                .map(LawChapterCode::valueOf)
                .map(LawChapterCode::getType)
                .map(LawType::name)
                .orElse(null);
        spotCheckUtils.checkObject(contentLawType, reference.getLawType(),
                typeString -> typeString.replaceAll("[^a-zA-Z]+", ""),
                obs, LAW_DOC_LAW_TYPE);
    }

    private void checkLocationId(LawDocView content, SenateSiteLawDoc reference, SpotCheckObservation<LawSpotCheckId> obs) {
        spotCheckUtils.checkString(content.getLocationId(), reference.getLocationId(), obs, LAW_DOC_LOCATION_ID);
    }

    private void checkText(LawDocView content, SenateSiteLawDoc reference, SpotCheckObservation<LawSpotCheckId> obs) {
        spotCheckUtils.checkObject(content.getText(), reference.getText(), this::formatLawText, obs, LAW_DOC_TEXT);
    }

    private String formatLawText(String lawText) {
        return lawText.replaceAll("\\\\n", "\n");
    }

}
