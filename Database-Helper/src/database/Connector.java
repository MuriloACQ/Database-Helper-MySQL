/**
 * This is a singleton class, you can only have one connection
 * 
 * @author Murilo Augusto Castagnoli de Quadros
 * @since 2014
 * @email macquadros@gmail.com
 */

package database;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class Connector {
	private static String status = "not connected";
	private static String server = "localhost";
	private static String schema = "public";
	private static String user = "root";
	private static String password = "";

	private static Connection connection = null;

	private static boolean connected = false;

	public static void setAuthentication(String username, String pass) {
		user = username;
		password = pass;
	}

	public static void setSchema(String schemaname) {
		schema = schemaname;
	}
	
	public static String getSchema() {
		return schema;
	}

	public static void setServer(String servername) {
		server = servername;
	}

	public static String getConnetionStatus() {
		return status;
	}

	public static boolean isConnected() {
		return connected;
	}

	public static Connection getConnection() {
		if (!connected) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				String url = "jdbc:mysql://" + server + "/" + schema + "?user="
						+ user + "&password=" + password;
				connection = (Connection) DriverManager.getConnection(url);
				status = "connected";
				connected = true;
			} catch (ClassNotFoundException e) {
				status = e.getMessage();
			} catch (SQLException e) {
				status = e.getMessage();
			} catch (Exception e) {
				status = e.getMessage();
			}
		}
		return connection;
	}
}
