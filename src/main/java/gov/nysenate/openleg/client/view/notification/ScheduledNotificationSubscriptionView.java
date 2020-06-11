package gov.nysenate.openleg.client.view.notification;

import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.ScheduledNotificationSubscription;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduledNotificationSubscriptionView extends NotificationSubscriptionView {

    private List<String> daysOfWeek;
    private String timeOfDay;
    private boolean sendEmpty;

    public ScheduledNotificationSubscriptionView(ScheduledNotificationSubscription subscription) {
        super(subscription);
        this.daysOfWeek = subscription.getDaysOfWeek().stream().map(DayOfWeek::name).collect(Collectors.toList());
        this.timeOfDay = subscription.getTimeOfDay().toString();
        this.sendEmpty = subscription.sendEmpty();
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public boolean isSendEmpty() {
        return sendEmpty;
    }

    @Override
    public String getViewType() {
        return "scheduled-notification-subscription";
    }
}
