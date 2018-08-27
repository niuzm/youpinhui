app.controller('baseController',function($scope){
	
	

	//分页控件配置 
	$scope.paginationConf = {
			 currentPage: 1,
			 totalItems: 10,
			 itemsPerPage: 10,
			 perPageOptions: [10, 20, 30, 40, 50],
			 onChange: function(){
			     $scope.reloadList();//重新加载
			 }
	}; 

	//刷新列表
	$scope.reloadList=function(){
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}
	
	
	
	
	$scope.selectIds=[];//用户勾选的id结合
	
	//勾选时添加元素
	$scope.updateSelection=function($event,id){
		if($event.target.checked){
		$scope.selectIds.push(id);
		}else{
			var index=$scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index,1);
		}
	}
	
	
	
	
	//转换json格式数据
	$scope.jsonToString=function(jsonString,key){
		var json=JSON.parse(jsonString);//将json字符串转换为json对象
		var value="";
		for(var i=0;i<json.length;i++){		
			if(i>0){
				value+=","
			}
			value+=json[i][key];			
		}
		return value;
	}

	
	//在list集合中根据某个key的值查询对象
	$scope.searchObjectByKey=function(list,key,keyValue){
		//遍历集合
		for (var i = 0; i < list.length; i++) {
			if(list[i][key]==keyValue){
				return list[i];
			}
		}
		return null;
	}
	
	
});