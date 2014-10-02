package gov.nysenate.openleg;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.service.entity.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


public class SillyTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    @Autowired
    NamedParameterJdbcTemplate jdbcNamed;

    //@Autowired
    public DataSource dataSource;

    //@Autowired
    private MemberService memberService;

    @Autowired
    private BillDao billDao;

    @Test
    public void testMapSomethign() throws Exception {
        String s = "SUBSTITUTED BY A1234";
//        logger.info("{}", StringUtils.containsIgnoreCase());

    }

    @Test
    public void testHstorePostgres() throws Exception {
        jdbcNamed.query("SELECT hstore_to_array(key) as k from master.sobi_change_log limit 1", (rs, row) -> {
            logger.info("{}", getHstore(rs, "k"));
            return null;
        });
    }

    private Map<String, String> getHstore(ResultSet rs, String column) throws SQLException {
        String[] hstoreArr = (String[]) rs.getArray(column).getArray();
        Map<String, String> hstoreMap = new HashMap<>();
        String key = "";
        for (int i = 0; i < hstoreArr.length; i++) {
            if (i % 2 == 0) {
                key = hstoreArr[i];
            }
            else {
                hstoreMap.put(key, hstoreArr[i]);
            }
        }
        return hstoreMap;
    }

    @Test
    public void testInsertHstore() throws Exception {
        Map<String, String> keys = new HashMap<>();
        keys.put("apple", "NY");
        keys.put("potatoes", "ID");
        String keystr = keys.entrySet().stream().map(kv -> kv.getKey() + "=>" + kv.getValue()).collect(Collectors.joining(","));
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("key", keystr);
        jdbcNamed.update("INSERT INTO public.test (key) VALUES (hstore(:key))", params);

    }
}
