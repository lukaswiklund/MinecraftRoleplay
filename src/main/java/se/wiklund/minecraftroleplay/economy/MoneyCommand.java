package se.wiklund.minecraftroleplay.economy;

import java.math.BigDecimal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import se.wiklund.minecraftroleplay.Main;
import se.wiklund.minecraftroleplay.constants.ConfigConstants;
import se.wiklund.minecraftroleplay.constants.PermissionConstants;
import se.wiklund.minecraftroleplay.models.Account;
import se.wiklund.minecraftroleplay.utils.Error;

public class MoneyCommand implements CommandExecutor {

	private Main main;

	public MoneyCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;

		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("set")) {
				if (!sender.hasPermission(PermissionConstants.Money.SET)) {
					Error.send(sender, Error.NO_PERMISSION);
					return true;
				}

				String targetPlayerName = args[1];
				Player targetPlayer = main.getServer().getPlayer(targetPlayerName);
				if (targetPlayer == null) {
					Error.send(sender, Error.PLAYER_NOT_EXIST);
					return true;
				}

				BigDecimal money;
				try {
					money = new BigDecimal(args[2]);
				} catch (NumberFormatException e) {
					Error.send(sender, Error.NUMBER_PARSE_ERROR);
					return true;
				}

				Account targetAccount = Account.getByPlayerId(targetPlayer.getUniqueId(), main.getDatabase());
				targetAccount.money = money;
				targetAccount.save(main.getDatabase());

				String messageTemplate = main.getConfig().getString(ConfigConstants.Text.PLAYER_MONEY_SET);
				String messageTemplateTarget = main.getConfig().getString(ConfigConstants.Text.PLAYER_MONEY_SET_TARGET);

				String moneyDisplay = MoneyUtils.getMoneyDisplay(money, main.getConfig());
				String message = messageTemplate.replace("[targetPlayer]", targetPlayer.getDisplayName()).replace("[money]", moneyDisplay);

				sender.sendMessage(message);
				targetPlayer.sendMessage(messageTemplateTarget.replace("[money]", moneyDisplay));

				return true;
			}
		}

		if (!isPlayer) {
			Error.send(sender, Error.NOT_PLAYER);
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			Account account = Account.getByPlayerId(player.getUniqueId(), main.getDatabase());
			String moneyStringTemplate = main.getConfig().getString("Text_PlayerMoney");
			player.sendMessage(moneyStringTemplate.replace("[money]", MoneyUtils.getMoneyDisplay(account.money, main.getConfig())));
			return true;
		}

		return false;
	}
}
