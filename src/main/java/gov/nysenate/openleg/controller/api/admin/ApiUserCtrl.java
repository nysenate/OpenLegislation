package gov.nysenate.openleg.controller.api.admin;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.service.auth.ApiUserService;
import gov.nysenate.openleg.service.auth.OpenLegRole;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(BASE_ADMIN_API_PATH + "/apiuser")
public class ApiUserCtrl extends BaseCtrl {

    @Autowired private ApiUserService apiUserService;

    @RequiresPermissions("admin:apiuser:post")
    @RequestMapping(value = "/roles", method = RequestMethod.POST)
    public BaseResponse addRoleToUser(@RequestParam String apiKey, @RequestParam String role) {
        apiUserService.grantRole(apiKey, getEnumParameter("role", role, OpenLegRole.class));
        return new SimpleResponse(true, "role granted", "role-granted");
    }

    @RequiresPermissions("admin:apiuser:delete")
    @RequestMapping(value = "/roles", method = RequestMethod.DELETE)
    public BaseResponse removeRoleFromUser(@RequestParam String apiKey, @RequestParam String role) {
        apiUserService.revokeRole(apiKey, getEnumParameter("role", role, OpenLegRole.class));
        return new SimpleResponse(true, "role revoked", "role-revoked");
    }
}
