package gov.nysenate.openleg.dao.base;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Common utility methods to be used by enums/classes that store sql queries.
 */
public abstract class SqlQueryUtils
{
    /**
     * Replaces the ${schema} placeholder in the given sql String with the given schema name.
     * This is mainly used for queries where the schema name can be user defined, e.g. the environment schema.
     * @param sql String
     * @param schema String
     * @return String
     */
    public static String getSqlWithSchema(String sql, String schema) {
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("schema", schema);
        return new StrSubstitutor(replaceMap).replace(sql);
    }
}
