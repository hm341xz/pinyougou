package cn.itcast.core.controller;

import cn.itcast.core.service.ChartService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.Chart;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chart")
public class ChartController {

    @Reference
    private ChartService chartService;

    @RequestMapping("/findChart")
    public List<Chart> findChart(){

        System.out.println("收到请求");
        return chartService.findChart();
    }

    @RequestMapping("/particularsById")
    public List<Chart> particularsById(String sellerId){
        return chartService.particularsById(sellerId);
    }
}
