package gov.nysenate.openleg.api.admin;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.auth.model.OpenLegRole;
import gov.nysenate.openleg.auth.user.ApiUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(BASE_ADMIN_API_PATH + "/apiuser")
public class ApiUserCtrl extends BaseCtrl {
    private final ApiUserService apiUserService;

    @Autowired
    public ApiUserCtrl(ApiUserService apiUserService) {
        this.apiUserService = apiUserService;
    }

    @RequiresPermissions("admin:apiuser:post")
    @PostMapping(value = "/roles")
    public BaseResponse addRoleToUser(@RequestParam String apiKey, @RequestParam String role) {
        apiUserService.grantRole(apiKey, getEnumParameter("role", role, OpenLegRole.class));
        return new SimpleResponse(true, "role granted", "role-granted");
    }

    @RequiresPermissions("admin:apiuser:delete")
    @DeleteMapping(value = "/roles")
    public BaseResponse removeRoleFromUser(@RequestParam String apiKey, @RequestParam String role) {
        apiUserService.revokeRole(apiKey, getEnumParameter("role", role, OpenLegRole.class));
        return new SimpleResponse(true, "role revoked", "role-revoked");
    }
}
