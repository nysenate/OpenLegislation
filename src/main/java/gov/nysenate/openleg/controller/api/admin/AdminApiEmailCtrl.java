package gov.nysenate.openleg.controller.api.admin;

import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import gov.nysenate.openleg.service.mail.apiuser.ApiUserBatchEmailServiceImpl;
import gov.nysenate.openleg.service.mail.apiuser.ApiUserEmailRequest;
import gov.nysenate.openleg.service.mail.apiuser.ApiUserMessage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(BASE_ADMIN_API_PATH + "/email")
public class AdminApiEmailCtrl extends BaseCtrl {

    @Autowired
    ApiUserBatchEmailServiceImpl apiUserBatchEmailService;


    @RequiresPermissions("admin:email:post")
    @RequestMapping(value = "/batchEmail", method = RequestMethod.POST)
    public void sendEmail(@RequestBody ApiUserEmailRequest request) {
        ApiUserMessage message = getMessage(request);
        apiUserBatchEmailService.sendMessage(message);
    }

    @RequiresPermissions("admin:email:post")
    @RequestMapping(value = "/testModeEmail", method = RequestMethod.POST)
    public void sendTestEmail(@RequestBody ApiUserEmailRequest request) {
        ApiUserMessage message = getMessage(request);
        String email = (String)SecurityUtils.getSubject().getPrincipal();
        apiUserBatchEmailService.sendTestMessage(email, message);
    }

    protected ApiUserMessage getMessage(ApiUserEmailRequest request) {
        List<String> subs = request.getSubscriptions();
        String body = request.getBody();
        String subject = request.getSubject();
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        for(String sub: subs) {
            subscriptions.add(ApiUserSubscriptionType.valueOf(sub));
        }
        return new ApiUserMessage(subscriptions, subject, body);
    }

}
