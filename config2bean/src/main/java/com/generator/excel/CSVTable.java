package com.generator.excel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVTable {
//	private List<String> heads = new ArrayList<String>();
	private List<List<String>> data = new ArrayList<List<String>>();
	private String filename;
	private CSVTable(String filename) {
		this.filename = filename;
	}
	private static Map<String, CSVTable> tables = new HashMap<String, CSVTable>();
	public static CSVTable getTable(String filename) {
		CSVTable tbl = tables.get(filename);
		if (tbl == null) {
			tbl = new CSVTable(filename);
			tbl.read();
			tables.put(filename, tbl);
		}
		return tbl;
	}
	
	private static void dealSimpleStr(String str, List<String> list)
	{
		str = str.replace("\"\"", "\"");
		String[] ss = str.split(",", -1);
		for (String s : ss) 
		{
			list.add(s);
		}
	}
	
	private static void dealStr(String str, List<String> list)
	{
		int left = findLeft(str);
		if (left == -1) {
			dealSimpleStr(str, list);
			return;
		}
		int right = findRight(str.substring(left + 1));
		if (right == -1) {
			throw new RuntimeException("");
		}
		right += left + 1;
		if (left != 0) {
			String str1 = str.substring(0, left);
			str1 = str1.substring(0, str1.length() - 1);
			dealSimpleStr(str1, list);
		}
		String str2 = str.substring(left + 1, right - 1);
		str2 = str2.replace("\"\"", "\"");
		list.add(str2);
		if (right != str.length()) {
			String str3 = str.substring(right).substring(1);
			dealStr(str3, list);
		}

	}
	
	
	public List<List<String>> listRow() {
		return data;
	}
	private static int findLeft(String substr)
	{
		int idx = substr.indexOf("\"");
		if (idx == -1)
			return -1;
		int idx2 = idx + 1;
		for (;idx2 < substr.length(); idx2++) 
		{
			if (substr.charAt(idx2) != '\"') {
				break;
			}
		}
		
		if ((idx2 - idx) % 2 == 0) {
			if (idx2 == substr.length())
				return -1;
			int nxt = findLeft(substr.substring(idx2));
			if (nxt == -1)
				return nxt;
			else
				return idx2 + nxt;
		} else {
			return idx;
		}
	}
	// find right ", if -1 not found
	private static int findRight(String substr)
	{
		int idx = substr.indexOf("\"");
		if (idx == -1)
			return -1;
		int idx2 = idx + 1;
		for (;idx2 < substr.length(); idx2++) 
		{
			if (substr.charAt(idx2) != '\"') {
				break;
			}
		}
		
		if ((idx2 - idx) % 2 == 0) {
			if (idx2 == substr.length())
				return -1;
			int nxt = findRight(substr.substring(idx2));
			if (nxt == -1)
				return nxt;
			else
				return idx2 + nxt;
		} else {
			return idx2;
		}
	}
	
	private static boolean isNewLinePoint(String substr) {
		// find first yinhao
		int left = findLeft(substr);
		if (left != -1) {
			if (left == substr.length() - 1)
				return true;
			int right = findRight(substr.substring(left + 1));
			if (right == -1)
				return true;
			right += left + 1;
			if (right == substr.length())
				return false;
			return isNewLinePoint(substr.substring(right));
		} else {
			return false;
		}
	}
	
	private static String checkSpecialLine(String str, BufferedReader br) throws IOException
	{
		while (isNewLinePoint(str)) {
			str += "\n" + br.readLine();
		}
		return str;
	}
	
	private void read() {
		try {
			FileInputStream fileinput = new FileInputStream(new File(filename));
			BufferedReader br = new BufferedReader(new InputStreamReader(fileinput, "gbk"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String trimline = line.trim();
				if (trimline.startsWith(",") || trimline.isEmpty())
					continue;
				line = checkSpecialLine(line, br);
				List<String> row = new ArrayList<String>();
				
				dealStr(line, row);
				data.add(row);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("read file "+ filename + " error");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
