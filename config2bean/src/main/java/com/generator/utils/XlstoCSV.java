package com.generator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlstoCSV {
    static void copyFile(File source, File dest) {
        InputStream input = null;
        OutputStream output = null;
        try {
            if (!dest.exists())
                dest.createNewFile();
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("copy file " + source.getAbsolutePath() + " to " + dest.getAbsolutePath() + " error");
            e.printStackTrace();
        }
    }

    private static class XlsContext {
        private String columnHeads = "";
        private Set<String> rows = new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int idx1 = o1.indexOf(',');
                int idx2 = o2.indexOf(',');
                if (idx1 < 0 && idx2 < 0)
                    return o1.compareTo(o2);
                if (idx1 < 0)
                    return 1;
                if (idx2 < 0)
                    return -1;
                Integer id1 = null, id2 = null;
                try {
                    id1 = Integer.valueOf(o1.substring(0, idx1).trim());
                } catch (NumberFormatException e) {
                    return 1;
                }
                try {
                    id2 = Integer.valueOf(o2.substring(0, idx2).trim());
                } catch (NumberFormatException e) {
                    return -1;
                }
                if (id1.intValue() == id2.intValue())
                    return 0;
                return id1 < id2 ? -1 : 1;
            }
        });

        private void write(File outputFile, String encode) {
            try {
                FileOutputStream fos = new FileOutputStream(outputFile);
                final java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(fos, encode);
                writer.write(columnHeads + "\n");
                for (String row : rows) {
                    writer.write(row + "\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void read(File inputFile) {
            try {
                // writer.append((char) 0xFEFF);
                // Get the workbook object for XLS file
                FileInputStream inputStream = new FileInputStream(inputFile);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                // Get first sheet from the workbook
                XSSFSheet sheet = workbook.getSheetAt(0);
                Cell cell;
                Row row;
                // Iterate through each rows from first sheet
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    row = rowIterator.next();
                    StringBuilder rowSb = new StringBuilder();
                    // For each row, iterate through each columns
                    Iterator<Cell> cellIterator = row.cellIterator();
                    int columnMax = row.getLastCellNum();
                    int curIdx = 0;
                    while (cellIterator.hasNext()) {
                        cell = cellIterator.next();
                        int cellIdx = cell.getColumnIndex();
                        for (; curIdx < cellIdx; curIdx++) {
                            rowSb.append(',');
                        }
                        switch (cell.getCellType()) {
                            case BOOLEAN:
                                rowSb.append(cell.getBooleanCellValue() + ",");
                                break;

                            case NUMERIC: {
                                String val = formatter.formatCellValue(cell);
                                rowSb.append(val + ",");
                            }
                            break;

                            case STRING: {
                                String val = cell.getStringCellValue();
                                val = val.replace("\"", "\"\"");
                                if (val.indexOf(',') != -1 || val.indexOf('\n') != -1) {
                                    val = "\"" + val + "\"";
                                }
                                rowSb.append(val + ",");
                            }
                            break;

                            case BLANK:
                                rowSb.append("" + ",");
                                break;

                            default:
                                //System.out.println("jump column = " + inputFile.getAbsolutePath() + "");
                                rowSb.append("" + ",");
                        }
                        curIdx++;
                    }
                    for (; curIdx < columnMax; curIdx++) {
                        if (curIdx > 0)
                            rowSb.append(",");
                    }
                    String rowStr = rowSb.toString();
                    if (columnHeads.isEmpty()) {
                        columnHeads = rowStr;
                    } else {
                        String trimRowStr = rowStr.trim();
                        if (trimRowStr.startsWith(",") || trimRowStr.isEmpty())
                            continue;
                        if (!rows.add(trimRowStr)) {
                            String id = trimRowStr.substring(0, trimRowStr.indexOf(','));
                            throw new RuntimeException("same id=" + id + " in table " + inputFile.getName());
                        }
                    }
                }
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ;
    private static DataFormatter formatter = new DataFormatter();
    static String[] jumpfiles = {"添加specialnode.xlsm"};//"z战斗NPC表.xlsx", "r任务线列表.xlsx",

    public static void xls2csv(File inputFile, File outputFile, String encode) {
        // For storing data into CSV files
        XlsContext context = new XlsContext();
        context.read(inputFile);
        context.write(outputFile, encode);
    }

    static File xlsDir, csvDir;

    public static void convertPath(File path, String encode) {
        for (File file : path.listFiles()) {
            if (file.isDirectory()) {
                convertPath(file, encode);
                continue;
            }
            String abPath = file.getAbsolutePath();
            String toMkdir = abPath.substring(xlsDir.getAbsolutePath().length() + 1);
            int idx = -1;
            String dirname = "";
            while ((idx = toMkdir.indexOf("\\")) != -1) {
                dirname += "\\" + toMkdir.substring(0, idx);
                toMkdir = toMkdir.substring(idx + 1);
                File newdir = new File(csvDir.getAbsolutePath() + dirname);
                if (!newdir.exists()) {
                    newdir.mkdir();
                } else if (newdir.isFile()) {
                    String err = "the file " + newdir.getAbsolutePath() + " should be a dir";
                    System.out.println(err);
                    throw new RuntimeException(err);
                }
            }

            abPath = csvDir.getAbsolutePath() + abPath.substring(xlsDir.getAbsolutePath().length());
            if ((!file.getName().endsWith(".xlsx") && !file.getName().endsWith(".xlsm"))) {
                System.out.println("jump file = " + file.getAbsolutePath());
                copyFile(file, new File(abPath));
                continue;
            }
            for (int i = 0; i < jumpfiles.length; i++) {
                if (file.getName().equals(jumpfiles[i])) {
                    System.out.println("jump file = " + file.getAbsolutePath());
                    copyFile(file, new File(abPath));
                    return;
                }
            }
            idx = abPath.lastIndexOf('.');
            if (idx != -1)
                abPath = abPath.substring(0, idx) + ".csv";
            xls2csv(file, new File(abPath), encode);
        }
    }

    public static void main(String[] args) {
        String encoding = "utf-8";
        if (args.length >= 2) {
            xlsDir = new File(args[0]);
            csvDir = new File(args[1]);
            if (args.length >= 3) {
                encoding = args[2];
            }
        } else {
            xlsDir = new File("csvdir");
            csvDir = new File("xlsdir");
        }
        if (!xlsDir.exists()) {
            System.out.println("No xlsDir=" + args[0] + " found");
            return;
        }
        if (!xlsDir.isDirectory()) {
            System.out.println("xlsDir=" + args[0] + " is not a directory");
            return;
        }
        if (!csvDir.exists()) {
            csvDir.mkdir();
        } else if (!csvDir.isDirectory()) {
            System.out.println("csvDir=" + args[1] + " is not a directory");
            return;
        }
        convertPath(xlsDir, encoding);
    }
}