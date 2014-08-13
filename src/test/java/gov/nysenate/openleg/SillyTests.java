package gov.nysenate.openleg;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import gov.nysenate.openleg.dao.agenda.SqlAgendaQuery;
import gov.nysenate.openleg.dao.base.SqlTable;
import gov.nysenate.openleg.dao.bill.BillDao;
import gov.nysenate.openleg.service.base.CachingService;
import gov.nysenate.openleg.service.entity.MemberService;
import gov.nysenate.openleg.util.StringDiffer;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


public class SillyTests //extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

//    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private static class MooseEvent extends ApplicationEvent {
        /**
         * Create a new ApplicationEvent.
         *
         * @param source the component that published the event (never {@code null})
         */
        public MooseEvent(Object source) {
            super(source);
        }
    }

    //@Autowired
    public DataSource dataSource;

    //@Autowired
    private MemberService memberService;


    @Autowired
    private BillDao billDao;

    @Test
    public void testPrint() throws Exception {
        logger.info("SELECT cv.vote_action, cv.refer_committee_name, cv.refer_committee_chamber, cv.with_amendment," +
                "       vi.bill_print_no, vi.bill_session_year, vi.bill_amend_version, vi.vote_date, vi.vote_type," +
                "       vi.sequence_no, vi.published_date_time, vi.modified_date_time," +
                "       vr.member_id, vr.session_year, vr.vote_code\n" +
                "FROM ${schema}." + SqlTable.AGENDA_VOTE_COMMITTEE_VOTE + " cv\n" +
                "JOIN ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + " vi ON cv.vote_info_id = vi.id\n" +
                "JOIN ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_ROLL + " vr ON vi.id = vr.vote_id\n" +
                "WHERE vote_committee_id IN (" + SqlAgendaQuery.SELECT_AGENDA_VOTE_COMMITTEE_ID.getSql("master") + ")");

    }

    @Test
    public void testDateParse() throws Exception {
        logger.info("{}", LocalDateTime.ofInstant(
                DateUtils.parseDateStrictly("SOBI.D110313.T090312.TXT", "'SOBI.D'yyMMdd'.T'HHmmss'.TXT'").toInstant(),
                ZoneId.systemDefault()));
    }

    @Test
    public void testLinkedHashSet() throws Exception {
        List<String> mooo = new LinkedList<>();
        mooo.add("ghjk");
        mooo.add("moo");
        mooo.add("ghjk");
        mooo.add("asdf");

        ImmutableList<String> immutableMoo = ImmutableList.copyOf(mooo);

        mooo.add(2, "cow");

        mooo.forEach(System.out::println);
        logger.info("=========");
        immutableMoo.forEach(System.out::println);
    }

    @Test
    public void testDiff() throws Exception {
//        Bill a6357 = billDao.getBill(new BillId("A6357", 2013));
        String s1 = "mew moew mewtwo moo";
        String s2 = "mew moew pew mewtwo moo";
        StringDiffer diff = new StringDiffer();
        LinkedList<StringDiffer.Diff> diffs = diff.diff_main(s1, s2);
        logger.info(diff.diff_prettyHtml(diffs));
        logger.info("{}", OutputUtils.toJson(diffs));
//        logger.info(StringUtils.difference("mew moew mewtwo moo", "mew moew pew mewtwo moo"));
    }

    @Test
    public void testNavigableSet() throws Exception {
        TreeSet<BigInteger> numberSet = new TreeSet<>();
        Resource input = new FileSystemResource("/home/ash/Desktop/mill_lines.txt");
        StopWatch sw = new StopWatch();
        Map<BigInteger, Boolean> bigIntegers = new HashMap<>();
        Files.readLines(input.getFile(), Charset.defaultCharset()).forEach(str -> {
            BigInteger bd = new BigInteger(str);
            bigIntegers.put(bd, false);
            numberSet.add(bd);
        });
        sw.start();
        HashSet<BigInteger> tSet = new HashSet<>();
        bigIntegers.keySet().forEach(i -> {
            NavigableSet<BigInteger> subset = numberSet.subSet(new BigInteger("-10000").subtract(i), true, new BigInteger("10000").subtract(i), true);
            for (BigInteger b : subset) {
                if (!bigIntegers.get(b)) {
                    tSet.add(b.add(i));
                }
            }
        });
        sw.stop();
        logger.info("{}", sw.getTime());
    }

    @Test
    public void testMedian() throws Exception {
        Resource input = new FileSystemResource("/home/ash/Desktop/Median.txt");
        PriorityQueue<Integer> medianQueue = new PriorityQueue<>();
        int rollingMedian = 0;
        for (String line : Files.readLines(input.getFile(), Charset.defaultCharset())) {
            Integer x = Integer.valueOf(line);
            medianQueue.add(x);
            Object[] arr = medianQueue.toArray();
            Arrays.sort(arr);
            int medianIndex = (arr.length % 2 == 0) ? (arr.length / 2) - 1 : (arr.length / 2);
            if (medianIndex >= 0) {
                rollingMedian += (Integer) arr[medianIndex];
            }
        }
        logger.info("{}", rollingMedian % 10000);
    }

    @Test
    public void testMapSomethign() throws Exception {
        TreeMap<String, Boolean> map = new TreeMap<>();
        map.put("A", false);
        map.put("B", true);
        logger.info("{}", map.descendingKeySet().stream().filter(map::get).findFirst());

    }
}
