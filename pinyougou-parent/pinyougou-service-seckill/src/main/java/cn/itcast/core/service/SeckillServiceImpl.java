package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<Item> findItemById(Long goodsId) {
        //创建商品详情查询对象
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(goodsId);

        //查询某商品的全部详情
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        return itemList;
    }

    @Override
    public void applySeckill(SeckillGoods seckillGoods, Long[] selectItemIds) {
        //根据传过来的商品详情id,查询标题,图片,原价格
        for (Long selectItemId : selectItemIds) {
            Item item = itemDao.selectByPrimaryKey(selectItemId);
            //如果申请秒杀的数量,大于库存数量,申请失败
            if (seckillGoods.getNum() > seckillGoods.getStockCount()){
                //获取标题
                seckillGoods.setTitle(item.getTitle());
                //获取图片
                seckillGoods.setSmallPic(item.getImage());
                //获取原价格
                seckillGoods.setPrice(item.getPrice());
                //添加时间
                seckillGoods.setCreateTime(new Date());
                //审核状态0,未审核
                seckillGoods.setStatus("0");
                //保存到数据库
                seckillGoodsDao.insertSelective(seckillGoods);
            }else {
                throw new RuntimeException("申请失败");
            }
        }
    }

    @Override
    public List<SeckillGoods> findList() {
        //先查询redis
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

        if (seckillGoodsList == null || seckillGoodsList.size()==0) {
            //创建天剑查询对象
            SeckillGoodsQuery query = new SeckillGoodsQuery();

            SeckillGoodsQuery.Criteria criteria = query.createCriteria();
            //设置条件//审核通过
            criteria.andStatusEqualTo("1");
            //剩余库存大于0
            criteria.andStockCountGreaterThan(0);
            //开始时间小于等于当前时间
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            //结束时间大于当前时间
            criteria.andEndTimeGreaterThan(new Date());
            seckillGoodsList = seckillGoodsDao.selectByExample(query);
            //将商品列表装入缓存
            System.out.println("将秒杀商品列表装入缓存");
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            }

        }
        return seckillGoodsList;
    }

    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        System.out.println(id);
        return (SeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(id);
    }


    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Override
    public void submitOrder(Long seckillId, String userId) {
        //从缓存中查询秒杀商品
        SeckillGoods seckillGoods =(SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if(seckillGoods==null){
            throw new RuntimeException("商品不存在");
        }
        if(seckillGoods.getStockCount()<=0){
            throw new RuntimeException("商品已抢购一空");
        }
        //扣减（redis）库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        //放回缓存
        redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);
        //如果已经被秒光
        if(seckillGoods.getStockCount()==0){
            //同步到数据库
            seckillGoodsDao.updateByPrimaryKey(seckillGoods);
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
        }
        //保存（redis）订单
        long orderId = idWorker.nextId();
        //创建秒杀订单对象
        SeckillOrder seckillOrder=new SeckillOrder();
        //封装订单id
        seckillOrder.setId(orderId);
        //创建下单时间
        seckillOrder.setCreateTime(new Date());
        //封装秒杀价格
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
        //封装秒杀商品id
        seckillOrder.setSeckillId(seckillId);
        //封装卖家id
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        //封装用户id
        seckillOrder.setUserId(userId);
        //封装支付状态
        seckillOrder.setStatus("0");
        seckillOrderDao.insertSelective(seckillOrder);
        redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
    }


}
