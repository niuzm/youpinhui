app.controller('searchController',function($scope,searchService){
	
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':''};
	
	//enter
	  $scope.enterEvent = function(e) {
	        var keycode = window.event?e.keyCode:e.which;
	        if(keycode==13){
	            $scope.search();
	        }
	    }
	//搜索方法
	$scope.search=function(){
		searchService.search($scope.searchMap).success(
				function(response){
					$scope.resultMap=response;
				}
		);
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
	
	
	
});