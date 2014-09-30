package gov.nysenate.openleg.service.law.data;

import gov.nysenate.openleg.dao.law.LawDataDao;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CachedLawDataService implements LawDataService
{
    @Autowired
    private LawDataDao lawDataDao;

    @Override
    public void saveLawDocument(LawFile lawFile, LawDocument lawDocument) {
        if (lawDocument == null) throw new IllegalArgumentException("Supplied lawDocument cannot be null");
        if (lawFile == null) throw new IllegalArgumentException("Supplied lawFile cannot be null");
        lawDataDao.updateLawDocument(lawFile, lawDocument);
    }
}
