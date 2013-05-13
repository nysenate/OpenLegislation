package gov.nysenate.openleg.model;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.TreeSet;

import org.apache.commons.dbutils.QueryRunner;


import com.mysql.jdbc.Statement;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import gov.nysenate.openleg.scripts.SpotCheck;

import gov.nysenate.openleg.util.DbConnect;
import gov.nysenate.openleg.util.Storage;

public class ReportCreate extends SpotCheck
{
    Bill bill;
    Report object=new Report();
    Error ob =new Error();
    static HashMap<String, SpotCheckBill> bills = new HashMap<String, SpotCheckBill>();
    QueryRunner qRunner = new QueryRunner();
    protected static Storage storage;
    String query; 
    Connection con=null;
    /*
      This method reads the files-assembly.low.html,assembly.high.html,senate.low.html,senate.high.html
      Change the run time argument to edit the path of the files
      
     */
   public static void main(String[] args) throws IOException
   {
      ReportCreate rr=new ReportCreate();
      String dateval=args[1];
      if (bills.size() ==0) { 
          storage  = new Storage("/home/shweta/lbdc_test/json");
          String arg[] = { args[0]+args[1]+".assembly.low.html",
                  args[0]+args[1]+".assembly.high.html",
                  args[0]+args[1]+".senate.low.html",
                  args[0]+args[1]+".senate.high.html" };
           readFiles(arg);
      }
     
      rr.insertReport( dateval);
      rr.createErrorReport();
       
       
   }
 
  /* Reads all the files
   */
  
  public static void readFiles(String[] files) throws IOException{
      
     
         
              
               for (String arg : files) {
                   bills.putAll(SpotCheck.readDaybreak(new File(arg)));
               }
          }
         
      /*
       This method creates a new report for the 4 files,assign it a unique id and sets the timestamp in the database.
       
       */
        
     
  public void insertReport( String val) 
  {
      con= DbConnect.connect();
        
      query="insert into report(rdate) values(?)";
       System.out.println((val.substring(4, 6)));
      String day=((val.substring(6)));
      String year= (val.substring(0, 4));
      String month=(val.substring(4, 6));
      String d2=month+"/"+year+"/"+day;
      
      java.util.Date date=new java.util.Date(d2);
    
      //date.setDate(day);
      
      //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      //String sqlDate = sdf.format(date);
      //System.out.println(sqlDate);
     
      java.sql.Date d1= new Date(date.getTime());
      object.setDate(date);
     
      
     
      try {
        PreparedStatement pst=con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
        pst.setDate(1, d1);
        pst.executeUpdate();
        ResultSet rs = pst.getGeneratedKeys();
        
        while (rs.next()) {
           object.setReportId(rs.getInt(1));
           ob.setReportId(rs.getInt(1));
           
         }
       
          }
    catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
     
  }
  /*This method creates the error report.It calls several methods which check for errors in summary,title,sponsor,co-sponsor and action.
   * The methods insert a record in the error report if any mismatch is found between json value and lbdc value.
   */
   public void createErrorReport()
   {   
     con= DbConnect.connect();
     for(String id : bills.keySet()) {
         String billNo = id+"-2013";
         Bill bill = (Bill)storage.get("2013/bill/"+billNo, Bill.class);
          checkTitleError(bill,id,con); // Insert title errors
          checkSummaryError(bill,id,con);  // Insert Summary Errors
          checkSponsorError(bill,id,con); // Insert sponsor Errors
          checkcosponsorError(bill,id,con); // Insert coSponsor Errors
          checkActionError(bill,id,con); //  Insert Action Errors
          
         
          }
     
      
     }
   /* Inserts the title Errors in the report */
   public void checkTitleError(Bill bill,String id,Connection con)
   {
      
     
    // Compare the titles, ignore white space differences
       
       String jsonTitle = unescapeHTML(bill.getTitle());
       String lbdcTitle = bills.get(id).getTitle();
       if (!stringEquals(jsonTitle, lbdcTitle, true, true)) {
           String billNo = id+"-2013";
          
           query="insert into error(report_id,bill_id,error_info,lbdc,json) values(?,?,?,?,?)";
           
           try {
               PreparedStatement pst=con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
               pst.setInt(1, ob.getReportId());
               pst.setString(2,billNo);
               pst.setString(3,"title");
               pst.setString(4,lbdcTitle);
               pst.setString(5,jsonTitle);
               pst.executeUpdate();
               ResultSet rs = pst.getGeneratedKeys();
               ob.setBillId(billNo);
               ob.setErrorType("title");
               ob.setJson(jsonTitle);
               ob.setLbdc(lbdcTitle);
               while (rs.next()) {
                 
                  ob.setErrorId(rs.getInt(1));
                  
                }
             
              
                 }
           catch (SQLException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
          
           
          
           
       }
    
   }
   /* Inserts the Summary Errors in the report */
   public void checkSummaryError(Bill bill,String id,Connection con)
   {
       
      
       // Compare the summary, ignore white space differences
       String jsonLaw = bill.getLaw();
       String jsonSummary = unescapeHTML(bill.getSummary());
       String lbdcSummary = bills.get(id).getSummary();

       if( jsonLaw != null && jsonLaw != "" && jsonLaw != "null") {
           jsonSummary = unescapeHTML(jsonLaw)+" "+jsonSummary;
       }

       if ( !jsonSummary.replace(" ","").equals(lbdcSummary.replace(" ", "")) ) {
           if (!id.startsWith("D")) {
               String billNo = id+"-2013";
               query="insert into error(report_id,bill_id,error_info,lbdc,json) values(?,?,?,?,?)";
           
           try {
               PreparedStatement pst=con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
               pst.setInt(1, ob.getReportId());
               pst.setString(2,billNo);
               pst.setString(3,"summary");
               pst.setString(4,lbdcSummary);
               pst.setString(5,jsonSummary);
               pst.executeUpdate();
               ResultSet rs = pst.getGeneratedKeys();
               ob.setBillId(billNo);
               ob.setErrorType("summary");
               ob.setJson(jsonSummary);
               ob.setLbdc(lbdcSummary);
               while (rs.next()) {
                 
                  ob.setErrorId(rs.getInt(1));
                  
                }
           }
                
           catch (SQLException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
           }}
          
           
       }
   /* Inserts the sponsor errors in the report */
    public void checkSponsorError(Bill bill,String id,Connection con)
         {
        // Checking Sponsor Errors
         String jsonSponsor = unescapeHTML(bill.getSponsor().getFullname()).toUpperCase().replace(" (MS)","").replace("BILL", "").replace("COM", "");
         String lbdcSponsor = bills.get(id).getSponsor().toUpperCase().replace("BILL", "").replace("COM", "");
               if ( !jsonSponsor.replace(" ","").equals(lbdcSponsor.replace(" ", "")) ) {
                   if (!id.startsWith("D")) {
                       String billNo = id+"-2013";
                       query="insert into error(report_id,bill_id,error_info,lbdc,json) values(?,?,?,?,?)";
                   
               try {
                       PreparedStatement pst=con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
                       pst.setInt(1, ob.getReportId());
                       pst.setString(2,billNo);
                       pst.setString(3,"sponsor");
                       pst.setString(4,lbdcSponsor);
                       pst.setString(5,jsonSponsor);
                       pst.executeUpdate();
                       ResultSet rs = pst.getGeneratedKeys();
                       
                       while (rs.next()) {
                         
                          ob.setErrorId(rs.getInt(1));
                          
                        }
                    
                         }
                   catch (SQLException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                   }
                  
                  
               }
           }  
      
        }
    /* Inserts the co-Sponsor Errors in the report */
    public void checkcosponsorError(Bill bill,String id,Connection con)
    {
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
                
       
                  String billNo = id+"-2013";
                  query="insert into error(report_id,bill_id,error_info,lbdc,json) values(?,?,?,?,?)";
              
          try {
                  PreparedStatement pst=con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
                  StringBuilder lbdcSponsers=new StringBuilder();
                 
                  for(String s: lbdcCosponsors)
                  {
                      lbdcSponsers=lbdcSponsers.append(" ") ;  
                     lbdcSponsers=lbdcSponsers.append(s) ;    
                  }
                  String l=lbdcSponsers.toString();
                  StringBuilder jsonSponsers=new StringBuilder();
                  
                  for(String s: jsonCosponsors)
                  {
                      jsonSponsers=jsonSponsers.append(" ") ;  
                      jsonSponsers=jsonSponsers.append(s) ;    
                  }
                  String j=jsonSponsers.toString();
                  pst.setInt(1, ob.getReportId());
                  pst.setString(2,billNo);
                  pst.setString(3,"cosponsor");
                  pst.setString(4,l);
                  pst.setString(5,j);
                  pst.executeUpdate();
                  ResultSet rs = pst.getGeneratedKeys();
                  
                  while (rs.next()) {
                    
                     ob.setErrorId(rs.getInt(1));
                     
                   }
               
                    }
              catch (SQLException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }
             
             
          }
      }  
 
   }
    
