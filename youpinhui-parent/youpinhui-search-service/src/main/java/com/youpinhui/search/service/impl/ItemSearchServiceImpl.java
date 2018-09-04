package com.youpinhui.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
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
		//返回的map
		Map map=new HashMap();
		
		//1查询列表
		map.putAll(searchList(searchMap));
		//2查询商品分类列表
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		return map;
	}
	
	//查询列表
	private Map searchList(Map searchMap) {
		//返回的map
		Map map=new HashMap();
		//高亮显示
		HighlightQuery query=new SimpleHighlightQuery();
		//高亮设置
		HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//高亮域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
		highlightOptions.setSimplePostfix("</em>");//后缀
		
		query.setHighlightOptions(highlightOptions);//设置高亮选项
		
		//关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
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

}
