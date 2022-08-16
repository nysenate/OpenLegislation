package gov.nysenate.openleg.api.spotcheck;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.spotcheck.view.SpotCheckMismatchTypeView;
import gov.nysenate.openleg.api.spotcheck.view.SpotCheckRefTypeView;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/spotcheck", produces = APPLICATION_JSON_VALUE)
public class SpotCheckModelDataCtrl extends BaseCtrl {

    /**
     * Mismatch Types API
     * ------------------
     *
     * Returns the enum name and display name for all SpotCheckMismatchType's.
     * Usage: (GET) /api/3/admin/spotcheck/mismatch-types
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/mismatch-types", method = RequestMethod.GET)
    public BaseResponse getMismatchTypes() {
        var mismatchTypes = EnumSet.allOf(SpotCheckMismatchType.class).stream()
                .map(SpotCheckMismatchTypeView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(mismatchTypes);
    }

    /**
     * Reference Types API
     * ------------------
     *
     * Returns the enum name and display name for all SpotCheckRefType's.
     * Usage: (GET) /api/3/admin/spotcheck/reference-types
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/reference-types", method = RequestMethod.GET)
    public BaseResponse getRefTypes() {
        var refTypes = EnumSet.allOf(SpotCheckRefType.class).stream()
                .map(SpotCheckRefTypeView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(refTypes);
    }
}
