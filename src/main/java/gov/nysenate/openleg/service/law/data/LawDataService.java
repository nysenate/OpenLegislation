package gov.nysenate.openleg.service.law.data;

import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawFile;

public interface LawDataService
{
    public void saveLawDocument(LawFile lawFile, LawDocument lawDocument);
}
