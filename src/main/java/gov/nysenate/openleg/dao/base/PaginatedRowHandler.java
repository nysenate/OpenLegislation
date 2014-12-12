package gov.nysenate.openleg.dao.base;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a common handler implementation to return a PaginatedList which contains a total
 * count of the available row as well as a subset of results. This allows for the services that
 * utilize this result set to paginate through results instead of getting all the rows at once.
 * @param <T>
 */
public class PaginatedRowHandler<T> implements RowCallbackHandler
{
    private LimitOffset limOff;
    private String totalRowsColumn;
    private RowMapper<T> rowMapper;
    private List<T> results = new ArrayList<>();
    private int totalCount = 0;
    private int rowNum = 0;

    public PaginatedRowHandler(LimitOffset limOff, String totalRowsColumn, RowMapper<T> rowMapper) {
        this.limOff = limOff;
        this.totalRowsColumn = totalRowsColumn;
        this.rowMapper = rowMapper;
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        if (totalCount == 0) {
            totalCount = rs.getInt(totalRowsColumn);
        }
        results.add(rowMapper.mapRow(rs, ++rowNum));
    }

    public PaginatedList<T> getList() {
        return new PaginatedList<>(totalCount, limOff, results);
    }
}
