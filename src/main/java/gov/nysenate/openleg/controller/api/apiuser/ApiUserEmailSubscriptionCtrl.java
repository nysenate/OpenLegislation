package gov.nysenate.openleg.controller.api.apiuser;

import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.auth.SqlApiUserDao;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Set;

@RestController
@RequestMapping(BaseCtrl.BASE_API_PATH + "/email/subscription")
public class ApiUserEmailSubscriptionCtrl extends BaseCtrl {

    @Autowired
    protected SqlApiUserDao sqlApiUserDao;

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void updateSubscriptions(@RequestParam String key, @RequestBody List<String> body) {
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        for (String sub : body) {
            subscriptions.add(ApiUserSubscriptionType.valueOf(sub));
        }
        sqlApiUserDao.setSubscriptions(key, subscriptions);
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public List<String> currentSubscriptions(@RequestParam String key) {
        Set<ApiUserSubscriptionType> subs = sqlApiUserDao.getApiUserFromKey(key).getSubscriptions();
        List<String> subStrings = new ArrayList<>();
        for(ApiUserSubscriptionType s: subs) {
            subStrings.add(s.toString());
        }
        return subStrings;
    }


}
