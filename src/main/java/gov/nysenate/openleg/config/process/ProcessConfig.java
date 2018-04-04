package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.SourceType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.sourcefiles.SourceType.SOBI;
import static gov.nysenate.openleg.model.sourcefiles.SourceType.XML;

@Component
public class ProcessConfig extends AbstractDataProcessor {

    //Map contains all property configurations for all years initialized on init
    private HashMap<LocalDate,ProcessYear> processYearMap = new HashMap<>();

    //default constructor
    public ProcessConfig() {}

    //Populates the
    @PostConstruct
    public void init() {
        //Create NonDefault ProcessYears
        ProcessYear mixed2017 = createDefaultSobiProcessYear();
        mixed2017.setAllXml(true);
        mixed2017.setBillText(true);
        mixed2017.setLdBlurb(true);
        mixed2017.setSenMemo(true);
        mixed2017.setText(false);
        mixed2017.setResolutionText(false);

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
        processYearMap.put(LocalDate.of(2009,1,1), createDefaultSobiProcessYear());
        processYearMap.put(LocalDate.of(2010,1,1), createDefaultSobiProcessYear());
        processYearMap.put(LocalDate.of(2011,1,1), createDefaultSobiProcessYear());
        processYearMap.put(LocalDate.of(2012,1,1), createDefaultSobiProcessYear());
        processYearMap.put(LocalDate.of(2013,1,1), createDefaultSobiProcessYear());
        processYearMap.put(LocalDate.of(2014,1,1), createDefaultSobiProcessYear());
        processYearMap.put(LocalDate.of(2015,1,1), createDefaultSobiProcessYear());
        processYearMap.put(LocalDate.of(2016,1,1), createDefaultSobiProcessYear());

        //2017
        processYearMap.put(LocalDate.of(2017,1,1), mixed2017);

        //Xml Only
        processYearMap.put(LocalDate.of(2018,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2019,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2020,1,1), createDefaultXmlProcessYear());
        processYearMap.put(LocalDate.of(2021,1,1), createDefaultXmlProcessYear());

        //Set the year field in each ProcessYear
        for (LocalDate year : processYearMap.keySet()) {
            processYearMap.get(year).setYear(year);
        }
    }

    /**
     * This method filters the given list of fragments,
     * returning a list that is filtered according to the process config.
     *
     * @param fragments {@link SobiFragment}
     * @return {@link List<SobiFragment>}
     */
    public List<SobiFragment> filterFileFragments(List<SobiFragment> fragments) {
        return fragments.stream()
                .filter(frag -> {
                    ProcessYear fragProcYear = getProcessYearFromMap(frag.getPublishedDateTime());
                    SourceType sourceType = frag.getParentSobiFile().getSourceType();
                    if (sourceType == XML) {
                        return !removeXmlFragment(frag, fragProcYear);
                    }
                    if (sourceType == SOBI) {
                        return !removeSobiFragment(frag, fragProcYear);
                    }
                    throw new IllegalStateException("Unknown source type: " + sourceType);
                })
                .collect(Collectors.toList());
    }

    /**
     * This method filters the given list of sobi blocks,
     * returning a list that is filtered according to the process config.
     *
     * @param fragment {@link SobiFragment}
     * @param blocks {@link List<SobiBlock>}
     * @return {@link List<SobiBlock>}
     */
    public List<SobiBlock> filterSobiBlocks(SobiFragment fragment, List<SobiBlock> blocks) {
        ProcessYear processYear = getProcessYearFromMap(fragment.getPublishedDateTime());
        return blocks.stream()
                .filter(block -> !removeSobiBlock(block, processYear))
                .collect(Collectors.toList());
    }

    //Determines Whether a xml fragment should continue processing or not
    private boolean removeXmlFragment(SobiFragment fragment, ProcessYear fragProcYear) {
        boolean removeFragment = false;

        if (!fragProcYear.isAllXml()) {
            removeFragment = true;
        }
        else {
            switch ( fragment.getType() ) {
                case ANACT:
                    if (!fragProcYear.isAnAct()) {
                        removeFragment = true;
                    }
                    break;
                case APPRMEMO:
                    if (!fragProcYear.isApprMemo()) {
                        removeFragment = true;
                    }
                    break;
                case BILLSTAT:
                    if (!fragProcYear.isBillStat()) {
                        removeFragment = true;
                    }
                    break;
                case BILLTEXT:
                    if (!fragProcYear.isBillText()) {
                        removeFragment = true;
                    }
                    break;
                case LDSUMM:
                    if (!fragProcYear.isLdSumm()) {
                        removeFragment = true;
                    }
                    break;
                case LDSPON:
                    if (!fragProcYear.isLdSpon()) {
                        removeFragment = true;
                    }
                    break;
                case LDBLURB:
                    if (!fragProcYear.isLdBlurb()) {
                        removeFragment = true;
                    }
                    break;
                case SAMEAS:
                    if (!fragProcYear.isSameas()) {
                        removeFragment = true;
                    }
                    break;
                case COMMITTEE:
                    if (!fragProcYear.isSenComm() ) {
                        removeFragment = true;
                    }
                    break;
                case SENMEMO:
                    if (!fragProcYear.isSenMemo()) {
                        removeFragment = true;
                    }
                    break;
                case SENFLVOTE:
                    if (!fragProcYear.isSenFlVot()) {
                        removeFragment = true;
                    }
                    break;
                case CALENDAR:
                    if (!fragProcYear.isSenCal() ) {
                        removeFragment = true;
                    }
                    break;
                case CALENDAR_ACTIVE:
                    if (!fragProcYear.isSenCalal() ) {
                        removeFragment = true;
                    }
                    break;
                case AGENDA:
                    if (!fragProcYear.isSenAgen() ) {
                        removeFragment = true;
                    }
                    break;
                case SENAGENV:
                    if (!fragProcYear.isSenAgenV() ) {
                        removeFragment = true;
                    }
                    break;
                case VETOMSG:
                    if (!fragProcYear.isVetoMsg()) {
                        removeFragment = true;
                    }
                    break;
                default: removeFragment = false;
            }
        }
        return removeFragment;
    }

