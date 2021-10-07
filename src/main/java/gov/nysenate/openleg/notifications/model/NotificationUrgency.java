package gov.nysenate.openleg.notifications.model;

/**
 * Describes the urgency of notifications.
 */
public enum NotificationUrgency {

    INFO,       // Not urgent, used for informational purposes. i.e. A notification to inform us of a new API users.
    WARNING,    // Important, may require user intervention.
    ERROR       // Extremely urgent, something is broken.
    ;
}
