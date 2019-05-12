package cn.itcast.core.service;

import cn.itcast.core.pojo.seckill.SeckillOrder;

public interface SeckillOrderService {

    /**
     * 根据用户名查询秒杀订单
     * @param userId
     */
    public SeckillOrder searchOrderFromRedisByUserId(String userId);
}
