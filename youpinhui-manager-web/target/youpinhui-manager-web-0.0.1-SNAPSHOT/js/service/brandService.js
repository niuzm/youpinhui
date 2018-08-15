
	//品牌服务
	app.service("brandService",function($http){
		this.findAll=function(){
			return $http.get("../brand/findAll.do");
		}
		//品牌分页查询
		this.findPage=function(page,size){
			return $http.get('../brand/findPage.do?page='+page+'&size='+size);
		}
		//查询一个品牌的详细信息
		this.findOne=function(id){
			return $http.get('../brand/findOne.do?id='+id);
		}
		//添加品牌
		this.add=function(entity){
			return $http.post('../brand/add.do',entity);
		}
		
		//修改品牌
		this.update=function(entity){
			return $http.post('../brand/update.do',entity);
		}
		
		//删除品牌
		this.dele=function(ids){
			return $http.get('../brand/delete.do?ids='+ids);
		}
		//搜索
		this.search=function(page,size,searchEntity){
			return $http.post('../brand/search.do?page='+page +'&size='+size,searchEntity);
		}
		//下拉列表数据
		this.selectOptionList=function(){
			return $http.post('../brand/selectOptionList.do');
		}
		
		
	});	