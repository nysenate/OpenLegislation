package gov.nysenate.openleg.api.ui;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.openleg.api.ViewObject;

public class GlobalsView implements ViewObject {
    private String ipWhitelist;
    private String userIpAddress;
    private String senSitePath;
    private String openlegRefPath;

    public GlobalsView() {
    }

    public GlobalsView(String ipWhitelist, String userIpAddress, String senSitePath, String openlegRefPath) {
        this.ipWhitelist = ipWhitelist;
        this.userIpAddress = userIpAddress;
        this.senSitePath = senSitePath;
        this.openlegRefPath = openlegRefPath;
    }

    @JsonProperty("isWhitelisted")
    public boolean isWhitelisted() {
        return userIpAddress.matches(ipWhitelist);
    }

    public String getIpWhitelist() {
        return ipWhitelist;
    }

    public String getUserIpAddress() {
        return userIpAddress;
    }

    public String getSenSitePath() {
        return senSitePath;
    }

    public String getOpenlegRefPath() {
        return openlegRefPath;
    }

    @Override
    public String getViewType() {
        return "globals-view";
    }
}
