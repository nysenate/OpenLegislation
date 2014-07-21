package gov.nysenate.openleg.processor;


import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sobi.SobiFileDao;
import gov.nysenate.openleg.dao.sobi.SobiFragmentDao;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.processors.DataProcessor;
import gov.nysenate.openleg.processors.sobi.SobiProcessor;
import gov.nysenate.openleg.processors.sobi.agenda.AgendaProcessor;
import gov.nysenate.openleg.processors.sobi.agenda.AgendaVoteProcessor;
import gov.nysenate.openleg.processors.sobi.bill.SobiBillProcessor;
import gov.nysenate.openleg.processors.sobi.calendar.SobiActiveListProcessor;
import gov.nysenate.openleg.processors.sobi.calendar.SobiCalendarProcessor;
import gov.nysenate.openleg.processors.sobi.entity.CommitteeProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;


public class SOBIProcessorTests extends BaseTests{


    private Map<SobiFragmentType, SobiProcessor> processorMap;

    @Autowired
    protected DataProcessor dataProcessor;
    @Autowired
    protected SobiBillProcessor sobiBillProcessor;
    @Autowired
    protected CommitteeProcessor committeeProcessor;

    @Autowired
    protected SobiFileDao sobiFileDao;
    @Autowired
    protected SobiFragmentDao sobiFragmentDao;

    @PostConstruct
    public void init() {
        this.processorMap = new HashMap<SobiFragmentType, SobiProcessor>();
        this.processorMap.put(SobiFragmentType.BILL, sobiBillProcessor);
        this.processorMap.put(SobiFragmentType.AGENDA, new AgendaProcessor());
        this.processorMap.put(SobiFragmentType.AGENDA_VOTE, new AgendaVoteProcessor());
        this.processorMap.put(SobiFragmentType.CALENDAR, new SobiCalendarProcessor());
        this.processorMap.put(SobiFragmentType.CALENDAR_ACTIVE, new SobiActiveListProcessor());
        this.processorMap.put(SobiFragmentType.COMMITTEE, committeeProcessor);
    }

    public void ProcessorSubsetTest(Set<SobiFragmentType> types) throws Exception{
        dataProcessor.stage(null, null);
        dataProcessor.collate();

        for (SobiFile sobiFile : sobiFileDao.getPendingSobiFiles(SortOrder.ASC, 0, 0)) {
            List<SobiFragment> fragments = sobiFragmentDao.getSOBIFragments(sobiFile, SortOrder.ASC);
            for (SobiFragment fragment : fragments) {
                if(types.contains(fragment.getType()) && processorMap.containsKey(fragment.getType())){
                    processorMap.get(fragment.getType()).process(fragment);
                }
            }
            sobiFile.incrementProcessedCount();
            sobiFile.setProcessedDateTime(new Date());
            sobiFile.setPendingProcessing(false);
            sobiFileDao.updateSobiFile(sobiFile);
        }
    }

    @Test
    public void CommitteeProcessorTest() throws Exception {
        Set<SobiFragmentType> types = new HashSet<SobiFragmentType>();
        types.add(SobiFragmentType.COMMITTEE);
        ProcessorSubsetTest(types);
    }

    @Test
    public void BillProcessorTest() throws Exception {
        Set<SobiFragmentType> types = new HashSet<SobiFragmentType>();
        types.add(SobiFragmentType.BILL);
        ProcessorSubsetTest(types);
    }
}
