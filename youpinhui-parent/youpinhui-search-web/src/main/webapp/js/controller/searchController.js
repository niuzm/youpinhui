app.controller('searchController',function($scope,$location,searchService){
	
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':25,'sort':'','sortField':''};
	
	//enter
	  $scope.enterEvent = function(e) {
	        var keycode = window.event?e.keyCode:e.which;
	        if(keycode==13){
	            $scope.search();
	        }
	    }
	//搜索方法
	$scope.search=function(){
		$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(
				function(response){
					$scope.resultMap=response;
					buildPageLable();//构建分页栏
				}
		);
	}
	
	
	//构建分页栏
	buildPageLable=function(){
		var startPage=1;//开始页码
		var endPage=$scope.resultMap.totalPages;//结束页码
		
		$scope.startDot=true;
		$scope.endDot=true;
		
		
		if($scope.resultMap.totalPages>5){//总页面大于5
			if($scope.searchMap.pageNo<=3){
				endPage=5;//当前页码小于等于3 显示前五页
				$scope.startDot=false;
			}else if($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2){
				// 显示末尾五页
				startPage=$scope.resultMap.totalPages-4;
				$scope.endDot=false;
			}else{
				//显示中间五页
				startPage=$scope.searchMap.pageNo-2;
				endPage=$scope.searchMap.pageNo+2;
				
			}
		}else{
			$scope.startDot=false;
			$scope.endDot=false;
			
		}
		$scope.pageLable=[];
		for (var i = startPage; i <=endPage; i++) {
			$scope.pageLable.push(i);
		}
	}
	
	
	//添加搜索项
	$scope.addSearchItem=function(key,value){
		
		
		if(key=='category'||key=='brand' || key=='price'){
			//分类或品牌操作
			$scope.searchMap[key]=value;
		}else{
			//规格操作
			$scope.searchMap.spec[key]=value;
		}
		
		$scope.search();
	}
	
	//移除搜索项
	$scope.removeSearchItem=function(key){
		
		if(key=='category'||key=='brand'|| key=='price'){
			//分类或品牌操作
			$scope.searchMap[key]="";
		}else{
			//规格操作
			delete $scope.searchMap.spec[key];
		}
		$scope.search();
		
	}
	
	//分页查询
	$scope.queryByPage=function(pageNo){
		if(pageNo<1 || pageNo> $scope.resultMap.totalPges){
			return;
		}
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	
	//判断当前页是否是第一页
	$scope.isStartPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}
	}
	//判断当前页是否是第一页
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	//排序查询
	$scope.sortSearch=function(sortField,sort){
		$scope.searchMap.sortField=sortField;
		$scope.searchMap.sort=sort;
		
		$scope.search();
	}
	
	// 判断关键字是否是品牌
	$scope.keywordsIsBrand=function(){
		for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
	
	//获取关键字
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords=$location.search()['keywords'];
		$scope.search();
	}
	
});