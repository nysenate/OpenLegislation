package gov.nysenate.openleg.controller.api.senatesite;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceEvent;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpFragment;
import gov.nysenate.openleg.util.AsyncRunner;
import gov.nysenate.openleg.util.SenateSiteDumpFragParser;
import gov.nysenate.openleg.util.SenateSiteDumpFragParser.SenateSiteDumpFragParserException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(BASE_API_PATH + "/senatesite")
public class SenateSiteDumpCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteDumpCtrl.class);

    @Autowired private SenateSiteDao senateSiteDao;
    @Autowired private AsyncRunner asyncRunner;
    @Autowired private EventBus eventBus;
    @Autowired private SenateSiteDumpFragParser parser;

    /**
     * nysenate.gov Bill Dump API
     *
     * Posts a fragment of a json bill data dump
     *
     * Usage: (POST) /api/3/senatesite/billdump
     */
    @RequiresPermissions("senatesite:billdump:post")
    @RequestMapping(value = "/billdump", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse sendSenateSiteBillDumpFragment(@RequestBody String billFragmentJson) throws IOException {
        if(!saveDump(billFragmentJson,SpotCheckRefType.SENATE_SITE_BILLS)){
            return new SimpleResponse(false, "could not save dump :(", "bill-dump-failed");
        }
        return new SimpleResponse(true, "bill dump received.  Thanks!", "bill-dump-received");
    }

    /**
     * nysenate.gov Calendar Dump API
     *
     * Posts a fragment of a json calendar data dump
     *
     * Usage: (POST) /api/3/senatesite/caldump
     */
    //@RequiresPermissions("senatesite:caldump:post")
    @RequestMapping(value = "/caldump",method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse sendSenateSiteCalDumpFragment(@RequestBody String calFragmentJson) throws IOException{
        if(!saveDump(calFragmentJson,SpotCheckRefType.SENATE_SITE_CALENDAR)){
            return new SimpleResponse(false, "could not save dump :(", "calendar-dump-failed");
        }
        return new SimpleResponse(true, "calendar dump received.  Thanks!", "calendar-dump-received");
    }

    /**
     * This method saves FragmentJson received with appropriate SpotchekRefType
     * @param fragmentJson: Bill or Calendar Dump
     * @param refType: Bill or Calendar SpotCheckRefType
     * @return true: if successful or false: otherwise
     * @throws IOException
     */
    private boolean saveDump(String fragmentJson, SpotCheckRefType refType) throws IOException{
        SenateSiteDumpFragment fragment = parser.parseFragment(fragmentJson, refType);
        try {
            senateSiteDao.saveDumpFragment(fragment, fragmentJson);
        } catch (IOException ex) {
            return false;
        }
        asyncRunner.run(() ->
                eventBus.post(new SpotCheckReferenceEvent(refType)));
        return true;
    }
    /** Exception Handling */

    @ExceptionHandler(SenateSiteDumpFragParserException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleBadJsonData(SenateSiteDumpFragParserException ex) {
        logger.error("Senate site dump fragment json parsing error", ex);
        return new ViewObjectErrorResponse(ErrorCode.SENATE_SITE_JSON_DUMP_MISSING_FIELDS, ex.getMessage());
    }
}
