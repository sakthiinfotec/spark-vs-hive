import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
 
public class HiveJdbcClient {
  private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
 
  public static void main(String[] args) throws SQLException {
    try {
      Class.forName(driverName);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(1);
    }
    Connection con = DriverManager.getConnection("jdbc:hive://localhost:10000/default", "", "");
    Statement stmt = con.createStatement();
    String tableName = "hive_hbase";

    // describe table
    String sql = "describe " + tableName;
    System.out.println("Running: " + sql);
    ResultSet res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(res.getString(1) + "\t" + res.getString(2));
    }
    
    // select * query
    sql = "select * from " + tableName;
    System.out.println("Running: " + sql);

    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println("res:" + String.valueOf(res.getString(1)) + "\t" + String.valueOf(res.getString(2)));
    }
 
    // regular hive query
    //sql = "select count(1) from " + tableName;
    //System.out.println("Running: " + sql);
    //res = stmt.executeQuery(sql);
    //while (res.next()) {
    //  System.out.println(res.getString(1));
    //}
  }
}
