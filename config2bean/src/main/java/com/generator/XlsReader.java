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
			sb.append("sheet " + i + "\n");
			for (Row row : sh) {
				for (Cell cell : row) {
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BLANK: continue;
					case Cell.CELL_TYPE_BOOLEAN: sb.append(cell.getBooleanCellValue() + ", "); continue;
					case Cell.CELL_TYPE_ERROR: continue;
					case Cell.CELL_TYPE_FORMULA: sb.append(cell.getCellFormula() + ", "); continue;
					case Cell.CELL_TYPE_NUMERIC: sb.append(cell.getNumericCellValue() + ", "); continue;
					case Cell.CELL_TYPE_STRING: sb.append(cell.getStringCellValue() + ", "); continue;
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
