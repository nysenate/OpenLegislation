package gov.nysenate.openleg.api.ui;

import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class PingCtrl {
    @RequestMapping("")
    public BaseResponse ping() {
        return new SimpleResponse(true, "OK", "ping");
    }
}
