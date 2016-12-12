package br.lopes.poker.helper;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.util.StringUtils;

public class Sheets {

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
                return null;
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
            String saldoString = cell.getStringCellValue();
            saldoString = saldoString.trim();
            saldoString = saldoString.replace(",", ".");
            saldoString = saldoString.replace("+", "");
            saldoString = saldoString.replace("O", "0");

            if (saldoString.equals("-") || saldoString.equals("")) {
                return null;
            } else {
                return new BigDecimal(saldoString);
            }
        }
    }
}
