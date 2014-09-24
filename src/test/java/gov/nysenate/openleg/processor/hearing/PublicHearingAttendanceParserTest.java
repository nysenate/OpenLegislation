package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.entity.Member;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PublicHearingAttendanceParserTest extends BaseTests
{

    @Ignore
    @Test
    public void a() {
        List<String> a = new ArrayList<>();
        a.add("9                       January 24, 2012\n");
        a.add("                        12:00 p.m. to 3:00 p.m.\n");
        a.add("10\n");
        a.add("11\n");
        a.add("12      PRESIDING:\n");
        a.add("13         Senator Kenneth P. LaValle\n");
        a.add("Chair\n");
        a.add("14\n");
        a.add("15      SENATE MEMBERS PRESENT:\n");
        a.add("16         Senator Jack Martins, Forum Moderator)\n");
        a.add("17         Senator Lee M. Zeldin (RM)\n");
        a.add("18         Senator David Carlucci\n");
        a.add("19         Senator Joseph A. Griffo\n");
        a.add("20         Senator George D. Maziarz\n");
        a.add("21         Senator Gustavo Rivera\n");
        a.add("22         Assemblyman Mark C. Johns\n");
        a.add("23\n");
        a.add("24\n");
        a.add("25\n");
        a.add("2\n");
        a.add("1\n");
        a.add("    SPEAKERS:                               PAGE QUESTIONS\n");
        a.add("2\n");
        a.add("\n");


        PublicHearingAttendanceParser parser = new PublicHearingAttendanceParser();
        List<Member> actual = parser.parse(a);
    }

}