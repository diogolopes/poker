package br.lopes.poker.helper;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Dates {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

	public static Date localDateToDateWithoutTime(final LocalDate date) {
		final LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.of(0, 0, 0, 0));
		final Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	public static Date localDateToDate(LocalDate date) {
		final Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	public static LocalDate dateToLocalDate(final Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static String formatPtBr(final Date date) {
		return SDF.format(date);
	}

	public static String format(final Date date, final String format) {
		return dateToLocalDate(date).format(DateTimeFormatter.ofPattern(format));
	}

}
