package cn.itcast.core.service;

import cn.itcast.common.utils.PageUtils;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vo.Chart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单管理
 */
@Service
@Transactional
public class BackOrderServiceImpl implements  BackOrderService {

    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    public PageResult search(Integer currentPage, Integer itemsPerPage) {
        List<Chart> list = new ArrayList<>();

        //开始分页  分页小助手
        //PageHelper.startPage(currentPage,itemsPerPage);


        //查询所有商家
        List<Seller> sellerList = sellerDao.selectByExample(null);
        //某一个商家
        for (Seller seller : sellerList) {
            Chart chart = new Chart();

            //获取每个商家的id
            String sellerId = seller.getSellerId();
            //创建订单查询对象
            OrderQuery orderQuery = new OrderQuery();
            //订单状态为2,表示已付款   状态为5,表示交易完成
            orderQuery.createCriteria().andSellerIdEqualTo(sellerId).andStatusEqualTo("2");

            //查询订单表,获取订单id
            List<Order> orderList = orderDao.selectByExample(orderQuery);
            //定义 一个变量,保存每个商家的订单总金额
            double totalMoney = 0;
            //定义一个变量,保存每个商家的订单总数
            int orderNum = 0;
            //某一个商家的所有的订单
            for (Order order : orderList) {
                //获得每个支付订单的支付金额
                BigDecimal payment = order.getPayment();
                totalMoney += payment.doubleValue();

                orderNum ++ ;

            }

            //现在又了商家,和商家获取到的总金额
            chart.setSellerId(sellerId);
            chart.setTotalMoney(totalMoney);
            chart.setOrderNum(orderNum);

            list.add(chart);

        }
        //调用分页工具类,此处PageHelper不能用
        List startPage = PageUtils.startPage(list, currentPage, itemsPerPage);
        return new PageResult((long)list.size(),startPage);
    }
}
