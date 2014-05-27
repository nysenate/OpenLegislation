package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;

public class BillDao extends BaseDao
{
    private static Logger logger = Logger.getLogger(BillDao.class);

    public Bill getBill(String printNo, int sessionYear) {
        return null;
    }

    private static String UPDATE_BILL_SQL =
        "UPDATE " + defaultEnvSchema + ".bill \n" +
        "SET title = ?, law_section = ?, summary = ?, active_version = ?, sponsor_id = ?, active_year = ?, " +
        "    modified_date_time = ?, published_date_time = ? \n" +
        "WHERE print_no = ? AND session_year = ?";

    private static String INSERT_BILL_SQL =
        "INSERT INTO " + defaultEnvSchema + ".bill (print_no, session_year, title, law_section, summary, active_version, " +
        "                                    sponsor_id, active_year, modified_date_time, published_date_time)\n" +
        "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ? \n" +
        "WHERE NOT EXISTS (SELECT 1 FROM " + defaultEnvSchema + ".bill WHERE print_no = ? AND session_year = ?)";

    public void saveBill(Bill bill, SOBIFragment sobiFragment) {
        if (bill != null) {
            try {

                runner.update(UPDATE_BILL_SQL, bill.getTitle(), bill.getLawSection(), bill.getSummary(), bill.getActiveVersion(),
                        bill.getSponsor().getId(), bill.getYear(), new Timestamp(bill.getModifiedDate().getTime()),
                        new Timestamp(bill.getPublishDate().getTime()), bill.getPrintNumber(), bill.getSession());

                runner.update(INSERT_BILL_SQL,
                        bill.getPrintNumber(), bill.getSession(), bill.getTitle(), bill.getLawSection(),
                        bill.getSummary(), bill.getActiveVersion(), bill.getSponsor().getId(), bill.getYear(),
                        new Timestamp(bill.getModifiedDate().getTime()), new Timestamp(bill.getPublishDate().getTime()), bill.getPrintNumber(), bill.getSession());
            }
            catch (SQLException e) {
                logger.error("Failed to ", e);
            }
        }
    }
}
