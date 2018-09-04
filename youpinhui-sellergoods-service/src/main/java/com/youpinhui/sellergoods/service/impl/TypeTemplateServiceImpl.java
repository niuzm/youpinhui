package com.youpinhui.sellergoods.service.impl;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youpinhui.mapper.TbSpecificationOptionMapper;
import com.youpinhui.mapper.TbTypeTemplateMapper;
import com.youpinhui.pojo.TbSpecificationOption;
import com.youpinhui.pojo.TbSpecificationOptionExample;
import com.youpinhui.pojo.TbTypeTemplate;
import com.youpinhui.pojo.TbTypeTemplateExample;
import com.youpinhui.pojo.TbTypeTemplateExample.Criteria;
import com.youpinhui.sellergoods.service.TypeTemplateService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);		
		
		//缓存 规格列表和品牌列表
		savetoRedis();
		
		return new PageResult(page.getTotal(), page.getResult());
	}
		
		@Autowired
		private RedisTemplate redisTemplate;
		
		/**
		 * 缓存规格列表和品牌列表
		 */
		private void savetoRedis() {
			List<TbTypeTemplate> typeTemplateList = findAll();
			for (TbTypeTemplate typeTemplate : typeTemplateList) {
				//缓存品牌列表
				List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(),Map.class);
				redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),brandList );
				//缓存规格列表
				List<Map> specList = findSpecList(typeTemplate.getId());
				redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList );
			}
			System.out.println("品牌列表放入缓存");
			System.out.println("规格列表放入缓存");
		}
		
		
		@Autowired
		private TbSpecificationOptionMapper specificationOptionMapper;
		@Override
		public List<Map> findSpecList(Long id) {
			//查询模板
			TbTypeTemplate typeTemplate =typeTemplateMapper.selectByPrimaryKey(id);
			//转换成map类型的集合
			List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(),Map.class);
			for (Map map : list) {
				//查询规格选项列表
				TbSpecificationOptionExample example=new TbSpecificationOptionExample();
				com.youpinhui.pojo.TbSpecificationOptionExample.Criteria createCriteria = example.createCriteria();
				createCriteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
				List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
				map.put("option", options);
			}
			
			return list;
		}
	
}
