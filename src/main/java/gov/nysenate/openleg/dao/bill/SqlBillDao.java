package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.dao.entity.MemberDao;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.service.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.entity.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class SqlBillDao extends SqlBaseDao implements BillDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillDao.class);

    @Autowired
    private MemberService memberService;

    /* --- Implemented Methods --- */

    @Override
    public Bill getBill(BillId billId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", billId.getBasePrintNo());
        params.addValue("sessionYear", billId.getSession());
        logger.trace("Fetching Bill {} from database...", billId);
        // Retrieve base Bill object
        Bill bill = getBaseBill(params);
        // Fetch the amendments
        List<BillAmendment> billAmendments = getBillAmendment(params);
        for (BillAmendment amendment : billAmendments) {
            params.addValue("version", amendment.getVersion());
            // Fetch all the same as bill ids
            amendment.setSameAs(new HashSet<>(getSameAsBills(params)));
            // Get the cosponsors for the amendment
            amendment.setCoSponsors(getCoSponsors(params));
            // Get the multi-sponsors for the amendment
            amendment.setMultiSponsors(getMultiSponsors(params));
        }
        // Set the amendments
        bill.addAmendments(billAmendments);
        // Get the sponsor
        bill.setSponsor(getBillSponsor(params));
        // Get the actions
        bill.setActions(getBillActions(params));
        // Get the prev bill version ids
        bill.setPreviousVersions(new HashSet<>(getPrevVersions(params)));
        return bill;
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
        logger.trace("Updating Bill {} in database...", bill);
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
            updateBillSameAs(amendment, sobiFragment, amendParams);
            // Update the co-sponsors list
            updateBillCosponsor(amendment, sobiFragment, amendParams);
            // Update the multi-sponsors list
            updateBillMultiSponsor(amendment, sobiFragment, amendParams);
            // Update votes
            updateBillVotes(amendment, sobiFragment, amendParams);
        }
        // Update the sponsor
        updateBillSponsor(bill, sobiFragment, billParams);
        // Determine which actions need to be inserted/deleted. Individual actions are never updated.
        updateActions(bill, sobiFragment, billParams);
        // Determine if the previous versions have changed and insert accordingly.
        updatePreviousBillVersions(bill, sobiFragment, billParams);
    }

    /** {@inheritDoc} */
    @Override
    public void publishBill(Bill bill) {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    @Override
    public void unPublishBill(Bill bill) {
        throw new NotImplementedException();
    }

    /** --- Internal Methods --- */

    private Bill getBaseBill(MapSqlParameterSource params) {
        return jdbcNamed.queryForObject(SqlBillQuery.SELECT_BILL_SQL.getSql(schema()), params, new BillRowMapper());
    }

    private List<BillAction> getBillActions(MapSqlParameterSource params) {
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_ACTIONS_SQL.getSql(schema()), params, new BillActionRowMapper());
    }

    private List<BillId> getPrevVersions(MapSqlParameterSource params) {
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_PREVIOUS_VERSIONS_SQL.getSql(schema()), params,
                               new BillPreviousVersionRowMapper());
    }

    private List<BillId> getSameAsBills(MapSqlParameterSource params) {
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_SAME_AS_SQL.getSql(schema()), params, new BillSameAsRowMapper());
    }

    private List<BillVote> getBillVotes(MapSqlParameterSource params) {
//        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_VOTES_SQL.getSql(schema()), params, new RowCallbackHandler() {
//            @Override
//            public void processRow(ResultSet rs) throws SQLException {
//
//            }
//        })
    return null;
    }

    private BillSponsor getBillSponsor(MapSqlParameterSource params) {
        try {
            return jdbcNamed.queryForObject(
                SqlBillQuery.SELECT_BILL_SPONSOR_SQL.getSql(schema()), params, new BillSponsorRowMapper(memberService));
        }
        catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private List<BillAmendment> getBillAmendment(MapSqlParameterSource params) {
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_AMENDMENTS_SQL.getSql(schema()), params, new BillAmendmentRowMapper());
    }

    private List<Member> getCoSponsors(MapSqlParameterSource params) {
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_MULTISPONSORS_SQL.getSql(schema()), params, new BillMemberRowMapper(memberService));
    }

    private List<Member> getMultiSponsors(MapSqlParameterSource params) {
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_MULTISPONSORS_SQL.getSql(schema()), params, new BillMemberRowMapper(memberService));
    }

    /**
     * Save the bill's same as list by replacing the existing records with the current records if they are not
     * the same.
     */
    private void updateBillSameAs(BillAmendment amendment, SOBIFragment sobiFragment, MapSqlParameterSource amendParams) {
        List<BillId> existingSameAs = getSameAsBills(amendParams);
        if (existingSameAs.size() != amendment.getSameAs().size() || !existingSameAs.containsAll(amendment.getSameAs())) {
            jdbcNamed.update(SqlBillQuery.DELETE_SAME_AS_FOR_BILL_SQL.getSql(schema()), amendParams);
            for (BillId sameAsBillId : amendment.getSameAs()) {
                MapSqlParameterSource sameAsParams = getBillSameAsParams(amendment, sameAsBillId, sobiFragment);
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_SAME_AS_SQL.getSql(schema()), sameAsParams);
            }
        }
    }

    /**
     * Save the bill's action list into the database. Only delete/insert where necessary to allow for a
     * better snapshot of how the actions came in.
     */
    private void updateActions(Bill bill, SOBIFragment sobiFragment, MapSqlParameterSource billParams) {
        List<BillAction> existingBillActions = getBillActions(billParams);
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

    /**
     * Save the bill's previous version list by replacing the existing list with the current list.
     */
    private void updatePreviousBillVersions(Bill bill, SOBIFragment sobiFragment, MapSqlParameterSource billParams) {
        List<BillId> existingPrevBills = getPrevVersions(billParams);
        if (existingPrevBills.size() != bill.getPreviousVersions().size() || existingPrevBills.containsAll(bill.getPreviousVersions())) {
            jdbcNamed.update(SqlBillQuery.DELETE_BILL_PREVIOUS_VERSIONS_SQL.getSql(schema()), billParams);
        }
        for (BillId prevBillId : bill.getPreviousVersions()) {
            MapSqlParameterSource prevParams = getBillPrevVersionParams(bill, prevBillId, sobiFragment);
            jdbcNamed.update(SqlBillQuery.INSERT_BILL_PREVIOUS_VERSION_SQL.getSql(schema()), prevParams);
        }
    }

    /**
     * Update the bill's sponsor information.
     */
    private void updateBillSponsor(Bill bill, SOBIFragment sobiFragment, MapSqlParameterSource billParams) {
        if (bill.getSponsor() != null) {
            MapSqlParameterSource params = getBillSponsorParams(bill, sobiFragment);
            if (jdbcNamed.update(SqlBillQuery.UPDATE_BILL_SPONSOR_SQL.getSql(schema()), params) == 0) {
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_SPONSOR_SQL.getSql(schema()), params);
            }
        }
        else {
            jdbcNamed.update(SqlBillQuery.DELETE_BILL_SPONSOR_SQL.getSql(schema()), billParams);
        }
    }

    /**
     * Update the bill's co sponsor list.
     */
    private void updateBillCosponsor(BillAmendment billAmendment, SOBIFragment sobiFragment, MapSqlParameterSource amendParams) {
        List<Member> existingCoSponsors = getCoSponsors(amendParams);
        if (!existingCoSponsors.equals(billAmendment.getCoSponsors())){
            jdbcNamed.update(SqlBillQuery.DELETE_BILL_COSPONSORS_SQL.getSql(schema()), amendParams);
            int sequenceNo = 1;
            // Iterate through a linked set to preserve order while removing possible duplicates.
            for (Member member : new LinkedHashSet<>(billAmendment.getCoSponsors())) {
                if (member != null) {
                    MapSqlParameterSource params = getCoMultiSponsorMemberParams(billAmendment, member, sequenceNo++, sobiFragment);
                    jdbcNamed.update(SqlBillQuery.INSERT_BILL_COSPONSORS_SQL.getSql(schema()), params);
                }
            }
        }
    }

    /**
     * Update the bill's multi sponsor list.
     */
    private void updateBillMultiSponsor(BillAmendment billAmendment, SOBIFragment sobiFragment, MapSqlParameterSource amendParams) {
        List<Member> existingMultiSponsors = getMultiSponsors(amendParams);
        if (!existingMultiSponsors.equals(billAmendment.getMultiSponsors())){
            jdbcNamed.update(SqlBillQuery.DELETE_BILL_MULTISPONSORS_SQL.getSql(schema()), amendParams);
            int sequenceNo = 1;
            for (Member member : billAmendment.getMultiSponsors()) {
                if (member != null) {
                    MapSqlParameterSource params = getCoMultiSponsorMemberParams(billAmendment, member, sequenceNo++, sobiFragment);
                    jdbcNamed.update(SqlBillQuery.INSERT_BILL_MULTISPONSORS_SQL.getSql(schema()), params);
                }
            }
        }
    }

    /**
     * Update the bill amendment's list of votes.
     */
    private void updateBillVotes(BillAmendment amendment, SOBIFragment sobiFragment, MapSqlParameterSource amendParams) {
    }

    /** --- Helper Classes --- */

    private static class BillRowMapper implements RowMapper<Bill>
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
            bill.setYear(rs.getInt("active_year"));
            bill.setModifiedDate(rs.getDate("modified_date_time"));
            bill.setPublishDate(rs.getDate("published_date_time"));
            return bill;
        }
    }

    private static class BillAmendmentRowMapper implements RowMapper<BillAmendment>
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

    private static class BillActionRowMapper implements RowMapper<BillAction>
    {
        @Override
        public BillAction mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillAction billAction = new BillAction();
            billAction.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                                            rs.getString("bill_amend_version")));
            billAction.setSession(rs.getInt("bill_session_year"));
            billAction.setSequenceNo(rs.getInt("sequence_no"));
            billAction.setDate(rs.getDate("effect_date"));
            billAction.setText(rs.getString("text"));
            billAction.setModifiedDate(rs.getDate("modified_date_time"));
            billAction.setPublishDate(rs.getDate("published_date_time"));
            return billAction;
        }
    }

    private static class BillSameAsRowMapper implements RowMapper<BillId>
    {
        @Override
        public BillId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BillId(rs.getString("same_as_bill_print_no"), rs.getInt("same_as_session_year"),
                              rs.getString("same_as_amend_version"));
        }
    }

    private static class BillPreviousVersionRowMapper implements RowMapper<BillId>
    {
        @Override
        public BillId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BillId(rs.getString("prev_bill_print_no"), rs.getInt("prev_bill_session_year"),
                              rs.getString("prev_amend_version"));
        }
    }

    private static class BillSponsorRowMapper implements RowMapper<BillSponsor>
    {
        MemberService memberService;

        private BillSponsorRowMapper(MemberService memberService) {
            this.memberService = memberService;
        }

        @Override
        public BillSponsor mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillSponsor sponsor = new BillSponsor();
            int memberId = rs.getInt("member_id");
            int sessionYear = rs.getInt("bill_session_year");
            sponsor.setBudgetBill(rs.getBoolean("budget_bill"));
            sponsor.setRulesSponsor(rs.getBoolean("rules_sponsor"));
            if (memberId > 0) {
                try {
                    sponsor.setMember(memberService.getMemberById(memberId, sessionYear));
                }
                catch (MemberNotFoundEx memberNotFoundEx) {
                    logger.warn("Bill referenced a sponsor that does not exist. {}", memberNotFoundEx.getMessage());
                }
            }
            return sponsor;
        }
    }

    private static class BillMemberRowMapper implements RowMapper<Member>
    {
        MemberService memberService;

        private BillMemberRowMapper(MemberService memberService) {
            this.memberService = memberService;
        }

        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            int memberId = rs.getInt("member_id");
            int sessionYear = rs.getInt("bill_session_year");
            try {
                return memberService.getMemberById(memberId, sessionYear);
            }
            catch (MemberNotFoundEx memberNotFoundEx) {
                logger.warn("Bill referenced a member that does not exist: {}", memberNotFoundEx.getMessage());
            }
            return null;
        }
    }

    private static class BillVoteRowCallbackHandler implements RowCallbackHandler
    {
        private TreeMap<String, BillVote> billVoteMap = new TreeMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            BillId billId = new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"), rs.getString("bill_amend_version"));
            Date voteDate = rs.getDate("vote_date");
            int voteType = rs.getInt("vote_type");
            int sequenceNo = rs.getInt("sequence_no");

            BillVote billVote = new BillVote(billId, voteDate, BillVoteType.valueOfCode(voteType), sequenceNo);

            if (!billVoteMap.containsKey(billVote.getVoteId())) {
                billVoteMap.put(billVote.getVoteId(), billVote);
            }

            billVote = billVoteMap.get(billVote.getVoteId());
            rs.getString("vote_code");
            rs.getInt("member_id");
            rs.getString("member_short_name");
            rs.getDate("published_date_time");
            rs.getDate("modified_date_time");
        }
    }

    /** --- Param Source Methods --- */

    /**
     * Returns a MapSqlParameterSource with columns mapped to Bill values for use in update/insert queries on
     * the bill table.
     * @param bill String
     * @return MapSqlParameterSource
     */
    private static MapSqlParameterSource getBillParams(Bill bill, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("title", bill.getTitle());
        params.addValue("lawSection", bill.getLawSection());
        params.addValue("lawCode", bill.getLaw());
        params.addValue("summary", bill.getSummary());
        params.addValue("activeVersion", bill.getActiveVersion());
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
    private static MapSqlParameterSource getBillAmendmentParams(BillAmendment amendment, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(amendment, params);
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
    private static MapSqlParameterSource getBillActionParams(BillAction billAction, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", billAction.getBillId().getBasePrintNo());
        params.addValue("sessionYear", billAction.getBillId().getSession());
        params.addValue("version", billAction.getBillId().getVersion());
        params.addValue("effectDate", billAction.getDate());
        params.addValue("text", billAction.getText());
        params.addValue("sequenceNo", billAction.getSequenceNo());
        params.addValue("modifiedDateTime", toTimestamp(billAction.getModifiedDate()));
        params.addValue("publishedDateTime", toTimestamp(billAction.getPublishDate()));
        addSOBIFragmentParams(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillSameAsParams(BillAmendment billAmendment, BillId sameAs, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("sameAsPrintNo", sameAs.getBasePrintNo());
        params.addValue("sameAsSessionYear", sameAs.getSession());
        params.addValue("sameAsVersion", sameAs.getVersion());
        addSOBIFragmentParams(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillPrevVersionParams(Bill bill, BillId prevVersion, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("prevPrintNo", prevVersion.getBasePrintNo());
        params.addValue("prevSessionYear", prevVersion.getSession());
        params.addValue("prevVersion", prevVersion.getVersion());
        addSOBIFragmentParams(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillSponsorParams(Bill bill, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        BillSponsor billSponsor = bill.getSponsor();
        boolean hasMember = billSponsor != null && billSponsor.hasMember();
        addBillIdParams(bill, params);
        params.addValue("memberId", (hasMember) ? billSponsor.getMember().getMemberId() : null);
        params.addValue("budgetBill", (billSponsor != null && billSponsor.isBudgetBill()));
        params.addValue("rulesSponsor", (billSponsor != null && billSponsor.isRulesSponsor()));
        addSOBIFragmentParams(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getCoMultiSponsorMemberParams(BillAmendment billAmendment, Member member,
                                                                       int sequenceNo, SOBIFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("memberId", member.getMemberId());
        params.addValue("sequenceNo", sequenceNo);
        addSOBIFragmentParams(fragment, params);
        return params;
    }

    /**
     * Applies columns that identify the base bill.
     * @param bill Bill
     * @param params MapSqlParameterSource
     */
    private static void addBillIdParams(Bill bill, MapSqlParameterSource params) {
        params.addValue("printNo", bill.getPrintNo());
        params.addValue("sessionYear", bill.getSession());
    }

    /**
     * Adds columns that identify the bill amendment.
     * @param billAmendment BillAmendment
     * @param params MapSqlParameterSource
     */
    private static void addBillIdParams(BillAmendment billAmendment, MapSqlParameterSource params) {
        params.addValue("printNo", billAmendment.getBaseBillPrintNo());
        params.addValue("sessionYear", billAmendment.getSession());
        params.addValue("version", billAmendment.getVersion());
    }

    /**
     * Applies columns that identify a SOBIFragment to an existing MapSqlParameterSource.
     * @param fragment SOBIFragment
     * @param params MapSqlParameterSource
     */
    private static void addSOBIFragmentParams(SOBIFragment fragment, MapSqlParameterSource params) {
        params.addValue("lastFragmentFileName", (fragment != null) ? fragment.getFileName() : null);
        params.addValue("lastFragmentType", (fragment != null) ? fragment.getType().name() : null);
    }
}
