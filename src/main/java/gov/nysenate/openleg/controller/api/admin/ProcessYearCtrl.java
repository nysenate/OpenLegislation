package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.Range;
import gov.nysenate.openleg.config.process.ProcessConfig;
import gov.nysenate.openleg.config.process.ProcessYear;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/process/map", method = RequestMethod.GET)
public class ProcessYearCtrl {
    private static final Logger logger = LoggerFactory.getLogger(ProcessYearCtrl.class);

    ArrayList<LocalDate> validYears = new ArrayList<>();

    @Autowired private ProcessConfig processConfig;

    @PostConstruct
    public void init() {
        validYears.addAll(processConfig.getProcessYearMap().keySet());

        int count = 0;
        while (!validYears.contains(LocalDate.of( (LocalDate.now().getYear() - count),1,1) ) ){
            validYears.add(LocalDate.of( (LocalDate.now().getYear() - count),1,1));
            count++;
        }
    }

    /**
     *  View Complete Process Map API
     * ---------------------------
     *
     * Get a single data process run via the process id (int).
     * Usage: (GET) /api/3/admin/process/map/complete
     *
     * Expected Output: Json structure of the entire ProcessYearMap
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/complete", method = RequestMethod.GET)
    public String showCompleteProcessMap() {
        return OutputUtils.toJson(processConfig.getProcessYearMap()) ;
    }

    /**
     *  View Single Process Map API
     * ---------------------------
     *
     * Get a single data process run via the process id (int).
     * Usage: (GET) /api/3/admin/process/map/{year}
     *
     * Path variable
     * year: The year you want to retrieve the ProcessYear config for
     *
     * Expected Output: Json Structure of the requested ProcessYear
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/{year}", method = RequestMethod.GET)
    public String getSingleProcessYearFromMap(@PathVariable int year) {
        LocalDate requestedYear = LocalDate.of(year,1,1);
        return OutputUtils.toJson(processConfig.getProcessYearFromMap(requestedYear)) ;
    }

    /**
     *  View Range Process Map API
     * ---------------------------
     *
     * Get a single data process run via the process id (int).
     * Usage: (GET) /api/3/admin/process/map/{startYear}/{endYear}
     *
     * Path variables
     * startYear Beginning year which must be before the end year by at least 1 year
     * endyear End year which must be after the start year by at at least 1 year
     * are the (inclusive) years that you want to see the processMaps for
     *
     *
     * Expected Output: Json Structure of the requested ProcessYears
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/{startYear}/{endYear}", method = RequestMethod.GET)
    public String getRangeOfProcessYearsFromMap(@PathVariable int startYear, @PathVariable int endYear) {
        if (startYear >= endYear) {
            return "Invalid Range: The start year must be before the end year";
        }
        else if (startYear > LocalDate.now().getYear()) {
            return "Invalid Range: The start year must be within 1995 - " + LocalDate.now().getYear();
        }
        LocalDate requestedStartYear = LocalDate.of(startYear,1,1);
        LocalDate requestedEndYear = LocalDate.of(endYear,1,1);
        Range<LocalDate> mapRange = Range.closedOpen(requestedStartYear, requestedEndYear);
        HashMap<LocalDate,ProcessYear> requestedProcessYearMap = new HashMap<>();
        for (LocalDate validYear : validYears) {
            if (mapRange.contains(validYear)) {
                requestedProcessYearMap.put(LocalDate.of(validYear.getYear(),1,1),
                        processConfig.getProcessYearFromMap(validYear));
            }
        }
        return OutputUtils.toJson(requestedProcessYearMap) ;
    }


    /**
     *  Single Year Method API
     * ---------------------------
     *
     * Set a single data process run via the process id (int).
     * Usage: (GET) /api/3/admin/process/map/set/{year}/{allXml}/{allSobi}
     *
     * Path variables:
     *  year: you want to retrieve the ProcessYear for
     *  property: the property you want to change in the ProcessYear
     *  value: true = Enable false = disable
     *
     * Expected Output: Json Structure of the requested ProcessYear
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/set/{year}/{allXml}/{allSobi}", method = RequestMethod.GET)
    public String setOverarchingDataProcessingOnSingleProcessYear(@PathVariable int year,@PathVariable boolean allXml,
                                                                  @PathVariable boolean allSobi) {
        LocalDate requestedYear = LocalDate.of(year,1,1);
        ProcessYear requestedProcessYear = processConfig.getProcessYearFromMap(requestedYear);
        requestedProcessYear.setOverarchingDataConfigs(allXml,allSobi);
        return OutputUtils.toJson(requestedProcessYear);
    }

    /**
     *  Single Year Method API
     * ---------------------------
     *
     * Set a single data process run via the process id (int).
     * Usage: (GET)
     * /api/3/admin/process/map/set/{year}/{xml}/{sobi}/{specificSobi}
     *
     * Path variables:
     *  year: you want to retrieve the ProcessYear for
     *  xml - should xml params be set true or false in the requestedProcessyear
     *  sobi - should general sobi params be set true or false in the requestedProcessYear
     *  specificSobi - should the specific sobi params be set true or false in the requestedProcessYear
     *  value: true = Enable false = disable
     *
     * Expected Output: Json Structure of the requested ProcessYear
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/set/{year}/{xml}/{sobi}/{specificSobi}",
            method = RequestMethod.GET)
    public String setOverarchingGeneralConfigsOnSingleProcessYear(@PathVariable int year,
                                                                 @PathVariable boolean xml,
                                                                 @PathVariable boolean sobi,
                                                                 @PathVariable boolean specificSobi) {
        LocalDate requestedYear = LocalDate.of(year,1,1);
        ProcessYear requestedProcessYear = processConfig.getProcessYearFromMap(requestedYear);
        determineSetMethodOnProcessYear(requestedProcessYear, xml,sobi, specificSobi);
        return OutputUtils.toJson(requestedProcessYear);
    }

    /**
     *  Single Year Method API
     * ---------------------------
     *
     * Set a single data process run via the process id (int).
     * Usage: (GET)
     * /api/3/admin/process/map/set/{year}/{allCalendar}/{allActiveLists}/{allCommittees}/{allAgendas}/{allAgendaVotes}
     *
     * Path variables:
     *  year: you want to retrieve the ProcessYear for
     *  allCalendar: the value you want to change allCalendar in the process year map
     *  allActiveLists: the value you want to change allCalendar in the process year map
     *  allCommittess: the value you want to change allCalendar in the process year map
     *  allAgendas: the value you want to change allCalendar in the process year map
     *  allAgendaVotes: the value you want to change allCalendar in the process year map
     *  value: true = Enable false = disable
     *
     * Expected Output: Json Structure of the requested ProcessYear
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/set/{year}/{allCalendar}/{allActiveLists}/{allCommittees}/{allAgendas}/{allAgendaVotes}",
            method = RequestMethod.GET)
    public String setOverarchingSharedConfigsOnSingleProcessYear(@PathVariable int year,
                                                                 @PathVariable boolean allCalendar,
                                                                 @PathVariable boolean allActiveLists,
                                                                 @PathVariable boolean allCommittees,
                                                                 @PathVariable boolean allAgendas,
                                                                 @PathVariable boolean allAgendaVotes) {
        LocalDate requestedYear = LocalDate.of(year,1,1);
        ProcessYear requestedProcessYear = processConfig.getProcessYearFromMap(requestedYear);
        requestedProcessYear.setOverarchingSharedConfigs(allCalendar,allActiveLists,allCommittees,
                allAgendas,allAgendaVotes);
        return OutputUtils.toJson(requestedProcessYear);
    }

    /**
     *  Range Year Method API
     * ---------------------------
     *
     * Set a single data process run via the process id (int).
     * Usage: (GET) /api/3/admin/process/map/set/{startYear}/{endYear}/{allXml}/{allSobi}
     *
     * Path variables:
     *  startYear: The first inclusive year you want to change a property in the ProcessYearMap
     *  endYear: The last inclusive year you want to change a property in the ProcessYearMap
     *  property: the property you want to change in the ProcessYear
     *  value: true = Enable false = disable
     *
     * Expected Output: Json Structure of the requested ProcessYears
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/set/{startYear}/{endYear}/{allXml}/{allSobi}", method = RequestMethod.GET)
    public String setOverarchingDataProcessingOnRangeOfProcessYears(@PathVariable int startYear,
                                                                   @PathVariable int endYear,
                                                                   @PathVariable boolean allXml,
                                                                   @PathVariable boolean allSobi) {
        if (startYear >= endYear) {
            return "Invalid Range: The start year must be before the end year";
        }
        else if (startYear > LocalDate.now().getYear()) {
            return "Invalid Range: The start year must be within 1995 - " + LocalDate.now().getYear();
        }
        LocalDate requestedStartYear = LocalDate.of(startYear,1,1);
        LocalDate requestedEndYear = LocalDate.of(endYear,1,1);
        Range<LocalDate> mapRange = Range.closedOpen(requestedStartYear, requestedEndYear);
        for (LocalDate validYear : validYears) {
            if (mapRange.contains(validYear)) {
                processConfig.getProcessYearFromMap(validYear).setOverarchingDataConfigs(allXml,allSobi);
            }
        }

        return getRangeOfProcessYearsFromMap(startYear, endYear);
    }

    /**
     *  Range Year Method API
     * ---------------------------
     *
     * Set a single data process run via the process id (int).
     * Usage: (GET)
     * /api/3/admin/process/map/set/{year}/{allCalendar}/{allActiveLists}/{allCommittees}/{allAgendas}/{allAgendaVotes}
     *
     * Path variables:
     *  startYear: The first inclusive year you want to change the properties in the ProcessYearMap
     *  endYear: The last inclusive year you want to change the properties in the ProcessYearMap
     *  allCalendar: the value you want to change allCalendar in the process year map
     *  allActiveLists: the value you want to change allCalendar in the process year map
     *  allCommittess: the value you want to change allCalendar in the process year map
     *  allAgendas: the value you want to change allCalendar in the process year map
     *  allAgendaVotes: the value you want to change allCalendar in the process year map
     *  value: true = Enable false = disable
     *
     * Expected Output: Json Structure of the requested ProcessYear
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(
            value = "/set/{startYear}/{endYear}/{allCalendar}/{allActiveLists}/{allCommittees}/{allAgendas}/{allAgendaVotes}",
            method = RequestMethod.GET)
    public String setOverarchingSharedConfigsOnRangeOfProcessYears(@PathVariable int startYear,
                                                                 @PathVariable int endYear,
                                                                 @PathVariable boolean allCalendar,
                                                                 @PathVariable boolean allActiveLists,
                                                                 @PathVariable boolean allCommittees,
                                                                 @PathVariable boolean allAgendas,
                                                                 @PathVariable boolean allAgendaVotes) {
        if (startYear >= endYear) {
            return "Invalid Range: The start year must be before the end year";
        }
        else if (startYear > LocalDate.now().getYear()) {
            return "Invalid Range: The start year must be within 1995 - " + LocalDate.now().getYear();
        }
        LocalDate requestedStartYear = LocalDate.of(startYear,1,1);
        LocalDate requestedEndYear = LocalDate.of(endYear,1,1);
        Range<LocalDate> mapRange = Range.closedOpen(requestedStartYear, requestedEndYear);
        for (LocalDate validYear : validYears) {
            if (mapRange.contains(validYear)) {
                processConfig.getProcessYearFromMap(validYear).setOverarchingSharedConfigs(allCalendar,
                        allActiveLists,allCommittees, allAgendas,allAgendaVotes);
            }
        }
        return getRangeOfProcessYearsFromMap(startYear, endYear);
    }

    /**
     *  Range Year Method API
     * ---------------------------
     *
     * Set a single data process run via the process id (int).
     * Usage: (GET)
     * /api/3/admin/process/map/set/{year}/{xml}/{sobi}/{specificSobi}
     *
     * Path variables:
     *  year: you want to retrieve the ProcessYear for
     *  xml - should xml params be set true or false in the requestedProcessyear
     *  sobi - should general sobi params be set true or false in the requestedProcessYear
     *  specificSobi - should the specific sobi params be set true or false in the requestedProcessYear
     *  value: true = Enable false = disable
     *
     * Expected Output: Json Structure of the requested ProcessYear
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/set/{startYear}/{endYear}/{xml}/{sobi}/{specificSobi}",
            method = RequestMethod.GET)
    public String setOverarchingGeneralConfigsOnRangeOfProcessYears(@PathVariable int startYear,
                                                                  @PathVariable int endYear,
                                                                  @PathVariable boolean xml,
                                                                  @PathVariable boolean sobi,
                                                                  @PathVariable boolean specificSobi) {
        if (startYear >= endYear) {
            return "Invalid Range: The start year must be before the end year";
        }
        else if (startYear > LocalDate.now().getYear()) {
            return "Invalid Range: The start year must be within 1995 - " + LocalDate.now().getYear();
        }
        LocalDate requestedStartYear = LocalDate.of(startYear,1,1);
        LocalDate requestedEndYear = LocalDate.of(endYear,1,1);
        Range<LocalDate> mapRange = Range.closedOpen(requestedStartYear, requestedEndYear);
        for (LocalDate validYear : validYears) {
            if (mapRange.contains(validYear)) {
                determineSetMethodOnProcessYear(processConfig.getProcessYearFromMap(validYear)
                        , xml,sobi, specificSobi);
            }
        }

        return getRangeOfProcessYearsFromMap(startYear, endYear);
    }


    /**
     * The following api calls all use the following variables as properties in their api calls
     * anAct
     * apprMemo
     * billStat
     * billText
     * ldSumm
     * ldSpon
     * ldBlurb
     * sameas
     * senComm
     * senMemo
     * senFlVot
     * senCal
     * senCalal
     * senAgen
     * senAgenV
     * vetoMsg
     * agenda
     * agendaVote
     * calendar
     * calendarActive
     * committee
     * annotation
     * bill
     * billInfo
     * lawSection
     * title
     * billEvent
     * sameasSobi
     * sponsor
     * cosponsor
     * multisponsor
     * programInfo
     * actClause
     * law
     * summary
     * sponsorMemo
     * resolutionText
     * voteMemo
     * vetoApprMemo
     */

