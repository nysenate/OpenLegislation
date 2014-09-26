package gov.nysenate.openleg.dao.transcript;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class SqlTranscriptDaoTest extends BaseTests
{

    @Autowired
    private TranscriptDao dao;

    @Test
    public void selectingIdsByYearWorks() {
        int year = 2012;
        List<TranscriptId> ids = dao.getTranscriptIds(year);
        System.out.println(ids.size());
    }

}