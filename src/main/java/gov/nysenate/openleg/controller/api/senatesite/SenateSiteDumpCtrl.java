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
     * nysenate.gov Dump API
     *
     * Posts a fragment of a json node data dump
     *
     * Usage: (POST) /api/3/senatesite/dump
     */
    @RequiresPermissions("senatesite:dump:post")
    @RequestMapping(value = "/dump", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse sendSenateSiteDumpFragment(@RequestBody String billFragmentJson) throws IOException {
        if(saveDump(billFragmentJson)){
            return new SimpleResponse(true, "Dump received.  Thanks!", "dump-received");
        }
        return new SimpleResponse(false, "could not save dump :(", "dump-failed");
    }

    /**
     * This method saves Fragment Json
     * @param fragmentJson: Bill or Calendar or Agenda Dump
     * @return true: if successful or false: otherwise
     * @throws IOException
     */
    private boolean saveDump(String fragmentJson) throws IOException{
        SenateSiteDumpFragment fragment = parser.parseFragment(fragmentJson);
        try {
            senateSiteDao.saveDumpFragment(fragment, fragmentJson);
        } catch (IOException ex) {
            logger.error("Error while saving senate site dump fragment " + fragment.toString(), ex);
            return false;
        }
        asyncRunner.run(() ->
                eventBus.post(new SpotCheckReferenceEvent(fragment.getDumpId().getRefType())));
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
