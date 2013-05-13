package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.*;

public abstract class TestSetup
{
	protected static Environment env;
	protected static File sobiDirectory;
	protected static Storage storage;

	@BeforeClass
	public static void initalSetup()
	{
		loadProperties();
		sobiDirectory = new File("src/test/resources/sobi");
		
		storage = new Storage(env.getStorageDirectory());
	}

	@Before
	public void setup()
	{
		try {
			env.reset();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadProperties()
	{
		Properties prop = new Properties();
    	try {
    		prop.load(new FileInputStream("src/test/resources/properties"));
    		env = new Environment(prop.getProperty("environment"));
    	}
    	catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
}