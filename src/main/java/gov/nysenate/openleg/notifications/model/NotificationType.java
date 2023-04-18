package gov.nysenate.openleg.notifications.model;

public enum NotificationType {

    REQUEST_EXCEPTION(NotificationCategory.ERROR, NotificationUrgency.ERROR),
    PROCESS_EXCEPTION(NotificationCategory.ERROR, NotificationUrgency.ERROR),
    SPOTCHECK_EXCEPTION(NotificationCategory.ERROR, NotificationUrgency.ERROR),
    EVENT_BUS_EXCEPTION(NotificationCategory.ERROR, NotificationUrgency.ERROR),
    PROCESS_WARNING(NotificationCategory.ERROR, NotificationUrgency.WARNING),
    SCRAPING_EXCEPTION(NotificationCategory.ERROR, NotificationUrgency.WARNING),
    LRS_OUTAGE(NotificationCategory.ERROR, NotificationUrgency.WARNING),
    BAD_MEMBER_NAME(NotificationCategory.ERROR, NotificationUrgency.WARNING),
    BUDGET_BILL_EMPTY_TEXT(NotificationCategory.INFO, NotificationUrgency.WARNING),
    DAYBREAK_SPOTCHECK(NotificationCategory.INFO, NotificationUrgency.WARNING),
    CALENDAR_SPOTCHECK(NotificationCategory.INFO, NotificationUrgency.WARNING),
    AGENDA_SPOTCHECK(NotificationCategory.INFO, NotificationUrgency.WARNING),
    BILL_TEXT_SPOTCHECK(NotificationCategory.INFO, NotificationUrgency.WARNING),
    SENSITE_BILL_SPOTCHECK(NotificationCategory.INFO, NotificationUrgency.WARNING),
    SENSITE_CALENDAR_SPOTCHECK(NotificationCategory.INFO, NotificationUrgency.WARNING),
    SENSITE_AGENDA_SPOTCHECK(NotificationCategory.INFO, NotificationUrgency.WARNING),
    SENSITE_LAW_SPOTCHECK(NotificationCategory.INFO, NotificationUrgency.WARNING),
    OPENLEG_SPOTCHECK(NotificationCategory.INFO, NotificationUrgency.WARNING),
    NEW_API_KEY(NotificationCategory.INFO, NotificationUrgency.INFO);

    private final NotificationCategory category;
    private final NotificationUrgency urgency;

    NotificationType(NotificationCategory category, NotificationUrgency urgency) {
        this.category = category;
        this.urgency = urgency;
    }

    public NotificationCategory getCategory() {
        return category;
    }

    public NotificationUrgency getUrgency() {
        return urgency;
    }

    /**
     * Should notifications of this type be group with ErrorGroup's to prevent
     * too many notifications if an error is occurring frequently.
     * @return
     */
    public boolean shouldErrorGroup() {
        return this.category.equals(NotificationCategory.ERROR);
    }
}
