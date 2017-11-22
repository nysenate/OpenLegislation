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

    /*
    set a process year property across multiple years

    create a new process year
    set common methods
     */

    /**
     *  Complete Process Map API
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
     *  Single Process Map API
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
     *  Range Process Map API
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
     * The following methods api calls use the following variables as properties in their respective calls
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
     * Set a single data process run via the process id (int).
     * Usage: (GET) /api/3/admin/process/map/{year}/{property}/{value}
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
    public String setPropertyOnSingleProcessYear(@PathVariable int year,@PathVariable String property, @PathVariable boolean value) {
        LocalDate requestedYear = LocalDate.of(year,1,1);
        ProcessYear requestedProcessYear = processConfig.getProcessYearFromMap(requestedYear);
        return setRquestedField(requestedProcessYear,property,value);
    }

    /**
     *  Range Property Process Map API
     * ---------------------------
     *
     * Set a single data process run via the process id (int).
     * Usage: (GET) /api/3/admin/process/map/{year}/{property}/{value}
     *
     * Path variables:
     *  year: you want to retrieve the ProcessYear for
     *  property: the property you want to change in the ProcessYear
     *  value: true = Enable false = disable
     *
     * Expected Output: Json Structure of the requested ProcessYear
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/set/{startYear}/{endYear}/{property}/{value}", method = RequestMethod.GET)
    public String setPropertyOnRangeOfProcessYears(@PathVariable int startYear,@PathVariable int endYear,@PathVariable String property, @PathVariable boolean value) {
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
                messages.add( setRquestedField(processConfig.getProcessYearFromMap(validYear),property,value));
            }
        }
        return OutputUtils.toJson(messages);
    }


    /*
    Helper method for setting range and single process year values
     */
    private String setRquestedField(ProcessYear requestedProcessYear, String property, boolean value) {
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

}
