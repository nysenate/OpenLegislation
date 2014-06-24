package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class SqlBillDao extends SqlBaseDao implements BillDao
{
    private static Logger logger = Logger.getLogger(SqlBillDao.class);

    public SqlBillDao(Environment environment) {
        super(environment);
    }

    /* --- Implemented Methods --- */

    @Override
    public Bill getBill(String printNo, int sessionYear) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", printNo);
        params.addValue("sessionYear", sessionYear);
        try {
            Bill bill = jdbcNamed.queryForObject(SqlBillQuery.SELECT_BILL_SQL.getSql(schema()), params, new BillRowMapper());
            List<BillAmendment> billAmendments =
                jdbcNamed.query(SqlBillQuery.SELECT_BILL_AMENDMENTS_SQL.getSql(schema()), params, new BillAmendmentRowMapper());
            for (BillAmendment amendment : billAmendments) {
                params.addValue("version", amendment.getVersion());
                List<BillId> sameAsList =
                    jdbcNamed.query(SqlBillQuery.SELECT_BILL_SAME_AS_SQL.getSql(schema()), params, new BillSameAsRowMapper());
                amendment.setSameAs(new HashSet<>(sameAsList));
            }
            List<BillAction> billActions =
                jdbcNamed.query(SqlBillQuery.SELECT_BILL_ACTIONS_SQL.getSql(schema()), params, new BillActionRowMapper());
            bill.addAmendments(billAmendments);
            bill.setActions(billActions);
            return bill;
        }
        catch (EmptyResultDataAccessException ex) {
            logger.debug("Bill " + printNo + "-" + sessionYear + " does not exist in database.");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Updates information for an existing bill or creates new records if the bill is new.
     * Due to the normalized nature of the database it takes several queries to update all
     * the relevant pieces of data contained within the Bill object. The sobiFragment
     * reference is used to keep track of changes to the bill.
     */
    @Override
    public void updateBill(Bill bill, SOBIFragment sobiFragment) {
        // Update the bill record
        MapSqlParameterSource billParams = getBillParams(bill, sobiFragment);
        if (jdbcNamed.update(SqlBillQuery.UPDATE_BILL_SQL.getSql(schema()), billParams) == 0) {
            jdbcNamed.update(SqlBillQuery.INSERT_BILL_SQL.getSql(schema()), billParams);
        }
        // Update the bill amendments
        for (BillAmendment amendment : bill.getAmendmentList()) {
            MapSqlParameterSource amendParams = getBillAmendmentParams(amendment, sobiFragment);
            if (jdbcNamed.update(SqlBillQuery.UPDATE_BILL_AMENDMENT_SQL.getSql(schema()), amendParams) == 0) {
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_AMENDMENT_SQL.getSql(schema()), amendParams);
            }
            // Update the same as bills
            List<BillId> existingSameAs =
                jdbcNamed.query(SqlBillQuery.SELECT_BILL_SAME_AS_SQL.getSql(schema()), amendParams, new BillSameAsRowMapper());
            if (existingSameAs.size() != amendment.getSameAs().size() || !existingSameAs.containsAll(amendment.getSameAs())) {
                jdbcNamed.update(SqlBillQuery.DELETE_SAME_AS_FOR_BILL_SQL.getSql(schema()), amendParams);
                for (BillId sameAsBillId : amendment.getSameAs()) {
                    MapSqlParameterSource sameAsParams = getBillSameAsParams(amendment, sameAsBillId, sobiFragment);
                    jdbcNamed.update(SqlBillQuery.INSERT_BILL_SAME_AS_SQL.getSql(schema()), sameAsParams);
                }
            }
        }
        // Determine which actions need to be inserted/deleted. Individual actions are not updated.
        List<BillAction> existingBillActions =
            jdbcNamed.query(SqlBillQuery.SELECT_BILL_ACTIONS_SQL.getSql(schema()), billParams, new BillActionRowMapper());
        List<BillAction> newBillActions = new ArrayList<>(bill.getActions());
        newBillActions.removeAll(existingBillActions);    // New actions to insert
        existingBillActions.removeAll(bill.getActions()); // Old actions to delete
        // Delete actions that are not in the updated list
        for (BillAction action : existingBillActions) {
            MapSqlParameterSource actionParams = getBillActionParams(action, sobiFragment);
            jdbcNamed.update(SqlBillQuery.DELETE_BILL_ACTION_SQL.getSql(schema()), actionParams);
        }
        // Insert all new actions
        for (BillAction action : newBillActions) {
            MapSqlParameterSource actionParams = getBillActionParams(action, sobiFragment);
            jdbcNamed.update(SqlBillQuery.INSERT_BILL_ACTION_SQL.getSql(schema()), actionParams);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBill(Bill bill) {

    }

    /** {@inheritDoc} */
    @Override
    public void publishBill(Bill bill) {

    }

    /** {@inheritDoc} */
    @Override
    public void unPublishBill(Bill bill) {

    }

    /** {@inheritDoc} */
    @Override
    public void deleteAllBills() {

    }

    /** --- Helper Classes --- */

    private class BillRowMapper implements RowMapper<Bill>
    {
        @Override
        public Bill mapRow(ResultSet rs, int rowNum) throws SQLException {
            Bill bill = new Bill();
            bill.setPrintNo(rs.getString("print_no"));
            bill.setSession(rs.getInt("session_year"));
            bill.setTitle(rs.getString("title"));
            bill.setLawSection(rs.getString("law_section"));
            bill.setLaw(rs.getString("law_code"));
            bill.setSummary(rs.getString("summary"));
            bill.setActiveVersion(rs.getString("active_version").trim());
            bill.setSponsor(null /** TODO */);
            bill.setYear(rs.getInt("active_year"));
            bill.setModifiedDate(rs.getDate("modified_date_time"));
            return bill;
        }
    }

    private class BillAmendmentRowMapper implements RowMapper<BillAmendment>
    {
        @Override
        public BillAmendment mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillAmendment amend = new BillAmendment();
            amend.setBaseBillPrintNo(rs.getString("bill_print_no"));
            amend.setSession(rs.getInt("bill_session_year"));
            amend.setVersion(rs.getString("version"));
            amend.setMemo(rs.getString("sponsor_memo"));
            amend.setActClause(rs.getString("act_clause"));
            amend.setFulltext(rs.getString("full_text"));
            amend.setStricken(rs.getBoolean("stricken"));
            amend.setCurrentCommittee(null); /** TODO */
            amend.setUniBill(rs.getBoolean("uni_bill"));
            amend.setModifiedDate(rs.getDate("modified_date_time"));
            amend.setPublishDate(rs.getDate("published_date_time"));
            return amend;
        }
    }

    private class BillActionRowMapper implements RowMapper<BillAction>
    {
        @Override
        public BillAction mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillAction billAction = new BillAction();
            billAction.setBaseBillPrintNo(rs.getString("bill_print_no"));
            billAction.setAmendmentVersion(rs.getString("bill_amend_version"));
            billAction.setSession(rs.getInt("bill_session_year"));
            billAction.setDate(rs.getDate("effect_date"));
            billAction.setText(rs.getString("text"));
            billAction.setModifiedDate(rs.getDate("modified_date_time"));
            billAction.setPublishDate(rs.getDate("published_date_time"));
            return billAction;
        }
    }

    private class BillSameAsRowMapper implements RowMapper<BillId>
    {
        @Override
        public BillId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BillId(rs.getString("same_as_bill_print_no"), rs.getInt("same_as_session_year"),
                              rs.getString("same_as_amend_version"));
        }
    }

    /** --- Internal Methods --- */

    /**
     * Returns a MapSqlParameterSource with columns mapped to Bill values for use in update/insert queries on
     * the bill table.
     * @param bill String
     * @return MapSqlParameterSource
     */
    private MapSqlParameterSource getBillParams(Bill bill, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", bill.getPrintNo());
        params.addValue("sessionYear", bill.getSession());
        params.addValue("title", bill.getTitle());
        params.addValue("lawSection", bill.getLawSection());
        params.addValue("lawCode", bill.getLaw());
        params.addValue("summary", bill.getSummary());
        params.addValue("activeVersion", bill.getActiveVersion());
        params.addValue("sponsorId", null /**TODO */);
        params.addValue("activeYear", bill.getYear());
        params.addValue("modifiedDateTime", toTimestamp(bill.getModifiedDate()));
        params.addValue("publishedDateTime", toTimestamp(bill.getPublishDate()));
        addSOBIFragmentParams(fragment, params);
        return params;
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to BillAmendment values for use in update/insert
     * queries on the bill amendment table.
     * @param amendment BillAmendment
     * @param fragment SOBIFragment
     * @return MapSqlParameterSource
     */
    private MapSqlParameterSource getBillAmendmentParams(BillAmendment amendment, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", amendment.getBaseBillPrintNo());
        params.addValue("sessionYear", amendment.getSession());
        params.addValue("version", amendment.getVersion());
        params.addValue("sponsorMemo", amendment.getMemo());
        params.addValue("actClause", amendment.getActClause());
        params.addValue("fullText", amendment.getFulltext());
        params.addValue("stricken", amendment.isStricken());
        params.addValue("currentCommitteeId", null);
        params.addValue("uniBill", amendment.isUniBill());
        params.addValue("modifiedDateTime", toTimestamp(amendment.getModifiedDate()));
        params.addValue("publishedDateTime", toTimestamp(amendment.getPublishDate()));
        addSOBIFragmentParams(fragment, params);
        return params;
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to BillAction for use in inserting records
     * into the bill action table.
     * @param billAction BillAction
     * @param fragment SOBIFragment
     * @return MapSqlParameterSource
     */
    private MapSqlParameterSource getBillActionParams(BillAction billAction, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", billAction.getBaseBillPrintNo());
        params.addValue("sessionYear", billAction.getSession());
        params.addValue("version", billAction.getAmendmentVersion());
        params.addValue("effectDate", billAction.getDate());
        params.addValue("text", billAction.getText());
        params.addValue("modifiedDateTime", toTimestamp(billAction.getModifiedDate()));
        params.addValue("publishedDateTime", toTimestamp(billAction.getPublishDate()));
        addSOBIFragmentParams(fragment, params);
        return params;
    }

    private MapSqlParameterSource getBillSameAsParams(BillAmendment billAmendment, BillId sameAs, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", billAmendment.getBaseBillPrintNo());
        params.addValue("sessionYear", billAmendment.getSession());
        params.addValue("version", billAmendment.getVersion());
        params.addValue("sameAsPrintNo", sameAs.getBasePrintNo());
        params.addValue("sameAsSessionYear", sameAs.getSession());
        params.addValue("sameAsVersion", sameAs.getVersion());
        addSOBIFragmentParams(fragment, params);
        return params;
    }

    /**
     * Applies columns that identify a SOBIFragment to an existing MapSqlParameterSource.
     * @param fragment SOBIFragment
     * @param params MapSqlParameterSource
     */
    private void addSOBIFragmentParams(SOBIFragment fragment, MapSqlParameterSource params) {
        params.addValue("lastFragmentFileName", fragment.getFileName());
        params.addValue("lastFragmentType", fragment.getType().name());
    }
}
