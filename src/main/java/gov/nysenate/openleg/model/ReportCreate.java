package gov.nysenate.openleg.model;

import java.io.File;
import java.io.IOException;
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
import java.text.SimpleDateFormat;

import gov.nysenate.openleg.scripts.SpotCheck;

import gov.nysenate.openleg.util.DbConnect;
import gov.nysenate.openleg.util.Storage;

public class ReportCreate extends SpotCheck
{
    
   public static void main(String[] args) throws IOException
   {
      ReportCreate rr=new ReportCreate();
      readFiles();
      rr.insertReport();
      rr.createErrorReport();
       
       
   }
  Connection con=null;
 
  Bill bill;
  Report object=new Report();
  Error ob =new Error();
  static HashMap<String, SpotCheckBill> bills = new HashMap<String, SpotCheckBill>();
  QueryRunner qRunner = new QueryRunner();
  protected static Storage storage;
  String query;
  public static void readFiles() throws IOException{
      
     
          if (bills.size() ==0) { 
              storage  = new Storage("/home/shweta/test/processed/lbdc_test/json");
              String args[]= { "/home/shweta/test/processed/lbdc_test/files/20130323.assembly.low.html",
                      "/home/shweta/test/processed/lbdc_test/files/20130323.assembly.high.html",
                      "/home/shweta/test/processed/lbdc_test/files/20130323.senate.low.html",
                      "/home/shweta/test/processed/lbdc_test/files/20130323.senate.high.html" };
              
               for (String arg : args) {
                   bills.putAll(SpotCheck.readDaybreak(new File(arg)));
               }
          }
         
      
        
     }
  public void insertReport() 
  {
      con= DbConnect.connect();
     
      query="insert into report(timestamp) values(?)";
      java.util.Date date= new java.util.Date();
      Timestamp timestamp=new Timestamp(date.getTime());
      object.setTimestamp(timestamp);
     
      
     
      try {
        PreparedStatement pst=con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
        pst.setTimestamp(1, timestamp);
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
    
    
    
    

    
    
    

