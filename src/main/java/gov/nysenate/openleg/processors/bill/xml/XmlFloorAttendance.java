package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.processors.AbstractLegDataProcessor;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.LegDataFragmentType;
import org.springframework.stereotype.Service;

@Service
public class XmlFloorAttendance extends AbstractLegDataProcessor {
    @Override
    public LegDataFragmentType getSupportedType() {
        return LegDataFragmentType.SENFLATD;
    }

    @Override
    public void process(LegDataFragment fragment) {
        // TODO: implement
    }
}
