package gov.nysenate.openleg.api.auth;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(BaseCtrl.BASE_API_PATH + "/auth/permission")
public class PermissionCtrl extends BaseCtrl {

    @Value("${api.auth.ip.whitelist}") private String ipWhitelist;

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public BaseResponse checkPermission(@RequestParam String permission, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        // Allow browsing of the UI without logging in from whitelisted ip addresses.
        if (permission.equals("ui:view") && request.getRemoteAddr().matches(ipWhitelist)) {
            return new ViewObjectResponse<>(new PermissionView(permission, true, subject.isAuthenticated()));
        }

        if (subject.isPermitted(permission)) {
            return new ViewObjectResponse<>(new PermissionView(permission, true, subject.isAuthenticated()));
        }
        return new ViewObjectResponse<>(new PermissionView(permission, false, subject.isAuthenticated()));
    }
}
