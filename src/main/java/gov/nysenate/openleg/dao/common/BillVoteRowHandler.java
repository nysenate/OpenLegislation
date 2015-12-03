package gov.nysenate.openleg.dao.common;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.bill.BillVoteId;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Iterates through the result set of a bill vote query and returns a collection of BillVotes.
 */
public class BillVoteRowHandler extends SqlBaseDao implements RowCallbackHandler
{
    private static final Logger logger = LoggerFactory.getLogger(BillVoteRowHandler.class);

    private static final BillVoteIdRowMapper voteIdRowMapper = new BillVoteIdRowMapper();
    private final TreeMap<BillVoteId, BillVote> billVoteMap = new TreeMap<>();
    private final MemberService memberService;

    public BillVoteRowHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    public TreeMap<BillVoteId, BillVote> getBillVoteMap() {
        return billVoteMap;
    }

    public List<BillVote> getBillVotes() {
        return new ArrayList<>(billVoteMap.values());
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        BillVoteId billVoteId = voteIdRowMapper.mapRow(rs, 1);
        if (!billVoteMap.containsKey(billVoteId)) {
            logger.trace("Encountered bill vote id {}", billVoteId);
            BillVote billVote = new BillVote(billVoteId);
            SqlBaseDao.setModPubDatesFromResultSet(billVote, rs);
            billVoteMap.put(billVote.getVoteId(), billVote);
        }
        BillVote billVote = billVoteMap.get(billVoteId);
        try {
            SessionMember voter = memberService.getMemberBySessionId(rs.getInt("session_member_id"));
            BillVoteCode voteCode = BillVoteCode.getValue(rs.getString("vote_code"));
            billVote.addMemberVote(voteCode, voter);
        }
        catch (MemberNotFoundEx memberNotFoundEx) {
            logger.error("Failed to add member vote since member could not be found!", memberNotFoundEx);
        }
    }
}
