package cn.itcast.core.service;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public SeckillOrder searchOrderFromRedisByUserId(String userId) {
        return (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }
}
