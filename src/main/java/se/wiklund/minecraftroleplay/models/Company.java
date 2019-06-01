package se.wiklund.minecraftroleplay.models;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import se.wiklund.minecraftroleplay.Database;

public class Company {

	private static final String SQL_CREATE = "INSERT INTO companies(ownerId, name) VALUES(?, ?)";
	private static final String SQL_GET_BY_ID = "SELECT * FROM companies WHERE id = ? LIMIT 1";
	private static final String SQL_GET_BY_OWNER_ID = "SELECT * FROM companies WHERE ownerId = ?";
	private static final String SQL_GET_BY_NAME = "SELECT * FROM companies WHERE LOWER(name) = ?";
	private static final String SQL_SAVE = "UPDATE companies SET ownerId = ?, name = ?, money = ?, description = ? WHERE id = ?";
	private static final String SQL_DELETE = "DELETE FROM companies WHERE id = ?";

	public final int id;
	public UUID ownerId;
	public String name;
	public BigDecimal money;
	public String description;
	public final Timestamp registerDate;
	public Timestamp editDate;

	private Company(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getInt("id");
		this.ownerId = UUID.fromString(resultSet.getString("ownerId"));
		this.name = resultSet.getString("name");
		this.money = resultSet.getBigDecimal("money");
		this.description = resultSet.getString("description");
		this.registerDate = resultSet.getTimestamp("registerDate");
		this.editDate = resultSet.getTimestamp("editDate");
	}

	public boolean save(Database database) {
		try {
			PreparedStatement statement = database.getConnection().prepareStatement(SQL_SAVE);

			statement.setString(1, this.ownerId.toString());
			statement.setString(2, this.name.trim());
			statement.setBigDecimal(3, this.money);
			statement.setString(4, this.description);

			int numRows = statement.executeUpdate();
			statement.close();
			return numRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(Database database) {
		try {
			PreparedStatement statement = database.getConnection().prepareStatement(SQL_DELETE);
			statement.setInt(1, this.id);
			int numRows = statement.executeUpdate();
			statement.close();
			return numRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean create(UUID ownerId, String name, Database database) {
		Company conflictCompany = getByName(name, database);
		if (conflictCompany != null) return false;
		try {
			PreparedStatement statement = database.getConnection().prepareStatement(SQL_CREATE);

			statement.setString(1, ownerId.toString());
			statement.setString(2, name.trim());

			int numRows = statement.executeUpdate();
			statement.close();
			return numRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Company getById(int id, Database database) {
		try {
			PreparedStatement statement = database.getConnection().prepareStatement(SQL_GET_BY_ID);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			Company company = new Company(resultSet);
			statement.close();
			return company;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Company> getByOwnerId(UUID ownerId, Database database) {
		try {
			PreparedStatement statement = database.getConnection().prepareStatement(SQL_GET_BY_OWNER_ID);
			statement.setString(1, ownerId.toString());
			ResultSet resultSet = statement.executeQuery();
			List<Company> companies = new ArrayList<>();
			while (resultSet.next())
				companies.add(new Company(resultSet));

			statement.close();
			return companies;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Company>();
		}
	}

	public static Company getByName(String name, Database database) {
		try {
			PreparedStatement statement = database.getConnection().prepareStatement(SQL_GET_BY_NAME);
			statement.setString(1, name.trim().toLowerCase());
			ResultSet resultSet = statement.executeQuery();
			Company company = null;
			if (resultSet.next()) company = new Company(resultSet);
			statement.close();
			return company;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
