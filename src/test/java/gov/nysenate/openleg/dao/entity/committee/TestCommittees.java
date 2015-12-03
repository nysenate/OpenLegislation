package gov.nysenate.openleg.dao.entity.committee;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.*;

//@Component
public class TestCommittees {
    private static Logger logger = LoggerFactory.getLogger(TestCommittees.class);

//    @Autowired
    private MemberService memberService;

    private Map<String, Committee> createdCommittees = new HashMap<String,Committee>();

    private static final Object[][] testMembers = {
            {"SAMPSON", 369, 2009},
            {"MORAHAN", 441, 2009},
            {"SEWARD", 371, 2009},
            {"BRESLIN", 372, 2009}
    };

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private CommitteeMember committeeMemberFromTriple(Object[] triple){
        try {
            CommitteeMember cm = new CommitteeMember();
            SessionMember m = memberService.getMemberById((int)triple[1], new SessionYear((int)triple[2]));
            cm.setMember(m);
            cm.setMajority(m.getMemberId()%2==0);
            cm.setTitle(CommitteeMemberTitle.MEMBER);
            return cm;
        } catch (MemberNotFoundEx memberNotFoundEx) {
            logger.error(memberNotFoundEx.getMessage());
            return null;
        }
    }

    @PostConstruct
    public void init(){
        Committee test1 = new Committee();
        test1.setName("test committee 1");
        test1.setChamber(Chamber.SENATE);
//        test1.setMeetTime(new Time(LocalTime.parse("09:00").toDateTimeToday().toDate().getTime()));
        test1.setLocation("my house");
        test1.setMeetDay(DayOfWeek.FRIDAY);
        test1.setMeetAltWeek(false);
        test1.setMeetAltWeekText("dont do it");
        test1.setSession(SessionYear.of(2009));
//        test1.setPublishDate(dateFormat.parseDateTime("2009-01-01").toDate());
        test1.setMembers(new ArrayList<CommitteeMember>());
        for(int n=0; n<2; n++){
            CommitteeMember cm = committeeMemberFromTriple(testMembers[n]);
            cm.setSequenceNo(n+1);
            test1.getMembers().add(cm);
        }
        createdCommittees.put("test1", test1);

        Committee test1nomod = new Committee(test1);
//        test1nomod.setPublishDate(dateFormat.parseDateTime("2009-02-01").toDate());
        createdCommittees.put("test1nomod", test1nomod);

        Committee test1v2 = new Committee(test1);
        CommitteeMember seward = committeeMemberFromTriple(testMembers[2]);
        seward.setSequenceNo(3);
        test1v2.getMembers().add(seward);
//        test1v2.setPublishDate(dateFormat.parseDateTime("2009-03-01").toDate());
        createdCommittees.put("test1v2", test1v2);

        Committee test1MeetChange = new Committee(test1);
        test1MeetChange.setLocation("broom closet");
//        test1MeetChange.setPublishDate(dateFormat.parseDateTime("2009-04-01").toDate());
        createdCommittees.put("test1MeetChange", test1MeetChange);

        Committee test1replace = new Committee(test1);
        CommitteeMember breslin = committeeMemberFromTriple(testMembers[3]);
        test1replace.getMembers().add(breslin);
        breslin.setSequenceNo(4);
        createdCommittees.put("test1replace", test1replace);

        Committee test1v2merge = new Committee(test1v2);
//        test1v2merge.setPublishDate(dateFormat.parseDateTime("2009-02-10").toDate());
        createdCommittees.put("test1v2merge", test1v2merge);

        Committee test1v2mergeReplace = new Committee(test1);
        test1v2mergeReplace.setPublishedDateTime(test1v2merge.getPublishedDateTime());
        createdCommittees.put("test1v2mergeReplace", test1v2mergeReplace);

        Committee test1v3 = new Committee(test1);
//        test1v3.setPublishDate((dateFormat.parseDateTime("2009-05-01").toDate()));
        createdCommittees.put("test1v3", test1v3);

        Committee test2 = new Committee(test1);
        test2.setName("test committee 2");
        createdCommittees.put("test2", test2);
    }

    public Committee getCommittee(String name){
        if(!createdCommittees.containsKey(name)){
            throw new NoSuchElementException("No test committee named " + name);
        }
        return createdCommittees.get(name);
    }
    public List<Committee> getCommittees(){
        return new ArrayList<Committee>(createdCommittees.values());
    }
    public void putCommittee(String name, Committee committee){
        createdCommittees.put(name, committee);
    }
}
