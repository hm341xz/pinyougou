package cn.itcast.core.service;

import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long parentId);

    List<ItemCat> findByParentId();

    ItemCat findOne(Long id);

    List<ItemCat> findAll();

    String findNameById(Long id);


}
