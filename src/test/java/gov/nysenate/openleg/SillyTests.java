package gov.nysenate.openleg;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.service.agenda.data.AgendaDataService;
import gov.nysenate.openleg.service.entity.MemberService;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SillyTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    /** --- Your silly tests go here --- */

    /*
                      __     __,
                      \,`~"~` /
      .-=-.           /    . .\
     / .-. \          {  =    Y}=
    (_/   \ \          \      /
           \ \        _/`'`'`b
            \ `.__.-'`        \-._
             |            '.__ `'-;_
             |            _.' `'-.__)
              \    ;_..--'/     //  \
              |   /  /   |     //    |
              \  \ \__)   \   //    /
               \__)        './/   .'
                             `'-'`
    */



    private class MyEvent<T> {
        private String msg;
        private T content;

        private MyEvent(String msg, T content) {
            this.msg = msg;
            this.content = content;
        }

        public String getMsg() {
            return msg;
        }
    }

    @Test
    public void testName() throws Exception {
        this.getClass().getClassLoader().getResource("").getPath();

    }

    private class Subscriber {

        @Subscribe
        public void handleMyEvent(MyEvent myEvent) {
            logger.info("Handled MyEvent!");
        }

        @Subscribe
        public void handleDateEvent(MyEvent<LocalDate> dateMyEvent) {
            logger.info("Handled  Date event!");
        }

        @Subscribe
        public void handleStringEvent(MyEvent<String> stringMyEvent) {
            logger.info("Handled String event!");
        }

    }

    @Test
    public void testEventBusWithGenericClasses() throws Exception {
        EventBus eventBus = new EventBus();
        MyEvent<LocalDate> myDateEvent = new MyEvent<>("My String", LocalDate.now());
        Subscriber s = new Subscriber();
        eventBus.register(s);
        eventBus.register(this);
        eventBus.post(myDateEvent);
    }

    @Autowired private AgendaDataService agendaDataService;

    @Test
    public void testAgendaContainsTest() throws Exception {
        Agenda agenda = agendaDataService.getAgenda(new AgendaId(18, 2014));
        logger.info("{}", agenda.hasCommittee(new CommitteeId(Chamber.SENATE, "Finance")));
        logger.info("{}", agenda.getCommittees().size());
    }

    @Test
    public void testSplit() throws Exception {
        String s = "2014-1";
        logger.info("{} {}", s.split("-")[0], s.split("-")[1]);
    }

    @Test
    public void testBreak() throws Exception {
        outer: for (int i = 0; i < 10; i ++) {
            for (int j = 0; j < 5; j ++) {
                logger.info("{} {}", i, j);
                if (j == 3) break outer;
            }
        }
    }

    @Autowired MemberService memberService;

    @Test
    public void testCachePerformance() throws Exception {
        Member member = memberService.getMemberBySessionId(456);
        ExecutorService executorService = Executors.newFixedThreadPool(9);
        for (int i = 0; i < 10; i++) {
            final int j = i;
            executorService.submit(() -> logger.info("Hello {}", j));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {}
    }
}
