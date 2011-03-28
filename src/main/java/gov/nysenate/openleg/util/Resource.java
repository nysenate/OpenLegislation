package gov.nysenate.openleg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Jared Williams
 *	This class is the property loader and accessor.  It allows the properties file
 *	to be accessed from both the servlet and non-servlet context
 */
public class Resource {
	
	private static String classResource = "src/main/webapp/WEB-INF/app.properties";
//	private static String classResource = "/vol/share/tomcat/webapps/legislation/WEB-INF/app.properties";
	private static InputStream INPUT;
	private static Properties properties;
	
	/*
	 * If current context is servlet grab resource stream and load props, otherwise
	 * use typical file reader
	 */
	private static Properties load() {
		try{
			if(properties == null) {
				init();
				properties = new Properties();
				properties.load(INPUT);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			properties = null;
		}
		return properties;
	}
	
	public static void init() throws FileNotFoundException {
		INPUT = new FileInputStream(new File(classResource));
	}
	
	public static String get(String key) {
		return load().getProperty(key);
	}
}
