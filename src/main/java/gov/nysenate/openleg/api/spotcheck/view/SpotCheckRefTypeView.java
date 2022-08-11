package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;

public class SpotCheckRefTypeView implements ViewObject {

    private String name;
    private String refName;
    private String displayName;
    private String datasource;
    private String contentType;

    public SpotCheckRefTypeView() {
    }

    public SpotCheckRefTypeView(SpotCheckRefType refType) {
        this.name = refType.name();
        this.refName = refType.getRefName();
        this.displayName = refType.getDisplayName();
        this.datasource = refType.getDataSource().name();
        this.contentType = refType.getContentType().name();
    }

    public String getName() {
        return name;
    }

    public String getRefName() {
        return refName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDatasource() {
        return datasource;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public String getViewType() {
        return "spotcheck-ref-type";
    }
}
