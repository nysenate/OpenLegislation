package gov.nysenate.openleg.api.admin;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import gov.nysenate.openleg.notifications.mail.MailConfigurationException;
import gov.nysenate.openleg.notifications.mail.apiuser.ApiUserBatchEmailServiceImpl;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailInProgressException;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailResult;
import gov.nysenate.openleg.notifications.mail.apiuser.BatchEmailStatus;
import gov.nysenate.openleg.notifications.mail.apiuser.ApiUserEmailRequest;
import gov.nysenate.openleg.notifications.mail.apiuser.ApiUserMessage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
     *                  plain-text body of the email, as well as a list of subscription types that the
     *                  email will be sent out to.
     *
     *  Sending continues in the background; the response is the batch's status as it begins
     *  (BatchEmailStatusView), and GET on the same path follows its progress. Each recipient's copy is sent
     *  separately. Only one batch email is sent at a time: while another is being sent, the response is
     *  409 Conflict and nothing is sent.
     */
    @RequiresPermissions("admin:email:post")
    @PostMapping(value = "/batchEmail")
    public BaseResponse sendEmail(@RequestBody ApiUserEmailRequest request) {
        ApiUserMessage message = getMessage(request);
        return new ViewObjectResponse<>(new BatchEmailStatusView(apiUserBatchEmailService.startMessage(message)));
    }

    /**
     *  Get The Batch Email Status
     *  --------------------------
     *
     *  The batch email being sent, or the last one sent since the server started (BatchEmailStatusView): the
     *  number of recipients, the number of copies sent so far, and each recipient so far that was skipped, failed,
     *  unconfirmed or not sent, with the reason. Test emails aren't included. There is no result if no batch email
     *  has been sent since the server started. Must be called by an admin.
     *
     *  (GET) /api/3/admin/email/batchEmail
     */
    @RequiresPermissions("admin:email:post")
    @GetMapping(value = "/batchEmail")
    public BaseResponse getBatchEmailStatus() {
        BatchEmailStatus status = apiUserBatchEmailService.getStatus();
        if (status == null) {
            return new SimpleResponse(true, "No batch email has been sent since the server started",
                    "batch-email-status");
        }
        return new ViewObjectResponse<>(new BatchEmailStatusView(status));
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
     *                  plain-text body of the email, as well as a list of subscription types that the
     *                  email would be sent out to if it were not a test email.
     *
     *  The response is a BatchEmailResultView for the one recipient.
     */
    @RequiresPermissions("admin:email:post")
    @PostMapping(value = "/testModeEmail")
    public BaseResponse sendTestEmail(@RequestBody ApiUserEmailRequest request) {
        ApiUserMessage message = getMessage(request);
        String email = (String)SecurityUtils.getSubject().getPrincipal();
        return resultResponse(apiUserBatchEmailService.sendTestMessage(email, message));
    }

    /**
     * A partial result is still a successful request: the admin follows up on the listed recipients.
     */
    private static BaseResponse resultResponse(BatchEmailResult result) {
        int total = result.total();
        String message = "Sent " + (result.unsent().isEmpty() ? "" : result.sent() + " of ") + total
                + (total == 1 ? " email" : " emails");
        return new ViewObjectResponse<>(new BatchEmailResultView(result), message);
    }

    /**
     *  Preview A Batch Email
     *  ---------------------
     *
     *  Renders the HTML part of a batch email, including the footer, as a subscriber would
     *  receive it. The subscriber's API key in the preferences link is replaced by a placeholder.
     *  Must be called by an admin.
     *
     *  (POST) /api/3/admin/email/preview
     *
     *  Request params: request (ApiUserEmailRequest) An object containing the plain-text body of
     *                  the email. The subject and subscriptions are ignored.
     */
    @RequiresPermissions("admin:email:post")
    @PostMapping(value = "/preview")
    public BaseResponse previewEmail(@RequestBody ApiUserEmailRequest request) {
        return new ViewObjectResponse<>(
                new BatchEmailPreviewView(apiUserBatchEmailService.renderPreviewHtml(request.getBody())));
    }

    /**
     * Tells the admin which mail setting prevents sending; nothing was sent.
     */
    @ExceptionHandler(MailConfigurationException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleMailConfigurationException(MailConfigurationException ex) {
        ErrorResponse response = new ErrorResponse(ErrorCode.MAIL_NOT_CONFIGURED);
        response.setMessage(ex.getMessage());
        return response;
    }

    /**
     * Tells the admin that another batch email is still being sent; nothing was sent.
     */
    @ExceptionHandler(BatchEmailInProgressException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleBatchEmailInProgressException(BatchEmailInProgressException ex) {
        ErrorResponse response = new ErrorResponse(ErrorCode.BATCH_EMAIL_IN_PROGRESS);
        response.setMessage(ex.getMessage());
        return response;
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
