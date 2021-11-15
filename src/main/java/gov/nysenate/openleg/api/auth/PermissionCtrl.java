package gov.nysenate.openleg.api.auth;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseCtrl.BASE_API_PATH + "/auth/permission")
public class PermissionCtrl extends BaseCtrl {

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public BaseResponse checkPermission(@RequestParam String permission) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isPermitted(permission)) {
            return new ViewObjectResponse<>(new PermissionView(permission, true, subject.isAuthenticated()));
        }

        return new ViewObjectResponse<>(new PermissionView(permission, false, subject.isAuthenticated()));
    }
}
