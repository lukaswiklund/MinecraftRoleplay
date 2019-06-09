package se.wiklund.minecraftroleplay;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Locale;

import org.bukkit.plugin.java.JavaPlugin;

import se.wiklund.minecraftroleplay.company.CompanyCommand;
import se.wiklund.minecraftroleplay.company.CompanyTabCompleter;
import se.wiklund.minecraftroleplay.constant.ConfigConstants;
import se.wiklund.minecraftroleplay.economy.MoneyCommand;
import se.wiklund.minecraftroleplay.listeners.NewPlayerListener;

public class Main extends JavaPlugin {

	private Database database;
	private ZoneId timeZone;
	private Locale locale;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		try {
			database = new Database(this);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			getLogger().severe("Failed to connect to database. Disabling plugin...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		loadTimeZone();
		loadLocale();

		getServer().getPluginManager().registerEvents(new NewPlayerListener(database), this);

		getCommand("money").setExecutor(new MoneyCommand(this));
		getCommand("company").setExecutor(new CompanyCommand(this));

		getCommand("company").setTabCompleter(new CompanyTabCompleter(this));

		getLogger().info("MinecraftRoleplay --> Enabled");
	}

	@Override
	public void onDisable() {
		getLogger().info("MinecraftRoleplay --> Disabled");
	}

	private void loadTimeZone() {
		try {
			timeZone = ZoneId.of(getConfig().getString(ConfigConstants.TIME_ZONE));
		} catch (DateTimeException e) {
			timeZone = ZoneId.systemDefault();
			getLogger().warning("Failed to parse time zone from config (TimeZone). Using system default.");
		}
	}

	private void loadLocale() {
		try {
			String localeString = getConfig().getString(ConfigConstants.LOCALE);
			if (localeString == null) return;
			if (localeString.indexOf('_') < 0) {
				getLogger().severe("Locale is not formatted correctly. It should be language_COUNTRY, e.g en_US");
				return;
			}
			String[] localeParts = localeString.split("_");
			locale = new Locale(localeParts[0], localeParts[1]);
		} catch (NullPointerException e) {
			getLogger().warning("Failed to parse locale from config (Locale). Using system default.");
		}
	}

	public Database getDatabase() {
		return database;
	}

	public ZoneId getTimeZone() {
		return timeZone;
	}

	public Locale getLocale() {
		return locale;
	}
}
