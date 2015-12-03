package gov.nysenate.openleg.dao.base;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class SqlBaseDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBaseDaoTests.class);

    @Autowired
    private MemberService memberService;

    @Test
    public void testOrdinalMapTests() throws Exception {
        List<SessionMember> members1 = Lists.newArrayList();
        List<SessionMember> members2 = Lists.newArrayList();

        members1.add(memberService.getMemberByShortName("BALL", SessionYear.of(2013), Chamber.SENATE));
        members1.add(memberService.getMemberByShortName("SAVINO", SessionYear.of(2013), Chamber.SENATE));
        members1.add(memberService.getMemberByShortName("MARTINS", SessionYear.of(2013), Chamber.SENATE));

        Map<SessionMember, Integer> map1 = Maps.newHashMap();
        for (int i = 0; i < members1.size(); i++) {
            map1.put(members1.get(i), i);
        }


        members2.add(memberService.getMemberByShortName("BALL", SessionYear.of(2013), Chamber.SENATE));
        members2.add(memberService.getMemberByShortName("MARTINS", SessionYear.of(2013), Chamber.SENATE));
        members2.add(memberService.getMemberByShortName("ZELDIN", SessionYear.of(2013), Chamber.SENATE));

        Map<SessionMember, Integer> map2 = Maps.newHashMap();
        for (int i = 0; i < members2.size(); i++) {
            map2.put(members2.get(i), i);
        }

        MapDifference<SessionMember, Integer> diff = Maps.difference(map1, map2);
        logger.info("{}", diff);
//        Map<Integer, String> map1 =
//                SqlBaseDao.getOridinalMapFromList(Lists.newArrayList("moose", "cow", "sheep"), 1);
//        Map<Integer, String> map2 =
//                SqlBaseDao.getOridinalMapFromList(Lists.newArrayList("loser", "moose", "cow", "sheep"), 1);
//        MapDifference<Integer, String> mapDiff = Maps.difference(map1, map2);
//        logger.info("{}", mapDiff.entriesOnlyOnRight());



    }
}
