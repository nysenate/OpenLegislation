package gov.nysenate.openleg.legislation.agenda.dao;

import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillVoteId;
import gov.nysenate.openleg.legislation.bill.BillVoteType;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Creates a BillVoteId reference from the result set.
 */
public class BillVoteIdRowMapper implements RowMapper<BillVoteId>
{
    @Override
    public BillVoteId mapRow(ResultSet rs, int rowNum) throws SQLException {
        BillId billId = new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                rs.getString("bill_amend_version"));
        LocalDate voteDate = SqlBaseDao.getLocalDateFromRs(rs, "vote_date");
        BillVoteType voteType = BillVoteType.getValue(rs.getString("vote_type"));
        int sequenceNo = rs.getInt("sequence_no");
        CommitteeId commId = null;
        if (rs.getString("committee_name") != null) {
            commId = new CommitteeId(Chamber.getValue(rs.getString("committee_chamber").toUpperCase()),
                                     rs.getString("committee_name"));
        }
        return new BillVoteId(billId, voteDate, voteType, sequenceNo, commId);
    }
}