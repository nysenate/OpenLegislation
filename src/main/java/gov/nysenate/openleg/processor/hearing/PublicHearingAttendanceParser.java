package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.model.entity.Member;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@Service
public class PublicHearingAttendanceParser extends BasePublicHearingParser
{

    // TODO: take first 2 pages??
    public List<Member> parse(List<String> firstPage) {


        throw new NotImplementedException();
    }
}
