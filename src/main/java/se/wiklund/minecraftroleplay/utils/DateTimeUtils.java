package se.wiklund.minecraftroleplay.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateTimeUtils {

	public static String formatDate(Instant instant, FormatStyle style, ZoneId timeZoneId, Locale locale) {
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(style).withZone(timeZoneId).withLocale(locale);
		return formatter.format(instant);
	}
}
