package gov.nysenate.openleg.controller.api.base;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseCtrl.BASE_API_PATH + "/ping")
public class ApiPingCtrl extends BaseCtrl
{
    @RequestMapping("")
    public BaseResponse ping() {
        return new SimpleResponse(true, "OK", "ping");
    }
}
