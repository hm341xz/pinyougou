package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.PayService;
import cn.itcast.core.service.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制层
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    @Reference
    private SeckillOrderService seckillOrderService;



    /**
     * 生成二维码
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative(){
        //获取当前用户
        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        //到redis查询秒杀订单
        SeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        //判断秒杀订单存在
        if(seckillOrder!=null){
            //long fen=  (long)(seckillOrder.getMoney().doubleValue()*100);//金额（分）
            return payService.createNativeSeckill(userId);
        }else{
            return new HashMap();
        }
    }


    //查询支付状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            int x = 0;

            while (true){
                Map<String,String> map = payService.queryPayStatus(out_trade_no);
                //未支付 再查
                if("NOTPAY".equals(map.get("trade_state"))){

                    //睡一会
                    Thread.sleep(3000);
                    x++;
                    if(x > 100){
                        //再次调用 微信服务器Api  关闭订单(同学写了)
                        payService.closeOrder(out_trade_no);
                        return new Result(false,"支付超时");
                        //删除缓存中的订单
//                        payService.deleteRedisOrder(userId);
                    }
                }else{
                    return new Result(true,"支付成功");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }

    }
}