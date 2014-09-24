package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagedLawProcessService implements LawProcessService
{
    private static final Logger logger = LoggerFactory.getLogger(ManagedLawProcessService.class);

    /** {@inheritDoc} */
    @Override
    public int collateLawFiles() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public List<LawFile> getPendingLawFiles() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void processLawFiles(List<LawFile> lawFiles) {

    }

    /** {@inheritDoc} */
    @Override
    public void processPendingLawFiles() {

    }
}
