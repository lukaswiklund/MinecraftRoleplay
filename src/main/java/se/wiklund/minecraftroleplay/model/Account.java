package se.wiklund.minecraftroleplay.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.bukkit.entity.Player;

import se.wiklund.minecraftroleplay.Database;

public class Account {

	private static final String SQL_CREATE = "INSERT INTO accounts(playerId, playerName) VALUES(?, ?)";
	private static final String SQL_GET_BY_PLAYER_ID = "SELECT * FROM accounts WHERE playerId = ? LIMIT 1";
	private static final String SQL_SAVE = "UPDATE accounts SET playerName = ?, playTime = ?, money = ?, lastLogin = ? WHERE playerId = ?";

	public final UUID playerId;
	public String playerName;
	public int playTime;
	public BigDecimal money;
	public final Instant firstLogin;
	public Instant lastLogin;

	private Account(ResultSet resultSet) throws SQLException {
		this.playerId = UUID.fromString(resultSet.getString("playerId"));
		this.playerName = resultSet.getString("playerName");
		this.playTime = resultSet.getInt("playTime");
		this.money = resultSet.getBigDecimal("money");
		this.firstLogin = resultSet.getTimestamp("firstLogin").toInstant();
		this.lastLogin = resultSet.getTimestamp("lastLogin").toInstant();
	}

	public boolean save(Database database) {
		try {
			PreparedStatement statement = database.getConnection().prepareStatement(SQL_SAVE);

			statement.setString(1, this.playerName);
			statement.setInt(2, this.playTime);
			statement.setBigDecimal(3, this.money);
			statement.setTimestamp(4, Timestamp.from(this.lastLogin));
			statement.setString(5, this.playerId.toString());

			int numRows = statement.executeUpdate();
			statement.close();
			return numRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean createFromPlayer(Player player, Database database) {
		try {
			PreparedStatement statement = database.getConnection().prepareStatement(SQL_CREATE);

			statement.setString(1, player.getUniqueId().toString());
			statement.setString(2, player.getDisplayName());

			return statement.executeUpdate() >= 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Account getByPlayerId(UUID playerId, Database database) {
		try {
			PreparedStatement statement = database.getConnection().prepareStatement(SQL_GET_BY_PLAYER_ID);
			statement.setString(1, playerId.toString());
			ResultSet result = statement.executeQuery();
			if (!result.next()) {
				statement.close();
				return null;
			}
			Account account = new Account(result);
			statement.close();
			return account;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
