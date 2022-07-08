package gov.nysenate.openleg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * An interface that designates that its implementer is a view object
 * */
@FunctionalInterface
public interface ViewObject extends Serializable {
    /**
     * Returns a string indicating the type of the content that the view object encapsulates
     * @return String
     */
    // TODO: check to normalize it?
    @JsonIgnore
    String getViewType();

    /**
     * Infers the view type of any object
     * This allows for the identification of strings and integers
     *
     * @param obj
     * @return String
     */
    static String getViewTypeOf(Object obj) {
        if (obj instanceof ViewObject) {
            return ((ViewObject) obj).getViewType();
        }
        else if (obj instanceof String) {
            return "string";
        }
        else if (obj instanceof Integer) {
            return "integer";
        }
        else {
            return "unspecified";
        }
    }
}