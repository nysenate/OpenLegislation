package gov.nysenate.openleg.dao.base;

import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.HashMap;
import java.util.Map;

/**
 * This class addresses the need for a parameter source that prevents values from being
 * added or modified after it's created. For example a param source that needs to be passed
 * to several methods containing some base params should not allow modifications.
 */
public class ImmutableParams extends AbstractSqlParameterSource
{
    protected MapSqlParameterSource paramSource = new MapSqlParameterSource();

    /** --- Constructors --- */

    public static ImmutableParams from(MapSqlParameterSource params) {
        return new ImmutableParams(params.getValues());
    }

    private ImmutableParams(Map<String, Object> values) {
        paramSource.addValues(values);
    }

    /** --- Modifiers --- */

    /**
     * Creates a new instance with the additional parameters included.
     *
     * @param additional Map<String, Object>
     * @return ImmutableParams
     */
    public ImmutableParams add(Map<String, Object> additional) {
        Map<String, Object> combinedValues = new HashMap<>();
        combinedValues.putAll(paramSource.getValues());
        combinedValues.putAll(additional);
        return new ImmutableParams(combinedValues);
    }

    public ImmutableParams add(ImmutableParams additional) {
        return add(additional.getValues());
    }

    public ImmutableParams add(MapSqlParameterSource additional) {
        return add(additional.getValues());
    }

    /** --- Delegates --- */

    @Override
    public boolean hasValue(String paramName) {
        return paramSource.hasValue(paramName);
    }

    @Override
    public Object getValue(String paramName) throws IllegalArgumentException {
        return paramSource.getValue(paramName);
    }

    public Map<String, Object> getValues() {
        return paramSource.getValues();
    }
}