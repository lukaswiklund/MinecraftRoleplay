package se.wiklund.minecraftroleplay.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import se.wiklund.minecraftroleplay.Main;
import se.wiklund.minecraftroleplay.models.Company;

public class CompanyTabCompleter implements TabCompleter {

	private static final List<String> EMPTY = new ArrayList<>();
	private static final List<String> ACTIONS = Arrays.asList(new String[] {
		"list",
		"info",
		"details",
		"register",
		"deregister",
	});

	private Main main;

	public CompanyTabCompleter(Main main) {
		this.main = main;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) return ACTIONS;

		Player player = sender instanceof Player ? (Player) sender : null;

		if (args.length == 2) {
			String action = args[0];
			if (action.equalsIgnoreCase("info")) {
				List<Company> companies = Company.getAll(main.getDatabase());
				String[] result = new String[companies.size()];
				for (int i = 0; i < companies.size(); i++)
					result[i] = companies.get(i).name;

				return Arrays.asList(result);
			}

			if (action.equalsIgnoreCase("details") || action.equalsIgnoreCase("deregister")) {
				List<Company> companies = Company.getByOwnerId(player.getUniqueId(), main.getDatabase());
				String[] result = new String[companies.size()];
				for (int i = 0; i < companies.size(); i++)
					result[i] = companies.get(i).name;

				return Arrays.asList(result);
			}
		}

		return EMPTY;
	}
}
