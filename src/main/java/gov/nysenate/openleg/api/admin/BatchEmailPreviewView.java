package gov.nysenate.openleg.api.admin;

import gov.nysenate.openleg.api.ViewObject;

/**
 * The HTML part of a batch email as a subscriber would receive it.
 */
public record BatchEmailPreviewView(String html) implements ViewObject {

    @Override
    public String getViewType() {
        return "batch-email-preview";
    }
}
