package gov.nysenate.openleg.api.admin;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import gov.nysenate.openleg.notifications.mail.apiuser.ApiUserBatchEmailServiceImpl;
import gov.nysenate.openleg.notifications.mail.apiuser.ApiUserEmailRequest;
import gov.nysenate.openleg.notifications.mail.apiuser.ApiUserMessage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(BASE_ADMIN_API_PATH + "/email")
public class AdminApiEmailCtrl extends BaseCtrl {
    private final ApiUserBatchEmailServiceImpl apiUserBatchEmailService;

    @Autowired
    public AdminApiEmailCtrl(ApiUserBatchEmailServiceImpl apiUserBatchEmailService) {
        this.apiUserBatchEmailService = apiUserBatchEmailService;
    }

    /**
     *  Send A Batch Email
     *  -----------------------------------
     *
     *  Sends a batch email to selected subscription types. Must be called by an admin.
     *
     *  (POST) /api/3/admin/email/batchEmail
     *
     *  Request params: request (ApiUserEmailRequest) An object containing the subject and
     *                  body of the email, as well as a list of subscription types that the
     *                  email will be sent out to.
     */
    @RequiresPermissions("admin:email:post")
    @PostMapping(value = "/batchEmail")
    public BaseResponse sendEmail(@RequestBody ApiUserEmailRequest request) {
        ApiUserMessage message = getMessage(request);
        apiUserBatchEmailService.sendMessage(message);
        return new SimpleResponse(true, "Emails have been sent", "Batch Email");
    }

    /**
     *  Send A Test Batch Email
     *  -----------------------
     *
     *  Sends a test batch email to the admin who makes the call. Must be called by an admin.
     *
     *  (POST) /api/3/admin/email/testModeEmail
     *
     *  Request params: request (ApiUserEmailRequest) An object containing the subject and
     *                  body of the email, as well as a list of subscription types that the
     *                  email would be sent out to if it were not a test email.
     */
    @RequiresPermissions("admin:email:post")
    @PostMapping(value = "/testModeEmail")
    public BaseResponse sendTestEmail(@RequestBody ApiUserEmailRequest request) {
        ApiUserMessage message = getMessage(request);
        String email = (String)SecurityUtils.getSubject().getPrincipal();
        apiUserBatchEmailService.sendTestMessage(email, message);
        return new SimpleResponse(true, "Emails have been sent", "Batch Email");
    }

    /**
     * Helper function to translate an ApiUserEmailRequest into an ApiUserMessage
     * @param request ApiUserEmailRequest
     * @return ApiUserMessage
     */
    protected ApiUserMessage getMessage(ApiUserEmailRequest request) {
        List<String> subs = request.getSubscriptions();
        String body = request.getBody();
        String subject = request.getSubject();
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        for(String sub: subs) {
            subscriptions.add(getEnumParameter("subscriptions", sub, ApiUserSubscriptionType.class));
        }
        return new ApiUserMessage(subscriptions, subject, body);
    }

}
