package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.ApprovalId;
import gov.nysenate.openleg.model.bill.ApprovalMessage;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SqlApprovalDao extends SqlBaseDao implements ApprovalDao
{
    /** {@inheritDoc} */
    @Override
    public ApprovalMessage getApprovalMessage(ApprovalId approvalId) throws DataAccessException {
        MapSqlParameterSource params = getApprovalIdParams(approvalId);
        return jdbcNamed.queryForObject(SqlApprovalQuery.SELECT_APPROVAL_BY_ID.getSql(schema()),
                                        params, new ApprovalMessageRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public ApprovalMessage getApprovalMessage(BaseBillId baseBillId) throws DataAccessException {
        MapSqlParameterSource params = getBaseBillIdParams(baseBillId);
        return jdbcNamed.queryForObject(
            SqlApprovalQuery.SELECT_APPROVAL_BY_BILL.getSql(schema()), params, new ApprovalMessageRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<ApprovalMessage> getApprovalMessages(int year) throws DataAccessException {
        MapSqlParameterSource params = getYearParams(year);
        return jdbcNamed.query(SqlApprovalQuery.SELECT_APPROVALS_BY_YEAR.getSql(schema()),
                params, new ApprovalMessageRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public void updateApprovalMessage(ApprovalMessage approvalMessage, SobiFragment sobiFragment) {
        MapSqlParameterSource params = getApprovalMessageParams(approvalMessage, sobiFragment);
        if(jdbcNamed.update(SqlApprovalQuery.UPDATE_APPROVAL.getSql(schema()), params) == 0){
            jdbcNamed.update(SqlApprovalQuery.INSERT_APPROVAL.getSql(schema()), params);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteApprovalMessage(ApprovalId approvalId) {
        MapSqlParameterSource params = getApprovalIdParams(approvalId);
        jdbcNamed.update(SqlApprovalQuery.DELETE_APPROVAL_BY_ID.getSql(schema()), params);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteApprovalMessage(BaseBillId baseBillId) {
        MapSqlParameterSource params = getBaseBillIdParams(baseBillId);
        jdbcNamed.update(SqlApprovalQuery.DELETE_APPROVAL_BY_BILL.getSql(schema()), params);
    }

    /** --- Row Mappers --- */

    private class ApprovalMessageRowMapper implements RowMapper<ApprovalMessage>{
        @Override
        public ApprovalMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
            ApprovalMessage approvalMessage = new ApprovalMessage();
            approvalMessage.setApprovalNumber(rs.getInt("approval_number"));
            approvalMessage.setYear(rs.getInt("year"));
            approvalMessage.setSession(new SessionYear(rs.getInt("bill_session_year")));
            approvalMessage.setBillId(new BillId(
               rs.getString("bill_print_no"), rs.getInt("bill_session_year"), rs.getString("bill_amend_version")));
            approvalMessage.setChapter(rs.getInt("chapter"));
            approvalMessage.setSigner(rs.getString("signer"));
            approvalMessage.setMemoText(rs.getString("memo_text"));
            return approvalMessage;
        }
    }

    /** --- Param Mappers --- */

    private MapSqlParameterSource getYearParams(int year){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("year", year);
        return params;
    }

    private MapSqlParameterSource getApprovalIdParams(ApprovalId approvalId){
        MapSqlParameterSource params = getYearParams(approvalId.getYear());
        params.addValue("approvalNumber", approvalId.getApprovalNumber());
        return params;
    }

    private MapSqlParameterSource getBaseBillIdParams(BaseBillId baseBillId){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("billPrintNo", baseBillId.getBasePrintNo());
        params.addValue("sessionYear", baseBillId.getSession().getYear());
        return params;
    }

    private MapSqlParameterSource getApprovalMessageParams(ApprovalMessage approvalMessage, SobiFragment sobiFragment){
        MapSqlParameterSource params = getApprovalIdParams(approvalMessage.getApprovalId());
        params.addValue("billPrintNo", approvalMessage.getBillId().getBasePrintNo());
        params.addValue("sessionYear", approvalMessage.getBillId().getSession().getYear());
        params.addValue("billVersion", approvalMessage.getBillId().getVersion().getValue());
        params.addValue("chapter", approvalMessage.getChapter());
        params.addValue("signer", approvalMessage.getSigner());
        params.addValue("memoText", approvalMessage.getMemoText());
        addLastFragmentParam(sobiFragment, params);
        return params;
    }
}