    //Determines Whether a sobi fragment should continue processing or not
    private boolean removeSobiFragment(SobiFragment fragment, ProcessYear fragProcYear) {
        boolean removeFragment = false;

        if (!fragProcYear.isAllSobi()) {
            removeFragment = true;
        }
        else {
            switch ( fragment.getType() ) {
                case BILL:
                    if (!fragProcYear.isBill()) {
                        removeFragment = true;
                    }
                    break;
                case AGENDA:
                    if (!fragProcYear.isAgenda() ) {
                        removeFragment = true;
                    }
                    break;
                case AGENDA_VOTE:
                    if (!fragProcYear.isAgendaVote() ) {
                        removeFragment = true;
                    }
                    break;
                case CALENDAR:
                    if (!fragProcYear.isCalendar() ) {
                        removeFragment = true;
                    }
                    break;
                case CALENDAR_ACTIVE:
                    if (!fragProcYear.isCalendarActive() ) {
                        removeFragment = true;
                    }
                    break;
                case COMMITTEE:
                    if (!fragProcYear.isCommittee() ) {
                        removeFragment = true;
                    }
                    break;
                case ANNOTATION:
                    if (!fragProcYear.isAnnotation()) {
                        removeFragment = true;
                    }
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
              if (!fragProcYear.isBillInfo()) {
                  removeBlock = true;
              }
              break;
          case LAW_SECTION:
              if (!fragProcYear.isLawSection()) {
                  removeBlock = true;
              }
              break;
          case TITLE:
              if (!fragProcYear.isTitle()) {
                  removeBlock = true;
              }
              break;
          case BILL_EVENT:
              if (!fragProcYear.isBillEvent()) {
                  removeBlock = true;
              }
              break;
          case SAME_AS:
              if (!fragProcYear.isSameasSobi()) {
                  removeBlock = true;
              }
              break;
          case SPONSOR:
              if (!fragProcYear.isSponsor()) {
                  removeBlock = true;
              }
              break;
          case CO_SPONSOR:
              if (!fragProcYear.isCosponsor()) {
                  removeBlock = true;
              }
              break;
          case MULTI_SPONSOR:
              if (!fragProcYear.isMultisponsor()) {
                  removeBlock = true;
              }
              break;
          case PROGRAM_INFO:
              if (!fragProcYear.isProgramInfo()) {
                  removeBlock = true;
              }
              break;
          case ACT_CLAUSE:
              if (!fragProcYear.isActClause()) {
                  removeBlock = true;
              }
              break;
          case LAW:
              if (!fragProcYear.isLaw()) {
                  removeBlock = true;
              }
              break;
          case SUMMARY:
              if (!fragProcYear.isSummary()) {
                  removeBlock = true;
              }
              break;
          case SPONSOR_MEMO:
              if (!fragProcYear.isSponsorMemo()) {
                  removeBlock = true;
              }
              break;
          case RESOLUTION_TEXT:
              if (!fragProcYear.isResolutionText()) {
                  removeBlock = true;
              }
              break;
          case TEXT:
              if (!fragProcYear.isText()) {
                  removeBlock = true;
              }
              break;
          case VETO_APPROVE_MEMO:
              if (!fragProcYear.isVetoApprMemo()) {
                  removeBlock = true;
              }
              break;
          case VOTE_MEMO:
              if (!fragProcYear.isVoteMemo()) {
                  removeBlock = true;
              }
              break;
          default: removeBlock = false;
      }
      return removeBlock;
    }

    //Creates the default Xml only process years
    private ProcessYear createDefaultXmlProcessYear() {
        ProcessYear xmlYear = new ProcessYear();
        xmlYear.setOverarchingDataConfigs(true,false);
        //xmlYear.setOverarchingSharedConfigs(true,true,true,true,true);
        xmlYear.setAllXmlConfigsTrue();
        xmlYear.setGeneralSobiConfigsFalse();
        xmlYear.setSpecificSobiConfigsFalse();
        return xmlYear;
    }

    //Creates the default sobi only process years
    private ProcessYear createDefaultSobiProcessYear() {
        ProcessYear sobiYear = new ProcessYear();
        sobiYear.setOverarchingDataConfigs(false,true);
        //sobiYear.setOverarchingSharedConfigs(true,true,true,true,true);
        sobiYear.setGeneralSobiConfigsTrue();
        sobiYear.setSpecificSobiConfigsTrue();
        sobiYear.setAllXmlConfigsFalse();
        return sobiYear;
    }

    //Retrieves a single ProcessYear from the processYearMap
    private ProcessYear getProcessYearFromMap(LocalDateTime publishDate) {
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

}
