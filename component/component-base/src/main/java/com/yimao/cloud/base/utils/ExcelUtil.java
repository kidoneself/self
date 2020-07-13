package com.yimao.cloud.base.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.system.StockCountDTO;
import com.yimao.cloud.pojo.vo.system.StoreHouseVO;

public class ExcelUtil {
    private ExcelUtil() {

    }

    /**
     * 导出excel头部标题
     *
     * @param title
     * @param cellRangeAddressLength
     * @return
     */
    public static HSSFWorkbook makeExcelHead(String title, int cellRangeAddressLength) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle styleTitle = createStyle(workbook, (short) 16);
        HSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(25);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, cellRangeAddressLength);
        sheet.addMergedRegion(cellRangeAddress);
        HSSFRow rowTitle = sheet.createRow(0);
        HSSFCell cellTitle = rowTitle.createCell(0);
        // 为标题设置背景颜色
        styleTitle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleTitle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cellTitle.setCellValue(title);
        cellTitle.setCellStyle(styleTitle);
        return workbook;
    }

    public static XSSFWorkbook makeExcelHeadX(String title, int cellRangeAddressLength) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFCellStyle styleTitle = createStyle(workbook, (short) 16);
        XSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(25);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, cellRangeAddressLength);
        sheet.addMergedRegion(cellRangeAddress);
        XSSFRow rowTitle = sheet.createRow(0);
        XSSFCell cellTitle = rowTitle.createCell(0);
        // 为标题设置背景颜色
        styleTitle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleTitle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cellTitle.setCellValue(title);
        cellTitle.setCellStyle(styleTitle);
        return workbook;
    }

    public static SXSSFWorkbook makeExcelHeadSXSSF(String title, int cellRangeAddressLength) {
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        CellStyle styleTitle = createStyle(workbook, (short) 16);
        Sheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(25);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, cellRangeAddressLength + 1);
        sheet.addMergedRegion(cellRangeAddress);
        Row rowTitle = sheet.createRow(0);
        Cell cellTitle = rowTitle.createCell(0);
        // 为标题设置背景颜色
        styleTitle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleTitle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cellTitle.setCellValue(title);
        cellTitle.setCellStyle(styleTitle);
        return workbook;
    }

    /**
     * 设定二级标题
     *
     * @param workbook
     * @param secondTitles
     * @return
     */
    public static HSSFWorkbook makeSecondHead(HSSFWorkbook workbook, String[] secondTitles) {
        // 创建用户属性栏
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFRow rowField = sheet.createRow(1);
        HSSFCellStyle styleField = createStyle(workbook, (short) 13);
        for (int i = 0; i < secondTitles.length; i++) {
            HSSFCell cell = rowField.createCell(i);
            cell.setCellValue(secondTitles[i]);
            cell.setCellStyle(styleField);
        }
        return workbook;
    }

    /**
     * 插入数据
     *
     * @param workbook
     * @param dataList
     * @param beanPropertys
     * @return
     */
    public static <T> HSSFWorkbook exportExcelData(HSSFWorkbook workbook, List<T> dataList, String[] beanPropertys, Integer count) {
        try {
            HSSFSheet sheet = workbook.getSheetAt(0);
            // 填充数据
            HSSFCellStyle styleData = workbook.createCellStyle();
            styleData.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            styleData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

            for (int j = 0; j < dataList.size(); j++) {
                HSSFRow rowData = sheet.createRow(j + 2 + count);
                T t = dataList.get(j);
                for (int k = 0; k < beanPropertys.length; k++) {
                    Object value = null;

                    value = BeanUtils.getProperty(t, beanPropertys[k]);

                    HSSFCell cellData = rowData.createCell(k);
                    if (value != null) {
                        cellData.setCellValue(value.toString());
                    } else {
                        cellData.setCellValue("");
                    }
                    cellData.setCellStyle(styleData);
                }
            }
            return workbook;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 插入数据
     *
     * @param workbook
     * @param dataList
     * @param beanPropertys
     * @return
     */
    public static <T> XSSFWorkbook exportExcelDataX(XSSFWorkbook workbook, List<T> dataList, String[] beanPropertys, Integer count) {
        try {
            XSSFSheet sheet = workbook.getSheetAt(0);
            // 填充数据
            XSSFCellStyle styleData = workbook.createCellStyle();
            styleData.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            styleData.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

            for (int j = 0; j < dataList.size(); j++) {
                XSSFRow rowData = sheet.createRow(j + 2 + count);
                T t = dataList.get(j);
                for (int k = 0; k < beanPropertys.length; k++) {
                    Object value = null;

                    value = BeanUtils.getProperty(t, beanPropertys[k]);

                    XSSFCell cellData = rowData.createCell(k);
                    if (value != null) {
                        cellData.setCellValue(value.toString());
                    } else {
                        cellData.setCellValue("");
                    }
                    cellData.setCellStyle(styleData);
                }
            }
            return workbook;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 插入数据
     *
     * @param workbook
     * @param dataList
     * @param beanProperties
     * @return
     */
    public static <T> SXSSFWorkbook exportExcelDataSXSSF(SXSSFWorkbook workbook, List<T> dataList, String[] beanProperties, Integer count) {
        try {
            Sheet sheet = workbook.getSheetAt(0);
            // 填充数据
            CellStyle styleData = workbook.createCellStyle();
            styleData.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            styleData.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

            for (int j = 0; j < dataList.size(); j++) {
                Row rowData = sheet.createRow(j + 2 + count);
                T t = dataList.get(j);

                //创建序号列数据
                Cell cellData = rowData.createCell(0);
                cellData.setCellValue(j + 1);
                cellData.setCellStyle(styleData);

                for (int k = 0; k < beanProperties.length; k++) {
                    Object value = BeanUtils.getProperty(t, beanProperties[k]);
                    cellData = rowData.createCell(k + 1);
                    if (value != null) {
                        cellData.setCellValue(value.toString());
                    } else {
                        cellData.setCellValue("");
                    }
                    cellData.setCellStyle(styleData);
                }
            }
            return workbook;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /***
     * 
     * @param workbook
     * @param dataList 数据集合
     * @param beanProperties  属性
     * @param isFirstBatch 是否第一批数据
     * @return
     */
    public static <T> SXSSFWorkbook exportExcelDataSXSSFPage(SXSSFWorkbook workbook, List<T> dataList, String[] beanProperties,boolean isFirstBatch) {
        try {
            Sheet sheet = workbook.getSheetAt(0);
            // 填充数据
            CellStyle styleData = workbook.createCellStyle();
            styleData.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            styleData.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
            int lastRowNum=sheet.getLastRowNum();
            for (int j = 0; j < dataList.size(); j++) {
                Row rowData = sheet.createRow(isFirstBatch?(j + 2):(++lastRowNum));
                T t = dataList.get(j);

                //创建序号列数据
                Cell cellData = rowData.createCell(0);
                cellData.setCellValue(isFirstBatch?(j+1):(lastRowNum-1));
                cellData.setCellStyle(styleData);

                for (int k = 0; k < beanProperties.length; k++) {
                    Object value = BeanUtils.getProperty(t, beanProperties[k]);
                    cellData = rowData.createCell(k + 1);
                    if (value != null) {
                        cellData.setCellValue(value.toString());
                    } else {
                        cellData.setCellValue("");
                    }
                    cellData.setCellStyle(styleData);
                }
            }
            return workbook;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用批量导入方法时，请注意需要导入的Bean的字段和excel的列一一对应
     *
     * @param clazz
     * @param file
     * @param beanPropertys
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> List<T> parserExcel(Class<T> clazz, MultipartFile file, String[] beanPropertys, int start) throws Exception {
        // 得到workbook
        List<T> list = new ArrayList<T>();

        //Workbook workbook = WorkbookFactory.create(file);
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        // 直接从第start行开始获取数据
        int rowSize = sheet.getPhysicalNumberOfRows();
        //获取标题格数
        int cellSize = sheet.getRow(0).getPhysicalNumberOfCells();
        if (rowSize > 1) {
            for (int i = start; i < rowSize; i++) {
                T t = clazz.newInstance();
                Row row = sheet.getRow(i);
                //有问题int cellSize = row.getPhysicalNumberOfCells();
                if ((ExcelUtil.getCellValue(row.getCell(0)) == null || ExcelUtil.getCellValue(row.getCell(0)) == "") &&
                        (ExcelUtil.getCellValue(row.getCell(1)) == null || ExcelUtil.getCellValue(row.getCell(1)) == "") &&
                        (ExcelUtil.getCellValue(row.getCell(2)) == null || ExcelUtil.getCellValue(row.getCell(2)) == "")) {
                    continue;
                }
                for (int j = 0; j < cellSize; j++) {

                    Object cellValue = getCellValue(row.getCell(j));
                    org.apache.commons.beanutils.BeanUtils.copyProperty(t, beanPropertys[j], cellValue);
                }

                list.add(t);

            }
        }

        return list;

    }

    /**
     * 通用的读取excel单元格的处理方法
     *
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        Object result = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    result = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    // 对日期进行判断和解析
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        double cellValue = cell.getNumericCellValue();
                        result = HSSFDateUtil.getJavaDate(cellValue);
                    } else {
                        //纯数字全部转换为字符串
                        result = new DecimalFormat("#").format(cell.getNumericCellValue());
                    }

                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    result = cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    result = cell.getCellFormula();
                    break;
                case Cell.CELL_TYPE_ERROR:
                    result = cell.getErrorCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * 提取公共的样式
     *
     * @param workbook
     * @param fontSize
     * @return
     */
    private static HSSFCellStyle createStyle(HSSFWorkbook workbook, short fontSize) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 创建一个字体样式
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        return style;
    }

    private static XSSFCellStyle createStyle(XSSFWorkbook workbook, short fontSize) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 创建一个字体样式
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        return style;
    }

    private static CellStyle createStyle(SXSSFWorkbook workbook, short fontSize) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 创建一个字体样式
        Font font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        return style;
    }

    /**
     * 生成excel
     *
     * @param fileName
     * @param response
     * @param workbook
     */
    public static void createExcel(String fileName, HttpServletResponse response, HSSFWorkbook workbook) {
        try {
            response.reset();// 清空输出流
            fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1") + ".xls";
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出格式
     *
     * @param head        头
     * @param title       标题
     * @param strProperty
     * @return
     */
    public static <T> HSSFWorkbook getHSSFWorkbook(String head, String title, String[] strProperty, String[] beanProperty, List<T> ptPage) {
        HSSFWorkbook workbook = makeExcelHead(DateUtil.formatCurrentTime("yyyy年MM月dd日") + head, strProperty.length);
        HSSFSheet sheet = workbook.getSheet(DateUtil.formatCurrentTime("yyyy年MM月dd日") + title);
        //合并单元格
        CellRangeAddress cra;
        for (int i = 0; i < strProperty.length; i++) {
            cra = new CellRangeAddress(1, 2, i, i);
            sheet.addMergedRegion(cra);
        }
        //列
        for (int i = 0; i < strProperty.length + 1; i++) {
            sheet.setColumnWidth(i, 200 * 15);
        }
        //-------------定义公共变量--------------
        HSSFRow row;
        HSSFCell cell;
        int nRow = 1;
        int nCell = 0;

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        row = sheet.createRow(++nRow);
        for (String str : strProperty) {
            cell = row.createCell(nCell++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(str);
        }
        return ExcelUtil.exportExcelData(workbook, ptPage, beanProperty, 1);
    }

    public static boolean exportSXSSF(List resultList, String header, int count, String[] titles, String[] beanPropertys, HttpServletResponse response) {
        try {
            SXSSFWorkbook workbook = ExcelUtil.makeExcelHeadSXSSF(header, count);
            Sheet sheet = workbook.getSheet(header);
            for (int i = 0; i < count; i++) {
                sheet.setColumnWidth(i, 256 * 15);
            }
            //-------------定义公共变量--------------
            Row row;
            Cell cell;
            int nRow = 1;
            int nCell = 0;
            //-------------定义公共变量--------------

            //--------------设置单元格样式--------------
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中

            Font font = workbook.createFont();
            font.setFontName("黑体");
            font.setFontHeightInPoints((short) 12);//设置字体大小
            cellStyle.setFont(font);//选择需要用到的字体格式
            //--------------设置单元格样式--------------

            //--------------创建标题栏-------------
            row = sheet.createRow(nRow++);

            //创建序号列
            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("序号");

            for (int i = 0; i < titles.length; i++) {
                cell = row.createCell(++nCell);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(titles[i]);
            }
            //--------------创建标题栏-------------

            workbook = ExcelUtil.exportExcelDataSXSSF(workbook, resultList, beanPropertys, 0);

            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(header + ".xlsx", "UTF-8"));
            workbook.write(response.getOutputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 云平台仓库特用导出
     */
    public static String exportStoreHouseSXSSF(List resultList, String header, int count, String[] titles) {
        try {
            SXSSFWorkbook workbook = ExcelUtil.makeExcelHeadSXSSF(header, count);
            Sheet sheet = workbook.getSheet(header);
            for (int i = 0; i < count; i++) {
                sheet.setColumnWidth(i, 256 * 15);
            }
            //-------------定义公共变量--------------
            Row row;
            Cell cell;
            int nRow = 1;
            int nCell = 0;
            //-------------定义公共变量--------------

            //--------------设置单元格样式--------------
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中

            Font font = workbook.createFont();
            font.setFontName("黑体");
            font.setFontHeightInPoints((short) 12);//设置字体大小
            cellStyle.setFont(font);//选择需要用到的字体格式
            //--------------设置单元格样式--------------

            //--------------创建标题栏-------------
            row = sheet.createRow(nRow++);

            for (int i = 0; i < titles.length; i++) {
                cell = row.createCell(nCell++);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(titles[i]);
            }
            //--------------创建标题栏-------------

            workbook = ExcelUtil.exportExcelStoreHouseSXSSF(workbook, resultList, 0);

            //上传到ftp服务器
            return SFTPUtil.upload(workbook, header);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("组装Excel出现错误");
        }
    }

    /**
     * 云平台仓库特用--插入数据
     *
     * @param workbook
     * @param dataList
     * @param count
     * @return
     */
    public static SXSSFWorkbook exportExcelStoreHouseSXSSF(SXSSFWorkbook workbook, List<StoreHouseVO> dataList, Integer count) {
        try {
            Sheet sheet = workbook.getSheetAt(0);
            // 填充数据
            CellStyle styleData = workbook.createCellStyle();
            styleData.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            styleData.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

            for (int j = 0; j < dataList.size(); j++) {
                Row rowData = sheet.createRow(2 + count);
                StoreHouseVO storeHouseVO = dataList.get(j);
                int k = 0;
                int var51;
                rowData.createCell(k).setCellValue(j + 1);
                if ("1".equals(storeHouseVO.getPlace())) {
                    var51 = k + 2;
                    rowData.createCell(k + 1).setCellValue("总仓");
                } else {
                    var51 = k + 2;
                    rowData.createCell(k + 1).setCellValue(storeHouseVO.getProvince() + storeHouseVO.getCity() + storeHouseVO.getRegion());
                }

                List<StockCountDTO> counts = storeHouseVO.getCounts();
                if (CollectionUtil.isNotEmpty(counts)) {
                    Iterator var20 = counts.iterator();

                    while (var20.hasNext()) {
                        StockCountDTO stockCount = (StockCountDTO) var20.next();
                        rowData.createCell(var51++).setCellValue(stockCount.getCount());
                    }
                }

                rowData.createCell(var51++).setCellValue(storeHouseVO.getUpdateTime() == null ? "" : DateUtil.dateToString(storeHouseVO.getUpdateTime()));
                ++count;
            }
            return workbook;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String exportToFtp(List resultList, String title, int count, String[] titles, String[] beanProperties) {
        try {
            SXSSFWorkbook workbook = ExcelUtil.makeExcelHeadSXSSF(title, count);
            Sheet sheet = workbook.getSheet(title);
            for (int i = 0; i < count; i++) {
                sheet.setColumnWidth(i, 256 * 15);
            }
            //-------------定义公共变量--------------
            Row row;
            Cell cell;
            int nRow = 1;
            int nCell = 0;
            //-------------定义公共变量--------------

            //--------------设置单元格样式--------------
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中

            Font font = workbook.createFont();
            font.setFontName("黑体");
            font.setFontHeightInPoints((short) 12);//设置字体大小
            cellStyle.setFont(font);//选择需要用到的字体格式
            //--------------设置单元格样式--------------

            //--------------创建标题栏-------------
            row = sheet.createRow(nRow++);

            //创建序号列
            cell = row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("序号");

            for (int i = 0; i < titles.length; i++) {
                cell = row.createCell(++nCell);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(titles[i]);
            }
            //--------------创建标题栏-------------

            workbook = ExcelUtil.exportExcelDataSXSSF(workbook, resultList, beanProperties, 0);
            //上传到ftp服务器
            return SFTPUtil.upload(workbook, title);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("组装Excel出现错误");
        }
    }
    
    /***
     * 导出,支持追加数据
     * @param workbook
     * @param resultList
     * @param title
     * @param count
     * @param titles
     * @param beanProperties
     * @param isFistBatch 是否第一批数据
     * @return
     */
    public static SXSSFWorkbook generateWorkBook(SXSSFWorkbook workbook, List resultList, String title, int count, String[] titles, String[] beanProperties, boolean isFistBatch) {
        try {
        	if(isFistBatch){
	            workbook = ExcelUtil.makeExcelHeadSXSSF(title, count);
	            Sheet sheet = workbook.getSheet(title);
	            for (int i = 0; i < count; i++) {
	                sheet.setColumnWidth(i, 256 * 15);
	            }
	            //-------------定义公共变量--------------
	            Row row;
	            Cell cell;
	            int nRow = 1;
	            int nCell = 0;
	            //-------------定义公共变量--------------
	
	            //--------------设置单元格样式--------------
	            CellStyle cellStyle = workbook.createCellStyle();
	            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
	            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中
	
	            Font font = workbook.createFont();
	            font.setFontName("黑体");
	            font.setFontHeightInPoints((short) 12);//设置字体大小
	            cellStyle.setFont(font);//选择需要用到的字体格式
	            //--------------设置单元格样式--------------
	
	            //--------------创建标题栏-------------
	            row = sheet.createRow(nRow++);
	
	            //创建序号列
	            cell = row.createCell(0);
	            cell.setCellStyle(cellStyle);
	            cell.setCellValue("序号");
	
	            for (int i = 0; i < titles.length; i++) {
	                cell = row.createCell(++nCell);
	                cell.setCellStyle(cellStyle);
	                cell.setCellValue(titles[i]);
	            }
        	}
            //--------------创建标题栏-------------

            return ExcelUtil.exportExcelDataSXSSFPage(workbook, resultList, beanProperties,isFistBatch);
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("组装Excel出现错误");
        }
    }
}
