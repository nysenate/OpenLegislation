package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;

/**
 * Created by robert on 2/15/17.
 */
public class AnActSobiProcessor extends AbstractDataProcessor implements SobiProcessor{

    public AnActSobiProcessor(){

    }

    @Override
    public void init() {

    }

    @Override
    public SobiFragmentType getSupportedType() {
        return SobiFragmentType.ANACT;
    }

    @Override
    public void process(SobiFragment fragment) {

    }

    @Override
    public void postProcess() {

    }
}
