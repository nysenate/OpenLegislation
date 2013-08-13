package gov.nysenate.openleg.model;

/**
 *
 * @author GraylinKim
 */
public interface ISenateSerializer
{
    /**
     * @return the data type of the serialization; e.g. xml, json
     */
	String getType();

	/**
	 * @param object The object to serialize
	 * @return the string representation of the serialized object
	 */
	String getData(BaseObject object);
}