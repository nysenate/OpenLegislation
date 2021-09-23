package gov.nysenate.openleg.spotchecks.openleg;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.spotchecks.model.SpotCheckContentType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.base.SpotcheckRunService;
import gov.nysenate.openleg.common.util.AsyncUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;
import static gov.nysenate.openleg.spotchecks.model.SpotCheckDataSource.OPENLEG;

/**
 * Created by Chenguang He on 2017/3/27.
 * This api controller is used to give the access of openleg checking service.
 * It generates the report of difference between openleg dev and xml-processing bills.
 */

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/spotcheck/openleg")
public class OpenlegSpotCheckCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(OpenlegSpotCheckCtrl.class);

    private static final String defaultSessionIndicator = "-945100001";

    private final SpotcheckRunService spotcheckRunService;
    private final AsyncUtils asyncUtils;

    @Autowired
    public OpenlegSpotCheckCtrl(SpotcheckRunService spotcheckRunService,
                                AsyncUtils asyncUtils) {
        this.spotcheckRunService = spotcheckRunService;
        this.asyncUtils = asyncUtils;
    }

    @RequiresPermissions("admin:spotcheck")
    @RequestMapping(value = "/{contentType}", method = RequestMethod.POST)
    public BaseResponse performSpotCheck(@PathVariable String contentType,
                                         @RequestParam(defaultValue = defaultSessionIndicator) int year) {
        if (year == Integer.parseInt(defaultSessionIndicator)) {
            year = Year.now().getValue();
        }
        SpotCheckRefType refType = getRefType(contentType);
        logger.info("Running {} Spotcheck for year: {}", refType, year);

        LocalDateTime sessionYearStart = Year.of(year).atDay(1).atStartOfDay();
        asyncUtils.run(() ->
                spotcheckRunService.runReports(refType, Range.singleton(sessionYearStart)));
        return new SimpleResponse(true,
                "Initiated run of " + refType + " spotcheck",
                "openleg-spotcheck-start");
    }

    private SpotCheckRefType getRefType(String contentTypeStr) {
        SpotCheckContentType contentType =
                getEnumParameter("contentType", contentTypeStr, SpotCheckContentType.class);
        List<SpotCheckRefType> spotCheckRefTypes = SpotCheckRefType.get(OPENLEG, contentType);
        if (spotCheckRefTypes.size() != 1) {
            throw new InvalidRequestParamEx(contentTypeStr, "contentType",
                    SpotCheckContentType.class.getSimpleName(),
                    "there must be 1 openleg ref type for the given content type");
        }
        return spotCheckRefTypes.get(0);
    }

}
