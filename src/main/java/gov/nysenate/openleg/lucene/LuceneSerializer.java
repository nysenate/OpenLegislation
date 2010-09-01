package gov.nysenate.openleg.lucene;

public class LuceneSerializer {
	/*
	 * 	The LuceneSerializer should implement 1 or more methods in the following format
	 * 		
	 * 		public String to<Label>(LuceneObject object)
	 * 
	 *	openleg.Lucene's addDocument method will detect all such methods and attach their
	 *	return value to the label (converted to lowercase) as a stored but non indexed
	 * 	field on the object's document.
	 *
	 * 	For Example:
	 * 
	 * 		public String toXml(LuceneObject o) {
	 * 			return XStreamBuilder('xml').toXml(o);
	 * 		}
	 * 		
	 * 		is then stored as...
	 * 
	 * 		doc.add(new Field("xml",serializer.toXml(o),Field.Store.YES,Field.Index.NO))
	 */
	
	LuceneSerializerType[] serializers;
	
	public LuceneSerializer(LuceneSerializerType... serializers) {
		this.serializers = serializers;
	}
	
	public LuceneSerializerType[] getSerializers() {
		return serializers;
	}
	
	public void setSerializers(LuceneSerializerType... serializers) {
		this.serializers = serializers;
	}
}
