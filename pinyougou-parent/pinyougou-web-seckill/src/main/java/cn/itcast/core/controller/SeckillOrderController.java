package cn.itcast.core.controller;

import cn.itcast.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("seckillOrder")
@RestController
public class SeckillOrderController {

    @Reference
    private SeckillService seckillService;

    @RequestMapping("/submitOrder")
    public Result submitOrder(Long seckillId){
        //获取登录的名字
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("111111111");
        if("anonymousUser".equals(userId)){//如果未登录
            return new Result(false, "用户未登录");
        }
        try {
            //调用服务层
            seckillService.submitOrder(seckillId, userId);
            return new Result(true, "提交成功");
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "提交失败");
        }
    }
}
