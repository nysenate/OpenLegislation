package gov.nysenate.openleg.controller.api.law;

import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_API_PATH + "/laws", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class LawUpdatesCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(LawUpdatesCtrl.class);


}
