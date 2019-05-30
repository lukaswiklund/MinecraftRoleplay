package se.wiklund.minecraftroleplay;

import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import se.wiklund.minecraftroleplay.company.CompanyCommand;
import se.wiklund.minecraftroleplay.economy.MoneyCommand;
import se.wiklund.minecraftroleplay.listeners.NewPlayerListener;

public class Main extends JavaPlugin {

	private Database database;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		try {
			database = new Database(this);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			getLogger().severe("Failed to setup to database. Disabling plugin...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		getServer().getPluginManager().registerEvents(new NewPlayerListener(database), this);

		getCommand("money").setExecutor(new MoneyCommand(this));
		getCommand("company").setExecutor(new CompanyCommand(this));

		getLogger().info("MinecraftRoleplay --> Enabled");
	}

	@Override
	public void onDisable() {
		getLogger().info("MinecraftRoleplay --> Disabled");
	}

	public Database getDatabase() {
		return database;
	}
}
