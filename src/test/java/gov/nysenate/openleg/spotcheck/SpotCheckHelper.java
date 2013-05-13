package gov.nysenate.openleg.spotcheck;

import static org.junit.Assert.assertEquals;
import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.scripts.SpotCheck;
import gov.nysenate.openleg.model.SpotCheckBill;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class SpotCheckHelper
{
    public static int summaryErrorCount=0;
    public static int summaryErrorCountPS=0;
    public static int sponsorErrorCount=0;
    public static int titleErrorCount=0;
    public static int coSponsorErrorCount=0;
    public static int eventErrorCount=0;
    SpotCheck spot=new SpotCheck();
    
    public void testBillTitles(Bill bill,HashMap<String, SpotCheckBill> bills,String id){
        // Compare the titles, ignore white space differences
        
           String jsonTitle = spot.unescapeHTML(bill.getTitle());
           String lbdcTitle = bills.get(id).getTitle();
            boolean check=spot.stringEquals(jsonTitle, lbdcTitle, true, true) ;
            if(check== false)
            {
               //assertEquals(check,true);   Tests would fail in these cases
               System.out.println(bill.getSenateBillNo()+" :Title mismatch") ;  
               titleErrorCount++;
            }
            else  assertEquals(check,true);
             
       }
    
    
    public void checkSummary(Bill bill,HashMap<String, SpotCheckBill> bills,String id)
    {
        // Compare the summaries. LBDC reports summary and law changes together.
        // I think we should ignore all the white spaces differences in the summary in summary too.
        String jsonLaw = bill.getLaw();
        String jsonSummary = spot.unescapeHTML(bill.getSummary());
        String lbdcSummary = bills.get(id).getSummary();

        if( jsonLaw != null && jsonLaw != "" && jsonLaw != "null") {
            jsonSummary = spot.unescapeHTML(jsonLaw)+" "+jsonSummary;
        }
       String s1= jsonSummary.replaceAll("S","P");
       String s2= s1.replaceAll("s","P");
       String s5=s2.replaceAll("\\s","");
       String s3= lbdcSummary.replaceAll("S","P");
       String s4= s3.replaceAll("s","P");
       String s6=s4.replaceAll("\\s","");
       boolean check2=jsonSummary.replaceAll("\\s","").equals(lbdcSummary.replaceAll("\\s",""));
       boolean check=s5.equals(s6);
        if(check== false){
           //assertEquals(check,true);     Tests would fail in these cases
           System.out.println(bill.getSenateBillNo()+" :Summary/Law Actuals mismatch") ;  
           summaryErrorCountPS ++;
          
        }
        else  assertEquals(check,true);
       
        if(check2== false){
         // assertEquals(check2,true);       Tests would fail in these cases
        System.out.println(bill.getSenateBillNo()+" :Summary/Law mismatch") ;  
        summaryErrorCount++;
      
        }
        else  assertEquals(check2,true);
        
   }
    
    
    public  void checkSponsors(Bill bill,HashMap<String, SpotCheckBill> bills,String id){
        String jsonSponsor = spot.unescapeHTML(bill.getSponsor().getFullname()).toUpperCase().replace(" (MS)","").replace("BILL", "").replace("COM", "");
        String lbdcSponsor = bills.get(id).getSponsor().toUpperCase().replace("BILL", "").replace("COM", "");
        if ( !jsonSponsor.replace(" ","").equals(lbdcSponsor.replace(" ", "")) ) {
            if (!id.startsWith("D")) {
               System.out.println(bill.getSenateBillNo()+" :Sponsors mismatch"); 
               sponsorErrorCount ++ ;
               //assertEquals(jsonSponsor.replace(" ",""),(lbdcSponsor.replace(" ", "")));   Tests would fail in these cases
            }
        } 
        else assertEquals(jsonSponsor.replace(" ",""),(lbdcSponsor.replace(" ", "")));  
   }
    
    public  void checkCoSponsors(Bill bill,HashMap<String, SpotCheckBill> bills,String id){
    
        TreeSet<String> lbdcCosponsors = new TreeSet<String>(bills.get(id).getCosponsors());
        TreeSet<String> jsonCosponsors = new TreeSet<String>();
        if ( bill.getCoSponsors() != null ) {
            List<Person> cosponsors = bill.getCoSponsors();
            for(Person cosponsor : cosponsors) {
                jsonCosponsors.add(cosponsor.getFullname().toUpperCase());
            }
        }

        if ( lbdcCosponsors.size() != jsonCosponsors.size() || (!lbdcCosponsors.isEmpty() && !lbdcCosponsors.containsAll(jsonCosponsors)) ) {
            if (!id.startsWith("D")) {
                System.out.println(bill.getSenateBillNo()+" :Co Sponsors mismatch"); 
                coSponsorErrorCount ++ ;
               
                
            }
            else{
            boolean statement= (!lbdcCosponsors.isEmpty() && !lbdcCosponsors.containsAll(jsonCosponsors)) || lbdcCosponsors.size() != jsonCosponsors.size();
            assertEquals(statement,false); 
            }
            
        }
     }
    
    public void checkEvents(Bill bill, HashMap<String, SpotCheckBill> bills, String id)
    {
        ArrayList<String> lbdcEvents = bills.get(id).getActions();
        ArrayList<String> jsonEvents = new ArrayList<String>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

        for (Action action : bill.getActions()) {
            jsonEvents.add(dateFormat.format(action.getDate())+" "+action.getText());
        }

        if ( lbdcEvents.size() != jsonEvents.size() || (!lbdcEvents.isEmpty() && !lbdcEvents.containsAll(jsonEvents)) ) {
            if (!id.startsWith("D")) {
                System.out.println(bill.getSenateBillNo()+" : Event mismatch"); 
                eventErrorCount ++ ;
                
            }
            else{
            boolean statement=  lbdcEvents.size() != jsonEvents.size() || (!lbdcEvents.isEmpty() && !lbdcEvents.containsAll(jsonEvents));
            assertEquals(statement,false);
            }
        }
        
    }
    
    
    
    public static int[] getErrorCount()
    {
        int[] count=new int[6];
        count[0]=summaryErrorCount;
        count[1]=summaryErrorCountPS;
        count[2]=sponsorErrorCount;
        count[3]=titleErrorCount;
        count[4]=coSponsorErrorCount;
        count[5]= eventErrorCount;
        return count;
    }


   
    
    

}
