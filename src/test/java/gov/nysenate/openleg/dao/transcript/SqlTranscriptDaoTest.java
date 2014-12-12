package gov.nysenate.openleg.dao.transcript;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SqlTranscriptDaoTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlTranscriptDaoTest.class);

    @Autowired
    private TranscriptDao dao;

    @Test
    public void selectingIdsByYearWorks() {
        int year = 2012;
      List<TranscriptId> ids = dao.getTranscriptIds(SortOrder.DESC, LimitOffset.ALL);
      logger.info("{}", OutputUtils.toJson(ids));
    }

}