    /**
     *  Single Property Process Map API
     * ---------------------------
     *
     * Usage: (GET) /api/3/admin/process/map/set/{year}/{property}/{value}
     *
     * Path variables:
     *  year: you want to retrieve the ProcessYear for
     *  property: the property you want to change in the ProcessYear
     *  value: true = Enable false = disable
     *
     * Expected Output: Json Structure of the requested ProcessYear
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/set/{year}/{property}/{value}", method = RequestMethod.GET)
    public String setPropertyOnSingleProcessYear(@PathVariable int year,@PathVariable String property,
                                                 @PathVariable boolean value) {
        LocalDate requestedYear = LocalDate.of(year,1,1);
        ProcessYear requestedProcessYear = processConfig.getProcessYearFromMap(requestedYear);
        return setRequestedField(requestedProcessYear,property,value);
    }

    /**
     *  Range Property Process Map API
     * ---------------------------
     *.
     * Usage: (GET) /api/3/admin/process/map/set/{startYear}/{endYear}/{property}/{value}
     *
     * Path variables:
     *  startYear: The first inclusive year you want to change a property in the ProcessYearMap
     *  endYear: The last inclusive year you want to change a property in the ProcessYearMap
     *  property: the property you want to change in the ProcessYear
     *  value: true = Enable false = disable
     *
     * Expected Output: List of messages detailing which properties were applied successfully
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/set/{startYear}/{endYear}/{property}/{value}", method = RequestMethod.GET)
    public String setPropertyOnRangeOfProcessYears(@PathVariable int startYear,@PathVariable int endYear,
                                                   @PathVariable String property, @PathVariable boolean value) {
        if (startYear >= endYear) {
            return "Invalid Range: The start year must be before the end year";
        }
        else if (startYear > LocalDate.now().getYear()) {
            return "Invalid Range: The start year must be within 1995 - " + LocalDate.now().getYear();
        }
        LocalDate requestedStartYear = LocalDate.of(startYear,1,1);
        LocalDate requestedEndYear = LocalDate.of(endYear,1,1);
        Range<LocalDate> mapRange = Range.closedOpen(requestedStartYear, requestedEndYear);
        ArrayList<String> messages = new ArrayList<>();
        for (LocalDate validYear : validYears) {
            if (mapRange.contains(validYear)) {
                messages.add( setRequestedField(processConfig.getProcessYearFromMap(validYear),property,value));
            }
        }
        return OutputUtils.toJson(messages);
    }


    /*
    Helper method for setting range and single process year values
    @param requestedProcessYear - year
    @param property - the requested property the admin wants to change
    @param value - the value to set the  requested property
     */
    private String setRequestedField(ProcessYear requestedProcessYear, String property, boolean value) {
        Class<?> processYearClass = requestedProcessYear.getClass();
        try {
            Field requestedField = processYearClass.getDeclaredField(property);
            requestedField.setAccessible(true);
            requestedField.setBoolean(requestedProcessYear, value);
            return  property + " set to " + value +" for "+ requestedProcessYear.getYear() ;
        }
        catch (Exception e){
            return "Unable to Update " + property + " for " + requestedProcessYear.getYear() + ".\n\n" + e.getMessage();
        }
    }

    /*
    Helper method for single and range api calls involving internal methods that set groups of variables
    @param requestedProcessYear - the process year requested by a user
    @param xml - should xml params be set true or false in the requestedProcessyear
    @param sobi - should general sobi params be set true or false in the requestedProcessYear
    @param specificSobi - should the specific sobi params be set true or false in the requestedProcessYear
     */
    private void determineSetMethodOnProcessYear(ProcessYear requestedProcessYear,boolean xml,
                                                 boolean sobi, boolean specificSobi) {
        if (xml) {
            requestedProcessYear.setAllXmlConfigsTrue();
        }
        else {
            requestedProcessYear.setAllXmlConfigsFalse();
        }

        if (sobi) {
            requestedProcessYear.setGeneralSobiConfigsTrue();
        }
        else {
            requestedProcessYear.setGeneralSobiConfigsFalse();
        }

        if (specificSobi) {
            requestedProcessYear.setSpecificSobiConfigsTrue();
        }
        else {
            requestedProcessYear.setSpecificSobiConfigsFalse();
        }
    }

}
