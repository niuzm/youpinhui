package com.youpinhui.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youpinhui.mapper.TbBrandMapper;
import com.youpinhui.mapper.TbGoodsDescMapper;
import com.youpinhui.mapper.TbGoodsMapper;
import com.youpinhui.mapper.TbItemCatMapper;
import com.youpinhui.mapper.TbItemMapper;
import com.youpinhui.mapper.TbSellerMapper;
import com.youpinhui.pojo.TbBrand;
import com.youpinhui.pojo.TbGoods;
import com.youpinhui.pojo.TbGoodsDesc;
import com.youpinhui.pojo.TbGoodsExample;
import com.youpinhui.pojo.TbGoodsExample.Criteria;
import com.youpinhui.pojo.TbItem;
import com.youpinhui.pojo.TbItemCat;
import com.youpinhui.pojo.TbItemExample;
import com.youpinhui.pojo.TbSeller;
import com.youpinhui.pojogroup.Goods;
import com.youpinhui.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbGoodsDescMapper descMapper;
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		System.out.println("-----");
		System.out.println("-----");
		System.out.println(goods.getGoods().getIsEnableSpec()+"   ---");
		//状态未审核
		goods.getGoods().setAuditStatus("0");
		//插入商品的基本信息
		goodsMapper.insert(goods.getGoods());
		//获取商品的id
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		//插入商品扩展信息
		descMapper.insert(goods.getGoodsDesc());
		
		insertItemList(goods);
		
	}
	//保存sku数据
	private void insertItemList(Goods goods) {

		if("1".equals(goods.getGoods().getIsEnableSpec())) {
		//获取itemList
		List<TbItem> itemList = goods.getItemList();
		for (TbItem tbItem : itemList) {
			//构建标题 SPU名称+规格选项值
			String topic=goods.getGoods().getGoodsName();
			Map<String,Object> map = JSON.parseObject(tbItem.getSpec());
			for(String key:map.keySet()){
				topic+=map.get(key);
			}
			tbItem.setTitle(topic);
			

			//商品分类
			tbItem.setCategoryid(goods.getGoods().getCategory3Id());
			
			//创建日期
			tbItem.setCreateTime(new Date());
			
			//更新日期
			tbItem.setUpdateTime(new Date());
			
			//商品id
			tbItem.setGoodsId(goods.getGoods().getId());
			//商家id
			tbItem.setSellerId(goods.getGoods().getSellerId());
			
			//分类名称
			TbItemCat itemcat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());			
			tbItem.setCategory(itemcat.getName());
			
			//品牌名称
			TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
			tbItem.setBrand(brand.getName());
			
			//商家名称
			TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
			tbItem.setSeller(seller.getNickName());
			//图片
			List<Map> imgList = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
			if(imgList.size()>0) {
				tbItem.setImage((String)imgList.get(0).get("url"));
			}
			
			itemMapper.insert(tbItem);
		}
	}else {
			
			TbItem item=new TbItem();
			//标题
			item.setTitle(goods.getGoods().getGoodsName());
			//价格
			item.setPrice(goods.getGoods().getPrice());
			item.setNum(99999);//库存数量
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setSpec("{}");//规格
			

			//商品分类
			item.setCategoryid(goods.getGoods().getCategory3Id());
			
			//创建日期
			item.setCreateTime(new Date());
			
			//更新日期
			item.setUpdateTime(new Date());
			
			//商品id
			item.setGoodsId(goods.getGoods().getId());
			//商家id
			item.setSellerId(goods.getGoods().getSellerId());
			
			//分类名称
			TbItemCat itemcat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());			
			item.setCategory(itemcat.getName());
			
			//品牌名称
			TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
			item.setBrand(brand.getName());
			
			//商家名称
			TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
			item.setSeller(seller.getNickName());
			//图片
			List<Map> imgList = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
			if(imgList.size()>0) {
				item.setImage((String)imgList.get(0).get("url"));
			}
			 itemMapper.insert(item);
		}
	}
	
	private void setItemValues(TbItem tbItem,Goods goods) {

	}
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//基本表
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//扩展表
		descMapper.updateByPrimaryKey(goods.getGoodsDesc());
		
		//删除原有SKU表的数据
		TbItemExample example=new TbItemExample();
		com.youpinhui.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		
		//新增SKU数据
		insertItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods=new Goods();
		//商品基本表
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		//商品扩展表
		TbGoodsDesc goodsDesc = descMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(goodsDesc);
		
		//SKU数据
		TbItemExample example=new TbItemExample();
		com.youpinhui.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");//逻辑删除
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();//指定条件为未删除的商品
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
							criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	/**
	 * 更新商品状态
	 * @param ids
	 * @param status
	 */
	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
	}

	@Override
	public void setMarketableStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsMarketable(status);//设置上下架状态
			goodsMapper.updateByPrimaryKey(goods);
		}
	}
	
	/**
	 * 根据SPU的id集合和状态查询SKU列表
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIdListAndStatus(Long[] goodsIds,String status){
		
		//条件
		TbItemExample example=new TbItemExample();
		com.youpinhui.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status);//已审核
		criteria.andGoodsIdIn(Arrays.asList(goodsIds));//SPU  ID集合
	
		
		return 	itemMapper.selectByExample(example);
		
	}
	
}
