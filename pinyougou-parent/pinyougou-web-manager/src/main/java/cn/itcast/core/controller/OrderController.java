package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BackOrderService;
import cn.itcast.core.service.BrandService;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 品牌管理
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    //远程调用 品牌接口
    //成员变量
    @Reference
    private BackOrderService backOrderService;


    //查询分页对象 条件对象
    @RequestMapping("/search")
    public PageResult search( Integer currentPage, Integer itemsPerPage){

        PageResult pageResult = backOrderService.search(currentPage, itemsPerPage);
        return pageResult;
    }



}
