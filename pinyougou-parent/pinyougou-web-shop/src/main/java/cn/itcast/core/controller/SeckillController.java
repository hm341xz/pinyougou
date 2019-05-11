package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("seckill")
@RestController
public class SeckillController {

    @Reference
    private SeckillService seckillService;

    /**
     * 申请秒杀
     * @param selectItemIds
     * @param seckillGoods
     * @return
     */
    @RequestMapping("applySeckill")
    public Result applySeckill(Long[] selectItemIds, @RequestBody SeckillGoods seckillGoods){

        try {
            //获取登录id
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            seckillGoods.setSellerId(sellerId);
            //调用service层方法
            seckillService.applySeckill(seckillGoods,selectItemIds);
            return new Result(true,"申请成功");
        }catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"申请失败");
        }
    }

    @RequestMapping("findItemById")
    public List<Item> findItemById(Long goodsId){
        return seckillService.findItemById(goodsId);
    }
}
