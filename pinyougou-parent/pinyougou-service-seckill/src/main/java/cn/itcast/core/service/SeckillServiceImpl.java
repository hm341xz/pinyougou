package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
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
            if (seckillGoods.getNum() < item.getNum()){
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
                //剩余库存数
                seckillGoods.setStockCount(item.getNum());
                //保存到数据库
                seckillGoodsDao.insertSelective(seckillGoods);
            }else {
                throw new RuntimeException("库存数量不足,申请失败");
            }
        }
    }


}
