package com.youpinhui.sellergoods.service;
/**
 * 品牌接口
 * @author 39224
 *
 */

import java.util.List;
import java.util.Map;

import com.youpinhui.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	//查找所有品牌
	public List<TbBrand> findAll();
	
	/**
	 * 品牌分页
	 * @param pageNum  当前页面
	 * @param pageSize 页面大小
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);

	
	/**
	 * 增加 
	 * @param brand
	 */
	public void add(TbBrand brand);
	
	
	/**
	 * 根据id查询实体
	 * @param brand
	 */
	public TbBrand findOne(Long id);
	
	
	/**
	 * 修改
	 * @param brand
	 */
	public void update(TbBrand brand);
	
	

	/**
	 * 删除
	 * @param brand
	 */
	public void delete(Long[] ids);
	
	

	/**
	 * 根据条件  品牌分页
	 * @param pageNum  当前页面
	 * @param pageSize 页面大小
	 * @return
	 */
	public PageResult findPage(TbBrand brand,int pageNum,int pageSize);

	/**
	 * 返回下拉列表数据
	 * @return
	 */
	
	public List<Map> selectOptionList();
}
