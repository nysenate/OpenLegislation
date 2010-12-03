package gov.nysenate.openleg.lucene;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.document.Document;


public class DocumentBuilder {
	
	public static final String JAVA_PRIMITIVES = "char|byte|short|int|long|float|double|boolean|void|String";
	public static final String GET = "get";
	public static final String LUCENE = "Lucene";
	
	public static final org.apache.lucene.document.Field.Store DEFAULT_STORE = org.apache.lucene.document.Field.Store.YES;
	public static final org.apache.lucene.document.Field.Index DEFAULT_INDEX = org.apache.lucene.document.Field.Index.ANALYZED;
		
	public static final String LUCENE_OTYPE = "luceneOtype";
	public static final String LUCENE_OID = "luceneOid";
	public static final String LUCENE_OSEARCH = "luceneOsearch";
	public static final String LUCENE_FIELDS = "luceneFields";
	public static final String LUCENE_SUMMARY = "luceneSummary";
	public static final String LUCENE_TITLE = "luceneTitle";
	public static final String LUCENE_WHEN = "luceneWhen"; //lastmodified
	
	public static final String OTYPE = "otype";
	public static final String OID = "oid";
	public static final String OSEARCH = "osearch";
	public static final String SUMMARY = "summary";
	public static final String TITLE = "title";
	public static final String MODIFIED = "modified"; //lastmodified
	
	public Document buildDocument(LuceneObject o, LuceneSerializer[] serializer) {
		if(o == null) {
			return null;
		}
		
		HashMap<String,org.apache.lucene.document.Field> fields = new HashMap<String,org.apache.lucene.document.Field>();
		
		try {
			fields.put(OTYPE,
					new org.apache.lucene.document.Field(
						OTYPE,
						getLuceneFields(o, LUCENE_OTYPE),
						DEFAULT_STORE,
						DEFAULT_INDEX));
			fields.put(OID,
					new org.apache.lucene.document.Field(
							OID,
						getLuceneFields(o, LUCENE_OID).replace(" ", "+"),
						DEFAULT_STORE,
						DEFAULT_INDEX));
			fields.put(OSEARCH,
					new org.apache.lucene.document.Field(
							OSEARCH,
						getLuceneFields(o, LUCENE_OSEARCH),
						DEFAULT_STORE,
						DEFAULT_INDEX));
			fields.put(TITLE,
					new org.apache.lucene.document.Field(
							TITLE,
						getLuceneFields(o, LUCENE_TITLE),
						DEFAULT_STORE,
						DEFAULT_INDEX));
			fields.put(SUMMARY,
					new org.apache.lucene.document.Field(
							SUMMARY,
						getLuceneFields(o, LUCENE_SUMMARY),
						DEFAULT_STORE,
						DEFAULT_INDEX));
			
			String whenTimeString = new Date().getTime() + "";
			fields.put(MODIFIED, new org.apache.lucene.document.Field(
					MODIFIED,
					whenTimeString,
					DEFAULT_STORE,
					DEFAULT_INDEX));
			
			Field[] objectFields = o.getClass().getDeclaredFields();

			for(Field f:objectFields) {
				AnnotatedField af = null;
				if((af = getAnnotatedField(f)) != null) {
					String name = af.name.equals("!-_-!") ? f.getName().toLowerCase() : af.name;
					org.apache.lucene.document.Field.Store store = af.store;
					org.apache.lucene.document.Field.Index index = af.index;
					
					String fieldName = fixCase(f.getName());
					
					Method fieldMethod = null;
					Object fieldObject = null;
					try {
						fieldMethod = o.getClass().getDeclaredMethod(GET + LUCENE + fieldName);
						fieldObject = fieldMethod.invoke(o);
					}
					catch(NoSuchMethodException e1) {
						try {
							fieldMethod = o.getClass().getDeclaredMethod(GET + fieldName);
							fieldObject = fieldMethod.invoke(o);
						}
						catch (NoSuchMethodException e2) {
							f.setAccessible(true);
							fieldObject = f.get(o);
							f.setAccessible(false);
						}
					}
					
					if(fieldObject != null) {
						fields.put(name,
							new org.apache.lucene.document.Field(
								name,
								fieldObject.toString(),
								store,
								index));
					}
					else {
						fields.put(name,
								new org.apache.lucene.document.Field(
									name,
									"",
									store,
									index));
					}
				}
			}
			
			Method otherMethod = o.getClass().getDeclaredMethod(LUCENE_FIELDS);
			HashMap<String,org.apache.lucene.document.Field> otherMap = (HashMap<String,org.apache.lucene.document.Field>)otherMethod.invoke(o);
		
			if(otherMap != null) {
				fields.putAll(otherMap);
			}
			
			if(serializer != null) {
				for(LuceneSerializer lst:serializer) {
					fields.put(lst.getType(),
							new org.apache.lucene.document.Field(
								lst.getType(),
								lst.getData(o),
								DEFAULT_STORE,
								DEFAULT_INDEX));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		Document document = new Document();
		
		for(String key:fields.keySet()) {
			document.add(fields.get(key));
		}
		
		return document;
	}
	
	private String getLuceneFields(Object o, String method) throws Exception {
		Method m = o.getClass().getDeclaredMethod(method);
		String ret = (String)m.invoke(o);
		return (ret==null) ? "":ret;
	}
	
	public AnnotatedField getAnnotatedField(Field field) {
		LuceneField lf = field.getAnnotation(LuceneField.class);
		if(lf != null) {
			return new AnnotatedField(lf);
			
		}
		return null;		
	}
	
	public String classCompare(Class<?>[] list, Class<?> clazz) {
		for(Class<?> c:list) {
			if(c.equals(clazz)) {
				return clazz.getSimpleName();
				
			}
		}
		return null;
	}
	
	private String fixCase(String s) {
		char[] chars = s.toCharArray();
		
		chars[0] = Character.toUpperCase(chars[0]);
		
		return new String(chars);
	}	
	
	class AnnotatedField {
		public String name;
		public org.apache.lucene.document.Field.Store store;
		public org.apache.lucene.document.Field.Index index;
		
		public AnnotatedField(LuceneField luceneField) {
			name = luceneField.value();
			store = luceneField.store();
			index = luceneField.index();
		}	
	}
}
