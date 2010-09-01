package gov.nysenate.openleg.lucene;

public interface LuceneSerializer {
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
}
