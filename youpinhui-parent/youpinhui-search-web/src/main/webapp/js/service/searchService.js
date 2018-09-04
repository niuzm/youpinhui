app.service("searchService",function($http){
	
	//搜索
	this.search=function(searchmap){
		return $http.post('itemsearch/search.do',searchmap);
	}
	
});