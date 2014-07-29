package gov.nysenate.openleg;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.bill.BillVoteType;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.service.entity.MemberService;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class SillyTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    @Autowired
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

    @Autowired
    public DataSource dataSource;

    @Autowired
    private MemberService memberService;

    @Test
    public void testName() throws Exception {
        LocalDate date = new LocalDate();
        Member member = new Member();
        member.setLbdcShortName("MOOSE");
        Member member2 = new Member();
        member2.setLbdcShortName("MOOSE");
        BillVote b1 = new BillVote(new BillId("S1234", 2011), date.toDate(), BillVoteType.FLOOR, 1);
        b1.addMemberVote(BillVoteCode.ABS, member);
        BillVote b2 = new BillVote(new BillId("S1234", 2011), date.toDate(), BillVoteType.FLOOR, 1);
        b2.addMemberVote(BillVoteCode.ABS, member2);
        b2.addMemberVote(BillVoteCode.ABD, member);
        assertTrue(b1.equals(b2));
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
        List<String> myList = ImmutableList.of("summer", "in", "the", "city");
        List<String> filteredList = Lists.newArrayList(Collections2.filter(myList, new GREATER_THAN_FOUR()));
        logger.info("{}", filteredList);
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
}
