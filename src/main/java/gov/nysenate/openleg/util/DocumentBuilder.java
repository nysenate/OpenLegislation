package gov.nysenate.openleg.util;

import gov.nysenate.openleg.PMF;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.committee.Meeting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;

public class DocumentBuilder {
	
	public static final String JAVA_PRIMITIVES = "char|byte|short|int|long|float|double|boolean|void";

	
	public static void main(String[] args) throws Exception {
		
		DocumentBuilder db = new DocumentBuilder();
		TestObject to = db.new TestObject();
		
		converter(to, null);		
		
//		/*bill from db*/
//		Bill b = PMF.getDetachedBill("S5000");		
//		converter(b, null);
		
//		System.out.println();
		
//		/*calendar from db*/
//		System.out.println("\n\n-----CALENDAR-----\n\n");
//		Calendar c = (Calendar)PMF.getDetachedObject(Calendar.class, "id", "cal-active-00060-2009", "no descending");	
//		System.out.println();
//		
//		/*transcript from db to xstream xml*/
//		System.out.println("\n\n\n-----TRANSCRIPT-----\n\n");
//		Transcript t = PMF.getDetachedTranscript("292");
//		System.out.println();
//		
//		/*meeting from db to xstream xml*/
//		System.out.println("\n\n\n-----MEETING-----\n\n");
//		Collection<Meeting> meetings = PMF.getDetachedObjects(Meeting.class, "committeeName", ".*" + "Aging" + ".*", "meetingDateTime descending", 0, 1);
//		List<String> meeting_exclude = new ArrayList<String>();
//		meeting_exclude.add("votes");
//		for(Meeting m:meetings) {
//			System.out.println();
//		}
	}
	
	private static void converter(Object o, Collection<org.apache.lucene.document.Field> fields) {		
		if(fields == null) {
			fields = new ArrayList<org.apache.lucene.document.Field>();
		}
				

		try {
			Field[] objectFields = o.getClass().getDeclaredFields();

			for(Field f:objectFields) {
								
				if(isHidden(f, o.getClass()) || f.getName().contains("jdo") ||
						Modifier.isStatic(f.getModifiers()))
					continue;
				
				
				String fieldName = fixCase(f.getName());
				
				String fieldType = f.getType().getSimpleName();
				
				Method fieldMethod = null;
				Object fieldObject = null;				
				try {
					fieldMethod = o.getClass().getDeclaredMethod("get" + fieldName);
					fieldObject = fieldMethod.invoke(o);

				}
				catch (Exception e) {
					continue;
				}
					

				
				if(fieldObject != null) {
					if(fieldType.matches(JAVA_PRIMITIVES + "|String")) {
//						System.out.println(f.getName() + " : " + fieldMethod.invoke(o).getClass());
						System.out.println("val: " + fieldObject);
					}
					else {
						String collection = whichType(fieldObject.getClass());
						
						if(collection != null) {
							System.out.println(f.getName() + " : collection" + " : " + collection);
						}
						
						else {
							System.out.println(f.getName() + " : " + fieldMethod.invoke(o).getClass());
							
						}
					}
				
				

				}
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getName(Field field) {
		return null;
	}
	public static String getIndexingType(Field field, Class<?> clazz) {
		return null;
	}
	
	
	public static boolean isHidden(Field field, Class<?> clazz) {
		HideFrom hideFrom = field.getAnnotation(HideFrom.class);
		
		if(hideFrom != null) {
			if(classCompare(hideFrom.value(), clazz) != null) {
				return true;
			}
			
		}
		return false;
	}
	
	public static String classCompare(Class<?>[] list, Class<?> clazz) {
		for(Class<?> c:list) {
			if(c.equals(clazz)) {
				return clazz.getSimpleName();
				
			}
		}
		return null;
	}
	
	public static String whichType(Class<?> clazz) {
		Class<?>[] list = {Map.class, List.class, Set.class};
		
		for(Class<?> c:clazz.getInterfaces()) {
			
			String s = classCompare(list, c);
			if(s != null) {
				return s;
			}
			
		}
		
		return null;
		
		
	}
	
	private static String fixCase(String s) {
		char[] chars = s.toCharArray();
		
		chars[0] = Character.toUpperCase(chars[0]);
		
		return new String(chars);
	}
	
	public class TestObject {
		char c = 's';
		byte by = 0x3;
		short s = 0x3;
		int i = 1;
		long l = 1;
		float f = 1;
		double d = 1;
		boolean bo = true;
		
		@HideFrom({TestObject.class})
		Bill bill = new Bill();
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		ArrayList<String> list = new ArrayList<String>();

		char[] chars = {'s'};
		
		String str = "hi";

		public char getC() {
			return c;
		}

		public byte getBy() {
			return by;
		}

		public short getS() {
			return s;
		}

		public int getI() {
			return i;
		}

		public long getL() {
			return l;
		}

		public float getF() {
			return f;
		}

		public double getD() {
			return d;
		}

		public boolean getBo() {
			return bo;
		}

		public Bill getBill() {
			return bill;
		}

		public HashMap<String, String> getMap() {
			return map;
		}

		public ArrayList<String> getList() {
			return list;
		}

		public char[] getChars() {
			return chars;
		}

		public String getStr() {
			return str;
		}

		public void setC(char c) {
			this.c = c;
		}

		public void setBy(byte by) {
			this.by = by;
		}

		public void setS(short s) {
			this.s = s;
		}

		public void setI(int i) {
			this.i = i;
		}

		public void setL(long l) {
			this.l = l;
		}

		public void setF(float f) {
			this.f = f;
		}

		public void setD(double d) {
			this.d = d;
		}

		public void setBo(boolean bo) {
			this.bo = bo;
		}

		public void setBill(Bill bill) {
			this.bill = bill;
		}

		public void setMap(HashMap<String, String> map) {
			this.map = map;
		}

		public void setList(ArrayList<String> list) {
			this.list = list;
		}

		public void setChars(char[] chars) {
			this.chars = chars;
		}

		public void setStr(String str) {
			this.str = str;
		}
		

		
		
		
	}
	
	public class UnknownTypeException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public UnknownTypeException(String message) {
			super(message);
		}
		public UnknownTypeException(String message, Throwable t) {
			super(message,t);
		}
	}
	
}
