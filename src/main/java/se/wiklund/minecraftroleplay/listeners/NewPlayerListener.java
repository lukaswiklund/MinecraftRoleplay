package se.wiklund.minecraftroleplay.listeners;

import java.sql.Timestamp;
import java.time.Instant;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import se.wiklund.minecraftroleplay.Database;
import se.wiklund.minecraftroleplay.models.Account;

public class NewPlayerListener implements Listener {

	private Database database;

	public NewPlayerListener(Database database) {
		this.database = database;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Account account = Account.getByPlayerId(e.getPlayer().getUniqueId(), database);
		if (account == null) {
			Account.createFromPlayer(e.getPlayer(), database);
		}
		else {
			account.lastLogin = Timestamp.from(Instant.now());
			account.save(database);
			return;
		}
	}
}
