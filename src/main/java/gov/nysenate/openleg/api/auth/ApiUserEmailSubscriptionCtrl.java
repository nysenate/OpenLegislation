package gov.nysenate.openleg.api.auth;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(BaseCtrl.BASE_API_PATH + "/email/subscription")
public class ApiUserEmailSubscriptionCtrl extends BaseCtrl {

    @Autowired
    protected ApiUserService apiUserService;

    /**
     *  Update API User's subscriptions
     *  -------------------------------
     *
     *  Updates the subscriptions of the api user with the given key. The
     *  updated subscriptions are given in 'List<String> body'.
     *
     *  (POST) /api/3/email/subscription/update
     *
     *  Request params: key (string) - The apiKey of the user whose subscriptions are
     *                                  being updated
     *
     *  Request body: body (List<string>) - The list of subscriptions the user wants
     *                                      to be subscribed to.
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResponse updateSubscriptions(@RequestParam String key, @RequestBody List<String> body) {
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        for (String sub : body) {
            subscriptions.add(getEnumParameter("subscriptions", sub, ApiUserSubscriptionType.class));
        }
        apiUserService.setSubscriptions(key, subscriptions);
        return new SimpleResponse(true, "Successfully updated subscriptions", "emailSubscriptions");
    }

    /**
     *  Retrieve API User's current subscriptions
     *  -----------------------------------------
     *
     *  Gets the subscriptions a given api user is currently subscribed for
     *
     *  (GET) /api/3/email/subscription/current
     *
     *  Request params: key (string) - The apiKey of the use whose subscriptions are being accessed
     *
     *  @return List<String> - a list of current user subscriptions
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public List<String> currentSubscriptions(@RequestParam String key) {
        Set<ApiUserSubscriptionType> subs = apiUserService.getUserByKey(key)
                .map(ApiUser::getSubscriptions)
                .orElseThrow(() -> badApiKeyError(key));
        List<String> subStrings = new ArrayList<>();
        for(ApiUserSubscriptionType s: subs) {
            subStrings.add(s.toString());
        }
        return subStrings;
    }

    /**
     *  Update API User's Email
     *  -----------------------
     *
     *  Update the email stored for a given API user to be the new email they provide
     *
     *  (POST) /api/3/email/subscription/updateEmail
     *
     *  Request params: key (string) - The apiKey of the user whose email is being updated
     *
     *  Request body: body (string) - The new email the user would like to use
     */
    @RequestMapping(value = "/updateEmail", method = RequestMethod.POST)
    public BaseResponse updateEmail(@RequestParam String key, @RequestBody String body) {
        apiUserService.updateEmail(key, body);
        return new SimpleResponse(true, "Successfully update email", "updateEmail");
    }

    /**
     *  Search To Determine Email Existence
     *  -----------------------------------
     *
     *  Determine whether the email an API user wants to use as their new email is already in
     *  the database or not.
     *
     *  (GET) /api/3/email/subscription/emailSearch
     *
     *  Request params: email (string) The new email the API User would like to use; The email
     *                                  we are searching the database for
     *
     * @return List<bool> A list containing one boolean value; True - when the email already exists
     *                                                         False - when the email does not exist
     */
    @RequestMapping(value = "/emailSearch", method = RequestMethod.GET)
    public List<Boolean> emailSearch(@RequestParam String email) {
        List<Boolean> bool = List.of(true);
        try {
            apiUserService.getUser(email);
        } catch (EmptyResultDataAccessException ex) {
            bool = List.of(false);
        }
        return bool;
    }

    @RequestMapping(value = "/getEmail", method = RequestMethod.GET)
    public BaseResponse getEmail(@RequestParam String key) {
        String requestType = "get-email";
        Optional<ApiUser> user = apiUserService.getUserByKey(key);
        return user.map(apiUser -> new SimpleResponse(true, apiUser.getEmail(), requestType))
                .orElseGet(() -> new SimpleResponse(false, "Not a valid API key!", requestType));
    }

    private static InvalidRequestParamEx badApiKeyError(String key) {
        return new InvalidRequestParamEx(key, "key", "String", "Must be a valid API users key.");
    }
}
