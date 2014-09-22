package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagedLawProcessService implements LawProcessService
{
    private static final Logger logger = LoggerFactory.getLogger(ManagedLawProcessService.class);

    @Autowired
    private LawParser lawParser;

    @Override
    public int collateLaws() {
        return 0;
    }

    @Override
    public List<LawFragment> getPendingLawFragments() {
        return null;
    }

    @Override
    public void processLawFragments(List<LawFragment> fragments) {

    }

    @Override
    public void processPendingLawFragments() {

    }
}
