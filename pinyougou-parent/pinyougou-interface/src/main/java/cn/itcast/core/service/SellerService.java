package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import entity.PageResult;

import java.util.List;

public interface SellerService {
    void add(Seller seller);

    Seller findOne(String username);

    void updateStatus(String sellerId, String status);

    PageResult search(Integer page, Integer rows, Seller seller);

    List<Seller> findAll();
}
