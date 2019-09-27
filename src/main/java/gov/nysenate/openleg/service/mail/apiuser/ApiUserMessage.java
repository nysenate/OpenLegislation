package gov.nysenate.openleg.service.mail.apiuser;

import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;

import java.util.HashSet;
import java.util.Set;

/**
 * Models a mail message that will be sent to Api Users with
 * the designated subscription.
 */
public class ApiUserMessage {

    private Set<ApiUserSubscriptionType> subscriptionTypes;
    private String subject;
    private String body;

    public ApiUserMessage() {
        subscriptionTypes = new HashSet<>();
        subject = "No Subject";
        body = "";
    }

    /**
     * Constructor that takes a list of subscriptions
     */
    public ApiUserMessage(Set<ApiUserSubscriptionType> subscriptionTypes, String subject, String body) {
        this.subscriptionTypes = subscriptionTypes;
        this.subject = subject;
        this.body = body;
    }

    /**
     * Constructor that takes a single subscription
     */
    public ApiUserMessage(ApiUserSubscriptionType subscription_type, String subject, String body) {
        this.subscriptionTypes = new HashSet<>();
        this.subscriptionTypes.add(subscription_type);
        this.subject = subject;
        this.body = body;
    }

    /** Getters and setters  */
    public Set<ApiUserSubscriptionType> getSubscriptionTypes() {
        return this.subscriptionTypes;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getBody() {
        return this.body;
    }

    public void setSubscriptionTypes(Set<ApiUserSubscriptionType> subscriptionTypes) {
        this.subscriptionTypes = subscriptionTypes;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

