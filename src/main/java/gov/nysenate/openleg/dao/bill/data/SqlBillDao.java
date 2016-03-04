package gov.nysenate.openleg.dao.bill.data;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.dao.common.BillVoteRowHandler;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.bill.data.ApprovalDataService;
import gov.nysenate.openleg.service.bill.data.ApprovalNotFoundException;
import gov.nysenate.openleg.service.bill.data.VetoDataService;
import gov.nysenate.openleg.service.bill.data.VetoNotFoundException;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.openleg.util.CollectionUtils.difference;
import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlBillDao extends SqlBaseDao implements BillDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillDao.class);

    @Autowired private MemberService memberService;
    @Autowired private VetoDataService vetoDataService;
    @Autowired private ApprovalDataService approvalDataService;

    /* --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public Bill getBill(BillId billId) {
        logger.trace("Fetching Bill {} from database...", billId);
        final ImmutableParams baseParams = getBaseParams(billId);
        // Retrieve base Bill object
        Bill bill = getBaseBill(baseParams);
        // Fetch the amendments
        List<BillAmendment> billAmendments = getBillAmendments(baseParams);
        for (BillAmendment amendment : billAmendments) {
            final ImmutableParams amendParams = baseParams.add(
                new MapSqlParameterSource("version", amendment.getVersion().getValue()));
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
        // Set the publish status for each amendment
        bill.setPublishStatuses(getBillAmendPublishStatuses(baseParams));
        // Get the sponsor
        bill.setSponsor(getBillSponsor(baseParams));
        // Get any additional sponsors
        bill.setAdditionalSponsors(getAdditionalSponsors(baseParams));
        // Get the milestones
        bill.setMilestones(getBillMilestones(baseParams));
        // Get the actions
        bill.setActions(getBillActions(baseParams));
        // Get the prev bill version ids
        bill.setPreviousVersions(getAllPreviousVersions(baseParams));
        // Get the associated bill committees
        bill.setPastCommittees(getBillCommittees(baseParams));
        // Get the associated veto memos
        bill.setVetoMessages(getBillVetoMessages(bill.getBaseBillId()));
        // Get the approval message
        bill.setApprovalMessage(getBillApprovalMessage(bill.getBaseBillId()));
        // Get the associated committee agendas
        bill.setCommitteeAgendas(getCommitteeAgendas(baseParams));
        // Get the associated calendars
        bill.setCalendars(getCalendars(baseParams));
        // Bill has been fully constructed
        return bill;
    }

    /** {@inheritDoc} */
    @Override
    public BillInfo getBillInfo(BillId billId) throws DataAccessException {
        logger.trace("Fetching BillInfo {} from database...", billId);
        final ImmutableParams baseParams = getBaseParams(billId);
        // Retrieve base bill object
        Bill bill = getBaseBill(baseParams);
        bill.setSponsor(getBillSponsor(baseParams));
        bill.setMilestones(getBillMilestones(baseParams));
        bill.setActions(getBillActions(baseParams));
        return bill.getBillInfo();
    }

    /** {@inheritDoc} */
    @Override
    public void applyText(Bill strippedBill) throws DataAccessException {
        if (strippedBill == null) {
            throw new IllegalArgumentException("Cannot apply bill text on a null bill");
        }
        MapSqlParameterSource billParams = new MapSqlParameterSource();
        addBillIdParams(strippedBill, billParams);
        jdbcNamed.query(SqlBillQuery.SELECT_BILL_TEXT.getSql(schema()), billParams, (RowCallbackHandler) (ResultSet rs) -> {
            BillAmendment ba = strippedBill.getAmendment(Version.of(rs.getString("bill_amend_version")));
            ba.setMemo(rs.getString("sponsor_memo"));
            ba.setFullText(rs.getString("full_text"));
        });
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
        if (jdbcNamed.update(SqlBillQuery.UPDATE_BILL.getSql(schema()), billParams) == 0) {
            jdbcNamed.update(SqlBillQuery.INSERT_BILL.getSql(schema()), billParams);
        }
        // Update the bill amendments
        for (BillAmendment amendment : bill.getAmendmentList()) {
            final ImmutableParams amendParams = ImmutableParams.from(getBillAmendmentParams(amendment, sobiFragment));
            if (jdbcNamed.update(SqlBillQuery.UPDATE_BILL_AMENDMENT.getSql(schema()), amendParams) == 0) {
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_AMENDMENT.getSql(schema()), amendParams);
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
        // Update the publish statuses of the amendments
        updateBillAmendPublishStatus(bill, sobiFragment, billParams);
        // Update the sponsor
        updateBillSponsor(bill, sobiFragment, billParams);
        // Update the milestones
        updateBillMilestones(bill, sobiFragment, billParams);
        // Determine which actions need to be inserted/deleted. Individual actions are never updated.
        updateActions(bill, sobiFragment, billParams);
        // Determine if the previous versions have changed and insert accordingly.
        updatePreviousBillVersions(bill, sobiFragment, billParams);
        // Update associated committees
        updateBillCommittees(bill, sobiFragment, billParams);
        // Update veto messages
        updateVetoMessages(bill, sobiFragment);
        // Update approval message
        updateApprovalMessage(bill, sobiFragment);
    }

    /** {@inheritDoc} */
    @Override
    public List<BaseBillId> getBillIds(SessionYear sessionYear, LimitOffset limOff, SortOrder billIdSort) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("sessionYear", sessionYear.getYear()));
        OrderBy orderBy = new OrderBy("bill_print_no", billIdSort, "bill_session_year", billIdSort);
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_IDS_BY_SESSION.getSql(schema(), orderBy, limOff), params, (rs, row) ->
                new BaseBillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year")));
    }

    /** {@inheritDoc} */
    @Override
    public int getBillCount() throws DataAccessException {
        return jdbc.queryForObject(SqlBillQuery.SELECT_COUNT_ALL_BILLS.getSql(schema()), (rs, row) -> rs.getInt("total"));
    }

    /** {@inheritDoc} */
    @Override
    public int getBillCount(SessionYear sessionYear) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource("sessionYear", sessionYear.getYear()));
        return jdbcNamed.queryForObject(SqlBillQuery.SELECT_COUNT_ALL_BILLS_IN_SESSION.getSql(schema()), params,
                (rs, row) -> rs.getInt("total"));
    }

    /** {@inheritDoc} */
    @Override
    public String getAlternateBillPdfUrl(BillId billId) {
        SqlParameterSource params = getBillIdParams(billId);
        return jdbcNamed.queryForObject(SqlBillQuery.SELECT_ALTERNATE_PDF_URL.getSql(schema()), params,
            (rs, row) -> rs.getString("url_path"));
    }

    /** {@inheritDoc} */
    @Override
    public Range<SessionYear> activeSessionRange() {
        if (getBillCount() == 0) {
            throw new EmptyResultDataAccessException("No active session range since there are " +
                                                     "no bills in the database!", 1);
        }
        return jdbc.queryForObject(SqlBillQuery.ACTIVE_SESSION_YEARS.getSql(schema()), (rs, row) ->
            Range.closed(SessionYear.of(rs.getInt("min")), SessionYear.of(rs.getInt("max")))
        );
    }

    /** --- Methods --- */

    /**
     * Get the base bill instance for the base bill id in the params.
     */
    public Bill getBaseBill(ImmutableParams baseParams) {
        return jdbcNamed.queryForObject(SqlBillQuery.SELECT_BILL.getSql(schema()), baseParams, new BillRowMapper());
    }

    /**
     * Get a list of all the bill actions for the base bill id in the params.
     */
    public List<BillAction> getBillActions(ImmutableParams baseParams) {
        OrderBy orderBy = new OrderBy("sequence_no", SortOrder.ASC);
        LimitOffset limOff = LimitOffset.ALL;
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_ACTIONS.getSql(schema(), orderBy, limOff), baseParams, new BillActionRowMapper());
    }

    /**
     * Get previous session year bill ids for the base bill id in the params.
     */
    public Set<BillId> getPrevVersions(ImmutableParams baseParams) {
        return new TreeSet<>(jdbcNamed.query(SqlBillQuery.SELECT_BILL_PREVIOUS_VERSIONS.getSql(schema()), baseParams,
                             new BillPreviousVersionRowMapper()));
    }

    /**
     * Get all previous session year bill ids recursively for the base bill id in the params.
     */
    public Set<BillId> getAllPreviousVersions(ImmutableParams baseParams) {
        OrderBy orderBy = new OrderBy("prev_bill_session_year", SortOrder.DESC);
        return new TreeSet<>(jdbcNamed.query(
            SqlBillQuery.SELECT_ALL_BILL_PREVIOUS_VERSIONS.getSql(schema(), orderBy, LimitOffset.ALL), baseParams,
             new BillPreviousVersionRowMapper()));
    }

    /**
     * Get a set of the committee ids which represent the committees the bill was previously referred to.
     */
    public TreeSet<CommitteeVersionId> getBillCommittees(ImmutableParams baseParams){
        return new TreeSet<>(jdbcNamed.query(SqlBillQuery.SELECT_BILL_COMMITTEES.getSql(schema()), baseParams, new BillCommitteeRowMapper()));
    }

    /**
     * Get the same as bill ids for the bill id in the params.
     */
    public Set<BillId> getSameAsBills(ImmutableParams amendParams) {
        return new HashSet<>(jdbcNamed.query(SqlBillQuery.SELECT_BILL_SAME_AS.getSql(schema()), amendParams, new BillSameAsRowMapper()));
    }

    /**
     * Get the bill sponsor for the bill id in the params. Return null if the sponsor has not been set yet.
     */
    public BillSponsor getBillSponsor(ImmutableParams baseParams) {
        try {
            return jdbcNamed.queryForObject(
                SqlBillQuery.SELECT_BILL_SPONSOR.getSql(schema()), baseParams, new BillSponsorRowMapper(memberService));
        }
        catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    /**
     * Get any additional sponsors that were manually entered into the database.
     */
    public List<SessionMember> getAdditionalSponsors(ImmutableParams baseParams) {
        try {
            OrderBy orderBy = new OrderBy("sequence_no", SortOrder.ASC);
            return jdbcNamed.query(SqlBillQuery.SELECT_ADDTL_BILL_SPONSORS.getSql(schema(), orderBy, LimitOffset.ALL),
                    baseParams, new BillMemberRowMapper(memberService));
        }
        catch (EmptyResultDataAccessException ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Get the bill's milestone list.
     */
    public LinkedList<BillStatus> getBillMilestones(ImmutableParams baseParams) {
        OrderBy orderBy = new OrderBy("rank", SortOrder.ASC);
        return new LinkedList<>(jdbcNamed.query(SqlBillQuery.GET_BILL_MILESTONES.getSql(schema(), orderBy, LimitOffset.ALL), baseParams,
            (rs, rowNum) -> {
                BillStatus status = new BillStatus(BillStatusType.valueOf(rs.getString("status")), getLocalDateFromRs(rs, "date"));
                status.setActionSequenceNo(rs.getInt("action_sequence_no"));
                status.setCommitteeId(getCommitteeIdFromRs(rs));
                status.setCalendarNo((rs.getInt("cal_no") != 0) ? rs.getInt("cal_no") : null);
                return status;
            }));
    }

    /**
     * Fetch the collection of bill amendment references for the base bill id in the params.
     */
    public List<BillAmendment> getBillAmendments(ImmutableParams baseParams) {
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_AMENDMENTS.getSql(schema()), baseParams, new BillAmendmentRowMapper());
    }

    /**
     * Get a map of the publish statuses for each amendment version.
     */
    public TreeMap<Version, PublishStatus> getBillAmendPublishStatuses(ImmutableParams baseParams) {
        BillAmendPublishStatusHandler handler = new BillAmendPublishStatusHandler();
        jdbcNamed.query(SqlBillQuery.SELECT_BILL_AMEND_PUBLISH_STATUSES.getSql(schema()), baseParams, handler);
        return handler.getPublishStatusMap();
    }

    /**
     * Get the co sponsors listing for the bill id in the params.
     */
    public List<SessionMember> getCoSponsors(ImmutableParams amendParams) {
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_COSPONSORS.getSql(schema()), amendParams, new BillMemberRowMapper(memberService));
    }

    /**
     * Get the multi sponsors listing for the bill id in the params.
     */
    public List<SessionMember> getMultiSponsors(ImmutableParams amendParams) {
        return jdbcNamed.query(SqlBillQuery.SELECT_BILL_MULTISPONSORS.getSql(schema()), amendParams, new BillMemberRowMapper(memberService));
    }

    /**
     * Get the votes for the bill id in the params.
     */
    public List<BillVote> getBillVotes(ImmutableParams baseParams) {
        BillVoteRowHandler voteHandler = new BillVoteRowHandler(memberService);
        jdbcNamed.query(SqlBillQuery.SELECT_BILL_VOTES.getSql(schema()), baseParams, voteHandler);
        return voteHandler.getBillVotes();
    }

    /**
     * Get veto memos for the bill
     */
    public Map<VetoId,VetoMessage> getBillVetoMessages(BaseBillId baseBillId) {
        try {
            return vetoDataService.getBillVetoes(baseBillId);
        }
        catch (VetoNotFoundException ex) {
            return new HashMap<>();
        }
    }

    public ApprovalMessage getBillApprovalMessage(BaseBillId baseBillId){
        try{
            return approvalDataService.getApprovalMessage(baseBillId);
        }
        catch(ApprovalNotFoundException ex){
            return null;
        }
    }

    /**
     * Get a list of the associated committee agenda ids.
     */
    public List<CommitteeAgendaId> getCommitteeAgendas(ImmutableParams baseParams) {
        OrderBy orderBy = new OrderBy("aic.meeting_date_time", SortOrder.ASC);
        return jdbcNamed.query(SqlBillQuery.SELECT_COMM_AGENDA_IDS.getSql(schema(), orderBy, LimitOffset.ALL), baseParams,
            (rs, rowNum) ->
                new CommitteeAgendaId(new AgendaId(rs.getInt("agenda_no"), rs.getInt("year")),
                                      new CommitteeId(Chamber.SENATE, rs.getString("committee_name")))
            );
    }

    /**
     * Get a list of the associated calendar ids.
     */
    public List<CalendarId> getCalendars(ImmutableParams baseParams) {
        OrderBy orderBy = new OrderBy("cs.calendar_year", SortOrder.ASC, "cs.calendar_no", SortOrder.ASC);
        return jdbcNamed.query(SqlBillQuery.SELECT_CALENDAR_IDS.getSql(schema(), orderBy, LimitOffset.ALL), baseParams,
            (rs, rowNum) -> {
                return new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year"));
            });
    }

    /**
     * Updates the bill's same as set.
     */
    protected void updateBillSameAs(BillAmendment amendment, SobiFragment sobiFragment, ImmutableParams amendParams) {
        Set<BillId> existingSameAs = getSameAsBills(amendParams);
        if (!existingSameAs.equals(amendment.getSameAs())) {
            Set<BillId> newSameAs = new HashSet<>(amendment.getSameAs());
            newSameAs.removeAll(existingSameAs);             // New same as bill ids to insert
            existingSameAs.removeAll(amendment.getSameAs()); // Old same as bill ids to delete
            existingSameAs.forEach(billId -> {
                ImmutableParams sameAsParams = ImmutableParams.from(getBillSameAsParams(amendment, billId, sobiFragment));
                jdbcNamed.update(SqlBillQuery.DELETE_SAME_AS.getSql(schema()), sameAsParams);
            });
            newSameAs.forEach(billId -> {
                ImmutableParams sameAsParams = ImmutableParams.from(getBillSameAsParams(amendment, billId, sobiFragment));
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_SAME_AS.getSql(schema()), sameAsParams);
            });
        }
    }

    /**
     * Updates the bill's action list into the database.
     */
    protected void updateActions(Bill bill, SobiFragment sobiFragment, ImmutableParams billParams) {
        List<BillAction> existingBillActions = getBillActions(billParams);
        List<BillAction> newBillActions = new ArrayList<>(bill.getActions());
        newBillActions.removeAll(existingBillActions);    // New actions to insert
        existingBillActions.removeAll(bill.getActions()); // Old actions to delete
        // Delete actions that are not in the updated list
        for (BillAction action : existingBillActions) {
            MapSqlParameterSource actionParams = getBillActionParams(action, sobiFragment);
            jdbcNamed.update(SqlBillQuery.DELETE_BILL_ACTION.getSql(schema()), actionParams);
        }
        // Insert all new actions
        for (BillAction action : newBillActions) {
            MapSqlParameterSource actionParams = getBillActionParams(action, sobiFragment);
            jdbcNamed.update(SqlBillQuery.INSERT_BILL_ACTION.getSql(schema()), actionParams);
        }
    }

    /**
     * Update the bill's previous version set.
     */
    protected void updatePreviousBillVersions(Bill bill, SobiFragment sobiFragment, ImmutableParams billParams) {
        Set<BillId> existingPrevBills = getPrevVersions(billParams);
        if (!existingPrevBills.equals(bill.getPreviousVersions())) {
            Set<BillId> newPrevBills = new HashSet<>(bill.getPreviousVersions());
            newPrevBills.removeAll(existingPrevBills);               // New prev bill ids to insert
            existingPrevBills.removeAll(bill.getPreviousVersions()); // Old prev bill ids to delete
            existingPrevBills.forEach(billId -> {
                ImmutableParams prevParams = ImmutableParams.from(getBillPrevVersionParams(bill, billId, sobiFragment));
                jdbcNamed.update(SqlBillQuery.DELETE_BILL_PREVIOUS_VERSIONS.getSql(schema()), prevParams);
            });
            newPrevBills.forEach(billId -> {
                ImmutableParams prevParams = ImmutableParams.from(getBillPrevVersionParams(bill, billId, sobiFragment));
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_PREVIOUS_VERSION.getSql(schema()), prevParams);
            });
        }
    }

    /**
     * Update the bill's previous committee set.
     */
    protected void updateBillCommittees(Bill bill, SobiFragment sobiFragment, ImmutableParams billParams) {
        Set<CommitteeVersionId> existingComms = getBillCommittees(billParams);
        if (!existingComms.equals(bill.getPastCommittees())) {
            Set<CommitteeVersionId> newComms = new HashSet<>(bill.getPastCommittees());
            newComms.removeAll(existingComms);                 // New committees to insert
            existingComms.removeAll(bill.getPastCommittees()); // Old committees to delete
            existingComms.forEach(cvid -> {
                ImmutableParams commParams = ImmutableParams.from(getBillCommitteeParams(bill, cvid, sobiFragment));
                jdbcNamed.update(SqlBillQuery.DELETE_BILL_COMMITTEE.getSql(schema()), commParams);
            });
            newComms.forEach(cvid -> {
                ImmutableParams commParams = ImmutableParams.from(getBillCommitteeParams(bill, cvid, sobiFragment));
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_COMMITTEE.getSql(schema()), commParams);
            });
        }
    }

    /**
     * Update any veto messages through the veto data service
     */
    protected void updateVetoMessages(Bill bill, SobiFragment sobiFragment){
        vetoDataService.deleteBillVetoes(bill.getBaseBillId());
        for(VetoMessage vetoMessage : bill.getVetoMessages().values()){
            vetoDataService.updateVetoMessage(vetoMessage, sobiFragment);
        }
    }

    protected void updateApprovalMessage(Bill bill, SobiFragment sobiFragment){
        approvalDataService.deleteApprovalMessage(bill.getBaseBillId());
        if(bill.getApprovalMessage() != null){
            approvalDataService.updateApprovalMessage(bill.getApprovalMessage(), sobiFragment);
        }
    }

    /**
     * Update the bill's sponsor information.
     */
    protected void updateBillSponsor(Bill bill, SobiFragment sobiFragment, ImmutableParams billParams) {
        if (bill.getSponsor() != null) {
            MapSqlParameterSource params = getBillSponsorParams(bill, sobiFragment);
            if (jdbcNamed.update(SqlBillQuery.UPDATE_BILL_SPONSOR.getSql(schema()), params) == 0) {
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_SPONSOR.getSql(schema()), params);
            }
        }
        else {
            jdbcNamed.update(SqlBillQuery.DELETE_BILL_SPONSOR.getSql(schema()), billParams);
        }
    }

    /**
     * Update the bill milestones list.
     */
    protected void updateBillMilestones(Bill bill, SobiFragment sobiFragment, ImmutableParams billParams) {
        List<BillStatus> existingMilestones = getBillMilestones(billParams);
        List<BillStatus> newMilestones = bill.getMilestones();
        // If old list is not the same as the new list, wipe the old and insert the new. We won't
        // need to keep track of updates for this, so no reason to be precise like cosponsors for example.
        if (!existingMilestones.equals(newMilestones)) {
            jdbcNamed.update(SqlBillQuery.DELETE_BILL_MILESTONES.getSql(schema()), billParams);
            int rank = 1;
            for (BillStatus status : newMilestones) {
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_MILESTONE.getSql(schema()),
                    getMilestoneParams(bill, status, rank++, sobiFragment));
            }
        }
    }

    /**
     * Update the bill's amendment publish statuses.
     */
    protected void updateBillAmendPublishStatus(Bill bill, SobiFragment sobiFragment, ImmutableParams billParams) {
        Map<Version, PublishStatus> existingPubStatus = getBillAmendPublishStatuses(billParams);
        Map<Version, PublishStatus> newPubStatus = bill.getAmendPublishStatusMap();
        MapDifference<Version, PublishStatus> diff = Maps.difference(existingPubStatus, newPubStatus);
        // Old entries that do not show up in the new one should be marked as unpublished
        diff.entriesOnlyOnLeft().forEach((version,pubStatus) -> {
            if (!pubStatus.isOverride() && pubStatus.isPublished()) {
                LocalDateTime dateTime = (sobiFragment != null) ? sobiFragment.getPublishedDateTime()
                                                                : LocalDateTime.now();
                PublishStatus unPubStatus = new PublishStatus(false, dateTime, false, "No longer referenced");
                MapSqlParameterSource params = getBillPublishStatusParams(bill, version, unPubStatus, sobiFragment);
                jdbcNamed.update(SqlBillQuery.UPDATE_BILL_AMEND_PUBLISH_STATUS.getSql(schema()), params);
            }
        });
        // Update changed publish statuses if the existing is not an override
        diff.entriesDiffering().forEach((version,pubStatus) -> {
            if (!pubStatus.leftValue().isOverride()) {
                MapSqlParameterSource params = getBillPublishStatusParams(bill, version, pubStatus.rightValue(), sobiFragment);
                jdbcNamed.update(SqlBillQuery.UPDATE_BILL_AMEND_PUBLISH_STATUS.getSql(schema()), params);
            }
        });
        // Insert new publish statuses
        diff.entriesOnlyOnRight().forEach((version,pubStatus) -> {
            MapSqlParameterSource params = getBillPublishStatusParams(bill, version, pubStatus, sobiFragment);
            jdbcNamed.update(SqlBillQuery.INSERT_BILL_AMEND_PUBLISH_STATUS.getSql(schema()), params);
        });
    }

    /**
     * Update the bill's co sponsor list by deleting, inserting, and updating as needed.
     */
    protected void updateBillCosponsor(BillAmendment billAmendment, SobiFragment sobiFragment, ImmutableParams amendParams) {
        List<SessionMember> existingCoSponsors = getCoSponsors(amendParams);
        if (!existingCoSponsors.equals(billAmendment.getCoSponsors())) {
            MapDifference<SessionMember, Integer> diff = difference(existingCoSponsors, billAmendment.getCoSponsors(), 1);
            // Delete old cosponsors
            diff.entriesOnlyOnLeft().forEach((member,ordinal) -> {
                ImmutableParams cspParams = amendParams.add(new MapSqlParameterSource("sessionMemberId", member.getSessionMemberId()));
                jdbcNamed.update(SqlBillQuery.DELETE_BILL_COSPONSOR.getSql(schema()), cspParams);
            });
            // Update re-ordered cosponsors
            diff.entriesDiffering().forEach((member,ordinal) -> {
                ImmutableParams cspParams = ImmutableParams.from(
                    getCoMultiSponsorParams(billAmendment, member, ordinal.rightValue(),sobiFragment));
                jdbcNamed.update(SqlBillQuery.UPDATE_BILL_COSPONSOR.getSql(schema()), cspParams);
            });
            // Insert new cosponsors
            diff.entriesOnlyOnRight().forEach((member,ordinal) -> {
                ImmutableParams cspParams = ImmutableParams.from(
                    getCoMultiSponsorParams(billAmendment, member, ordinal,sobiFragment));
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_COSPONSOR.getSql(schema()), cspParams);
            });
        }
    }

    /**
     * Update the bill's multi-sponsor list by deleting, inserting, and updating as needed.
     */
    protected void updateBillMultiSponsor(BillAmendment billAmendment, SobiFragment sobiFragment, ImmutableParams amendParams) {
        List<SessionMember> existingMultiSponsors = getMultiSponsors(amendParams);
        if (!existingMultiSponsors.equals(billAmendment.getMultiSponsors())) {
            MapDifference<SessionMember, Integer> diff = difference(existingMultiSponsors, billAmendment.getMultiSponsors(), 1);
            // Delete old multisponsors
            diff.entriesOnlyOnLeft().forEach((member,ordinal) -> {
                ImmutableParams mspParams = amendParams.add(new MapSqlParameterSource("sessionMemberId", member.getSessionMemberId()));
                jdbcNamed.update(SqlBillQuery.DELETE_BILL_MULTISPONSOR.getSql(schema()), mspParams);
            });
            // Update re-ordered multisponsors
            diff.entriesDiffering().forEach((member,ordinal) -> {
                ImmutableParams mspParams = ImmutableParams.from(
                    getCoMultiSponsorParams(billAmendment, member, ordinal.rightValue(),sobiFragment));
                jdbcNamed.update(SqlBillQuery.UPDATE_BILL_MULTISPONSOR.getSql(schema()), mspParams);
            });
            // Insert new multisponsors
            diff.entriesOnlyOnRight().forEach((member,ordinal) -> {
                ImmutableParams mspParams = ImmutableParams.from(
                    getCoMultiSponsorParams(billAmendment, member, ordinal,sobiFragment));
                jdbcNamed.update(SqlBillQuery.INSERT_BILL_MULTISPONSOR.getSql(schema()), mspParams);
            });
        }
    }

    /**
     * Update the bill amendment's list of votes.
     */
    protected void updateBillVotes(BillAmendment billAmendment, SobiFragment sobiFragment, ImmutableParams amendParams) {
        List<BillVote> existingBillVotes = getBillVotes(amendParams);
        List<BillVote> newBillVotes = new ArrayList<>(billAmendment.getVotesList());
        newBillVotes.removeAll(existingBillVotes);
        existingBillVotes.removeAll(billAmendment.getVotesList());
        // Delete all outdated votes
        for (BillVote billVote : existingBillVotes) {
            MapSqlParameterSource voteInfoParams = getBillVoteInfoParams(billAmendment, billVote, sobiFragment);
            jdbcNamed.update(SqlBillQuery.DELETE_BILL_VOTES_INFO.getSql(schema()), voteInfoParams);
        }
        // Insert the new/updated votes
        for (BillVote billVote : newBillVotes) {
            MapSqlParameterSource voteParams = getBillVoteInfoParams(billAmendment, billVote, sobiFragment);
            jdbcNamed.update(SqlBillQuery.INSERT_BILL_VOTES_INFO.getSql(schema()), voteParams);
            for (BillVoteCode voteCode : billVote.getMemberVotes().keySet()) {
                voteParams.addValue("voteCode", voteCode.name().toLowerCase());
                for (SessionMember member : billVote.getMembersByVote(voteCode)) {
                    voteParams.addValue("sessionMemberId", member.getSessionMemberId());
                    voteParams.addValue("memberShortName", member.getLbdcShortName());
                    jdbcNamed.update(SqlBillQuery.INSERT_BILL_VOTES_ROLL.getSql(schema()), voteParams);
                }
            }
        }
    }

    /** --- Helper Classes --- */

    private static class BillRowMapper implements RowMapper<Bill>
    {
        @Override
        public Bill mapRow(ResultSet rs, int rowNum) throws SQLException {
            Bill bill = new Bill(new BaseBillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year")));
            bill.setTitle(rs.getString("title"));
            bill.setSummary(rs.getString("summary"));
            bill.setActiveVersion(Version.of(rs.getString("active_version")));
            if (rs.getString("program_info") != null) {
                bill.setProgramInfo(new ProgramInfo(rs.getString("program_info"), rs.getInt("program_info_num")));
            }
            bill.setYear(rs.getInt("active_year"));
            if (rs.getString("status") != null) {
                BillStatus status = new BillStatus(BillStatusType.valueOf(rs.getString("status")),
                                                   rs.getDate("status_date").toLocalDate());
                status.setCommitteeId(getCommitteeIdFromRs(rs));
                status.setCalendarNo(rs.getInt("bill_cal_no") != 0 ? rs.getInt("bill_cal_no") : null);
                bill.setStatus(status);
            }
            if (rs.getString("sub_bill_print_no") != null) {
                bill.setSubstitutedBy(new BaseBillId(rs.getString("sub_bill_print_no"), bill.getSession()));
            }
            setModPubDatesFromResultSet(bill, rs);
            return bill;
        }
    }

    private static class BillAmendmentRowMapper implements RowMapper<BillAmendment>
    {
        @Override
        public BillAmendment mapRow(ResultSet rs, int rowNum) throws SQLException {
            BaseBillId baseBillId = new BaseBillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"));
            BillAmendment amend = new BillAmendment(baseBillId, Version.of(rs.getString("bill_amend_version")));
            amend.setMemo(rs.getString("sponsor_memo"));
            amend.setActClause(rs.getString("act_clause"));
            amend.setFullText(rs.getString("full_text"));
            amend.setStricken(rs.getBoolean("stricken"));
            amend.setUniBill(rs.getBoolean("uni_bill"));
            amend.setLawSection(rs.getString("law_section"));
            amend.setLaw(rs.getString("law_code"));
            return amend;
        }
    }

    private static class BillAmendPublishStatusHandler implements RowCallbackHandler
    {
        TreeMap<Version, PublishStatus> publishStatusMap = new TreeMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            PublishStatus pubStatus = new PublishStatus(
                rs.getBoolean("published"), getLocalDateTimeFromRs(rs, "effect_date_time"),
                rs.getBoolean("override"), rs.getString("notes"));
            publishStatusMap.put(Version.of(rs.getString("bill_amend_version")), pubStatus);
        }

        public TreeMap<Version, PublishStatus> getPublishStatusMap() {
            return publishStatusMap;
        }
    }

    private static class BillActionRowMapper implements RowMapper<BillAction>
    {
        @Override
        public BillAction mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillAction billAction = new BillAction();
            billAction.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                    rs.getString("bill_amend_version")));
            billAction.setChamber(Chamber.valueOf(rs.getString("chamber").toUpperCase()));
            billAction.setSequenceNo(rs.getInt("sequence_no"));
            billAction.setDate(getLocalDateFromRs(rs, "effect_date"));
            billAction.setText(rs.getString("text"));
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
            int sessionMemberId = rs.getInt("session_member_id");
            sponsor.setBudget(rs.getBoolean("budget_bill"));
            sponsor.setRules(rs.getBoolean("rules_sponsor"));
            if (sessionMemberId > 0) {
                try {
                    sponsor.setMember(memberService.getMemberBySessionId(sessionMemberId));
                }
                catch (MemberNotFoundEx memberNotFoundEx) {
                    logger.warn("Bill referenced a sponsor that does not exist. {}", memberNotFoundEx.getMessage());
                }
            }
            return sponsor;
        }
    }

    private static class BillMemberRowMapper implements RowMapper<SessionMember>
    {
        private MemberService memberService;

        private BillMemberRowMapper(MemberService memberService) {
            this.memberService = memberService;
        }

        @Override
        public SessionMember mapRow(ResultSet rs, int rowNum) throws SQLException {
            int sessionMemberId = rs.getInt("session_member_id");
            try {
                return memberService.getMemberBySessionId(sessionMemberId);
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
            SessionYear session = getSessionYearFromRs(rs, "bill_session_year");
            LocalDate actionDate = getLocalDateFromRs(rs, "action_date");
            return new CommitteeVersionId(committeeChamber, committeeName, session, actionDate.atStartOfDay());
        }
    }

    /** --- Param Source Methods --- */

    public ImmutableParams getBaseParams(BillId billId) {
        return ImmutableParams.from(new MapSqlParameterSource()
                .addValue("printNo", billId.getBasePrintNo())
                .addValue("sessionYear", billId.getSession().getYear()));
    }

    public ImmutableParams getBillIdParams(BillId billId) {
        return ImmutableParams.from(new MapSqlParameterSource()
                .addValue("printNo", billId.getBasePrintNo())
                .addValue("sessionYear", billId.getSession().getYear())
                .addValue("version", billId.getVersion().getValue()));
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to Bill values for use in update/insert queries on
     * the bill table.
     */
    private static MapSqlParameterSource getBillParams(Bill bill, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("title", bill.getTitle())
              .addValue("summary", bill.getSummary())
              .addValue("activeVersion", bill.getActiveVersion().getValue())
              .addValue("activeYear", bill.getYear())
              .addValue("programInfo", bill.getProgramInfo()!=null ? bill.getProgramInfo().getInfo() : null)
              .addValue("programInfoNum", bill.getProgramInfo()!=null ? bill.getProgramInfo().getNumber() : null)
              .addValue("status", bill.getStatus() != null ? bill.getStatus().getStatusType().name() : null)
              .addValue("statusDate", bill.getStatus() != null ? toDate(bill.getStatus().getActionDate()) : null)
              .addValue("committeeName", bill.getStatus() != null && bill.getStatus().getCommitteeId() != null
                                         ? bill.getStatus().getCommitteeId().getName() : null)
              .addValue("committeeChamber", bill.getStatus() != null && bill.getStatus().getCommitteeId() != null
                                            ? bill.getStatus().getCommitteeId().getChamber().asSqlEnum() : null)
              .addValue("billCalNo", bill.getStatus() != null ? bill.getStatus().getCalendarNo() : null)
              .addValue("subPrintNo", bill.getSubstitutedBy() != null ? bill.getSubstitutedBy().getBasePrintNo() : null);
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
              .addValue("fullText", amendment.getFullText())
              .addValue("stricken", amendment.isStricken())
              .addValue("lawSection", amendment.getLawSection())
              .addValue("lawCode", amendment.getLaw())
              .addValue("uniBill", amendment.isUniBill());
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillPublishStatusParams(Bill bill, Version version, PublishStatus pubStatus,
                                                                    SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("version", version.getValue());
        params.addValue("published", pubStatus.isPublished());
        params.addValue("effectDateTime", toDate(pubStatus.getEffectDateTime()));
        params.addValue("override", pubStatus.isOverride());
        params.addValue("notes", pubStatus.getNotes());
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
              .addValue("sessionYear", billAction.getBillId().getSession().getYear())
              .addValue("chamber", billAction.getChamber().toString().toLowerCase())
              .addValue("version", billAction.getBillId().getVersion().getValue())
              .addValue("effectDate", toDate(billAction.getDate()))
              .addValue("text", billAction.getText())
              .addValue("sequenceNo", billAction.getSequenceNo());
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillSameAsParams(BillAmendment billAmendment, BillId sameAs, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("sameAsPrintNo", sameAs.getBasePrintNo())
              .addValue("sameAsSessionYear", sameAs.getSession().getYear())
              .addValue("sameAsVersion", sameAs.getVersion().getValue());
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillPrevVersionParams(Bill bill, BillId prevVersion, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("prevPrintNo", prevVersion.getBasePrintNo())
              .addValue("prevSessionYear", prevVersion.getSession().getYear())
              .addValue("prevVersion", prevVersion.getVersion().getValue());
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getBillSponsorParams(Bill bill, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        BillSponsor billSponsor = bill.getSponsor();
        boolean hasMember = billSponsor != null && billSponsor.hasMember();
        addBillIdParams(bill, params);
        params.addValue("sessionMemberId", (hasMember) ? billSponsor.getMember().getSessionMemberId() : null)
              .addValue("budgetBill", (billSponsor != null && billSponsor.isBudget()))
              .addValue("rulesSponsor", (billSponsor != null && billSponsor.isRules()));
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getMilestoneParams(Bill bill, BillStatus status, int rank, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(bill, params);
        params.addValue("status", status.getStatusType().name())
            .addValue("rank", rank)
            .addValue("actionSequenceNo", status.getActionSequenceNo())
            .addValue("date", toDate(status.getActionDate()))
            .addValue("committeeName", (status.getCommitteeId() != null ? status.getCommitteeId().getName() : null))
            .addValue("committeeChamber", (status.getCommitteeId() != null ?
                    status.getCommitteeId().getChamber().asSqlEnum() : null))
            .addValue("calNo", status.getCalendarNo());
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getCoMultiSponsorParams(BillAmendment billAmendment, SessionMember member,
                                                                 int sequenceNo, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addBillIdParams(billAmendment, params);
        params.addValue("sessionMemberId", member.getSessionMemberId())
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
              .addValue("sequenceNo", billVote.getSequenceNo())
              .addValue("committeeName", (billVote.getCommitteeId() != null)
                                         ? billVote.getCommitteeId().getName() : null)
              .addValue("committeeChamber", (billVote.getCommitteeId() != null)
                                            ? billVote.getCommitteeId().getChamber().asSqlEnum() : null);
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
              .addValue("sessionYear", bill.getSession().getYear());
    }

    /**
     * Adds columns that identify the bill amendment.
     */
    private static void addBillIdParams(BillAmendment billAmendment, MapSqlParameterSource params) {
        params.addValue("printNo", billAmendment.getBasePrintNo())
              .addValue("sessionYear", billAmendment.getSession().getYear())
              .addValue("version", billAmendment.getVersion().getValue());
    }

    /**
     * Get a CommitteeId from the result set or null if column doesn't have a value.
     */
    private static CommitteeId getCommitteeIdFromRs(ResultSet rs) throws SQLException {
        if (rs.getString("committee_name") != null) {
            return new CommitteeId(Chamber.getValue(rs.getString("committee_chamber")), rs.getString("committee_name"));
        }
        return null;
    }
}