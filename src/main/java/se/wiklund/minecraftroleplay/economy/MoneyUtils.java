package se.wiklund.minecraftroleplay.economy;

import java.math.BigDecimal;

import org.bukkit.configuration.file.FileConfiguration;

import se.wiklund.minecraftroleplay.constant.ConfigConstants;

public class MoneyUtils {

	public static String getMoneyDisplay(BigDecimal money, FileConfiguration config) {
		String currency = config.getString(ConfigConstants.Currency.UNIT);
		boolean placeBefore = config.getBoolean(ConfigConstants.Currency.PLACE_BEFORE);
		boolean spaceBetween = config.getBoolean(ConfigConstants.Currency.SPACE_BETWEEN);

		if (placeBefore) return String.format("%s%s%s", currency, spaceBetween ? " " : "", money);
		else return String.format("%s%s%s", money, spaceBetween ? " " : "", currency);
	}
}
