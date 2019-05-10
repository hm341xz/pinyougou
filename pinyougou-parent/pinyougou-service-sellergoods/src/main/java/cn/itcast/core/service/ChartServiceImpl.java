package cn.itcast.core.service;

import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vo.Chart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ChartServiceImpl implements ChartService{

    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;

    /**
     * 查询卖家表,根据卖家id
     * 再查询订单表,条件是状态为2
     * 再查询订单详情表的每个商家的订单数量
     * @return
     */
    @Override
    public List<Chart> findChart() {
        List<Chart> list = new ArrayList<>();

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
            //某一个商家的所有的订单
            for (Order order : orderList) {
                //获得每个支付订单的支付金额
                BigDecimal payment = order.getPayment();
                totalMoney += payment.doubleValue();
            }
            //现在又了商家,和商家获取到的总金额
            chart.setSellerId(sellerId);
            chart.setTotalMoney(totalMoney);

            list.add(chart);

        }

        return list;
    }
}
