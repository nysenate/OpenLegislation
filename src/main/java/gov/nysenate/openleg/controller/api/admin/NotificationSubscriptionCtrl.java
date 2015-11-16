package gov.nysenate.openleg.controller.api.admin;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.view.notification.NotificationSubscriptionView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParamEx;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.service.notification.subscription.NotificationSubscriptionDataService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BaseCtrl.BASE_ADMIN_API_PATH + "/notifications", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class NotificationSubscriptionCtrl extends BaseCtrl
{
    @Autowired
    private NotificationSubscriptionDataService subscriptionDataService;

    /**
     * Notification Subscription API
     * -----------------------------
     *
     * Register for notification tracking (GET) /api/3/admin/notifications/subscribe
     * Request Parameters:  type (string) - The Notification Type to subscribe for.
     *                      target (string) - The medium through which the notification will be sent.
     *                      address (string) - The address for the specified target medium.
     *
     */
    @RequiresPermissions("admin:notification-subscribe")
    @RequestMapping(value = "/subscribe")
    public BaseResponse subscribeToNotification(@RequestParam String type,
                                                @RequestParam String target,
                                                @RequestParam String address) {
        NotificationSubscription subscription = buildSubscriptionFromParams(type, target, address);
        subscriptionDataService.insertSubscription(subscription);
        return new ViewObjectResponse<>(new NotificationSubscriptionView(subscription));
    }


    /**
     * Notification Unsubscribe API
     * ----------------------------
     *
     * Unsubscribe from a notification (GET) /api/3/admin/notifications/unsubscribe
     * @see #subscribeToNotification
     */
    @RequiresPermissions("admin:notification-subscribe")
    @RequestMapping(value = "/unsubscribe")
    public BaseResponse unsubscribeFromNotification(@RequestParam String type,
                                                    @RequestParam String target,
                                                    @RequestParam String address) {
        NotificationSubscription subscription = buildSubscriptionFromParams(type, target, address);
        subscriptionDataService.removeSubscription(subscription);
        return new ViewObjectResponse<>(new NotificationSubscriptionView(subscription));
    }

    /**
     * Notification Subscription Listing API
     * -------------------------------------
     *
     * Lists the logged in admin's notification subscriptions (GET) /api/3/admin/notifications/subscriptions
     *
     * Request Parameters: limit, offset (int) - Paginate.
     *
     * Expected Output: List of NotificationSubscriptionView
     */
    @RequiresPermissions("admin:notification-subscribe")
    @RequestMapping(value = "/subscriptions")
    public BaseResponse viewSubscriptions(WebRequest request) {
        String user = (String) SecurityUtils.getSubject().getPrincipal();
        LimitOffset limOff = getLimitOffset(request, 0);
        return ListViewResponse.of(subscriptionDataService.getSubscriptions(user).stream()
                               .map(NotificationSubscriptionView::new).collect(Collectors.toList()),
                0, limOff);
    }

    /** --- Internal --- */

    private NotificationSubscription buildSubscriptionFromParams(String type, String target, String address) {
        String user = (String) SecurityUtils.getSubject().getPrincipal();
        NotificationType notificationType = getEnumParameter("type", type, NotificationType.class);
        NotificationTarget notificationTarget = getNotificationTargetFromString(target);
        return new NotificationSubscription(user, notificationType, notificationTarget, address);
    }

    private NotificationTarget getNotificationTargetFromString(String text) {
        try {
            return NotificationTarget.getValue(text);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestParamEx(text, "target", "String",
                    NotificationTarget.getAllNotificationTargets().stream()
                            .map(NotificationTarget::toString)
                            .reduce("", (a, b) -> a + "|" + b));
        }
    }
}
