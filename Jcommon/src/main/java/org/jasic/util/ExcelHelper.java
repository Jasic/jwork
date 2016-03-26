//package org.jasic.util;
//
//
//import com.midea.trade.common.util.DateUtils;
//import net.sf.ehcache.pool.sizeof.ReflectionSizeOf;
//import net.sf.ehcache.pool.sizeof.SizeOf;
//import org.apache.commons.lang.NumberUtils;
//import org.apache.poi.openxml4j.opc.OPCPackage;
//import org.apache.poi.ss.usermodel.DateUtil;
//import org.apache.poi.xssf.eventusermodel.XSSFReader;
//import org.apache.poi.xssf.model.SharedStringsTable;
//import org.apache.poi.xssf.usermodel.XSSFRichTextString;
//import org.springframework.util.StopWatch;
//import org.xml.sax.*;
//import org.xml.sax.helpers.DefaultHandler;
//import org.xml.sax.helpers.XMLReaderFactory;
//
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * /**
// *
// * @Author 菜鹰
// * @Date 2015/7/4
// * @Explain: 工作当中遇到要读取大数据量Excel（10万行以上，Excel 2007），用POI方式读取，用HSSFWorkbook读取时，并会内存溢出
// * 后来参考官方写法，蛋疼
// */
//public class ExcelHelper {
//
//    private List listAll;
//
//    public static final ExcelHelper instance;
//
//    static {
//        instance = new ExcelHelper();
//    }
//
//    private ExcelHelper() {
//        listAll = new ArrayList();
//    }
//
//    /**
//     * xxx 非常 不建议使用全部加载到内存，而是在com.midea.trade.erp.common.util.ExcelHelper#handleRow(int, java.util.List)方法内使用事件驱动
//     *
//     * @param fileName
//     * @return
//     */
//    public synchronized List getListAll(String fileName) {
//        try {
//            processOneSheet(fileName);
//        } catch (Exception e) {
//        }
//        List result = new ArrayList(listAll);
//        listAll = new ArrayList();
//        return result;
//    }
//
//    private void processOneSheet(String filename) throws Exception {
//        OPCPackage pkg = OPCPackage.open(filename);
//        XSSFReader r = new XSSFReader(pkg);
//        SharedStringsTable sst = r.getSharedStringsTable();
//        XMLReader parser = fetchSheetParser(sst);
//        // rId2 found by processing the Workbook
//        // Seems to either be rId# or rSheet#
//        InputStream sheet2 = r.getSheet("rId2");
//        InputSource sheetSource = new InputSource(sheet2);
//        parser.parse(sheetSource);
//        sheet2.close();
//    }
//
//    private void processAllSheets(String filename) throws Exception {
//        OPCPackage pkg = OPCPackage.open(filename);
//        XSSFReader r = new XSSFReader(pkg);
//        SharedStringsTable sst = r.getSharedStringsTable();
//
//        XMLReader parser = fetchSheetParser(sst);
//
//        Iterator<InputStream> sheets = r.getSheetsData();
//        while (sheets.hasNext()) {
//            System.out.println("Processing new sheet:\n");
//            InputStream sheet = sheets.next();
//            InputSource sheetSource = new InputSource(sheet);
//            parser.parse(sheetSource);
//            sheet.close();
//            System.out.println("");
//        }
//    }
//
//    private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
//        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
//        ContentHandler handler = new SheetHandler(sst);
//        parser.setContentHandler(handler);
//        return parser;
//    }
//
//
//    /**
//     * 处理每一行
//     *
//     * @param rowIndex
//     * @param celllist
//     */
//    private void handleRow(int rowIndex, List<String> celllist) {
//        listAll.add(rowIndex, celllist);
//    }
//
//    /**
//     * See org.xml.sax.helpers.DefaultHandler javadocs
//     */
//    private class SheetHandler extends DefaultHandler {
//        private SharedStringsTable sst;
//        private String lastContents;
//        private boolean nextIsString;
//
//        /**
//         * 当前行
//         */
//        private int rowIndex;
//        /**
//         * 当前列
//         */
//        private int columnIndex;
//
//        /**
//         * 用于装载每一行
//         */
//        private List celllist = new ArrayList();
//
//        private SheetHandler(SharedStringsTable sst) {
//            this.sst = sst;
//        }
//
//
//        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
//            // c => cell
//            if (name.equals("c")) {
//                // Print the cell reference
////                System.out.print(attributes.getValue("r") + " - ");
//                // Figure out if the value is an index in the SST
//                String cellType = attributes.getValue("t");
//                if (cellType != null && cellType.equals("s")) {
//                    nextIsString = true;
//                } else {
//                    nextIsString = false;
//                }
//            }
//            // Clear contents cache
//            lastContents = "";
//        }
//
//        public void endElement(String uri, String localName, String name)
//                throws SAXException {
//            // Process the last contents as required.
//            // Do now, as characters() may be called more than once
//            if (nextIsString) {
//                int idx = Integer.parseInt(lastContents);
//                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
//                nextIsString = false;
//            }
//
//            // v => contents of a cell
//            // Output after we've seen the string contents
//            if (name.equals("v")) {
//                Object value = "";
//                String v = lastContents.trim();
//                v = v.equals("") ? " " : v;
//                if (NumberUtils.isNumber(v) && v.indexOf('.') > 0) {
//                    if (DateUtil.isValidExcelDate(Double.parseDouble(v))) {
//                        Date date = DateUtil.getJavaDate(Double.parseDouble(v), false);
//                        value = DateUtils.formatDate(date);
//                    }
//                } else value = v;
//                celllist.add(columnIndex, value);
//                columnIndex++;
//            }
//
//            // 处理行
//            else if (name.equals("row")) {
//                handleRow(rowIndex, celllist);
//                celllist.clear();
//                rowIndex++;
//                columnIndex = 0;
//            }
//        }
//
//        public void characters(char[] ch, int start, int length)
//                throws SAXException {
//            lastContents += new String(ch, start, length);
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        ExcelHelper example = new ExcelHelper();
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
////        example.processOneSheet("C:\\Users\\Caiying\\Desktop\\1.xlsx");
////        example.processAllSheets(args[0]);
//        List result = example.getListAll("C:\\Users\\Caiying\\Desktop\\1.xlsx");
//        stopWatch.stop();
//        System.out.println("处理结束,result总条数:" + result.size());
//        System.out.println(stopWatch.prettyPrint());
//
//
//        SizeOf sizeOf = new ReflectionSizeOf();
//        System.out.println("int大小;" + sizeOf.sizeOf(1));
//        System.out.println("总大小;" + sizeOf.sizeOf(result.get(0)));
//        System.out.println("总大小;" + sizeOf.sizeOf(result.get(1)));
//    }
//}
