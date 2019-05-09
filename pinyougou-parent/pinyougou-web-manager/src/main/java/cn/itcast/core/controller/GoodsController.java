package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.PageResult;
import entity.Result;
import org.apache.activemq.protobuf.BufferOutputStream;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.RichTextString;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.GoodsVo;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 商品管理
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {


    @Reference
    private GoodsService goodsService;
    @Reference
    private ItemCatService itemCatService;
    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsVo vo){

        try {

            //商家ID
            String name = SecurityContextHolder.getContext().getAuthentication().getName();

            vo.getGoods().setSellerId(name);
            goodsService.add(vo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsVo vo){

        try {
            goodsService.update(vo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    //查询分页对象  条件入参
    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows,@RequestBody Goods goods){
        return goodsService.search(page,rows,goods);
    }
    //根据条件查询,并导出excel返回页面下载
    @RequestMapping("/dataToExcel")
    public void dataToExcel(String auditStatus,String goodsName,HttpServletResponse response){

        // 1 创建新的Excel工作薄
        HSSFWorkbook wb = new HSSFWorkbook();
        //2创建一个工作表,一个sheet  ,可以设置sheet的名称
        HSSFSheet sheet = wb.createSheet("数据统计表");
        // 创建单元格样式
        HSSFCellStyle titleCellStyle = wb.createCellStyle();
        //设置单元格样式       指定单元格居中对齐，边框为细
        titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        titleCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置单元格样式        设置填充色
        titleCellStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //设置单元格样式        指定当单元格内容显示不下时自动换行
        titleCellStyle.setWrapText(true);

        //设置单元格样式        设置单元格字体
        HSSFFont titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 12);
        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleCellStyle.setFont(titleFont);

        /*----------------------------------上面是创建表格,并设置第一行单元格的样式-----------------------------------------------*/


        /*```````````````````````````````开始:下面设置表格第一行```````````````````````````````*/
        //创建行  ,列头
        //3在索引0的位置创建行（最顶端的行)
        HSSFRow headerRow = sheet.createRow(0);

        HSSFCell headerCell = null;
        //HSSFCell.CELL_TYPE_STRING
        //excel文件的头 信息                 我们一般情况下是知道的，所以在这里定义一个数组
        String[] titles = { "商品ID", "商品名称","商品价格","一级分类","二级分类","三级分类","状态" };
        for (int c = 0; c < titles.length; c++) {
            //创建单元格
            //在索引0的位置创建单元格（左上端）
            headerCell = headerRow.createCell(c);
            //定义单元格的style
            headerCell.setCellStyle(titleCellStyle);
            //在单元格中输入一些内容　
            headerCell.setCellValue(titles[c]);
            //设置指定列的列宽，256 * 50这种写法是因为width参数单位是单个字符的256分之一
            sheet.setColumnWidth(c, (30 * 160));
        }
        /*```````````````````````````````结束:上面设置表格第一行```````````````````````````````*/


        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~开始:设置其余行的单元格格式~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
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
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~结束:设置其余行的单元格格式~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/


        /* *********************************开始:设置其余行的单元格内容********************************* */
        //获取数据库中的数据
        //查询service层,获取数据
        List<Goods> goodsList = goodsService.dataToExcel(auditStatus,goodsName);
        //查询分类数据
        List<ItemCat> itemCatList = itemCatService.findAll();
        for (int i = 0; i < goodsList.size(); i++) {
            Goods goods1 = goodsList.get(i);
            //创建行单元格
            //在索引1的位置创建单元格
            HSSFRow row = sheet.createRow(i + 1);
            HSSFCell cell = null;
            int c = 0;
            cell = row.createCell(c++);
            //设置表格样式
            cell.setCellStyle(cellStyle);
            //取出查询的内容.放入到表格中
            cell.setCellValue(String.valueOf(goods1.getId()));


            cell = row.createCell(c++);
            //设置表格样式
            cell.setCellStyle(cellStyle);
            //取出查询的内容,.放入到表格中
            cell.setCellValue(goods1.getGoodsName());


            cell = row.createCell(c++);
            //设置表格样式
            cell.setCellStyle(cellStyle);
            //取出查询的内容,.放入到表格中
            cell.setCellValue( goods1.getPrice().doubleValue());


            cell = row.createCell(c++);
            //设置表格样式
            cell.setCellStyle(cellStyle);
            //取出查询的内容,.放入到表格中
            cell.setCellValue(itemCatService.findNameById(goods1.getCategory1Id()));


            cell = row.createCell(c++);
            //设置表格样式
            cell.setCellStyle(cellStyle);
            //取出查询的内容,.放入到表格中
            cell.setCellValue(itemCatService.findNameById(goods1.getCategory2Id()));


            cell = row.createCell(c++);
            //设置表格样式
            cell.setCellStyle(cellStyle);
            //取出查询的内容,.放入到表格中
            cell.setCellValue(itemCatService.findNameById(goods1.getCategory3Id()));


            cell = row.createCell(c++);
            //设置表格样式
            cell.setCellStyle(cellStyle);
            //取出查询的内容,.放入到表格中
            cell.setCellValue(goods1.getAuditStatus().equals("1")?"审核通过":"未审核");
        }

        /* *********************************结束:设置其余行的单元格内容********************************* */

        //设置excel表名
        //设置excel表名
        //FileOutputStream fileOut = new FileOutputStream("test.xls");
        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            //fileOut = new FileOutputStream("test.xls");
            SimpleDateFormat sdforYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //清除buffer缓存
            response.reset();
            //指定下载的文件名
            response.setHeader("Content-Disposition","attachment; filename="+sdforYMDHMS.format(new Date()) + ".xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Pragma","no-cache");
            response.setHeader("Cache-Control","no-cache");
            response.setDateHeader("Expires",0);

            output = response.getOutputStream();
            bufferedOutput = new BufferedOutputStream(output);

            wb.write(bufferedOutput);
            bufferedOutput.flush();
            //flush()这个函数是清空的意思，用于清空缓冲区的数据流，进行流的操作时，
            // 数据先被读到内存中，然后再用数据写到文件中，那么当你数据读完时，
            // 我们如果这时调用close()方法关闭读写流，这时就可能造成数据丢失
            bufferedOutput.close();
            //System.out.println("导出成功");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //操作结束，关闭文件
            if (output != null){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutput != null){
                try {
                    bufferedOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //查询一个商品对象
    @RequestMapping("/findOne")
    public GoodsVo findOne(Long id){
        return goodsService.findOne(id);
    }


    //开始审核
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            goodsService.updateStatus(ids,status);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            goodsService.delete(ids);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
