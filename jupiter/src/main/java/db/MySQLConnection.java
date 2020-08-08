package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import entity.Item;

public class MySQLConnection {
	private Connection conn;
	
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(MySQLDBUtil.URL);

		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setFavoriteItems(String userId, Item item) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		saveItem(item);
		String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES(?, ?)";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setNString(2, item.getItemId());
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void unsetFavoriteItems(String userId, String itemId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setNString(2, itemId);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void saveItem(Item item) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		String sql = "INSERT IGNORE INTO items VALUES (? ,?, ?, ?, ?)";
		try {
			// save items to items table
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setNString(1, item.getItemId());
			stmt.setNString(2, item.getName());
			stmt.setNString(3, item.getAddress());
			stmt.setNString(4, item.getImageUrl());
			stmt.setNString(5, item.getUrl());
			stmt.executeUpdate();
			// save keywords to keywords table
			sql = "INSERT IGNORE INTO keywords VALUES (?, ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setNString(1, item.getItemId());
			for (String keyword : item.getKeywords()) {
				stmt.setNString(2, keyword);
				stmt.executeUpdate();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
