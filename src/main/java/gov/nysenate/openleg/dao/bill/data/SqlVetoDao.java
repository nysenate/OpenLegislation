package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.VetoId;
import gov.nysenate.openleg.model.bill.VetoMessage;
import gov.nysenate.openleg.model.bill.VetoType;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlVetoDao extends SqlBaseDao implements VetoDao
{
    public static final Logger logger = LoggerFactory.getLogger(SqlVetoDao.class);

    /** @inheritDoc */
    @Override
    public VetoMessage getVetoMessage(VetoId vetoId) throws DataAccessException {
        MapSqlParameterSource params = getVetoIdParams(vetoId);
        return jdbcNamed.queryForObject(SqlVetoQuery.SELECT_VETO_MESSAGE_SQL.getSql(schema()), params, new VetoRowMapper());
    }

    /** @inheritDoc */
    @Override
    public Map<VetoId,VetoMessage> getBillVetoes(BaseBillId baseBillId) throws DataAccessException {
        MapSqlParameterSource params = getBaseBillIdParams(baseBillId);
        OrderBy orderBy = new OrderBy("year", SortOrder.ASC, "veto_number", SortOrder.ASC);
        List<VetoMessage> vetoMessageList = jdbcNamed.query(
            SqlVetoQuery.SELECT_BILL_VETOES_SQL.getSql(schema(), orderBy, LimitOffset.ALL), params, new VetoRowMapper());
        Map<VetoId,VetoMessage> vetoMap = new HashMap<>();
        for (VetoMessage vetoMessage : vetoMessageList) {
            vetoMap.put(vetoMessage.getVetoId(), vetoMessage);
        }
        return vetoMap;
    }

    /** @inheritDoc */
    @Override
    public void updateVetoMessage(VetoMessage vetoMessage, SobiFragment sobiFragment) throws DataAccessException {
        MapSqlParameterSource params = getVetoParams(vetoMessage, sobiFragment);
        if (jdbcNamed.update(SqlVetoQuery.UPDATE_VETO_MESSAGE_SQL.getSql(schema()), params) == 0){
           jdbcNamed.update(SqlVetoQuery.INSERT_VETO_MESSAGE_SQL.getSql(schema()), params);
        }
    }

    /** @inheritDoc */
    @Override
    public void deleteVetoMessage(VetoId vetoId) {
        MapSqlParameterSource params = getVetoIdParams(vetoId);
        jdbcNamed.update(SqlVetoQuery.DELETE_VETO_MESSAGE.getSql(schema()), params);
    }

    /** @inheritDoc */
    @Override
    public void deleteBillVetoes(BaseBillId baseBillId) {
        MapSqlParameterSource params = getBaseBillIdParams(baseBillId);
        jdbcNamed.update(SqlVetoQuery.DELETE_BILL_VETOES.getSql(schema()), params);
    }

    private class VetoRowMapper implements RowMapper<VetoMessage>
    {
        @Override
        public VetoMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
            VetoMessage vetoMessage = new VetoMessage();
            vetoMessage.setYear(rs.getInt("year"));
            vetoMessage.setVetoNumber(rs.getInt("veto_number"));
            vetoMessage.setBillId(new BaseBillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year")));
            vetoMessage.setSession(getSessionYearFromRs(rs, "bill_session_year"));
            vetoMessage.setType(VetoType.valueOf(rs.getString("type").toUpperCase()));
            vetoMessage.setChapter(rs.getInt("chapter"));
            vetoMessage.setBillPage(rs.getInt("page"));
            vetoMessage.setLineStart(rs.getInt("line_start"));
            vetoMessage.setLineEnd(rs.getInt("line_end"));
            vetoMessage.setSigner(rs.getString("signer"));
            vetoMessage.setSignedDate(DateUtils.getLocalDate(rs.getTimestamp("date")));
            vetoMessage.setMemoText(rs.getString("memo_text"));
            setModPubDatesFromResultSet(vetoMessage, rs);
            return vetoMessage;
        }
    }

    private MapSqlParameterSource getVetoIdParams(VetoId vetoId){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("vetoNumber", vetoId.getVetoNumber());
        params.addValue("year", vetoId.getYear());
        return params;
    }

    private MapSqlParameterSource getVetoParams(VetoMessage vetoMessage, SobiFragment sobiFragment){
        MapSqlParameterSource params = getVetoIdParams(vetoMessage.getVetoId());
        params.addValue("printNum", vetoMessage.getBillId().getBasePrintNo());
        params.addValue("sessionYear", vetoMessage.getSession().getYear());
        params.addValue("chapter", vetoMessage.getChapter());
        params.addValue("page", vetoMessage.getBillPage());
        params.addValue("lineStart", vetoMessage.getLineStart());
        params.addValue("lineEnd", vetoMessage.getLineEnd());
        params.addValue("signer", vetoMessage.getSigner());
        params.addValue("date", toDate(vetoMessage.getSignedDate()));
        params.addValue("memoText", vetoMessage.getMemoText());
        params.addValue("type", vetoMessage.getType().toString().toLowerCase());
        addLastFragmentParam(sobiFragment, params);
        addModPubDateParams(vetoMessage.getModifiedDateTime(), vetoMessage.getPublishedDateTime(), params);
        return params;
    }

    private MapSqlParameterSource getBaseBillIdParams(BaseBillId baseBillId){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNum", baseBillId.getBasePrintNo());
        params.addValue("sessionYear", baseBillId.getSession().getYear());
        return params;
    }
}
