package gov.nysenate.openleg.util;
import org.apache.commons.dbutils.DbUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DbConnect {
   
      /* Function to connect to the database 
       @return Connection object
       @Exception SQL exception thrown  Class Not Found Exception
        */
    public static Connection connect()
      
   {
        String url="jdbc:mysql://localhost/senate";
        String username="root";
        String password="root";
       Connection con=null;
    try
      {
        
        DbUtils.loadDriver("com.mysql.jdbc.Driver");
        con=DriverManager.getConnection(url,username,password);
     }
    
    catch(SQLException e )
     {
    System.out.println(e.getMessage());
     }
    return (con) ;
   }

}
