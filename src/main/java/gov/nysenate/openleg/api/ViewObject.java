package gov.nysenate.openleg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * An interface that designates that its implementer is a view object, which can be serialized by an ObjectMapper.
 */
@FunctionalInterface
public interface ViewObject extends Serializable {
    /**
     * Returns a string indicating the type of the content that the view object encapsulates
     * @return String
     */
    @JsonIgnore
    String getViewType();

    /**
     * Infers the view type of any object
     * This allows for the identification of strings and integers
     * @return String
     */
    static String getViewTypeOf(Object obj) {
        return switch (obj) {
            case ViewObject viewObject -> viewObject.getViewType();
            case String ignored -> "string";
            case Integer ignored -> "integer";
            case null, default -> "unspecified";
        };
    }
}
