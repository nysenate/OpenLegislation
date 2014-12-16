package gov.nysenate.openleg.controller.api.admin;

import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/environment")
public class EnvironmentCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentCtrl.class);
}
