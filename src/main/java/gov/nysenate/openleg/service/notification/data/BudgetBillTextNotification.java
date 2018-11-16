package gov.nysenate.openleg.service.notification.data;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.dao.bill.data.SqlBillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
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
    private SqlBillDao sqlBillDao;

    String summary = "MISSING BUDGET BILL TEXT";
    String message = "The following Budget Bill(s) text must be aquired from https://www.budget.ny.gov/pubs/archive/ : \n";

    @Autowired
    public BudgetBillTextNotification(EventBus eventBus, SqlBillDao sqlBillDao) {
        this.eventBus = eventBus;
        this.sqlBillDao = sqlBillDao;
    }

    @Scheduled(cron = "${scheduler.budget.bill.reminder}")
    private void sendBudgetNotifications() {
        LocalDateTime now = LocalDateTime.now();
        SessionYear sessionYear = new SessionYear(now.getYear());
        logger.info("Starting check for missing budget bill(s) text for session year " + sessionYear);

        List<BillId> billIds = getBudgetBillPrintNumbers(sessionYear.getYear());

        if (billIds.size() >= 1) {
            logger.info("Found " + billIds.size() + " issues");
            for (BillId billId: billIds) {
                message = message + billId.toString() + "\n";
            }
            eventBus.post(new Notification(NotificationType.WARNING,
                    now,
                    summary,
                    message));
        }
        logger.info("The check for missing budget bill text has been completed");
    }

    private List<BillId> getBudgetBillPrintNumbers(Integer sessionYear) {
         return sqlBillDao.getBudgetBillIdsWithoutText(sessionYear);
    }

}
