package se.wiklund.minecraftroleplay.utils;

import org.json.JSONArray;

public class JsonUtils {

	public static String[] splitJsonArrayString(String jsonArrayString) {
		JSONArray jsonArray = new JSONArray(jsonArrayString);
		String[] result = new String[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			result[i] = jsonArray.get(i).toString();
		}
		return result;
	}
}
