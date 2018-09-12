package com.youpinhui.page.service.impl;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.alibaba.dubbo.config.annotation.Service;
import com.youpinhui.mapper.TbGoodsDescMapper;
import com.youpinhui.mapper.TbGoodsMapper;
import com.youpinhui.mapper.TbItemCatMapper;
import com.youpinhui.page.service.ItemPageService;
import com.youpinhui.pojo.TbGoods;
import com.youpinhui.pojo.TbGoodsDesc;

import freemarker.template.Configuration;
import freemarker.template.Template;
@Service
public class ItemPageServiceImpl implements ItemPageService {

	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	@Value("${pagedir}")
	private String pagedir;
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper catMapper;
	@Override
	public boolean genItemHtml(Long goodsId) {
		Configuration configuration = freeMarkerConfig.getConfiguration();
		
		try {
			Template template = configuration.getTemplate("item.ftl");
			
			//创建数据模型
			Map dataModel=new HashMap<>();
			//1.商品主表数据
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", goods);
			//2.商品扩展表数据
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc", goodsDesc);
			//3.读取商品分类
			String itemCat1 = catMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String itemCat2 = catMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String itemCat3 = catMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			dataModel.put("itemCat1", itemCat1);
			dataModel.put("itemCat1", itemCat2);
			dataModel.put("itemCat1", itemCat3);
			
			
			
			//输出对象
			Writer out=new FileWriter(pagedir+goodsId+".html");
			
			template.process(dataModel, out);
			
			out.close();
			
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
