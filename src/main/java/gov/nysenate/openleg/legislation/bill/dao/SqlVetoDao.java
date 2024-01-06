package gov.nysenate.openleg.legislation.bill.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.OrderBy;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.VetoId;
import gov.nysenate.openleg.legislation.bill.VetoMessage;
import gov.nysenate.openleg.legislation.bill.VetoType;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
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

import static gov.nysenate.openleg.common.util.DateUtils.toDate;

@Repository
public class SqlVetoDao extends SqlBaseDao implements VetoDao {
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
    public void updateVetoMessage(VetoMessage vetoMessage, LegDataFragment legDataFragment) throws DataAccessException {
        MapSqlParameterSource params = getVetoParams(vetoMessage, legDataFragment);
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

    private static class VetoRowMapper implements RowMapper<VetoMessage> {
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

    private static MapSqlParameterSource getVetoIdParams(VetoId vetoId){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("vetoNumber", vetoId.getVetoNumber());
        return params.addValue("year", vetoId.getYear());
    }

    private static MapSqlParameterSource getVetoParams(VetoMessage vetoMessage, LegDataFragment legDataFragment){
        MapSqlParameterSource params = getVetoIdParams(vetoMessage.getVetoId());
        params.addValue("printNum", vetoMessage.getBillId().getBasePrintNo());
        params.addValue("sessionYear", vetoMessage.getSession().year());
        params.addValue("chapter", vetoMessage.getChapter());
        params.addValue("page", vetoMessage.getBillPage());
        params.addValue("lineStart", vetoMessage.getLineStart());
        params.addValue("lineEnd", vetoMessage.getLineEnd());
        params.addValue("signer", vetoMessage.getSigner());
        params.addValue("date", toDate(vetoMessage.getSignedDate()));
        params.addValue("memoText", vetoMessage.getMemoText());
        params.addValue("type", vetoMessage.getType().toString().toLowerCase());
        addLastFragmentParam(legDataFragment, params);
        addModPubDateParams(vetoMessage.getModifiedDateTime(), vetoMessage.getPublishedDateTime(), params);
        return params;
    }

    private static MapSqlParameterSource getBaseBillIdParams(BaseBillId baseBillId){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNum", baseBillId.getBasePrintNo());
        return params.addValue("sessionYear", baseBillId.getSession().year());
    }
}
