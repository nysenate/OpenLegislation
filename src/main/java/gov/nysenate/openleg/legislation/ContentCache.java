package gov.nysenate.openleg.legislation;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillInfo;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum ContentCache {
    AGENDA(AgendaId.class, Agenda.class),
    BILL(BaseBillId.class, Bill.class),
    BILL_INFO(BaseBillId.class, BillInfo.class),
    CALENDAR(keyClass, valueClass),
    LAW(keyClass, valueClass),
    COMMITTEE(keyClass, valueClass),
    SESSION_MEMBER(keyClass, valueClass),
    FULL_MEMBER(keyClass, valueClass),
    SESSION_CHAMBER_SHORTNAME(keyClass, valueClass), // Session Member with a different key
    APIUSER(keyClass, valueClass),
    SHIRO(Object.class, Object.class),
    NOTIFICATION_SUBSCRIPTION(keyClass, valueClass);

    private static final ImmutableSet<ContentCache> allContentCaches = ImmutableSet.copyOf(ContentCache.values());
    private final Class<?> keyClass;
    private final Class<?> valueClass;

    ContentCache(Class<?> keyClass, Class<?> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public static ImmutableSet<ContentCache> getAllContentCaches() {
        return allContentCaches;
    }

    public Class<?> getKeyClass() {
        return keyClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }
}