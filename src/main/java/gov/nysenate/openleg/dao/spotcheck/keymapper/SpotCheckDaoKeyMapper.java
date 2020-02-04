package gov.nysenate.openleg.dao.spotcheck.keymapper;

import java.util.Map;

public interface SpotCheckDaoKeyMapper<ContentKey> {

    /**
     * @return the key class handled by this mapper
     */
    Class<ContentKey> getKeyClass();

    /**
     * Subclasses should implement this conversion from a Map containing certain key/val pairs to
     * an instance of ContentKey. This is needed since the keys are stored as an hstore in the
     * database.
     *
     * @param keyMap Map<String, String>
     * @return ContentKey
     */
    ContentKey getKeyFromMap(Map<String, String> keyMap);

    /**
     * Subclasses should implement a conversion from an instance of ContentKey to a Map of
     * key/val pairs that fully represent that ContentKey.
     *
     * @param key ContentKey
     * @return Map<String, String>
     */
    Map<String, String> getMapFromKey(ContentKey key);
}
