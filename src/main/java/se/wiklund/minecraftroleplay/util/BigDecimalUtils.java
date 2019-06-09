package se.wiklund.minecraftroleplay.util;

import java.math.BigDecimal;

public class BigDecimalUtils {

	public static BigDecimal parseBigDecimalOrDefault(String number) {
		try {
			return new BigDecimal(number);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
