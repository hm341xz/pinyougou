package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vo.SpecificationVo;

import java.util.List;
import java.util.Map;

/**
 * 规格管理
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    //查询分页 条件
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {

        //分页插件
        PageHelper.startPage(page,rows);
        SpecificationQuery specificationQuery = new SpecificationQuery();
        if (specification != null) {
            SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();
            if (specification.getSpecName() != null && !"".equals(specification.getSpecName().trim())) {
                criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
            }
            if (specification.getStatus() != null && !"".equals(specification.getStatus().trim())) {
                criteria.andStatusEqualTo(specification.getStatus().trim());
            }
        }
        //查询分页对象

        Page<Specification> p = (Page<Specification>) specificationDao.selectByExample(specificationQuery);

        return new PageResult(p.getTotal(),p.getResult());
    }

    //添加
    @Override
    public void add(SpecificationVo vo) {
        //规格表  1  返回ID
        specificationDao.insertSelective(vo.getSpecification());
         
        //规格选项表 多
        List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            //设置规格的ID 作为 外键
            specificationOption.setSpecId(vo.getSpecification().getId());
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    //查询一个
    @Override
    public SpecificationVo findOne(Long id) {

        SpecificationVo vo = new SpecificationVo();
        //规格对象
        vo.setSpecification(specificationDao.selectByPrimaryKey(id));
        //规格选项结果集对象
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(id);
        vo.setSpecificationOptionList(specificationOptionDao.selectByExample(query));

        return vo;
    }

    //修改
    @Override
    public void update(SpecificationVo vo) {
        //规格表 修改
        specificationDao.updateByPrimaryKeySelective(vo.getSpecification());

        //规格选项表
        // 1:先删除
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(vo.getSpecification().getId());
        specificationOptionDao.deleteByExample(query);

        //2:再添加
        List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            //设置规格的ID 作为 外键
            specificationOption.setSpecId(vo.getSpecification().getId());
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    //查询所有规格
    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }

    /**
     * 审核
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        //创建商品对象
        Specification specification = new Specification();
        //设置审核状态
        specification.setStatus(status);
        //遍历
        for (Long id : ids) {
            //1:改商品的状态
            specification.setId(id);
            //更新状态
            specificationDao.updateByPrimaryKeySelective(specification);
            //规格选项表
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo(id);
            List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(query);
            for (SpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setStatus(status);
                specificationOptionDao.updateByPrimaryKeySelective(specificationOption);
            }
        }
    }
}
