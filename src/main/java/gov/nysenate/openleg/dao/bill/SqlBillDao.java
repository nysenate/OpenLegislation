package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;

public class SqlBillDao extends SqlBaseDao
{
    private static Logger logger = Logger.getLogger(SqlBillDao.class);

    public SqlBillDao() {
        super(null);
    }

    public SqlBillDao(Environment environment) {
        super(environment);
    }

    public Bill getBill(String printNo, int sessionYear) {
        return null;
    }

    private static String UPDATE_BILL_SQL =
        "UPDATE " + DEFAULT_ENV_SCHEMA + ".bill \n" +
        "SET title = ?, law_section = ?, summary = ?, active_version = ?, sponsor_id = ?, active_year = ?, " +
        "    modified_date_time = ?, published_date_time = ? \n" +
        "WHERE print_no = ? AND session_year = ?";

    private static String INSERT_BILL_SQL =
        "INSERT INTO " + DEFAULT_ENV_SCHEMA + ".bill (print_no, session_year, title, law_section, summary, active_version, " +
        "                                    sponsor_id, active_year, modified_date_time, published_date_time)\n" +
        "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ? \n" +
        "WHERE NOT EXISTS (SELECT 1 FROM " + DEFAULT_ENV_SCHEMA + ".bill WHERE print_no = ? AND session_year = ?)";

    public void saveBill(Bill bill, SOBIFragment sobiFragment) {
        if (bill != null) {
            /* try {

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
            } */
        }
    }
}
