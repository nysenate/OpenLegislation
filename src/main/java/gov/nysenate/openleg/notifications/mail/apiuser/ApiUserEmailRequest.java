package gov.nysenate.openleg.notifications.mail.apiuser;

import java.util.ArrayList;
import java.util.List;

public class ApiUserEmailRequest {

    private List<String> subscriptions;
    private String subject;
    private String body;

    public ApiUserEmailRequest() {
        subscriptions = new ArrayList<>();
        subject = "No Subject";
        body = "";
    }

    public ApiUserEmailRequest(String subject, String body, List<String> subs) {
        this.subject = subject;
        this.body = body;
        this.subscriptions = subs;
    }

    //getters and setters
    public List<String> getSubscriptions() {
        return subscriptions;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public void setSubscriptions(List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
