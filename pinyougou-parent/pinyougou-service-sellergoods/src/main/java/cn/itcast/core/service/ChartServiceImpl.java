package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.item.ItemCat;
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
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemCatDao itemCatDao;

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
            String nickName = seller.getNickName();
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
            chart.setNickName(nickName);
            list.add(chart);

        }

        return list;
    }

    @Override
    public List<Chart> particularsById(String sellerId) {
        List<Chart> list = new ArrayList<>();

        //查询订单表,创建订单表查询对象
        OrderQuery orderQuery = new OrderQuery();
        //订单状态为2,表示已付款   状态为5,表示交易完成
        orderQuery.createCriteria().andSellerIdEqualTo(sellerId).andStatusEqualTo("2");
        List<Order> orderList = orderDao.selectByExample(orderQuery);
//        if (orderList != null){
//
//        }
        for (Order order : orderList) {
            //根据订单集合查询
            Chart chart = new Chart();

            //获取每个订单的订单号
            Long orderId = order.getOrderId();
            //创建订单详情查询对象
            OrderItemQuery orderItemQuery = new OrderItemQuery();
            orderItemQuery.createCriteria().andOrderIdEqualTo(orderId);
            //调用dao层方法,查询,获得某个商家的某个订单的订单详情对象
            List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
            //遍历订单详情对象,获取商品id
            int oneNum = 1;
            for (OrderItem orderItem : orderItemList) {
                //获得商品id
                Long goodsId = orderItem.getGoodsId();
                //调用goodsDao方法查询一级分类对象
                Goods goods = goodsDao.selectByPrimaryKey(goodsId);
                Long category1Id = goods.getCategory1Id();
                //查询一级分类id对象的名称
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(category1Id);
                String name = itemCat.getName();
                //判断集合中是否已经存在这个名字
                if (list == null || list.size()==0){
                    chart.setOneName(name);
                    chart.setOneNum(oneNum);
                    list.add(chart);
                }else {
//                    for (Chart chart1 : list) {
//                        String oneName = chart1.getOneName();
//                        if (!oneName.equals(name)){
//                            chart.setOneName(name);
//                            chart.setOneNum(oneNum);
//                            list.add(chart);
//                            break;
//                        }else {
//                            //如果已经存在,取出名字为name的
//                            int oneNum1 = chart1.getOneNum();
//                            chart1.setOneNum(oneNum1+1);
//                            //list.add(chart);
//                            //数量加1
//                        }
//                        //oneNum ++;
//                    }
                    //把for循环出来的数据放到一个数组里面
                    List<String> stringList = new ArrayList<>();
                    for (Chart chart1 : list) {
                        String oneName = chart1.getOneName();
                        stringList.add(oneName);
                    }
                    //判断新分类有没有在集合中
                    if (!stringList.contains(name)){
                        chart.setOneName(name);
                        chart.setOneNum(oneNum);
                        list.add(chart);
                        //break;
                    }else {
                        //如果已经存在,取出名字为name的
                        for (Chart chart1 : list) {
                            int oneNum1 = chart1.getOneNum();
                            chart1.setOneNum(oneNum1+1);
                        }
                        //int oneNum1 = chart1.getOneNum();
                        //chart1.setOneNum(oneNum1+1);
                        //list.add(chart);
                        //数量加1
                    }
                }
                //list.add(chart);
            }

        }
        return list;
    }
}
