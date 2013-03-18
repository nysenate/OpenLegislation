package gov.nysenate.openleg.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Bill2013S2230 {
	
	
	Environment env;
	@Before
	public void prepareEnv() throws IOException
	{
		
	 env= new Environment("/home/shweta/testing_environment");
	env.reset();
    
   
	}

	@Test
	public void verifyBill2013S2230() throws IOException
	{
		Bill theBill;
		File sobiDirectory = new File("/home/shweta/test/processed/2013/bills/");
	    Storage storage = new Storage(env.getStorageDirectory());
	    ArrayList<File> file = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory, "SOBI.D130114.T213149.TXT", 
	    "SOBI.D130115.T124554.TXT", "SOBI.D130114.T212649.TXT", "SOBI.D130115.T095048.TXT", "SOBI.D130115.T124054.TXT", 
	    "SOBI.D130115.T093046.TXT", "SOBI.D130116.T122729.TXT", "SOBI.D130114.T225652.TXT", "SOBI.D130114.T233653.TXT",
	    "SOBI.D130115.T093547.TXT", "SOBI.D130116.T120230.TXT", "SOBI.D130115.T162604.TXT", "SOBI.D130114.T232653.TXT", 
	    "SOBI.D130116.T115728.TXT", "SOBI.D130114.T223651.TXT", "SOBI.D130115.T162104.TXT", "SOBI.D130115.T132056.TXT", 
	    "SOBI.D130115.T094047.TXT", "SOBI.D130115.T101048.TXT");
	    
	    
	   // Tests for SOBI.D130114.T213149.TXT 
	    TestHelper.processFileC(env ,file.get(0));
	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    assertEquals(theBill.getSameAs(),"A2388");
	  
	    
		 // Tests for SOBI.D130114.T212649.TXT   
		 TestHelper.processFileC(env ,file.get(2));
		 theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		 ArrayList<Person> list=(ArrayList<Person>)theBill.getCoSponsors();
		 assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
		 assertEquals(list.get(0).toString(),"SMITH");
	  
	    // Tests for the SOBI.D130114.T223651.TXT
	    TestHelper.processFileC(env ,file.get(14));
	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    //Should have a title:It is left empty (Memo) Dont Care
	    //assertNull("The title should not be null",theBill.getTitle());
	    
	    // Tests for the SOBI.D130114.T225652.TXT
	    TestHelper.processFileC(env ,file.get(7));
	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    assertNotNull("The title should not be null",theBill.getTitle());
	    assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
	  
	    
	    
	    // Tests for the SOBI.D130114.T232653.TXT
	    TestHelper.processFileC(env ,file.get(12));
	    
	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    assertNotNull("The title should not be null",theBill.getTitle());
	    assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
	
	    // Tests for the SOBI.D130114.T233653.TXT
	    TestHelper.processFileC(env ,file.get(8));
	    theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	    Vote vote=processExpectedVote();
	    boolean var=TestHelper.voteCheck(theBill,vote);
	    assertEquals(var,true);
	    
	    
	 // Tests for the SOBI.D130115.T93046.TXT
	    TestHelper.processFileC(env ,file.get(5));
	     theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
	     assertEquals(theBill.getLawSection().equalsIgnoreCase("Criminal Procedure Law"),true);
	   
	     
	   /*  Nothing to test in SOBI.D130115.T093547.TXT,SOBI.D130115.T095048.TXT,SOBI.D130115.T101048.TXT,
	     SOBI.D130115.T124554.TXT, SOBI.D130115.T132056.TXT,SOBI.D130116.T120230.TXT(Memo), SOBI.D130116.T122729.TXT */
	     
	   //Tests for the SOBI.D130115.T094047.TXT,SOBI.D130115.T124054.TXT,SOBI.D130115.T162104.TXT,SOBI.D130116.T115728.TXT
	      TestHelper.processFileC(env ,file.get(17));
		  theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		  assertNotNull("The title should not be null",theBill.getTitle());
		  assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
		  
		  TestHelper.processFileC(env ,file.get(4));
		  theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		  assertNotNull("The title should not be null",theBill.getTitle());
		  assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
		  
		  TestHelper.processFileC(env ,file.get(15));
		  theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		  assertNotNull("The title should not be null",theBill.getTitle());
		  assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
		  
		  TestHelper.processFileC(env ,file.get(13));
		  theBill=(Bill)storage.get("2013/bill/S2230-2013", Bill.class);
		  assertNotNull("The title should not be null",theBill.getTitle());
		  assertEquals(theBill.getSponsor().getFullname(),"KLEIN");
		  
    	}
	
	
	public static Vote processExpectedVote() 
	{
		String[] yes= {"Adams", "Addabbo", "Avella", "Boyle", "Breslin", "Carlucci", "Diaz", "Dilan", "Espaillat", "Felder", "Flanagan", 
		  	      "Fuschillo", "Gianaris", "Gipson", "Golden", "Grisanti", "Hannon", "Hassell-Thomps", "Hoylman", "Kennedy", "Klein", 
		  	      "Krueger", "Lanza", "Latimer", "LaValle", "Marcellino", "Martins", "Montgomery", "O'Brien", "Parker", "Peralta", "Perkins",
		  	      "Rivera", "Sampson", "Sanders", "Savino", "Serrano", "Skelos", "Smith", "Squadron", "Stavisky", "Stewart-Cousin","Valesky"};
		    String[] no= {"Ball", "Bonacic", "DeFrancisco", "Farley", "Gallivan", "Griffo", "Larkin", "Libous", "Little", "Marchione", 
		    		"Maziarz", "Nozzolio", "O'Mara", "Ranzenhofer", "Ritchie", "Robach", "Seward", "Young"};
		    String [] excluded={ "Zeldin"};
		    String [] abstained={};
		    String [] absent={};
		    List<String> y=Arrays.asList(yes);
		    List<String> n=Arrays.asList(no);
		    List<String> e=Arrays.asList(excluded);
		    List<String> abstain=Arrays.asList(abstained);
		    List<String> ab=Arrays.asList(absent);
		    Vote vote=new Vote();
		    vote.setAyes(y);
		    vote.setNays(n);
		    vote.setExcused(e);
		    vote.setAbsent(ab);
		    vote.setAbstains(abstain);
		    return vote;
		
	}
	
	   

}
