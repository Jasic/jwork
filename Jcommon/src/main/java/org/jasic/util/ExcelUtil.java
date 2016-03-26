//package org.jasic.util;
//
//
//import com.midea.trade.common.util.DateUtils;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.NumberToTextConverter;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StopWatch;
//import org.xml.sax.InputSource;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
///**
// * @Author 菜鹰
// * @Date 2015/7/3
// * @Explain:
// */
//public class ExcelUtil {
//
//
//    public static void main(String[] args) {
//        File file = new File("C:\\Users\\Caiying\\Desktop\\2.xlsx");
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start("全部");
//        new ExcelUtil().getRows(file);
//        stopWatch.stop();
//        stopWatch.start("表头");
//        System.out.println(new ExcelUtil().getHeader(file));
//        stopWatch.stop();
//        System.out.println(stopWatch.prettyPrint());
//    }
//
//    public ExcelUtil() {
//    }
//
//    /**
//     * 获取表头，默认是第一行
//     *
//     * @param file
//     * @return
//     */
//    public static List<String> getHeader(File file) {
//        List<List<String>> rows = getRows(file, false);
//        if (CollectionUtils.isEmpty(rows)) return Collections.emptyList();
//        else
//            return rows.get(0);
//    }
//
//    /**
//     * 获取所有行
//     * |标题1|标题2|标题3|标题4|标题5|标题6|
//     * |行值1|行值2|行值3|行值4|行值5|行值6|
//     *
//     * @param file
//     * @return
//     */
//    public static List<List<String>> getRows(File file) {
//        return getRows(file, true);
//    }
//
//    public static List<List<String>> getRows(File file, boolean all) {
//        FileInputStream fis = null;
//        List list = new ArrayList();
//        try {
//            fis = new FileInputStream(file);
//            Workbook wb = WorkbookFactory.create(fis);
//            Sheet sheet = wb.getSheetAt(0);
//            int lastRowNum;
//
//            InputSource inputSource = new InputSource(fis);
//            if (all) lastRowNum = sheet.getLastRowNum();
//            else lastRowNum = 1;
//
//            for (int i = 0; i < lastRowNum; i++) {
//                List rowList = new ArrayList();
//                list.add(rowList);
//                Row row = sheet.getRow(i);
//                int lastColNum = row.getLastCellNum();
//                for (int index = 0; index < lastColNum; index++) {
//                    Object cellStr;
//                    Cell cell = row.getCell(index);
//
//                    if (cell == null) {// 单元格为空设置cellStr为空串
//                        cellStr = null;
//                    } else {
//                        switch (cell.getCellType()) {
//                            case Cell.CELL_TYPE_BLANK:
//                                cellStr = "";
//                                break;
//                            case Cell.CELL_TYPE_BOOLEAN:
//                                cellStr = String.valueOf(cell.getBooleanCellValue());
//                                break;
//                            case Cell.CELL_TYPE_ERROR:
//                                cellStr = null;
//                                break;
//                            case Cell.CELL_TYPE_FORMULA:
//                                // XXX 暂时不支持格中表
//                                cellStr = "Not support <table in grid> ";
////                            Workbook wbTemp = cell.getSheet().getWorkbook();
////                            CreationHelper crateHelper = wb.getCreationHelper();
////                            FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
////                            cellStr = getCellValue(evaluator.evaluateInCell(cell));
//                                break;
//                            case Cell.CELL_TYPE_NUMERIC:
//                                if (DateUtil.isCellDateFormatted(cell)) {
//                                    Date theDate = cell.getDateCellValue();
//                                    cellStr = DateUtils.formatDate(theDate);
//                                } else {
//                                    cellStr = NumberToTextConverter.toText(cell.getNumericCellValue());
//                                }
//                                break;
//                            case Cell.CELL_TYPE_STRING:
//                                cellStr = cell.getRichStringCellValue().getString();
//                                break;
//                            default:
//                                cellStr = null;
//                        }
//                    }
//                    rowList.add(cellStr);
//                }
//            }
//        } catch (Exception ee) {
//        } finally {
//            try {
//                fis.close();
//            } catch (Exception ee) {
//            }
//        }
//        return list;
//    }
//}
//
