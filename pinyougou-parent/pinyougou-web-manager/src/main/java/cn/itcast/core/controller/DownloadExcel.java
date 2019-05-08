/*
package cn.itcast.core.controller;

public class DownloadExcel {
}
*/
package cn.itcast.core.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.alibaba.fastjson.JSON;

class DataInfo {
    private String countDate; // 统计日期
    private String channelId; // 渠道号

    public String getCountDate() {
        return countDate;
    }

    public void setCountDate(String countDate) {
        this.countDate = countDate;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
};

public class DownloadExcel {
    public static void main(String[] args) throws IOException {

        // 1 创建新的Excel工作薄
        HSSFWorkbook wb = new HSSFWorkbook();
        //2创建一个工作表,一个sheet
        HSSFSheet sheet = wb.createSheet();
        // 创建单元格样式
        HSSFCellStyle titleCellStyle = wb.createCellStyle();
        // 指定单元格居中对齐，边框为细
        titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        titleCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        // 设置填充色
        titleCellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 指定当单元格内容显示不下时自动换行
        titleCellStyle.setWrapText(true);
        // 设置单元格字体
        HSSFFont titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 12);
        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleCellStyle.setFont(titleFont);
        //创建行
        //3在索引0的位置创建行（最顶端的行)
        HSSFRow headerRow = sheet.createRow(0);
        HSSFCell headerCell = null;
        //HSSFCell.CELL_TYPE_STRING

        String[] titles = { "统计日期", "渠道号" };
        for (int c = 0; c < titles.length; c++) {
            //创建单元格
            //在索引0的位置创建单元格（左上端）
            headerCell = headerRow.createCell(c);
            //定义单元格的style
            headerCell.setCellStyle(titleCellStyle);
            //在单元格中输入一些内容　
            headerCell.setCellValue(titles[c]);
            sheet.setColumnWidth(c, (30 * 160));
        }
        // ------------------------------------------------------------------
        // 创建单元格样式
        HSSFCellStyle cellStyle = wb.createCellStyle();
        // 指定单元格居中对齐，边框为细
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        // 设置单元格字体
        HSSFFont font = wb.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        cellStyle.setFont(font);
        String infoStr = "[{\"channelId\":\"bodao\",\"countDate\":\"2014-06-11\"},"
                + "{\"channelId\":\"dingzhi\",\"countDate\":\"2014-06-12\"},"
                + "{\"channelId\":\"ruiwei\",\"countDate\":\"2014-06-13\"}]";
        List<DataInfo> list = JSON.parseArray(infoStr, DataInfo.class);
        for (int r = 0; r < list.size(); r++) {
            DataInfo item = list.get(r);
            HSSFRow row = sheet.createRow(r + 1);
            HSSFCell cell = null;
            int c = 0;
            cell = row.createCell(c++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(item.getCountDate());
            cell = row.createCell(c++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(item.getChannelId());
        }

        FileOutputStream fileOut = new FileOutputStream("test.xls");
        wb.write(fileOut);
        //flush()这个函数是清空的意思，用于清空缓冲区的数据流，进行流的操作时，
        // 数据先被读到内存中，然后再用数据写到文件中，那么当你数据读完时，
        // 我们如果这时调用close()方法关闭读写流，这时就可能造成数据丢失
        fileOut.flush();
        //操作结束，关闭文件
        fileOut.close();
        System.out.println("Done");
    }
}

