package gov.nysenate.openleg;

import com.google.common.base.*;
import com.google.common.base.Objects;
import com.google.common.collect.*;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.service.entity.MemberService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


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

    @Test
    public void testName() throws Exception {
//        LocalDate date = new LocalDate();
//        Member member = new Member();
//        member.setLbdcShortName("MOOSE");
//        Member member2 = new Member();
//        member2.setLbdcShortName("MOOSE");
//        BillVote b1 = new BillVote(new BillId("S1234", 2011), date.toDate(), BillVoteType.FLOOR, 1);
//        b1.addMemberVote(BillVoteCode.ABS, member);
//        BillVote b2 = new BillVote(new BillId("S1234", 2011), date.toDate(), BillVoteType.FLOOR, 1);
//        b2.addMemberVote(BillVoteCode.ABS, member2);
//        b2.addMemberVote(BillVoteCode.ABD, member);
//        assertTrue(b1.equals(b2));
    }

    private void varArgsTest(int a, Object... args) {
        logger.info("IN METHOD");
    }

    class GREATER_THAN_FOUR implements Predicate<String>{
        @Override
        public boolean apply(String input) {
            return input.length() > 4;
        }
    }

    @Test
    public void testSometingElse() throws Exception {
        String s = "<senagendavote  ";
        logger.info("{}", Arrays.asList(SobiFragmentType.values()).stream()
                .filter(f -> s.matches(f.getStartPattern())).reduce(null, (a, b) -> a));
    }

    @Test
    public void testMapRetains() throws Exception {
        LinkedListMultimap<Integer, String> mm = LinkedListMultimap.create();
        mm.put(1, "e");
        mm.put(2, "d");
        logger.info("{}", LinkedListMultimap.create(mm).keySet().retainAll(Sets.newHashSet(2)));
        logger.info("{}", mm);
        Range<Date> dateRange = Range.atMost(new Date());
    }

    @Test
    public void testLocalDateTime() throws Exception {
        LocalDateTime date = LocalDateTime.now();
        logger.info("{}", date);
    }

    @Test
    public void testOutput() throws Exception {
        class TestObject {
            private String s;
            private int i;

            TestObject(String s, int i) {
                this.s = s;
                this.i = i;
            }

            public String getS() {
                return s;
            }

            public void setS(String s) {
                this.s = s;
            }

            public int getI() {
                return i;
            }

            public void setI(int i) {
                this.i = i;
            }

            @Override
            public String toString() {
                return Objects.toStringHelper(this)
                        .add("s", s)
                        .add("i", i)
                        .toString();
            }
        }

        List<TestObject> ss = Arrays.asList(new TestObject("S23", 2009));
        Map<Integer, TestObject> sMap = Maps.newHashMap(Maps.uniqueIndex(ss, TestObject::getI));
        sMap.put(2010, new TestObject("A", 2));
        logger.info("{}", sMap);
    }

    @Test
    public void testMaxMap() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("moose",42);
        map.put("dog", 9);
        map.put("cow", 299);
        map.put("sheep", 42);
        map.put("lamb", 420);
        map.keySet().retainAll(
            map.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(2).map(f -> f.getKey()).collect(Collectors.toSet()));
        logger.info("{}", map);
//        }
//        List<Double> maxValues = new ArrayList<>(map.values());
//        Collections.sort(maxValues, Collections.reverseOrder());
//        logger.info("{}", maxValues);
//        for (Double value : maxValues.subList(3, map.size())) {
//            map.remove(inverseMap.get(value));
//        }
//        logger.info("{}", map);
    }

    public void retainMaxNValues(Map<String, Integer> map, int n) {
        List<Map.Entry<String, Integer>> kv = new LinkedList<>(map.entrySet());

        Collections.sort(kv, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        for (Map.Entry<String,Integer> e: kv.subList(kv.size()-3, kv.size())) {
            System.out.println("e:" + e);
        }
    }

    @Test
    public void testForEach() throws Exception {
        Map<String, Integer> map = new TreeMap<>();
        map.put("moose",42);
        map.put("dog", 9);
        map.put("cow", 299);
        map.put("sheep", 42);
        map.put("lamb", 420);
        map.keySet().stream().forEach(
            f -> { logger.info("{}", f); }
        );
        map.keySet().parallelStream().forEach(
            f -> logger.info("{}", f)
        );
    }

    @Test
    public void testDates() throws Exception {
        logger.info("{}", Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
