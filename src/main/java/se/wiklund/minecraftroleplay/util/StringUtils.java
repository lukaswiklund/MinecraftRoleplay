package se.wiklund.minecraftroleplay.util;

public class StringUtils {

	public static String generateString(char character, int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) builder.append(character);
		return builder.toString();
	}
}
