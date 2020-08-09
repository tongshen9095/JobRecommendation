package db;

public class MySQLDBUtil {
	// endpoint
	private static final String INSTANCE = "laiproject.cqs5xbzcoti6.us-east-2.rds.amazonaws.com";
	private static final String PORT_NUM = "3306";
	public static final String DB_NAME = "jupiter";
	private static final String USERNAME = "tong";
	private static final String PASSWORD = "Charlie0929";
	public static final String URL = "jdbc:mysql://"
			+ INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";

}
