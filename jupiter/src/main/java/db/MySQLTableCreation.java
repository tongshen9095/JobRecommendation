package db;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	// Run this as Java application to reset the database
	public static void main(String[] args) {
		try {
			// step1: connect to MySQL
			System.out.println("Connecting to" + MySQLDBUtil.URL);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
            if (conn == null) {
            	return;
            }
            // step2: drop existing tables
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS keywords";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS history";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS items";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS users";
            stmt.executeUpdate(sql);
            
            //
            conn.close();
            System.out.println("Import done successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
