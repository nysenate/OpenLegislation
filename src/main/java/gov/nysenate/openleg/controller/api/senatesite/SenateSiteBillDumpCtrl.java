package gov.nysenate.openleg.controller.api.senatesite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteBillDao;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceEvent;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDumpFragId;
import gov.nysenate.openleg.util.AsyncRunner;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;

@RestController
@RequestMapping(BASE_API_PATH + "/senatesite")
public class SenateSiteBillDumpCtrl extends BaseCtrl {

    @Autowired private SenateSiteBillDao senateSiteBillDao;

    @Autowired private AsyncRunner asyncRunner;
    @Autowired private EventBus eventBus;
    @Autowired private ObjectMapper objectMapper;

    /**
     * nysenate.gov Bill Dump API
     *
     * Posts a fragment of a json bill data dump
     *
     * Usage: (POST) /api/3/senatesite/billdump
     */
    @RequiresPermissions("senatesite:billdump:post")
    @RequestMapping(value = "/billdump", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse sendSenateSiteBillDumpFragment(@RequestBody Object billFragmentJson) {
        SenateSiteBillDumpFragId fragId = objectMapper.convertValue(billFragmentJson, SenateSiteBillDumpFragId.class);
        try {
            senateSiteBillDao.saveDumpFragment(fragId, billFragmentJson);
        } catch (IOException ex) {
            return new SimpleResponse(false, "could not save dump :(", "bill-dump-failed");
        }
        asyncRunner.run(() ->
                eventBus.post(new SpotCheckReferenceEvent(SpotCheckRefType.SENATE_SITE_BILLS)));
        return new SimpleResponse(true, "bill dump received.  Thanks!", "bill-dump-received");
    }
}
