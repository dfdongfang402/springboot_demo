package com.generator;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsReader {
	public void read(String filepath) {
		XSSFWorkbook wb;
		try {
			wb = new XSSFWorkbook(filepath);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			Sheet sh = wb.getSheetAt(i);
			StringBuilder sb = new StringBuilder();
			sb.append("sheet ").append(i).append("\n");
			for (Row row : sh) {
				for (Cell cell : row) {
					switch (cell.getCellType()) {
                        case BLANK: continue;
                        case BOOLEAN: sb.append(cell.getBooleanCellValue()).append(", "); continue;
                        case ERROR: continue;
                        case FORMULA: sb.append(cell.getCellFormula()).append(", "); continue;
                        case NUMERIC: sb.append(cell.getNumericCellValue()).append(", "); continue;
                        case STRING: sb.append(cell.getStringCellValue()).append(", "); continue;
					}
				//	sb.append(cell.getStringCellValue() + ", ");
					continue;
				}
				sb.append("\n");
			}
			System.out.println(sb.toString());
		}
	}
	
	public static void main(String[] args) {
		XlsReader reader = new XlsReader();
		reader.read("xlsdir/地图.xlsx");
	}
}
