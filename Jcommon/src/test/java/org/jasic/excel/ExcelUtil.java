package org.jasic.excel;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jasic.util.CollectionUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Author 菜鹰
 * @Date 2015/7/3
 * @Explain:
 */
public class ExcelUtil {
    private FileOutputStream fos = null;
    private FileInputStream fis = null;
    private Workbook workbook = null;
    private Sheet sheet = null;
    private int rowIndex = 0;
    private String[] header = {"Id", "Name", "Age", "Date", "Tel", "Address"};

    public static void main(String[] args) {
        File file = new File("C:\\Users\\Caiying\\Desktop\\1.xlsx");
//        System.out.println(new ExcelUtil().getRows(file));
        System.out.println(new ExcelUtil().getHeader(file));
    }

    public ExcelUtil() {
    }

    public void InitExcel(String fileName) {
        try {
            workbook = new HSSFWorkbook();
            fos = new FileOutputStream(fileName);
            sheet = workbook.createSheet("result");
            Row headerRow = sheet.createRow(rowIndex);
            rowIndex++;
            for (short i = 0; i < header.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void WriteRows(HashMap hash) {
        try {

            Row row = sheet.createRow(rowIndex);
            rowIndex++;
            Cell cell = null;
            int index = 0;

            cell = row.createCell(index++);
            cell.setCellValue(hash.get("ID") + "");

            cell = row.createCell(index++);
            cell.setCellValue(hash.get("Name") + "");

            cell = row.createCell(index++);
            cell.setCellValue(hash.get("Age") + "");

            cell = row.createCell(index++);
            cell.setCellValue(hash.get("Date") + "");
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            if (hash.get("Date") != null)
                cell.setCellValue(hash.get("Date") + "");
            else
                cell.setCellType(Cell.CELL_TYPE_BLANK);

            cell = row.createCell(index++);
            cell.setCellValue(hash.get("Tel") + "");

            cell = row.createCell(index++);
            cell.setCellValue(hash.get("Address") + "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reader(String fileName) {
        try {
            fis = new FileInputStream(fileName);
            Workbook wb = WorkbookFactory.create(fis);
            Sheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 0; i < lastRowNum; i++) {
                Row row = sheet.getRow(i);
                int lastColNum = row.getLastCellNum();
                for (int index = 0; index < lastColNum; index++) {
                    Object cellStr;
                    Cell cell = row.getCell(index);

                    if (cell == null) {// 单元格为空设置cellStr为空串
                        cellStr = "";
                    } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {// 对布尔值的处理
                        cellStr = String.valueOf(cell.getBooleanCellValue());
                    } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {// 对数字值的处理
                        cellStr = cell.getNumericCellValue() + "";
                    } else {// 其余按照字符串处理
                        cellStr = cell.getStringCellValue();
                    }
                    System.out.printf(cellStr + "| ");
                }
                System.out.println();
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            fis = null;
        }
    }

    /**
     * 获取表头，默认是第一行
     * @param file
     * @return
     */
    public List<String> getHeader(File file) {
        List rows = getRows(file);
        if (CollectionUtil.isEmpty(rows)) return Collections.emptyList();
        else
            return getRows(file).get(0);
    }


    /**
     * 获取所有行
     *
     * @param file
     * @return
     */
    public List<List<String>> getRows(File file) {
        List list = new ArrayList();
        try {
            fis = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fis);
            Sheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 0; i < lastRowNum; i++) {
                List rowList = new ArrayList();
                list.add(rowList);
                Row row = sheet.getRow(i);
                int lastColNum = row.getLastCellNum();
                for (int index = 0; index < lastColNum; index++) {
                    Object cellStr;
                    Cell cell = row.getCell(index);

                    if (cell == null) {// 单元格为空设置cellStr为空串
                        cellStr = "";
                    } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {// 对布尔值的处理
                        cellStr = String.valueOf(cell.getBooleanCellValue());
                    } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {// 对数字值的处理
                        cellStr = cell.getNumericCellValue() + "";
                    } else {// 其余按照字符串处理
                        cellStr = cell.getStringCellValue().trim();
                    }
                    rowList.add(cellStr);
                }
            }
        } catch (Exception ee) {
        } finally {
            try {
                fis.close();
            } catch (Exception ee) {
            }
            fis = null;
        }
        return list;
    }

    public void finish() {
        try {
            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            fos = null;
        }
    }
}

