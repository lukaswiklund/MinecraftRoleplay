package se.wiklund.minecraftroleplay.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

public class BookTemplateUtils {

	public static String[] getPagesFromTemplate(String name, HashMap<String, String> parameters, JavaPlugin plugin) {
		String fileName = name + ".json";
		try {
			File file = Paths.get(plugin.getDataFolder().getAbsolutePath(), "book_templates", fileName).toFile();
			String content;
			if (file.exists()) {
				content = Files.readString(file.toPath());
			}
			else {
				String path = "book_templates/" + fileName;
				InputStream fileStream = BookTemplateUtils.class.getClassLoader().getResourceAsStream(path);
				if (fileStream == null) throw new IOException("Failed to load original book template file from path: " + path);

				file.getParentFile().mkdirs();
				file.createNewFile();
				content = new String(fileStream.readAllBytes());
				Files.writeString(file.toPath(), content);
			}

			if (parameters != null)
				for (String key : parameters.keySet())
					content = content.replace(key, parameters.get(key));

			return JsonUtils.splitJsonArrayString(content);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
