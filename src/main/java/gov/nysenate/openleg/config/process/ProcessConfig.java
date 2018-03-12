package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.dao.sourcefiles.sobi.SobiFragmentDao;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component
public class ProcessConfig extends AbstractDataProcessor {

    @Autowired
    private SobiFragmentDao sobiFragmentDao;

    //Map contains all property configurations for all years initialized on init
    private HashMap<LocalDate,ProcessYear> processYearMap = new HashMap<>();

    //defualt constructor
    public ProcessConfig() {}

    //Populates the
    @PostConstruct
    public void init() {
        //Create NonDefault ProcessYears
        ProcessYear mixed2017 = new ProcessYear();
        mixed2017.setOverarchingDataConfigs(true,true);
        mixed2017.setOverarchingSharedConfigs(true,true,true,true,true);
        mixed2017.setAllXmlConfigsTrue();
        mixed2017.setSenAgen(false);
        mixed2017.setSenAgenV(false);
        mixed2017.setSenCalal(false);
        mixed2017.setSenCal(false);
        mixed2017.setGeneralSobiConfigsTrue();
        mixed2017.setSpecificSobiConfigsFalse();

        //Put ProcessYear's in Hash map
        //Xml only years
        processYearMap.put(LocalDate.of(1995,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(1996,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(1997,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(1998,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(1999,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2000,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2001,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2002,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2003,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2004,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2005,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2006,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2007,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2008,1,1), createDefaultXmlProcessYear());

        //Sobi Mostly Years
        processYearMap.put(LocalDate.of(2009,1,1), createDefualtSobiProcessYear());
        processYearMap.put(LocalDate.of(2010,1,1), createDefualtSobiProcessYear());
        processYearMap.put(LocalDate.of(2011,1,1), createDefualtSobiProcessYear());
        processYearMap.put(LocalDate.of(2012,1,1), createDefualtSobiProcessYear());
        processYearMap.put(LocalDate.of(2013,1,1), createDefualtSobiProcessYear());
        processYearMap.put(LocalDate.of(2014,1,1), createDefualtSobiProcessYear());
        processYearMap.put(LocalDate.of(2015,1,1), createDefualtSobiProcessYear());
        processYearMap.put(LocalDate.of(2016,1,1), createDefualtSobiProcessYear());

        //2017
        processYearMap.put(LocalDate.of(2017,1,1), mixed2017);

        //Xml Only
        processYearMap.put(LocalDate.of(2018,1,1), createDefaultXmlProcessYear());

        //Set the year field in each ProcessYear
        for (LocalDate year : processYearMap.keySet()) {
            processYearMap.get(year).setYear(year);
        }
    }

    //This method removes sobi fragments that are not allowed to process from the processYearMap
    public List<SobiFragment> filterFileFragements(List<SobiFragment> fragments) {
        for (Iterator<SobiFragment> iterator = fragments.iterator(); iterator.hasNext();) {

            SobiFragment fragment = iterator.next();
            ProcessYear fragProcYear = getProcessYearFromMap(fragment.getPublishedDateTime());
            String fragmentName = fragment.getParentSobiFile().getFileName();

            if (fragmentName.contains("SOBI")) {
                if (removeSobiFragment(fragment,fragProcYear)) {
                    iterator.remove();
                    sendFragmentToDaoForUpdate(fragment);
                }
            }
            else if (fragmentName.contains("XML")) {
                if (removeXmlFragement(fragment,fragProcYear)) {
                    iterator.remove();
                    sendFragmentToDaoForUpdate(fragment);
                }
            }
        }
        return fragments;
    }

    //Removes Sobi bill blocks that are not allowed to process from the processYearMap
    public List<SobiBlock> filterSobiBlocks(SobiFragment fragment, List<SobiBlock> blocks) {
        for (Iterator<SobiBlock> iterator = blocks.iterator(); iterator.hasNext();) {
            SobiBlock block = iterator.next();
            if (removeSobiBlock(block,getProcessYearFromMap(fragment.getPublishedDateTime()))) {
                iterator.remove();
            }
        }
        return blocks;
    }


    //Determines Whether a xml fragment should continue processing or not
    public boolean removeXmlFragement(SobiFragment fragment, ProcessYear fragProcYear) {
        boolean removeFragment = false;

        if (!fragProcYear.isAllXml()) {
            removeFragment = true;
        }
        else {
            switch ( fragment.getType() ) {
                case ANACT:
                    if (!fragProcYear.isAnAct()) removeFragment = true;
                    break;
                case APPRMEMO:
                    if (!fragProcYear.isApprMemo()) removeFragment = true;
                    break;
                case BILLSTAT:
                    if (!fragProcYear.isBillStat()) removeFragment = true;
                    break;
                case BILLTEXT:
                    if (!fragProcYear.isBillText()) removeFragment = true;
                    break;
                case LDSUMM:
                    if (!fragProcYear.isLdSumm()) removeFragment = true;
                    break;
                case LDSPON:
                    if (!fragProcYear.isLdSpon()) removeFragment = true;
                    break;
                case LDBLURB:
                    if (!fragProcYear.isLdBlurb()) removeFragment = true;
                    break;
                case SAMEAS:
                    if (!fragProcYear.isSameas()) removeFragment = true;
                    break;
                case COMMITTEE:
                    if (!fragProcYear.isSenComm() || !fragProcYear.isAllCommittees()) removeFragment = true;
                    break;
                case SENMEMO:
                    if (!fragProcYear.isSenMemo()) removeFragment = true;
                    break;
                case SENFLVOTE:
                    if (!fragProcYear.isSenFlVot()) removeFragment = true;
                    break;
                case CALENDAR:
                    if (!fragProcYear.isSenCal() || !fragProcYear.isAllCalendar()) removeFragment = true;
                    break;
                case CALENDAR_ACTIVE:
                    if (!fragProcYear.isSenCalal() || !fragProcYear.isAllActiveLists()) removeFragment = true;
                    break;
                case AGENDA:
                    if (!fragProcYear.isSenAgen() || !fragProcYear.isAllAgendas()) removeFragment = true;
                    break;
                case SENAGENV:
                    if (!fragProcYear.isSenAgenV() || !fragProcYear.isAllAgendaVotes()) removeFragment = true;
                    break;
                case VETOMSG:
                    if (!fragProcYear.isVetoMsg()) removeFragment = true;
                    break;
                default: removeFragment = false; break;
            }
        }
        return removeFragment;
    }

    //Determines Whether a sobi fragment should continue processing or not
    public boolean removeSobiFragment(SobiFragment fragment, ProcessYear fragProcYear) {
        boolean removeFragment = false;

        if (!fragProcYear.isAllSobi()) {
            removeFragment = true;
        }
        else {
            switch ( fragment.getType() ) {
                case BILL:
                    if (!fragProcYear.isBill()) removeFragment = true;
                    break;
                case AGENDA:
                    if (!fragProcYear.isAgenda() || !fragProcYear.isAllAgendas()) removeFragment = true;
                    break;
                case AGENDA_VOTE:
                    if (!fragProcYear.isAgendaVote() || !fragProcYear.isAllAgendaVotes()) removeFragment = true;
                    break;
                case CALENDAR:
                    if (!fragProcYear.isCalendar() || !fragProcYear.isAllCalendar()) removeFragment = true;
                    break;
                case CALENDAR_ACTIVE:
                    if (!fragProcYear.isCalendarActive() || !fragProcYear.isAllActiveLists()) removeFragment = true;
                    break;
                case COMMITTEE:
                    if (!fragProcYear.isCommittee() || !fragProcYear.isAllCommittees()) removeFragment = true;
                    break;
                case ANNOTATION:
                    if (!fragProcYear.isAnnotation()) removeFragment = true;
                    break;
                default: removeFragment = false;
            }
        }
        return removeFragment;
    }

    //Determines whether a sobi block should continue processing or not
    private boolean removeSobiBlock(SobiBlock block, ProcessYear fragProcYear) {
      boolean removeBlock = false;
      switch (block.getType()) {
          case BILL_INFO:
              if (!fragProcYear.isBillInfo()) removeBlock = true;
              break;
          case LAW_SECTION:
              if (!fragProcYear.isLawSection()) removeBlock = true;
              break;
          case TITLE:
              if (!fragProcYear.isTitle()) removeBlock = true;
              break;
          case BILL_EVENT:
              if (!fragProcYear.isBillEvent()) removeBlock = true;
              break;
          case SAME_AS:
              if (!fragProcYear.isSameasSobi()) removeBlock = true;
              break;
          case SPONSOR:
              if (!fragProcYear.isSponsor()) removeBlock = true;
              break;
          case CO_SPONSOR:
              if (!fragProcYear.isCosponsor()) removeBlock = true;
              break;
          case MULTI_SPONSOR:
              if (!fragProcYear.isMultisponsor()) removeBlock = true;
              break;
          case PROGRAM_INFO:
              if (!fragProcYear.isProgramInfo()) removeBlock = true;
              break;
          case ACT_CLAUSE:
              if (!fragProcYear.isActClause()) removeBlock = true;
              break;
          case LAW:
              if (!fragProcYear.isLaw()) removeBlock = true;
              break;
          case SUMMARY:
              if (!fragProcYear.isSummary()) removeBlock = true;
              break;
          case SPONSOR_MEMO:
              if (!fragProcYear.isSponsorMemo()) removeBlock = true;
              break;
          case RESOLUTION_TEXT:
              if (!fragProcYear.isResolutionText()) removeBlock = true;
              break;
          case TEXT:
              if (!fragProcYear.isText()) removeBlock = true;
              break;
          case VETO_APPROVE_MEMO:
              if (!fragProcYear.isVetoApprMemo()) removeBlock = true;
              break;
          case VOTE_MEMO:
              if (!fragProcYear.isVoteMemo()) removeBlock = true;
              break;
          default: removeBlock = false;
      }
      return removeBlock;
    }

    //Creates the default Xml only process years
    private ProcessYear createDefaultXmlProcessYear() {
        ProcessYear xmlYear = new ProcessYear();
        xmlYear.setOverarchingDataConfigs(true,false);
        xmlYear.setOverarchingSharedConfigs(true,true,true,true,true);
        xmlYear.setAllXmlConfigsTrue();
        xmlYear.setGeneralSobiConfigsFalse();
        xmlYear.setSpecificSobiConfigsFalse();
        return xmlYear;
    }

    //Creates the default sobi only process years
    private ProcessYear createDefualtSobiProcessYear() {
        ProcessYear sobiYear = new ProcessYear();
        sobiYear.setOverarchingDataConfigs(false,true);
        sobiYear.setOverarchingSharedConfigs(true,true,true,true,true);
        sobiYear.setGeneralSobiConfigsTrue();
        sobiYear.setSpecificSobiConfigsTrue();
        sobiYear.setAllXmlConfigsFalse();
        return sobiYear;
    }

    //Retrieves a single ProcessYear from the processYearMap
    public ProcessYear getProcessYearFromMap(LocalDateTime publishDate) {
        ProcessYear requestedProcessYear = processYearMap.get(LocalDate.of(publishDate.getYear(),1,1));
        if (requestedProcessYear == null) {
            requestedProcessYear = createDefaultXmlProcessYear();
            requestedProcessYear.setYear(LocalDate.of(publishDate.getYear(),1,1));
        }
        return requestedProcessYear;
    }

    public ProcessYear getProcessYearFromMap(LocalDate publishDate) {
        ProcessYear requestedProcessYear = processYearMap.get(LocalDate.of(publishDate.getYear(),1,1));
        if (requestedProcessYear == null) {
            requestedProcessYear = createDefaultXmlProcessYear();
            requestedProcessYear.setYear(LocalDate.of(publishDate.getYear(),1,1));
        }
        return requestedProcessYear;
    }

    //Retrieves the entire processYearMap
    public HashMap<LocalDate, ProcessYear> getProcessYearMap() {
        return processYearMap;
    }

    //Calls the sobiFragmentDao to set the fragment to not pend process
    private void sendFragmentToDaoForUpdate(SobiFragment fragment) {
        ArrayList<SobiFragment> fragmentToSetFalse = new ArrayList<>();
        fragmentToSetFalse.add(fragment);
        sobiFragmentDao.setPendProcessingFalse(fragmentToSetFalse);
    }
}
