package gov.nysenate.openleg.processor;


import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.sobi.SOBIFileDao;
import gov.nysenate.openleg.dao.sobi.SOBIFragmentDao;
import gov.nysenate.openleg.model.sobi.SOBIFile;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.model.sobi.SOBIFragmentType;
import gov.nysenate.openleg.processors.DataProcessor;
import gov.nysenate.openleg.processors.agenda.AgendaProcessor;
import gov.nysenate.openleg.processors.agenda.AgendaVoteProcessor;
import gov.nysenate.openleg.processors.bill.BillProcessor;
import gov.nysenate.openleg.processors.calendar.CalendarActiveListProcessor;
import gov.nysenate.openleg.processors.calendar.CalendarProcessor;
import gov.nysenate.openleg.processors.entity.CommitteeProcessor;
import gov.nysenate.openleg.processors.sobi.SOBIProcessor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;


public class SOBIProcessorTests extends BaseTests{


    private Map<SOBIFragmentType, SOBIProcessor> processorMap;

    @Autowired
    protected DataProcessor dataProcessor;
    @Autowired
    protected BillProcessor billProcessor;
    @Autowired
    protected CommitteeProcessor committeeProcessor;

    @Autowired
    protected SOBIFileDao sobiFileDao;
    @Autowired
    protected SOBIFragmentDao sobiFragmentDao;

    @PostConstruct
    public void init() {
        this.processorMap = new HashMap<SOBIFragmentType, SOBIProcessor>();
        this.processorMap.put(SOBIFragmentType.BILL, billProcessor);
        this.processorMap.put(SOBIFragmentType.AGENDA, new AgendaProcessor());
        this.processorMap.put(SOBIFragmentType.AGENDA_VOTE, new AgendaVoteProcessor());
        this.processorMap.put(SOBIFragmentType.CALENDAR, new CalendarProcessor());
        this.processorMap.put(SOBIFragmentType.CALENDAR_ACTIVE, new CalendarActiveListProcessor());
        this.processorMap.put(SOBIFragmentType.COMMITTEE, committeeProcessor);
    }

    public void ProcessorSubsetTest(Set<SOBIFragmentType> types) throws Exception{
        dataProcessor.stage(null, null);
        dataProcessor.collate();

        for (SOBIFile sobiFile : sobiFileDao.getPendingSOBIFiles(SortOrder.ASC)) {
            List<SOBIFragment> fragments = sobiFragmentDao.getSOBIFragments(sobiFile, SortOrder.ASC);
            for (SOBIFragment fragment : fragments) {
                if(types.contains(fragment.getType()) && processorMap.containsKey(fragment.getType())){
                    processorMap.get(fragment.getType()).process(fragment);
                }
            }
            sobiFile.incrementProcessedCount();
            sobiFile.setProcessedDateTime(new Date());
            sobiFile.setPendingProcessing(false);
            sobiFileDao.updateSOBIFile(sobiFile);
        }
    }

    @Test
    public void CommitteeProcessorTest() throws Exception {
        Set<SOBIFragmentType> types = new HashSet<SOBIFragmentType>();
        types.add(SOBIFragmentType.COMMITTEE);
        ProcessorSubsetTest(types);
    }

    @Test
    public void BillProcessorTest() throws Exception {
        Set<SOBIFragmentType> types = new HashSet<SOBIFragmentType>();
        types.add(SOBIFragmentType.BILL);
        ProcessorSubsetTest(types);
    }
}
