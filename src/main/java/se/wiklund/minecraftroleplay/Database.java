package se.wiklund.minecraftroleplay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import se.wiklund.minecraftroleplay.constant.ConfigConstants;

public class Database {

	private Connection connection;

	public Database(JavaPlugin plugin) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");

		String host = plugin.getConfig().getString(ConfigConstants.MySQL.HOST);
		int port = plugin.getConfig().getInt(ConfigConstants.MySQL.PORT);
		String databaseName = plugin.getConfig().getString(ConfigConstants.MySQL.DATABASE_NAME);
		String username = plugin.getConfig().getString(ConfigConstants.MySQL.USERNAME);
		String password = plugin.getConfig().getString(ConfigConstants.MySQL.PASSWORD);

		String connectionString = String.format("jdbc:mysql://%s:%d/%s?useSSL=false", host, port, databaseName);

		this.connection = DriverManager.getConnection(connectionString, username, password);
	}

	public Connection getConnection() {
		return connection;
	}
}
