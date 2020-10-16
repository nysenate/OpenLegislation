package gov.nysenate.openleg;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;

import static gov.nysenate.openleg.model.entity.CommitteeMemberTitle.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TestData {

    public final static Map<Integer, Person> PERSON_DATA = new ImmutableMap.Builder<Integer, Person>()
            .put(188, new Person(188, "John L. Sampson", "John", "L.", "Sampson",
                    "sampson@senate.state.ny.us", "Senator", null, "369_john_l._sampson.jpg"))
            .put(263, new Person(263, "Thomas P. Morahan", "Thomas", "P.", "Morahan",
                    "district38@nysenate.gov", "Senator", null, "no_image.jpg"))
            .put(190, new Person(190, "James L. Seward", "James", "L.", "Seward",
                    "seward@senate.state.ny.us", "Senator", null, "371_james_l._seward.jpg"))
            .put(191, new Person(191, "Neil D. Breslin", "Neil", "D.", "Breslin",
                    "breslin@senate.state.ny.us", "Senator", null, "372_neil_d._breslin.jpg"))
            .put(942, new Person(942, "Billy Jones", "Billy", "", "Jones",
                    "", "Assembly Member", "", "no_image.jpg"))
            .put(944, new Person(944,	"Robert C. Carroll", "Robert", "C.", "Carroll",
                    "CarrollR@nyassembly.gov", "Assembly Member", "", "no_image.jpg"))
            .put(950, new Person(950, "Inez E. Dickens", "Inez", "E.", "Dickens",
                    "", "Assembly Member", "", "no_image.jpg"))
            .put(204, new Person(204,	"Adriano Espaillat",	"Adriano",	null,	"Espaillat",
                    "espailla@nysenate.gov",	"Senator", null, "385_adriano_espaillat.jpg"))
            .put(499, new Person(499, "Edward Hennessey", "Edward", null, "Hennessey",
                    null, null, null, "no_image.jpg"))
            .build();

    // TODO: test members should never be incumbents, for future-proofing, because no one is an incumbent forever.
    public final static Map<Integer, Member> MEMBER_DATA = new ImmutableMap.Builder<Integer, Member>()
            .put(369, new Member(PERSON_DATA.get(188), 369, Chamber.SENATE, false))
            .put(441, new Member(PERSON_DATA.get(263), 441, Chamber.SENATE, false))
            .put(371, new Member(PERSON_DATA.get(190), 371, Chamber.SENATE, true))
            .put(372, new Member(PERSON_DATA.get(191), 372, Chamber.SENATE, true))
            .put(1120, new Member(PERSON_DATA.get(942), 1120, Chamber.ASSEMBLY, false))
            .put(1122, new Member(PERSON_DATA.get(944), 1122, Chamber.ASSEMBLY, false))
            .put(1128, new Member(PERSON_DATA.get(950), 1128, Chamber.ASSEMBLY, false))
            .put(602,  new Member(PERSON_DATA.get(204), 602,  Chamber.ASSEMBLY, false))
            .put(385,  new Member(PERSON_DATA.get(204), 385,  Chamber.SENATE, false))
            .put(667,  new Member(PERSON_DATA.get(499), 667,  Chamber.ASSEMBLY, false))
            .build();

    public final static ImmutableMap<Integer, SessionMember> SESSION_MEMBER_DATA = new ImmutableMap.Builder<Integer, SessionMember>()
            .put(1, new SessionMember(1, MEMBER_DATA.get(369), "SAMPSON",
                    SessionYear.of(2009), 19, false))
            .put(2, new SessionMember(2, MEMBER_DATA.get(441), "MORAHAN",
                    SessionYear.of(2009), 38, false))
            .put(3, new SessionMember(3, MEMBER_DATA.get(371), "SEWARD",
                    SessionYear.of(2009), 51, false))
            .put(4, new SessionMember(4, MEMBER_DATA.get(372), "BRESLIN",
                    SessionYear.of(2009), 46, false))
            .put(1117, new SessionMember(1117, MEMBER_DATA.get(1120), "JONES",
                    SessionYear.of(2017), 115,false))
            .put(1119, new SessionMember(1119, MEMBER_DATA.get(1122), "CARROLL",
                    SessionYear.of(2017), 44, false))
            .put(1125, new SessionMember(1125, MEMBER_DATA.get(1128), "DICKENS",
                    SessionYear.of(2017), 70, false))
            .put(79, new SessionMember(79, MEMBER_DATA.get(385), "ESPAILLAT",
                    SessionYear.of(2011), 31, false))
            .put(140, new SessionMember(140, MEMBER_DATA.get(385), "ESPAILLAT",
                    SessionYear.of(2013), 31, false))
            .put(718, new SessionMember(718, MEMBER_DATA.get(385), "ESPAILLAT",
                    SessionYear.of(2015), 31, false))
            .put(312, new SessionMember(312, MEMBER_DATA.get(602), "ESPAILLAT",
                    SessionYear.of(2009), 72, false))
            .put(666, new SessionMember(666, MEMBER_DATA.get(677), "HENNESSEY",
                    SessionYear.of(2013), 3, false))
            .put(667, new SessionMember(666, MEMBER_DATA.get(677), "HENNESSY",
                    SessionYear.of(2013), 3, true))
            .build();

    public final static ImmutableSet<CommitteeMember> COMMITTEE_MEMBER_DATA = createCommitteeMemberData();

    private static ImmutableSet<CommitteeMember> createCommitteeMemberData() {
        Map<Integer, List<Integer>> temp = new HashMap<>();
        // Maps session member ID's to sequence numbers.
        temp.put(79, Arrays.asList(6, 9, 10, 12, 13, 14, 19, 24));
        temp.put(140, Arrays.asList(6, 8, 11, 13, 15, 19, 23, 34));

        ImmutableSet.Builder<CommitteeMember> builder = ImmutableSet.builder();
        for (Integer k : temp.keySet()) {
            for (Integer seqNo : temp.get(k))
                builder.add(new CommitteeMember(seqNo, SESSION_MEMBER_DATA.get(k), MEMBER, false));
        }
        return builder.build();
    }
}
