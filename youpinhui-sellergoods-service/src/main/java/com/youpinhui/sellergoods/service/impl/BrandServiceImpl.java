package com.youpinhui.sellergoods.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youpinhui.mapper.TbBrandMapper;
import com.youpinhui.pojo.TbBrand;
import com.youpinhui.pojo.TbBrandExample;
import com.youpinhui.pojo.TbBrandExample.Criteria;
import com.youpinhui.sellergoods.service.BrandService;

import entity.PageResult;

@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	
	//查询所有品牌列表
	@Override
	public List<TbBrand> findAll() {
		
		return brandMapper.selectByExample(null);
	}
	//分页
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		
		//mybatis分页插件
		PageHelper.startPage(pageNum, pageSize);
		
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		List<TbBrand> result = page.getResult();
		return new PageResult(page.getTotal(), page.getResult());
	}
	
	/**
	 * 增加 
	 * @param brand
	 */
	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);
	}
	
	/**
	 * 根据id查询实体
	 * @param brand
	 */
	@Override
	public TbBrand findOne(Long id) {
		return brandMapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 修改
	 * @param brand
	 */
	@Override
	public void update(TbBrand brand) {
		 brandMapper.updateByPrimaryKey(brand);
	}
	
	

	/**
	 * 删除
	 * @param brand
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}
	
	
	

	/**
	 * 根据条件  品牌分页
	 * @param pageNum  当前页面
	 * @param pageSize 页面大小
	 * @return
	 */
	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		
		//mybatis分页插件
		PageHelper.startPage(pageNum, pageSize);
		//条件
		TbBrandExample example=new TbBrandExample();
		Criteria criteria = example.createCriteria();
		
		if(brand!=null) {
			if(brand.getName()!=null&&brand.getName().length()>0) {
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			if(brand.getFirstChar()!=null&&brand.getFirstChar().length()>0) {
				criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
		}
		
		
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
		List<TbBrand> result = page.getResult();
		
		
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Override
	public List<Map> selectOptionList() {
		// TODO Auto-generated method stub
		return brandMapper.selectOptionList();
	}
	
	

}
