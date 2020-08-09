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
		try {
			String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES(?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, item.getItemId());
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
		try {
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, itemId);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void saveItem(Item item) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		try {
			// save items to items table
			String sql = "INSERT IGNORE INTO items VALUES (? ,?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, item.getItemId());
			stmt.setString(2, item.getName());
			stmt.setString(3, item.getAddress());
			stmt.setString(4, item.getImageUrl());
			stmt.setString(5, item.getUrl());
			stmt.executeUpdate();
			// save keywords to keywords table
			sql = "INSERT IGNORE INTO keywords VALUES (?, ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, item.getItemId());
			for (String keyword : item.getKeywords()) {
				stmt.setString(2, keyword);
				stmt.executeUpdate();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Set<String> getFavItemIds(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		
		Set<String> favItemIds = new HashSet<>();
		try {
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet res = stmt.executeQuery();
			while (res.next()) {
				String itemId = res.getString("item_id");
				favItemIds.add(itemId);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return favItemIds;
	}
	
	private Set<String> getKeywords(String itemId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		Set<String> keywords = new HashSet<>();
		try {
			String sql = "SELECT keyword FROM keywords WHERE item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, itemId);
			ResultSet res = stmt.executeQuery();
			while (res.next()) {
				String keyword = res.getString("keyword");
				keywords.add(keyword);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return keywords;
	}
	
	public Set<Item> getFavItems(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		Set<Item> favItems = new HashSet<>();
		Set<String> favItemIds = getFavItemIds(userId);
		try {
			String sql = "SELECT * FROM itmes WHERE item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : favItemIds) {
				stmt.setString(1, itemId);
				ResultSet res = stmt.executeQuery();
				if (res.next()) {
					Item item = Item.builder()
							.itemId(res.getString("item_id"))
							.name(res.getString("name"))
							.address(res.getString("address"))
							.url(res.getString("url"))
							.imageUrl(res.getString("image_url"))
							.keywords(getKeywords(itemId))
							.build();
					favItems.add(item);
				}
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return favItems;
	}

}
