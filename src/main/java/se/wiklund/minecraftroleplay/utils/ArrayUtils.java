package se.wiklund.minecraftroleplay.utils;

public class ArrayUtils {

	public static String flattenStringArray(String[] array, int startIndex) {
		StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < array.length; i++) {
			builder.append(array[i]);
			if (i != array.length - 1) builder.append(" ");
		}
		return builder.toString();
	}
}
