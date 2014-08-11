package gov.nysenate.openleg.dao.bill;

import com.google.common.collect.MapDifference;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.dao.common.BillVoteRowHandler;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.bill.VetoDataService;
import gov.nysenate.openleg.service.bill.VetoNotFoundException;
import gov.nysenate.openleg.service.entity.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static gov.nysenate.openleg.dao.bill.SqlBillQuery.*;
import static gov.nysenate.openleg.util.CollectionUtils.difference;

@Repository
public class SqlBillDao extends SqlBaseDao implements BillDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillDao.class);

    @Autowired
    private MemberService memberService;
    @Autowired
    private VetoDataService vetoDataService;

    /* --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public Bill getBill(BillId billId) {
        logger.trace("Fetching Bill {} from database...", billId);
        final ImmutableParams baseParams = ImmutableParams.from(new MapSqlParameterSource()
            .addValue("printNo", billId.getBasePrintNo())
            .addValue("sessionYear", billId.getSession()));
        // Retrieve base Bill object
        Bill bill = getBaseBill(baseParams);
        // Fetch the amendments
        List<BillAmendment> billAmendments = getBillAmendment(baseParams);
        for (BillAmendment amendment : billAmendments) {
            final ImmutableParams amendParams = baseParams.add(
                new MapSqlParameterSource("version", amendment.getVersion()));
            // Fetch all the same as bill ids
            amendment.setSameAs(getSameAsBills(amendParams));
            // Get the cosponsors for the amendment
            amendment.setCoSponsors(getCoSponsors(amendParams));
            // Get the multi-sponsors for the amendment
            amendment.setMultiSponsors(getMultiSponsors(amendParams));
            // Get the votes
            amendment.setVotesMap(getBillVotes(amendParams));
        }
        // Set the amendments
        bill.addAmendments(billAmendments);
        // Get the sponsor
        bill.setSponsor(getBillSponsor(baseParams));
        // Get the actions
        bill.setActions(getBillActions(baseParams));
        // Get the prev bill version ids
        bill.setPreviousVersions(getPrevVersions(baseParams));
        // Get the associated bill committees
        bill.setPastCommittees(getBillCommittees(baseParams));
        // Get the associated veto memos
        bill.setVetoMessages(getBillVetoMessages(bill.getBaseBillId()));
        // Bill has been fully constructed
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
        final ImmutableParams billParams = ImmutableParams.from(getBillParams(bill, sobiFragment));
        if (jdbcNamed.update(UPDATE_BILL.getSql(schema()), billParams) == 0) {
            jdbcNamed.update(INSERT_BILL.getSql(schema()), billParams);
        }
        // Update the bill amendments
        for (BillAmendment amendment : bill.getAmendmentList()) {
            final ImmutableParams amendParams = ImmutableParams.from(getBillAmendmentParams(amendment, sobiFragment));
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
        updateBillCommittees(bill, sobiFragment, billParams);
        // Update veto messages
        updateVetoMessages(bill, sobiFragment);
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
    private Bill getBaseBill(ImmutableParams baseParams) {
        return jdbcNamed.queryForObject(SELECT_BILL.getSql(schema()), baseParams, new BillRowMapper());
    }

    /**
     * Get a list of all the bill actions for the base bill id in the params.
     */
    private List<BillAction> getBillActions(ImmutableParams baseParams) {
        OrderBy orderBy = new OrderBy("sequence_no", SortOrder.ASC);
        LimitOffset limOff = LimitOffset.ALL;
        return jdbcNamed.query(SELECT_BILL_ACTIONS.getSql(schema(), orderBy, limOff), baseParams, new BillActionRowMapper());
    }

    /**
     * Get previous session year bill ids for the base bill id in the params.
     */
    private Set<BillId> getPrevVersions(ImmutableParams baseParams) {
        return new HashSet<>(jdbcNamed.query(SELECT_BILL_PREVIOUS_VERSIONS.getSql(schema()), baseParams,
                             new BillPreviousVersionRowMapper()));
    }

    /**
     * Get a set of the committee ids which represent the committees the bill was previously referred to.
     */
    private SortedSet<CommitteeVersionId> getBillCommittees(ImmutableParams baseParams){
        return new TreeSet<>(jdbcNamed.query(SELECT_BILL_COMMITTEES.getSql(schema()), baseParams, new BillCommitteeRowMapper()));
    }

    /**
     * Get the same as bill ids for the bill id in the params.
     */
    private Set<BillId> getSameAsBills(ImmutableParams amendParams) {
        return new HashSet<>(jdbcNamed.query(SELECT_BILL_SAME_AS.getSql(schema()), amendParams, new BillSameAsRowMapper()));
    }

    /**
     *
     * Get the bill sponsor for the bill id in the params. Return null if the sponsor has not been set yet.
     */
    private BillSponsor getBillSponsor(ImmutableParams baseParams) {
        try {
            return jdbcNamed.queryForObject(
                SELECT_BILL_SPONSOR.getSql(schema()), baseParams, new BillSponsorRowMapper(memberService));
        }
        catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    /**
     * Fetch the collection of bill amendment references for the base bill id in the params.
     */
    private List<BillAmendment> getBillAmendment(ImmutableParams baseParams) {
        return jdbcNamed.query(SELECT_BILL_AMENDMENTS.getSql(schema()), baseParams, new BillAmendmentRowMapper());
    }

    /**
     * Get the co sponsors listing for the bill id in the params.
     */
    private List<Member> getCoSponsors(ImmutableParams amendParams) {
        return jdbcNamed.query(SELECT_BILL_COSPONSORS.getSql(schema()), amendParams, new BillMemberRowMapper(memberService));
    }

    /**
     * Get the multi sponsors listing for the bill id in the params.
     */
    private List<Member> getMultiSponsors(ImmutableParams amendParams) {
        return jdbcNamed.query(SELECT_BILL_MULTISPONSORS.getSql(schema()), amendParams, new BillMemberRowMapper(memberService));
    }

    /**
     * Get the votes for the bill id in the params.
     */
    private List<BillVote> getBillVotes(ImmutableParams baseParams) {
        BillVoteRowHandler voteHandler = new BillVoteRowHandler(memberService);
        jdbcNamed.query(SELECT_BILL_VOTES.getSql(schema()), baseParams, voteHandler);
        return voteHandler.getBillVotes();
    }

    /**
     * Get veto memos for the bill
     */
    private Map<VetoId,VetoMessage> getBillVetoMessages(BaseBillId baseBillId) {
        try {
            return vetoDataService.getBillVetoes(baseBillId);
        }
        catch (VetoNotFoundException ex) {
            return new HashMap<>();
        }
    }

    /**
     * Updates the bill's same as set.
     */
    private void updateBillSameAs(BillAmendment amendment, SobiFragment sobiFragment, ImmutableParams amendParams) {
        Set<BillId> existingSameAs = getSameAsBills(amendParams);
        if (!existingSameAs.equals(amendment.getSameAs())) {
            Set<BillId> newSameAs = new HashSet<>(amendment.getSameAs());
            newSameAs.removeAll(existingSameAs);             // New same as bill ids to insert
            existingSameAs.removeAll(amendment.getSameAs()); // Old same as bill ids to delete
            existingSameAs.forEach(billId -> {
                ImmutableParams sameAsParams = ImmutableParams.from(getBillSameAsParams(amendment, billId, sobiFragment));
                jdbcNamed.update(DELETE_SAME_AS.getSql(schema()), sameAsParams);
            });
            newSameAs.forEach(billId -> {
                ImmutableParams sameAsParams = ImmutableParams.from(getBillSameAsParams(amendment, billId, sobiFragment));
                jdbcNamed.update(INSERT_BILL_SAME_AS.getSql(schema()), sameAsParams);
            });
        }
    }

    /**
     * Updates the bill's action list into the database.
     */
    private void updateActions(Bill bill, SobiFragment sobiFragment, ImmutableParams billParams) {
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
     * Update the bill's previous version set.
     */
    private void updatePreviousBillVersions(Bill bill, SobiFragment sobiFragment, ImmutableParams billParams) {
        Set<BillId> existingPrevBills = getPrevVersions(billParams);
        if (!existingPrevBills.equals(bill.getPreviousVersions())) {
            Set<BillId> newPrevBills = new HashSet<>(bill.getPreviousVersions());
            newPrevBills.removeAll(existingPrevBills);               // New prev bill ids to insert
            existingPrevBills.removeAll(bill.getPreviousVersions()); // Old prev bill ids to delete
            existingPrevBills.forEach(billId -> {
                ImmutableParams prevParams = ImmutableParams.from(getBillPrevVersionParams(bill, billId, sobiFragment));
                jdbcNamed.update(DELETE_BILL_PREVIOUS_VERSIONS.getSql(schema()), prevParams);
            });
            newPrevBills.forEach(billId -> {
                ImmutableParams prevParams = ImmutableParams.from(getBillPrevVersionParams(bill, billId, sobiFragment));
                jdbcNamed.update(INSERT_BILL_PREVIOUS_VERSION.getSql(schema()), prevParams);
            });
        }
    }

    /**
     * Update the bill's previous committee set.
     */
    private void updateBillCommittees(Bill bill, SobiFragment sobiFragment, ImmutableParams billParams) {
        Set<CommitteeVersionId> existingComms = getBillCommittees(billParams);
        if (!existingComms.equals(bill.getPastCommittees())) {
            Set<CommitteeVersionId> newComms = new HashSet<>(bill.getPastCommittees());
            newComms.removeAll(existingComms);                 // New committees to insert
            existingComms.removeAll(bill.getPastCommittees()); // Old committees to delete
            existingComms.forEach(cvid -> {
                ImmutableParams commParams = ImmutableParams.from(getBillCommitteeParams(bill, cvid, sobiFragment));
                jdbcNamed.update(DELETE_BILL_COMMITTEE.getSql(schema()), commParams);
            });
            newComms.forEach(cvid -> {
                ImmutableParams commParams = ImmutableParams.from(getBillCommitteeParams(bill, cvid, sobiFragment));
                jdbcNamed.update(INSERT_BILL_COMMITTEE.getSql(schema()), commParams);
            });
        }
    }

    /**
     * Update any veto messages through the veto data service
     */
    private void updateVetoMessages(Bill bill, SobiFragment sobiFragment){
        for(VetoMessage vetoMessage : bill.getVetoMessages().values()){
            vetoDataService.updateVetoMessage(vetoMessage, sobiFragment);
        }
    }

    /**
     * Update the bill's sponsor information.
     */
    private void updateBillSponsor(Bill bill, SobiFragment sobiFragment, SqlParameterSource billParams) {
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
     * Update the bill's co sponsor list by deleting, inserting, and updating as needed.
     */
    private void updateBillCosponsor(BillAmendment billAmendment, SobiFragment sobiFragment, ImmutableParams amendParams) {
        List<Member> existingCoSponsors = getCoSponsors(amendParams);
        if (!existingCoSponsors.equals(billAmendment.getCoSponsors())) {
            MapDifference<Member, Integer> diff = difference(existingCoSponsors, billAmendment.getCoSponsors(), 1);
            // Delete old cosponsors
            diff.entriesOnlyOnLeft().forEach((member,ordinal) -> {
                ImmutableParams cspParams = amendParams.add(new MapSqlParameterSource("memberId", member.getMemberId()));
                jdbcNamed.update(DELETE_BILL_COSPONSOR.getSql(schema()), cspParams);
            });
            // Update re-ordered cosponsors
            diff.entriesDiffering().forEach((member,ordinal) -> {
                ImmutableParams cspParams = ImmutableParams.from(
                    getCoMultiSponsorParams(billAmendment, member, ordinal.rightValue(),sobiFragment));
                jdbcNamed.update(UPDATE_BILL_COSPONSOR.getSql(schema()), cspParams);
            });
            // Insert new cosponsors
            diff.entriesOnlyOnRight().forEach((member,ordinal) -> {
                ImmutableParams cspParams = ImmutableParams.from(
                    getCoMultiSponsorParams(billAmendment, member, ordinal,sobiFragment));
                jdbcNamed.update(INSERT_BILL_COSPONSOR.getSql(schema()), cspParams);
            });
        }
    }

    /**
     * Update the bill's multi-sponsor list by deleting, inserting, and updating as needed.
     */
    private void updateBillMultiSponsor(BillAmendment billAmendment, SobiFragment sobiFragment, ImmutableParams amendParams) {
        List<Member> existingMultiSponsors = getMultiSponsors(amendParams);
        if (!existingMultiSponsors.equals(billAmendment.getMultiSponsors())) {
            MapDifference<Member, Integer> diff = difference(existingMultiSponsors, billAmendment.getMultiSponsors(), 1);
            // Delete old multisponsors
            diff.entriesOnlyOnLeft().forEach((member,ordinal) -> {
                ImmutableParams mspParams = amendParams.add(new MapSqlParameterSource("memberId", member.getMemberId()));
                jdbcNamed.update(DELETE_BILL_MULTISPONSOR.getSql(schema()), mspParams);
            });
            // Update re-ordered multisponsors
            diff.entriesDiffering().forEach((member,ordinal) -> {
                ImmutableParams mspParams = ImmutableParams.from(
                    getCoMultiSponsorParams(billAmendment, member, ordinal.rightValue(),sobiFragment));
                jdbcNamed.update(UPDATE_BILL_MULTISPONSOR.getSql(schema()), mspParams);
            });
            // Insert new multisponsors
            diff.entriesOnlyOnRight().forEach((member,ordinal) -> {
                ImmutableParams mspParams = ImmutableParams.from(
                    getCoMultiSponsorParams(billAmendment, member, ordinal,sobiFragment));
                jdbcNamed.update(INSERT_BILL_MULTISPONSOR.getSql(schema()), mspParams);
            });
        }
    }

    /**
     * Update the bill amendment's list of votes.
     */
    private void updateBillVotes(BillAmendment billAmendment, SobiFragment sobiFragment, ImmutableParams amendParams) {
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

    /** --- Param Source Methods --- */

    /**
     * Returns a MapSqlParameterSource with columns mapped to Bill values for use in update/insert queries on
     * the bill table.
     */
    private static MapSqlParameterSource getBillParams(Bill bill, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("title", bill.getTitle())
              .addValue("lawSection", bill.getLawSection())
              .addValue("lawCode", bill.getLaw())
              .addValue("summary", bill.getSummary())
              .addValue("activeVersion", bill.getActiveVersion())
              .addValue("activeYear", bill.getYear())
              .addValue("programInfo", bill.getProgramInfo());
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
        params.addValue("sponsorMemo", amendment.getMemo())
              .addValue("actClause", amendment.getActClause())
              .addValue("fullText", amendment.getFulltext())
              .addValue("stricken", amendment.isStricken())
              .addValue("currentCommitteeName", amendment.getCurrentCommittee() != null ?
                      amendment.getCurrentCommittee().getName() : null)
              .addValue("currentCommitteeAction", amendment.getCurrentCommittee() != null ?
                      toDate(amendment.getCurrentCommittee().getReferenceDate()) : null)
              .addValue("uniBill", amendment.isUniBill());
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
        params.addValue("printNo", billAction.getBillId().getBasePrintNo())
              .addValue("sessionYear", billAction.getBillId().getSession())
              .addValue("chamber", billAction.getChamber().toString().toLowerCase())
              .addValue("version", billAction.getBillId().getVersion())
              .addValue("effectDate", toDate(billAction.getDate()))
              .addValue("text", billAction.getText())
              .addValue("sequenceNo", billAction.getSequenceNo());
        addModPubDateParams(billAction.getModifiedDateTime(), billAction.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillSameAsParams(BillAmendment billAmendment, BillId sameAs, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("sameAsPrintNo", sameAs.getBasePrintNo())
              .addValue("sameAsSessionYear", sameAs.getSession())
              .addValue("sameAsVersion", sameAs.getVersion());
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillPrevVersionParams(Bill bill, BillId prevVersion, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("prevPrintNo", prevVersion.getBasePrintNo())
              .addValue("prevSessionYear", prevVersion.getSession())
              .addValue("prevVersion", prevVersion.getVersion());
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillSponsorParams(Bill bill, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        BillSponsor billSponsor = bill.getSponsor();
        boolean hasMember = billSponsor != null && billSponsor.hasMember();
        addBillIdParams(bill, params);
        params.addValue("memberId", (hasMember) ? billSponsor.getMember().getMemberId() : null)
              .addValue("budgetBill", (billSponsor != null && billSponsor.isBudgetBill()))
              .addValue("rulesSponsor", (billSponsor != null && billSponsor.isRulesSponsor()));
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getCoMultiSponsorParams(BillAmendment billAmendment, Member member,
                                                                 int sequenceNo, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("memberId", member.getMemberId())
              .addValue("sequenceNo", sequenceNo);
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillVoteInfoParams(BillAmendment billAmendment, BillVote billVote,
                                                               SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("voteDate", toDate(billVote.getVoteDate()))
              .addValue("voteType", billVote.getVoteType().name().toLowerCase())
              .addValue("sequenceNo", billVote.getSequenceNo());
        addModPubDateParams(billVote.getModifiedDateTime(), billVote.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillCommitteeParams(Bill bill, CommitteeVersionId committee,
                                                                SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("committeeName", committee.getName())
              .addValue("committeeChamber", committee.getChamber().asSqlEnum())
              .addValue("actionDate", toDate(committee.getReferenceDate()));
        addLastFragmentParam(fragment, params);
        return params;
    }

    /**
     * Applies columns that identify the base bill.
     */
    private static void addBillIdParams(Bill bill, MapSqlParameterSource params) {
        params.addValue("printNo", bill.getBasePrintNo())
              .addValue("sessionYear", bill.getSession());
    }

    /**
     * Adds columns that identify the bill amendment.
     */
    private static void addBillIdParams(BillAmendment billAmendment, MapSqlParameterSource params) {
        params.addValue("printNo", billAmendment.getBaseBillPrintNo())
              .addValue("sessionYear", billAmendment.getSession())
              .addValue("version", billAmendment.getVersion());
    }
}
