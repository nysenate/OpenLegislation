package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseCtrl.BASE_API_PATH + "/ping")
public class ApiPingCtrl extends BaseCtrl {
    @RequestMapping("")
    public BaseResponse ping() {
        return new SimpleResponse(true, "OK", "ping");
    }
}
