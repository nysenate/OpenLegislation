package gov.nysenate.openleg.dao.bill.text;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.calendar.CalendarActiveListEntry;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextSpotcheckReference;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kyle on 3/19/15.
 */
@Repository
public class SqlBillTextReferenceDao extends SqlBaseDao implements BillTextReferenceDao{
    @Override
    public void insertBillTextReference(BillTextSpotcheckReference ref) {
        MapSqlParameterSource params = getParams(ref);
        KeyHolder key = new GeneratedKeyHolder();
        if (jdbcNamed.update(SqlBillTextReferenceQuery.UPDATE_BILL_REFERENCE.getSql(schema()), params, key,new String[] { "id" }) == 0){
            jdbcNamed.update(SqlBillTextReferenceQuery.INSERT_BILL_TEXT_REFERENCE.getSql(schema()), params, key,new String[] { "id" });
        }
        jdbcNamed.update(SqlBillTextReferenceQuery.INSERT_BILL_TEXT_REFERENCE.getSql(schema()), params);
    }

    @Override
    public void deleteBillTextReference(BillTextSpotcheckReference ref) {
        MapSqlParameterSource params = getParams(ref);
        jdbcNamed.update(SqlBillTextReferenceQuery.DELETE_BILL_REFERENCE.getSql(schema()), params);
    }

    @Override
    public BillTextSpotcheckReference getMostRecentBillTextReference(BaseBillId id, LocalDateTime start, LocalDateTime end) {
        MapSqlParameterSource params = getParams(id);       //not sure if need this or need to change
        Range<LocalDateTime> range = Range.closed(start, end);
        addDateTimeRangeParams(params,  range);
        return jdbcNamed.queryForObject(SqlBillTextReferenceQuery.SELECT_BILL_TEXT_RANGE.getSql(schema()), params, new BillRowMapper());
    }

    @Override
    public List<BillTextSpotcheckReference> getBillTextReference(BaseBillId id) {
        MapSqlParameterSource params = getParams(id);
        return jdbcNamed.query(SqlBillTextReferenceQuery.SELECT_ALL_BILL_TEXT_REFERENCE.getSql(schema()), params, new BillRowMapper());
    }

    @Override
    public BillTextSpotcheckReference getBillTextReference(BaseBillId id, LocalDateTime refDateTime) {
        MapSqlParameterSource params = getParams(id, refDateTime);
        return jdbcNamed.queryForObject(SqlBillTextReferenceQuery.SELECT_BILL_TEXT_REFERENCE.getSql(schema()), params, new BillRowMapper());
    }
    @Override
    public BillTextSpotcheckReference getPKBillTextReference(BaseBillId id, LocalDateTime refDateTime) {
        MapSqlParameterSource params = getParams(id, refDateTime);
        return jdbcNamed.queryForObject(SqlBillTextReferenceQuery.SELECT_PK_BILL_TEXT_REFERENCE.getSql(schema()), params, new BillRowMapper());
    }


    /*----------   Map Parameters   -------*/
    public MapSqlParameterSource getParams(BillTextSpotcheckReference entry){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bill_print_no", entry.getPrintNo());
        params.addValue("bill_session_year", entry.getSessionYear());
        params.addValue("reference_date_time", DateUtils.toDate(entry.getReferenceDate()));
        params.addValue("bill_amend_version", entry.getAmendment().getValue());
        params.addValue("text", entry.getText());
        params.addValue("memo", entry.getMemo());

        return params;
    }
    public MapSqlParameterSource getParams(BaseBillId id, LocalDateTime refDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bill_print_no", id.getPrintNo());
        params.addValue("bill_session_year", id.getSession().getYear());
        params.addValue("bill_amend_version", id.getVersion().getValue());
        params.addValue("reference_date_time", DateUtils.toDate(refDateTime));

        return params;
    }
    public MapSqlParameterSource getParams(BaseBillId id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bill_print_no", id.getPrintNo());
        params.addValue("bill_session_year", id.getSession().getYear());
        params.addValue("bill_amend_version", id.getVersion().getValue());

        return params;
    }
    /*----------   Bill Row Mapper   -------*/

    private class BillRowMapper implements RowMapper<BillTextSpotcheckReference>{
        @Override
        public BillTextSpotcheckReference mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillTextSpotcheckReference ref = new BillTextSpotcheckReference();
            ref.setPrintNo(rs.getString("bill_print_no"));
            ref.setSessionYear(SessionYear.of(rs.getInt("bill_session_year")));
            ref.setText(rs.getString("text"));
            ref.setMemo(rs.getString("memo"));
            ref.setReferenceDate(DateUtils.getLocalDateTime(rs.getTimestamp("reference_date_time")));
            ref.setAmendment(Version.of(rs.getString("bill_amend_version")));
            return ref;
        }
    }


}
