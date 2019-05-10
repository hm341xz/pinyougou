package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;
import entity.PageResult;

public interface BackOrderService {
    /**
     * 运营商后台订单统计
     * @param currentPage
     * @param itemsPerPage
     * @return
     */
    PageResult search(Integer currentPage, Integer itemsPerPage);
}
