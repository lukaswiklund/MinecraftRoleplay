package se.wiklund.minecraftroleplay.company;

import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import se.wiklund.minecraftroleplay.Database;
import se.wiklund.minecraftroleplay.Main;
import se.wiklund.minecraftroleplay.constants.ConfigConstants;
import se.wiklund.minecraftroleplay.economy.MoneyUtils;
import se.wiklund.minecraftroleplay.models.Company;
import se.wiklund.minecraftroleplay.utils.ArrayUtils;
import se.wiklund.minecraftroleplay.utils.BookTemplateUtils;
import se.wiklund.minecraftroleplay.utils.BookUtils;
import se.wiklund.minecraftroleplay.utils.DateTimeUtils;
import se.wiklund.minecraftroleplay.utils.Error;
import se.wiklund.minecraftroleplay.utils.JsonUtils;

public class CompanyCommand implements CommandExecutor {

	private static final String[][] COMMAND_DESCRIPTIONS = new String[][] {
		new String[] { "company list", "List your companies" },
		new String[] { "company info <Name>", "See company info" },
		new String[] { "company details <Name>", "See detailed information about your company" },
		new String[] { "company register <Name>", "Register a new company" },
		new String[] { "company deregister <Name>", "Deregister a company" },
	};

	private Main main;

	public CompanyCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(main.getConfig().getString(ConfigConstants.Text.COMMAND_INFO_TITLE).replace("[command]", WordUtils.capitalizeFully(label)));
			sender.sendMessage(main.getConfig().getString(ConfigConstants.Text.COMMAND_INFO_SEPARATOR_COLOR) + "----------------");
			for (String[] commandDescription : COMMAND_DESCRIPTIONS) {
				sender.sendMessage(main.getConfig().getString(ConfigConstants.Text.COMMAND_INFO_DESCRIPTION)
					.replace("[command]", commandDescription[0])
					.replace("[commandDescription]", commandDescription[1])
				);
			}
			return true;
		}

		if (!(sender instanceof Player)) {
			Error.send(sender, Error.NOT_PLAYER);
			return true;
		}
		Player player = (Player) sender;
		Database database = main.getDatabase();

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				List<Company> companies = Company.getByOwnerId(player.getUniqueId(), database);
				if (companies.size() == 0) {
					Error.send(sender, main.getConfig().getString(ConfigConstants.Text.NO_COMPANIES));
					return true;
				}

				String listItemTemplate = main.getConfig().getString(ConfigConstants.Text.COMPANY_LIST_ITEM);
				player.sendMessage(main.getConfig().getString(ConfigConstants.Text.YOUR_COMPANIES));
				for (Company company : companies) {
					String money = MoneyUtils.getMoneyDisplay(company.money, main.getConfig());
					String message = listItemTemplate.replace("[companyName]", company.name).replace("[companyMoney]", money);
					player.sendMessage(message);
				}
				return true;
			}
		}

		if (args.length >= 2) {
			String action = args[0];
			String companyName = ArrayUtils.flattenStringArray(args, 1).trim();

			if (action.equalsIgnoreCase("info")) {
				Company company = Company.getByName(companyName, database);
				if (company == null) {
					Error.send(sender, Error.COMPANY_NOT_EXIST);
					return true;
				}

				ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
				BookMeta meta = (BookMeta) book.getItemMeta();
				String[] pages = JsonUtils.splitJsonArrayString(company.description);
				if (pages.length == 0) {
					pages = BookTemplateUtils.getPagesFromTemplate("company_info_empty", null, main);
				}
				BookUtils.setPages(meta, Arrays.asList(pages));
				book.setItemMeta(meta);
				BookUtils.openBook(book, player);
				
				return true;
			}

			if (action.equalsIgnoreCase("details")) {
				Company company = Company.getByName(companyName, database);
				if (company == null) {
					Error.send(sender, Error.COMPANY_NOT_EXIST);
					return true;
				}
				if (!company.ownerId.equals(player.getUniqueId())) {
					Error.send(sender, Error.COMPANY_NOT_OWNER);
					return true;
				}

				HashMap<String, String> parameters = new HashMap<>();
				parameters.put("[name]", company.name);
				parameters.put("[money]", MoneyUtils.getMoneyDisplay(company.money, main.getConfig()));
				parameters.put("[registerDate]", DateTimeUtils.formatDate(company.registerDate, FormatStyle.MEDIUM, main.getTimeZone(), main.getLocale()));

				String[] pages = BookTemplateUtils.getPagesFromTemplate("company_details", parameters, main);

				if (pages == null) return true;

				ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
				BookMeta meta = (BookMeta) book.getItemMeta();
				BookUtils.setPages(meta, Arrays.asList(pages));
				book.setItemMeta(meta);
				BookUtils.openBook(book, player);

				return true;
			}

			if (action.equalsIgnoreCase("register")) {
				Company conflictCompany = Company.getByName(companyName, database);
				if (conflictCompany != null) {
					Error.send(sender, Error.COMPANY_ALREADY_EXISTS);
					return true;
				}
				if (!Company.create(player.getUniqueId(), companyName, database)) {
					Error.send(sender, "Failed to register company.");
					return true;
				}
				String messageTemplate = main.getConfig().getString(ConfigConstants.Text.COMPANY_REGISTERED);
				player.sendMessage(messageTemplate.replace("[companyName]", companyName));
				return true;
			}

			if (action.equalsIgnoreCase("deregister")) {
				Company company = Company.getByName(companyName, database);
				if (company == null) {
					Error.send(sender, Error.COMPANY_NOT_EXIST);
					return true;
				}
				if (!company.ownerId.equals(player.getUniqueId())) {
					Error.send(sender, Error.COMPANY_NOT_OWNER);
					return true;
				}
				if (!company.delete(database)) {
					Error.send(sender, "Failed to deregister company.");
					return true;
				}
				String messageTemplate = main.getConfig().getString(ConfigConstants.Text.COMPANY_DEREGISTERED);
				player.sendMessage(messageTemplate.replace("[companyName]", company.name));
				return true;
			}
		}

		return false;
	}
}
