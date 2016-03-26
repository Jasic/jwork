package org.jasic.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * ExcelHelper po
 */
public class ExcelHelper {

    private HSSFWorkbook workbook;
    private HSSFSheet sheet;
    private HSSFRow row;
    private HSSFCell cell;
    private HSSFCellStyle cellStyle;
    private HSSFCellStyle headerStyle;

    /**
     * 新建一个Excel文件
     */
    public ExcelHelper() {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("Sheet1");
        this.initStyle();
    }

    /**
     * 新建一个Excel文件
     *
     * @param sheetName
     */
    public ExcelHelper(String sheetName) {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
        this.initStyle();
    }

    /**
     * 根据inputStream创建Excel文件(用于读取Excel数据)
     *
     * @param inputStream
     * @throws java.io.IOException
     */
    public ExcelHelper(InputStream inputStream) throws IOException {
        workbook = new HSSFWorkbook(inputStream);
        sheet = workbook.getSheetAt(0);
    }

    /**
     * 初始化样式
     */
    private void initStyle() {
        //设置表头样式
        headerStyle = workbook.createCellStyle();
        headerStyle.setWrapText(true);
        headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        headerStyle.setFont(font);

        //设置单元格样式
        cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    }

    /**
     * 获取Workbook
     *
     * @return
     */
    public HSSFWorkbook getWorkbook() {
        return workbook;
    }

    /**
     * 获取Sheet
     *
     * @return
     */
    public HSSFSheet getSheet() {
        return sheet;
    }

    /**
     * 获取行
     *
     * @param rownum
     * @return
     */
    public HSSFRow getRow(int rownum) {
        return sheet.getRow(rownum);
    }

    /**
     * 获取单元格
     *
     * @param rownum
     * @param column
     * @return
     */
    public HSSFCell getCell(int rownum, short column) {
        row = sheet.getRow(rownum);
        if (row == null) {
            return null;
        }
        return row.getCell(column);
    }

    /**
     * 获取单元格的值（字符串类型）
     *
     * @param rownum
     * @param column
     * @return
     */
    public String getCellStringValue(int rownum, short column) {
        cell = this.getCell(rownum, column);
        if (cell == null) {
            return null;
        }

        String value = null;
        int type = cell.getCellType();
        if (type == HSSFCell.CELL_TYPE_NUMERIC) {
            BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
            value = decimal.toPlainString();
        } else if (type == HSSFCell.CELL_TYPE_STRING) {
            value = cell.getRichStringCellValue().getString();
        } else if (type == HSSFCell.CELL_TYPE_BLANK) {
            value = "";
        } else if (type == HSSFCell.CELL_TYPE_BOOLEAN) {
            value = String.valueOf(cell.getBooleanCellValue());
        }

        if (value != null) {
            value = value.trim();
        }

        return value;
    }

    /**
     * 获取单元格的值（Integer类型）
     *
     * @param rownum
     * @param column
     * @return
     */
    public Integer getCellIntegerValue(int rownum, short column) {
        cell = this.getCell(rownum, column);
        if (cell == null) {
            return null;
        }

        Integer value = null;
        int type = cell.getCellType();
        if (type == HSSFCell.CELL_TYPE_NUMERIC) {
            BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
            value = decimal.intValue();
        } else if (type == HSSFCell.CELL_TYPE_STRING) {
            String strValue = cell.getRichStringCellValue().getString();
            if (StringUtils.isNumeric(strValue)) {
                value = Integer.parseInt(strValue);
            }
        }

        return value;
    }

    /**
     * 获取单元格的值（Double类型）
     *
     * @param rowNum
     * @param column
     * @return
     */
    public Double getCellNumericValue(int rowNum, short column) {
        cell = this.getCell(rowNum, column);
        if (cell == null) {
            return null;
        }

        Double value = null;
        int type = cell.getCellType();
        if (type == HSSFCell.CELL_TYPE_NUMERIC) {
            value = cell.getNumericCellValue();
        } else if (type == HSSFCell.CELL_TYPE_STRING) {
            String strValue = cell.getRichStringCellValue().getString();
            try {
                value = Double.parseDouble(strValue);
            } catch (NumberFormatException e) {
            }
        }

        return value;
    }

    /**
     * 获取总行数（包括标题）
     *
     * @return
     */
    public int getTotalRowCount() {
        return sheet.getLastRowNum() + 1;
    }

    /**
     * 获取总列数
     * 如果第一行为空，返回-1
     *
     * @return
     */
    public int getTotalCellCount() {
        row = sheet.getRow(0);
        if (row == null) {
            return -1;
        }
        return row.getLastCellNum();
    }

    /**
     * 设置表头
     *
     * @param column
     * @param width
     * @param title
     * @throws Exception
     */
    public void setHeader(short column, int width, String title) throws Exception {
        row = (sheet.getRow(0) == null) ? sheet.createRow(0) : sheet.getRow(0);
        cell = (row.getCell(column) == null) ? row.createCell(column) : row.getCell(column);

        cell.setCellStyle(headerStyle);
        cell.setCellValue(new HSSFRichTextString(title));

        sheet.setDefaultColumnStyle(column, cellStyle);
        sheet.setColumnWidth(column, (short) (width * 1000));
    }

    /**
     * 设置单元格的值
     *
     * @param rownum
     * @param column
     * @param value
     */
    public void setCellValue(int rownum, short column, String value) {
        row = (sheet.getRow(rownum) == null) ? sheet.createRow(rownum) : sheet.getRow(rownum);
        cell = (row.getCell(column) == null) ? row.createCell(column) : row.getCell(column);

        cell.setCellValue(new HSSFRichTextString(value));
    }

    /**
     * 合并单元格
     *
     * @param rowFrom
     * @param colFrom
     * @param rowTo
     * @param colTo
     */
    public void addMergedRegion(int rowFrom, short colFrom, int rowTo, short colTo) {
        sheet.addMergedRegion(new Region(rowFrom, colFrom, rowTo, colTo));
    }

    /**
     * 将Excel输出到OutputStream
     *
     * @param out
     * @throws java.io.IOException
     */
    public void write(OutputStream out) throws IOException {
        workbook.write(out);
    }

}
