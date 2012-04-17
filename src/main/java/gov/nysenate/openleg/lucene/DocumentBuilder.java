package gov.nysenate.openleg.lucene;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

public class DocumentBuilder {

    private static Logger logger = Logger.getLogger(DocumentBuilder.class);

    public static final String JAVA_PRIMITIVES = "char|byte|short|int|long|float|double|boolean|void|String";
    public static final String GET = "get";
    public static final String LUCENE = "Lucene";

    public static final org.apache.lucene.document.Field.Store DEFAULT_STORE = org.apache.lucene.document.Field.Store.YES;
    public static final org.apache.lucene.document.Field.Index DEFAULT_INDEX = org.apache.lucene.document.Field.Index.ANALYZED;

    public static final String OTYPE = "otype";
    public static final String OID = "oid";
    public static final String OSEARCH = "osearch";
    public static final String SUMMARY = "summary";
    public static final String TITLE = "title";
    public static final String MODIFIED = "modified";
    public static final String ACTIVE = "active";

    public Document buildDocument(ILuceneObject o, LuceneSerializer[] serializer) {
        if(o == null || o.luceneOtype() == null || o.luceneOid() == null) {
            return null;
        }

        HashMap<String,Fieldable> fields = new HashMap<String,Fieldable>();

        try {
            fields.put(OTYPE,
                    new org.apache.lucene.document.Field(
                        OTYPE,
                        o.luceneOtype(),
                        DEFAULT_STORE,
                        DEFAULT_INDEX));

            fields.put(OID,
                    new org.apache.lucene.document.Field(
                        OID,
                        o.luceneOid().replace(" ", "+"),
                        DEFAULT_STORE,
                        DEFAULT_INDEX));
            fields.put(OSEARCH,
                    new org.apache.lucene.document.Field(
                        OSEARCH,
                        o.luceneOsearch() ==  null ? "" : o.luceneOsearch(),
                        DEFAULT_STORE,
                        DEFAULT_INDEX));
            fields.put(TITLE,
                    new org.apache.lucene.document.Field(
                        TITLE,
                        o.luceneTitle() ==  null ? "" : o.luceneTitle(),
                        DEFAULT_STORE,
                        DEFAULT_INDEX));
            fields.put(SUMMARY,
                    new org.apache.lucene.document.Field(
                        SUMMARY,
                        o.luceneSummary() ==  null ? "" : o.luceneSummary(),
                        DEFAULT_STORE,
                        DEFAULT_INDEX));

            fields.put(MODIFIED, new org.apache.lucene.document.Field(
                    MODIFIED,
                    (o.getModified() == 0 ? new Date().getTime() : o.getModified()) + "",
                    DEFAULT_STORE,
                    DEFAULT_INDEX));

            fields.put(ACTIVE, new org.apache.lucene.document.Field(
                    ACTIVE,
                    new Boolean(o.getActive()).toString(),
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

            HashMap<String,Fieldable> otherMap = o.luceneFields();

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
            logger.error("Error building document: "+o.luceneOid(),e);
        }

        Document document = new Document();

        for(String key:fields.keySet()) {
            document.add(fields.get(key));
        }

        return document;
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
