package cn.itcast.core.service;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 商家管理
 */
@Service
@Transactional
public class SellerServiceImpl implements SellerService {


    @Autowired
    private SellerDao sellerDao;
    //申请入驻
    @Override
    public void add(Seller seller) {

        //加密密码
        seller.setPassword(new BCryptPasswordEncoder().encode(seller.getPassword()));

        //未审核
        seller.setStatus("0");

        //时间
        seller.setCreateTime(new Date());

        //保存
        sellerDao.insertSelective(seller);

    }

    //根据用户名查询一个用户对象
    @Override
    public Seller findOne(String username) {
        return sellerDao.selectByPrimaryKey(username);
    }

    /**
     * 商家审核
     * @param sellerId
     * @param status
     */
    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = sellerDao.selectByPrimaryKey(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }

    /**
     * 条件查询
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        PageHelper.startPage(page, rows);
        SellerQuery sellerQuery = new SellerQuery();
        if (seller != null) {
            SellerQuery.Criteria criteria = sellerQuery.createCriteria();
            if (seller.getStatus() != null && !"".equals(seller.getStatus().trim())) {
                criteria.andStatusEqualTo(seller.getStatus());
            }
            if (seller.getName() != null && !"".equals(seller.getName().trim())) {
                criteria.andNameLike("%" + seller.getName().trim() + "%");
            }
            if (seller.getNickName() != null && !"".equals(seller.getNickName().trim())) {
                criteria.andNickNameLike("%" + seller.getNickName().trim() + "%");
            }
        }
        Page<Seller> p = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        return new PageResult(p.getTotal(), p.getResult());
    }
}
