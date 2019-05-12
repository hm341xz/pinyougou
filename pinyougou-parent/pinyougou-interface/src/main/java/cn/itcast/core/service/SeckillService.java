package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillService {


    List<Item> findItemById(Long goodsId);


    void applySeckill(SeckillGoods seckillGoods, Long[] selectItemIds);

    List<SeckillGoods> findList();

    SeckillGoods findOneFromRedis(Long id);

    void submitOrder(Long seckillId, String userId);
}
