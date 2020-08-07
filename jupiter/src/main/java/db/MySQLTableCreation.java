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
            // step3: create new tables
            sql = "CREATE TABLE items ("
            		+ "item_id VARCHAR(255) NOT NULL,"
            		+ "name VARCHAR(255),"
            		+ "address VARCHAR(255),"
            		+ "image_url VARCHAR(255),"
            		+ "url VARCHAR(255),"
            		+ "PRIMARY KEY (item_id)"
            		+ ")";
            stmt.executeLargeUpdate(sql);
            
			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE keywords ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "keyword VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, keyword),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			stmt.executeUpdate(sql);
            
			sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "FOREIGN KEY (user_id, item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
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