    /* Inserts the Action Errors in the report */
    public void checkActionError(Bill bill,String id,Connection con)
    {
        ArrayList<String> lbdcEvents = bills.get(id).getActions();
        ArrayList<String> jsonEvents = new ArrayList<String>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

        for (Action action : bill.getActions()) {
            jsonEvents.add(dateFormat.format(action.getDate())+" "+action.getText());
        }

        if ( lbdcEvents.size() != jsonEvents.size() || (!lbdcEvents.isEmpty() && !lbdcEvents.containsAll(jsonEvents)) ) {
                  String billNo = id+"-2013";
                  query="insert into error(report_id,bill_id,error_info,lbdc,json) values(?,?,?,?,?)";
              
          try {
                  PreparedStatement pst=con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
                  StringBuilder lbdc=new StringBuilder();
                  
                  for(String s: lbdcEvents)
                  {
                      lbdc=lbdc.append(" ") ;  
                     lbdc=lbdc.append(s) ;    
                  }
                  String l=lbdc.toString();
                  StringBuilder json=new StringBuilder();
                  
                  for(String s: jsonEvents)
                  {
                      json=json.append(" ") ;  
                      json=json.append(s) ;    
                  }
                  String j=json.toString();
                  pst.setInt(1, ob.getReportId());
                  pst.setString(2,billNo);
                  pst.setString(3,"action");
                  pst.setString(4,l);
                  pst.setString(5,j);
                  pst.executeUpdate();
                  ResultSet rs = pst.getGeneratedKeys();
                  
                  while (rs.next()) {
                    
                     ob.setErrorId(rs.getInt(1));
                     
                   }
               
                    }
              catch (SQLException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }
             
             
          }
      }  
 
   }
    
    
    
    

    
    
    

