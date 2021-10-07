package gov.nysenate.openleg.notifications.budgetbill;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.legislation.bill.dao.BillDao;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BudgetBillTextNotification {

    private static final Logger logger = LoggerFactory.getLogger(BudgetBillTextNotification.class);
    private final EventBus eventBus;
    private BillDao billDao;

    private static final String summary = "MISSING BUDGET BILL TEXT";

    @Autowired
    public BudgetBillTextNotification(EventBus eventBus, BillDao billDao) {
        this.eventBus = eventBus;
        this.billDao = billDao;
    }

    @Scheduled(cron = "${scheduler.budget.bill.reminder}")
    private void sendBudgetNotifications() {
        String message = "The following Budget Bill(s) text must be acquired from https://www.budget.ny.gov/pubs/archive/ : \n";
        SessionYear sessionYear = SessionYear.current();
        logger.info("Starting check for missing budget bill(s) text for session year " + sessionYear);

        List<BillId> missingText = billDao.getBudgetBillIdsWithoutText(sessionYear);

        if (missingText.size() >= 1) {

            logger.info("Found " + missingText.size() + " issues");
            for (BillId billId: missingText) {
                message = message + billId.toString() + "\n";
            }
            eventBus.post(new Notification(
                    NotificationType.BUDGET_BILL_EMPTY_TEXT,
                    LocalDateTime.now(),
                    summary,
                    message));
        }
        logger.info("The check for missing budget bill text has been completed");
    }
}
