package com.youpinhui.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.youpinhui.pojo.TbItem;
import com.youpinhui.search.service.ItemSearchService;
@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	
	/**
	 * 搜索方法
	 * @param searchMap
	 * @return
	 */
	public Map search(Map searchMap) {
		
		//空格处理
		String keywords=(String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replaceAll(" ",""));
		//返回的map
		Map map=new HashMap();
		
		//1查询列表
		map.putAll(searchList(searchMap));
		//2查询商品分类列表
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		//3查询品格和规格列表;
		String category = (String) searchMap.get("category");
		if(!category.equals("")) {
			map.putAll(searchBrandAndSpecList(category));
		}else {
			if(categoryList.size()>0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		return map;
	}
	
	//查询列表
	private Map searchList(Map searchMap) {
		//返回的map
		Map map=new HashMap();
		
		//高亮选项初始化
		HighlightQuery query=new SimpleHighlightQuery();
		//高亮设置
		HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//高亮域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
		highlightOptions.setSimplePostfix("</em>");//后缀
		query.setHighlightOptions(highlightOptions);//设置高亮选项
		
		
		//1.1关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//1.2按商品分类过滤查询
		if(!"".equals(searchMap.get("category"))){			
			Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//1.3按商品品牌过滤查询
		if(!"".equals(searchMap.get("brand"))){			
			Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//1.4按规格分类过滤查询
		if(searchMap.get("spec")!=null){	
			Map<String, String> specMap=(Map<String, String>) searchMap.get("spec");
			for(String key :specMap.keySet()) {
				Criteria filterCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		
		//1.5按商品价格过滤查询
		if(!"".equals(searchMap.get("price"))){		
			String[] price = ((String) searchMap.get("price")).split("-");
			if(!price[0].equals("0")) {//如果最低价不为0
				Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			if(!price[1].equals("*")) {//如果最高价不为*
				Criteria filterCriteria=new Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		
		//1.6分页
		//页码
		Integer pageNo=(Integer) searchMap.get("pageNo");
		if(pageNo==null) {
			pageNo=1;//初始化页码
		}
		//页面大小
		Integer pageSize=(Integer) searchMap.get("pageSize");
		if(pageSize==null) {
			pageSize=20;//初始化大小
		}
		//起始索引
		query.setOffset((pageNo-1)*pageSize);
		//设置每页记录数
		query.setRows(pageSize);
		
		//1.7价格排序
		
		String sortDirec=(String) searchMap.get("sort");//升降序
		String sortField=(String) searchMap.get("sortField");
		if(sortDirec!=null && !sortDirec.equals("")) {
			if(sortDirec.equals("ASC")) {
				Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
				query.addSort(sort);
			}else if(sortDirec.equals("DESC")) {
				Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
				query.addSort(sort);
			}
			}
		
		
		
		//************ 高亮结果集  ************
		//高亮页对象
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query,TbItem.class);
		//高亮入口集合
		List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
		for (HighlightEntry<TbItem> highlightEntry : highlighted) {
			//高亮列表
			List<Highlight> highlightList = highlightEntry.getHighlights();
			/*for (Highlight highlight : highlightList) {
				List<String> snipplets = highlight.getSnipplets();
			}*/
			
			if(highlightList.size()>0 &&highlightList.get(0).getSnipplets().size()>0) {
				TbItem item = highlightEntry.getEntity();
				item.setTitle(highlightList.get(0).getSnipplets().get(0));
			}
		}
		map.put("rows", page.getContent());
		//总页数
		map.put("totalPages", page.getTotalPages());
		//总记录数
		map.put("total", page.getTotalElements());
		return map;
	}
	
	/**
	 * 查询分类列表  
	 * @param searchMap
	 * @return
	 */
	private  List searchCategoryList(Map searchMap){
		List<String> list=new ArrayList();	
		Query query=new SimpleQuery();		
		//按照关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		//设置分组选项
		GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		//得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		//根据列得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for(GroupEntry<TbItem> entry:content){
			list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中	
		}
		return list;
	}

	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询品牌和规格列表
	 * @param category 商品分类名称
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map=new HashMap<>();
		
		
		//1.根据商品分类名称得到模板id
		
		Long templateId=(Long) redisTemplate.boundHashOps("itemCat").get(category);
		if(templateId!=null) {
			//2.根据模板id获取品牌列表
			List brandList=(List) redisTemplate.boundHashOps("brandList").get(templateId);
			map.put("brandList", brandList);
			//3.根据模板id获取规格列表
			List specList=(List) redisTemplate.boundHashOps("specList").get(templateId);
			map.put("specList", specList);
		}
		
		return map;
	}

	/**
	 * 导入列表
	 * @param list
	 */
	@Override
	public void importList(List list) {
		// TODO Auto-generated method stub
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}
	/**
	 * 删除商品列表
	 * @param goodsIds
	 */
	@Override
	public void deleteByGoodsIds(List goodsIds) {
		SolrDataQuery query=new SimpleQuery();
		Criteria criteria=new Criteria("item_goodsid").in(goodsIds);
		query.addCriteria(criteria);
		
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
	
	
}
