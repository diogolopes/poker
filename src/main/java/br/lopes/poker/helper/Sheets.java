package br.lopes.poker.helper;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

public class Sheets {
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Sheets.class);

	public static Integer getIntegerValue(final Cell cell) {
		if (cell == null) {
			return null;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			final double numericCellValue = cell.getNumericCellValue();
			return Double.valueOf(numericCellValue).intValue();
		} else {
			final String stringCellValue = cell.getStringCellValue().trim();
			if (StringUtils.isEmpty(stringCellValue)
					|| !org.apache.commons.lang3.math.NumberUtils.isNumber(stringCellValue)) {
				return Integer.valueOf(0);
			} else {
				return Integer.valueOf(stringCellValue);
			}
		}
	}

	public static BigDecimal getBigDecimalValue(final Cell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return BigDecimal.valueOf(cell.getNumericCellValue());
		} else {

			try {
				String saldoString = cell.getStringCellValue();
				saldoString = saldoString.trim();
				saldoString = saldoString.replace(",", ".");
				saldoString = saldoString.replace("+", "");
				saldoString = saldoString.replace("O", "0");

				if (saldoString.equals("-") || saldoString.equals("")) {
					return BigDecimal.ZERO;
				} else {
					return new BigDecimal(saldoString);
				}
			} catch (final NumberFormatException ne) {
				LOGGER.error("Erro ao tentar converter o valor [" + cell.getStringCellValue() + "] para númerico.");
				throw ne;
			}

		}
	}

	public static boolean isIgnoreValue(final Cell cell) {
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			final String value = cell.getStringCellValue();
			return org.apache.commons.lang3.StringUtils.isEmpty(value) || "-".equals(value.trim());
		} else {
			return false;
		}
	}
}
