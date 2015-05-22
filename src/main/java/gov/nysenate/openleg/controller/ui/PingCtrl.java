package gov.nysenate.openleg.controller.ui;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class PingCtrl
{
    @RequestMapping("")
    public String ping() {
        return "OK";
    }
}