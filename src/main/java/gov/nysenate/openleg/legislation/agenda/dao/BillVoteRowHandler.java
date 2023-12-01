package gov.nysenate.openleg.legislation.agenda.dao;

import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.legislation.bill.BillVote;
import gov.nysenate.openleg.legislation.bill.BillVoteCode;
import gov.nysenate.openleg.legislation.bill.BillVoteId;
import gov.nysenate.openleg.legislation.member.SessionMember;
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
        SessionMember voter = new SessionMember();
        voter.setSessionMemberId(rs.getInt("session_member_id"));
        BillVoteCode voteCode = BillVoteCode.getValue(rs.getString("vote_code"));
        billVote.addMemberVote(voteCode, voter);
        if (rs.getBoolean("is_remote")) {
            billVote.getAttendance().addRemoteMember(voter);
        }
    }
}
