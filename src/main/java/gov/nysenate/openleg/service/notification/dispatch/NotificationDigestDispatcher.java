package gov.nysenate.openleg.service.notification.dispatch;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.notification.NotificationSearchDao;
import gov.nysenate.openleg.dao.notification.NotificationSubscriptionDao;
import gov.nysenate.openleg.model.notification.*;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.notification.data.NotificationDigestService;
import gov.nysenate.openleg.util.DateUtils;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A service that gets pending notification digest subscriptions, constructs the subscribed digests,
 *  and dispatches them to a notification digest sender
 */
@Service
public class NotificationDigestDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(NotificationDigestDispatcher.class);

    @Autowired
    private NotificationSubscriptionDao subDao;

    @Autowired
    private NotificationDigestService digestService;

    @Autowired
    private List<NotificationDigestSender> senderList;

    /** A map of target types (email, slack etc.) to services that will send formatted digests to these types */
    private ImmutableMap<NotificationTarget, NotificationDigestSender> senderMap;

    @PostConstruct
    public void init() {
        Map<NotificationTarget, NotificationDigestSender> senderMapInit = new HashMap<>();
        senderList.forEach(sender ->
                sender.getTargets().forEach(target -> senderMapInit.put(target, sender)));
        senderMap = ImmutableMap.copyOf(senderMapInit);
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void processPendingDigests() {
        Set<NotificationDigestSubscription> pendingDigests = subDao.getPendingDigests();
        if (!pendingDigests.isEmpty()) {
            logger.info("processing {} pending notification digests..", pendingDigests.size());
            pendingDigests.stream()
                    .peek(this::sendDigest)
                    .forEach(this::postProcess);
            logger.info("notification digests sent");
        }
    }

    public void sendDigest(NotificationDigestSubscription subscription) {
        NotificationDigest digest = null;
        try {
            digest = digestService.getDigest(subscription);

            if (subscription.isSendEmptyDigest() || !digest.isEmpty()) {
                logger.info("sending {} to {}:{}", NotificationDigestFormatter.getSummary(digest),
                        digest.getTarget(), digest.getAddress());
                senderMap.get(subscription.getTarget()).sendDigest(digest);
            }
        } catch (SearchException e) {
            logger.error("Could not retrieve notifications for digest subscription: \n" + subscription, e);
        }
    }

    /** --- Internal Methods --- */

    /** Sets the next digest time for a processed subscription */
    private void postProcess(NotificationDigestSubscription subscription) {
        subDao.updateNextDigest(subscription.getId(), subscription.getNewNextDigest());
    }
}
