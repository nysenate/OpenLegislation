package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;

/**
 * Created by uros on 2/14/17.
 */
public class XmlBillStatusProcessor extends AbstractDataProcessor implements SobiProcessor {


    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.BILLSTAT;
    }

    @Override
    public void process(SobiFragment fragment) {

    }

    @Override
    public void postProcess() {
        flushBillUpdates();
    }

    @Override
    public void init() {
        initBase();
    }
}
