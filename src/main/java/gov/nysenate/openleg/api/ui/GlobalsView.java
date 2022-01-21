package gov.nysenate.openleg.api.ui;

import gov.nysenate.openleg.api.ViewObject;

public class GlobalsView implements ViewObject {
    private String ipWhitelist;
    private String senSitePath;
    private String openlegRefPath;

    public GlobalsView() {
    }

    public GlobalsView(String ipWhitelist, String senSitePath, String openlegRefPath) {
        this.ipWhitelist = ipWhitelist;
        this.senSitePath = senSitePath;
        this.openlegRefPath = openlegRefPath;
    }

    public String getIpWhitelist() {
        return ipWhitelist;
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
