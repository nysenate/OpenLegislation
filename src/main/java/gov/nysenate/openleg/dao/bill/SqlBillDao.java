package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.entity.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.openleg.dao.bill.SqlBillQuery.*;

@Repository
public class SqlBillDao extends SqlBaseDao implements BillDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillDao.class);

    @Autowired
    private MemberService memberService;

    /* --- Implemented Methods --- */

    /** {@inheritDoc} */
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
            amendment.setSameAs(getSameAsBills(params));
            // Get the cosponsors for the amendment
            amendment.setCoSponsors(getCoSponsors(params));
            // Get the multi-sponsors for the amendment
            amendment.setMultiSponsors(getMultiSponsors(params));
            // Get the votes
            amendment.setVotesMap(getBillVotes(params));
        }
        // Set the amendments
        bill.addAmendments(billAmendments);
        // Get the sponsor
        bill.setSponsor(getBillSponsor(params));
        // Get the actions
        bill.setActions(getBillActions(params));
        // Get the prev bill version ids
        bill.setPreviousVersions(getPrevVersions(params));
        // Get the associated bill committees
        bill.setPastCommittees(getBillCommittees(params));

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
    public void updateBill(Bill bill, SobiFragment sobiFragment) {
        logger.trace("Updating Bill {} in database...", bill);
        // Update the bill record
        MapSqlParameterSource billParams = getBillParams(bill, sobiFragment);
        if (jdbcNamed.update(UPDATE_BILL.getSql(schema()), billParams) == 0) {
            jdbcNamed.update(INSERT_BILL.getSql(schema()), billParams);
        }
        // Update the bill amendments
        for (BillAmendment amendment : bill.getAmendmentList()) {
            MapSqlParameterSource amendParams = getBillAmendmentParams(amendment, sobiFragment);
            if (jdbcNamed.update(UPDATE_BILL_AMENDMENT.getSql(schema()), amendParams) == 0) {
                jdbcNamed.update(INSERT_BILL_AMENDMENT.getSql(schema()), amendParams);
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
        // Update associated committees
        updateBillCommittees(bill);
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


    /**
     * Get the base bill instance for the base bill id in the params.
     */
    private Bill getBaseBill(MapSqlParameterSource params) {
        return jdbcNamed.queryForObject(SELECT_BILL.getSql(schema()), params, new BillRowMapper());
    }

    /**
     * Get a list of all the bill actions for the base bill id in the params.
     */
    private List<BillAction> getBillActions(MapSqlParameterSource params) {
        return jdbcNamed.query(SELECT_BILL_ACTIONS.getSql(schema()), params, new BillActionRowMapper());
    }

    /**
     * Get previous session year bill ids for the base bill id in the params.
     */
    private Set<BillId> getPrevVersions(MapSqlParameterSource params) {
        return new HashSet<>(jdbcNamed.query(SELECT_BILL_PREVIOUS_VERSIONS.getSql(schema()), params,
                             new BillPreviousVersionRowMapper()));
    }

    /**
     * Get a set of the committee ids which represent the committees the bill was previously referred to.
     */
    private SortedSet<CommitteeVersionId> getBillCommittees(MapSqlParameterSource params){
        return new TreeSet<>(jdbcNamed.query(SELECT_BILL_COMMITTEES.getSql(schema()), params, new BillCommitteeRowMapper()));
    }

    /**
     * Get the same as bill ids for the bill id in the params.
     */
    private Set<BillId> getSameAsBills(MapSqlParameterSource params) {
        return new HashSet<>(jdbcNamed.query(SELECT_BILL_SAME_AS.getSql(schema()), params, new BillSameAsRowMapper()));
    }

    /**
     *
     * Get the bill sponsor for the bill id in the params. Return null if the sponsor has not been set yet.
     */
    private BillSponsor getBillSponsor(MapSqlParameterSource params) {
        try {
            return jdbcNamed.queryForObject(
                SELECT_BILL_SPONSOR.getSql(schema()), params, new BillSponsorRowMapper(memberService));
        }
        catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    /**
     * Fetch the collection of bill amendment references for the base bill id in the params.
     */
    private List<BillAmendment> getBillAmendment(MapSqlParameterSource params) {
        return jdbcNamed.query(SELECT_BILL_AMENDMENTS.getSql(schema()), params, new BillAmendmentRowMapper());
    }

    /**
     * Get the co sponsors listing for the bill id in the params.
     */
    private List<Member> getCoSponsors(MapSqlParameterSource params) {
        return jdbcNamed.query(SELECT_BILL_MULTISPONSORS.getSql(schema()), params, new BillMemberRowMapper(memberService));
    }

    /**
     * Get the multi sponsors listing for the bill id in the params.
     */
    private List<Member> getMultiSponsors(MapSqlParameterSource params) {
        return jdbcNamed.query(SELECT_BILL_MULTISPONSORS.getSql(schema()), params, new BillMemberRowMapper(memberService));
    }

    /**
     * Get the votes for the bill id in the params.
     */
    private List<BillVote> getBillVotes(MapSqlParameterSource params) {
        BillVoteRowCallbackHandler handler = new BillVoteRowCallbackHandler(memberService);
        jdbcNamed.query(SELECT_BILL_VOTES.getSql(schema()), params, handler);
        return handler.getBillVotes();
    }

    /**
     * Save the bill's same as list by replacing the existing records with the current records if they are not
     * the same.
     */
    private void updateBillSameAs(BillAmendment amendment, SobiFragment sobiFragment, MapSqlParameterSource amendParams) {
        Set<BillId> existingSameAs = getSameAsBills(amendParams);
        if (!existingSameAs.equals(amendment.getSameAs())) {
            jdbcNamed.update(DELETE_SAME_AS_FOR_BILL.getSql(schema()), amendParams);
            for (BillId sameAsBillId : amendment.getSameAs()) {
                MapSqlParameterSource sameAsParams = getBillSameAsParams(amendment, sameAsBillId, sobiFragment);
                jdbcNamed.update(INSERT_BILL_SAME_AS.getSql(schema()), sameAsParams);
            }
        }
    }

    /**
     * Save the bill's action list into the database. Only delete/insert where necessary to allow for a
     * better snapshot of how the actions came in.
     */
    private void updateActions(Bill bill, SobiFragment sobiFragment, MapSqlParameterSource billParams) {
        List<BillAction> existingBillActions = getBillActions(billParams);
        List<BillAction> newBillActions = new ArrayList<>(bill.getActions());
        newBillActions.removeAll(existingBillActions);    // New actions to insert
        existingBillActions.removeAll(bill.getActions()); // Old actions to delete
        // Delete actions that are not in the updated list
        for (BillAction action : existingBillActions) {
            MapSqlParameterSource actionParams = getBillActionParams(action, sobiFragment);
            jdbcNamed.update(DELETE_BILL_ACTION.getSql(schema()), actionParams);
        }
        // Insert all new actions
        for (BillAction action : newBillActions) {
            MapSqlParameterSource actionParams = getBillActionParams(action, sobiFragment);
            jdbcNamed.update(INSERT_BILL_ACTION.getSql(schema()), actionParams);
        }
    }

    /**
     * Save the bill's previous version list by replacing the existing set with the current set
     * when the sets are different.
     */
    private void updatePreviousBillVersions(Bill bill, SobiFragment sobiFragment, MapSqlParameterSource billParams) {
        Set<BillId> existingPrevBills = getPrevVersions(billParams);
        if (!existingPrevBills.equals(bill.getPreviousVersions())) {
            jdbcNamed.update(DELETE_BILL_PREVIOUS_VERSIONS.getSql(schema()), billParams);
            for (BillId prevBillId : bill.getPreviousVersions()) {
                MapSqlParameterSource prevParams = getBillPrevVersionParams(bill, prevBillId, sobiFragment);
                jdbcNamed.update(INSERT_BILL_PREVIOUS_VERSION.getSql(schema()), prevParams);
            }
        }
    }

    /**
     * Replace the bill's previous committee list with the current list
     */
    private void updateBillCommittees(Bill bill){
        MapSqlParameterSource deleteParams = new MapSqlParameterSource();
        addBillIdParams(bill, deleteParams);
        jdbcNamed.update(DELETE_BILL_COMMITTEES.getSql(schema()), deleteParams);
        for(CommitteeVersionId cvid : bill.getPastCommittees()){
            MapSqlParameterSource params = getBillCommitteeParams(bill, cvid);
            jdbcNamed.update(INSERT_BILL_COMMITTEE.getSql(schema()), params);
        }
    }

    /**
     * Update the bill's sponsor information.
     */
    private void updateBillSponsor(Bill bill, SobiFragment sobiFragment, MapSqlParameterSource billParams) {
        if (bill.getSponsor() != null) {
            MapSqlParameterSource params = getBillSponsorParams(bill, sobiFragment);
            if (jdbcNamed.update(UPDATE_BILL_SPONSOR.getSql(schema()), params) == 0) {
                jdbcNamed.update(INSERT_BILL_SPONSOR.getSql(schema()), params);
            }
        }
        else {
            jdbcNamed.update(DELETE_BILL_SPONSOR.getSql(schema()), billParams);
        }
    }

    /**
     * Update the bill's co sponsor list.
     */
    private void updateBillCosponsor(BillAmendment billAmendment, SobiFragment sobiFragment, MapSqlParameterSource amendParams) {
        List<Member> existingCoSponsors = getCoSponsors(amendParams);
        if (!existingCoSponsors.equals(billAmendment.getCoSponsors())){
            jdbcNamed.update(DELETE_BILL_COSPONSORS.getSql(schema()), amendParams);
            int sequenceNo = 1;
            // Iterate through a linked set to preserve order while removing possible duplicates.
            for (Member member : new LinkedHashSet<>(billAmendment.getCoSponsors())) {
                if (member != null) {
                    MapSqlParameterSource params = getCoMultiSponsorMemberParams(billAmendment, member, sequenceNo++, sobiFragment);
                    jdbcNamed.update(INSERT_BILL_COSPONSORS.getSql(schema()), params);
                }
            }
        }
    }

    /**
     * Update the bill's multi sponsor list.
     */
    private void updateBillMultiSponsor(BillAmendment billAmendment, SobiFragment sobiFragment, MapSqlParameterSource amendParams) {
        List<Member> existingMultiSponsors = getMultiSponsors(amendParams);
        if (!existingMultiSponsors.equals(billAmendment.getMultiSponsors())){
            jdbcNamed.update(DELETE_BILL_MULTISPONSORS.getSql(schema()), amendParams);
            int sequenceNo = 1;
            for (Member member : billAmendment.getMultiSponsors()) {
                if (member != null) {
                    MapSqlParameterSource params = getCoMultiSponsorMemberParams(billAmendment, member, sequenceNo++, sobiFragment);
                    jdbcNamed.update(INSERT_BILL_MULTISPONSORS.getSql(schema()), params);
                }
            }
        }
    }

    /**
     * Update the bill amendment's list of votes.
     */
    private void updateBillVotes(BillAmendment billAmendment, SobiFragment sobiFragment, MapSqlParameterSource amendParams) {
        List<BillVote> existingBillVotes = getBillVotes(amendParams);
        List<BillVote> newBillVotes = new ArrayList<>(billAmendment.getVotesList());
        newBillVotes.removeAll(existingBillVotes);                 // New votes to insert/update
        existingBillVotes.removeAll(billAmendment.getVotesList()); // Old votes to remove
        // Delete all votes that have been updated
        for (BillVote billVote : existingBillVotes) {
            MapSqlParameterSource voteInfoParams = getBillVoteInfoParams(billAmendment, billVote, sobiFragment);
            jdbcNamed.update(DELETE_BILL_VOTES_INFO.getSql(schema()), voteInfoParams);
        }
        // Insert the new/updated votes
        for (BillVote billVote : newBillVotes) {
            MapSqlParameterSource voteParams = getBillVoteInfoParams(billAmendment, billVote, sobiFragment);
            jdbcNamed.update(INSERT_BILL_VOTES_INFO.getSql(schema()), voteParams);
            for (BillVoteCode voteCode : billVote.getMemberVotes().keySet()) {
                voteParams.addValue("voteCode", voteCode.name().toLowerCase());
                for (Member member : billVote.getMembersByVote(voteCode)) {
                    voteParams.addValue("memberId", member.getMemberId());
                    voteParams.addValue("memberShortName", member.getLbdcShortName());
                    jdbcNamed.update(INSERT_BILL_VOTES_ROLL.getSql(schema()), voteParams);
                }
            }
        }
    }

    /** --- Helper Classes --- */

    private static class BillRowMapper implements RowMapper<Bill>
    {
        @Override
        public Bill mapRow(ResultSet rs, int rowNum) throws SQLException {
            Bill bill = new Bill(new BaseBillId(rs.getString("print_no"), rs.getInt("session_year")));
            bill.setTitle(rs.getString("title"));
            bill.setLawSection(rs.getString("law_section"));
            bill.setLaw(rs.getString("law_code"));
            bill.setSummary(rs.getString("summary"));
            bill.setActiveVersion(rs.getString("active_version").trim());
            bill.setProgramInfo(rs.getString("program_info"));
            bill.setYear(rs.getInt("active_year"));
            setModPubDatesFromResultSet(bill, rs);
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
            amend.setUniBill(rs.getBoolean("uni_bill"));
            setModPubDatesFromResultSet(amend, rs);
            String currentCommitteeName = rs.getString("current_committee_name");
            if (currentCommitteeName != null) {
                amend.setCurrentCommittee(
                    new CommitteeVersionId(
                        amend.getBillId().getChamber(), rs.getString("current_committee_name"),
                        amend.getSession(), getLocalDate(rs, "current_committee_action")
                    )
                );
            }
            else {
                amend.setCurrentCommittee(null);
            }
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
            billAction.setChamber(Chamber.valueOf(rs.getString("chamber").toUpperCase()));
            billAction.setSequenceNo(rs.getInt("sequence_no"));
            billAction.setDate(getLocalDate(rs, "effect_date"));
            billAction.setText(rs.getString("text"));
            setModPubDatesFromResultSet(billAction, rs);
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

    private static class BillCommitteeRowMapper implements RowMapper<CommitteeVersionId>
    {
        @Override
        public CommitteeVersionId mapRow(ResultSet rs, int rowNum) throws SQLException {
            String committeeName = rs.getString("committee_name");
            Chamber committeeChamber = Chamber.getValue(rs.getString("committee_chamber"));
            int session = rs.getInt("bill_session_year");
            LocalDate actionDate = getLocalDate(rs, "action_date");
            return new CommitteeVersionId(committeeChamber, committeeName, session, actionDate);
        }
    }

    private static class BillVoteRowCallbackHandler implements RowCallbackHandler
    {
        private TreeMap<BillVoteId, BillVote> billVoteMap = new TreeMap<>();
        private MemberService memberService;

        private BillVoteRowCallbackHandler(MemberService memberService) {
            this.memberService = memberService;
        }

        public List<BillVote> getBillVotes() {
            return new ArrayList<>(billVoteMap.values());
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            BillId billId = new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                                       rs.getString("bill_amend_version"));
            LocalDate voteDate = getLocalDate(rs, "vote_date");
            BillVoteType voteType = BillVoteType.getValue(rs.getString("vote_type"));
            int sequenceNo = rs.getInt("sequence_no");
            BillVote billVote = new BillVote(billId, voteDate, voteType, sequenceNo);

            if (!billVoteMap.containsKey(billVote.getVoteId())) {
                setModPubDatesFromResultSet(billVote, rs);
                billVoteMap.put(billVote.getVoteId(), billVote);
            }

            billVote = billVoteMap.get(billVote.getVoteId());
            try {
                Member voter = memberService.getMemberById(rs.getInt("member_id"), rs.getInt("session_year"));
                BillVoteCode voteCode = BillVoteCode.getValue(rs.getString("vote_code"));
                billVote.addMemberVote(voteCode, voter);
            }
            catch (MemberNotFoundEx memberNotFoundEx) {
                logger.error("Failed to add member vote since member could not be found!", memberNotFoundEx);
            }
        }
    }

    /** --- Param Source Methods --- */

    /**
     * Returns a MapSqlParameterSource with columns mapped to Bill values for use in update/insert queries on
     * the bill table.
     */
    private static MapSqlParameterSource getBillParams(Bill bill, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("title", bill.getTitle());
        params.addValue("lawSection", bill.getLawSection());
        params.addValue("lawCode", bill.getLaw());
        params.addValue("summary", bill.getSummary());
        params.addValue("activeVersion", bill.getActiveVersion());
        params.addValue("activeYear", bill.getYear());
        params.addValue("programInfo", bill.getProgramInfo());
        addModPubDateParams(bill.getModifiedDateTime(), bill.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to BillAmendment values for use in update/insert
     * queries on the bill amendment table.
     */
    private static MapSqlParameterSource getBillAmendmentParams(BillAmendment amendment, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(amendment, params);
        params.addValue("sponsorMemo", amendment.getMemo());
        params.addValue("actClause", amendment.getActClause());
        params.addValue("fullText", amendment.getFulltext());
        params.addValue("stricken", amendment.isStricken());
        params.addValue("currentCommitteeName",
            amendment.getCurrentCommittee() != null ? amendment.getCurrentCommittee().getName() : null);
        params.addValue("currentCommitteeAction",
            amendment.getCurrentCommittee() != null ? amendment.getCurrentCommittee().getReferenceDate() : null);
        params.addValue("uniBill", amendment.isUniBill());
        addModPubDateParams(amendment.getModifiedDateTime(), amendment.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to BillAction for use in inserting records
     * into the bill action table.
     */
    private static MapSqlParameterSource getBillActionParams(BillAction billAction, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", billAction.getBillId().getBasePrintNo());
        params.addValue("sessionYear", billAction.getBillId().getSession());
        params.addValue("chamber", billAction.getChamber().toString().toLowerCase());
        params.addValue("version", billAction.getBillId().getVersion());
        params.addValue("effectDate", billAction.getDate());
        params.addValue("text", billAction.getText());
        params.addValue("sequenceNo", billAction.getSequenceNo());
        addModPubDateParams(billAction.getModifiedDateTime(), billAction.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillSameAsParams(BillAmendment billAmendment, BillId sameAs, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("sameAsPrintNo", sameAs.getBasePrintNo());
        params.addValue("sameAsSessionYear", sameAs.getSession());
        params.addValue("sameAsVersion", sameAs.getVersion());
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillPrevVersionParams(Bill bill, BillId prevVersion, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("prevPrintNo", prevVersion.getBasePrintNo());
        params.addValue("prevSessionYear", prevVersion.getSession());
        params.addValue("prevVersion", prevVersion.getVersion());
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillSponsorParams(Bill bill, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        BillSponsor billSponsor = bill.getSponsor();
        boolean hasMember = billSponsor != null && billSponsor.hasMember();
        addBillIdParams(bill, params);
        params.addValue("memberId", (hasMember) ? billSponsor.getMember().getMemberId() : null);
        params.addValue("budgetBill", (billSponsor != null && billSponsor.isBudgetBill()));
        params.addValue("rulesSponsor", (billSponsor != null && billSponsor.isRulesSponsor()));
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getCoMultiSponsorMemberParams(BillAmendment billAmendment, Member member,
                                                                       int sequenceNo, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("memberId", member.getMemberId());
        params.addValue("sequenceNo", sequenceNo);
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillVoteInfoParams(BillAmendment billAmendment, BillVote billVote,
                                                               SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("voteDate", billVote.getVoteDate());
        params.addValue("voteType", billVote.getVoteType().name().toLowerCase());
        params.addValue("sequenceNo", billVote.getSequenceNo());
        addModPubDateParams(billVote.getModifiedDateTime(), billVote.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillCommitteeParams(Bill bill, CommitteeVersionId committee){
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("committeeName", committee.getName());
        params.addValue("committeeChamber", committee.getChamber().asSqlEnum());
        params.addValue("actionDate", committee.getReferenceDate());
        return params;
    }

    /**
     * Applies columns that identify the base bill.
     */
    private static void addBillIdParams(Bill bill, MapSqlParameterSource params) {
        params.addValue("printNo", bill.getBasePrintNo());
        params.addValue("sessionYear", bill.getSession());
    }

    /**
     * Adds columns that identify the bill amendment.
     */
    private static void addBillIdParams(BillAmendment billAmendment, MapSqlParameterSource params) {
        params.addValue("printNo", billAmendment.getBaseBillPrintNo());
        params.addValue("sessionYear", billAmendment.getSession());
        params.addValue("version", billAmendment.getVersion());
    }
}
