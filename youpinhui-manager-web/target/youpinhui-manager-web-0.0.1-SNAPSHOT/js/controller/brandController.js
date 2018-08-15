
	//创建控制器         $scope 视图层与控制层直接交换数据的桥梁
	app.controller("brandController",function($scope,$controller,brandService){
		
		$controller('baseController',{$scope:$scope});
		//查询品牌列表
		$scope.findAll=function(){
			brandService.findAll.success(
				function(response){
					$scope.list=response;
				}		
			);
		}

	
	 
	//分页
	$scope.findPage=function(page,size){	
		brandService.findPage(page,size).success(
			function(response){
				$scope.list=response.rows;//当前页的数据
				$scope.paginationConf.totalItems=response.total;//更新总记录数
				
			}
		);
	}
	

	
	//新增
	$scope.save=function(){
		var object=null;
		if($scope.entity.id!=null){
			object=brandService.update($scope.entity);
		}else{
			object=brandService.add($scope.entity);
		}
		object.success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新
				}else{
					alert(response.message);
				}				
			}		
		);
	}
	
	//查询一个品牌的详细信息
	$scope.findOne=function(id){
		brandService.findOne(id).success(
			function(response){
				$scope.entity=response;
			}
		);
	}
	

	
	
	$scope.dele=function(){
		if($scope.selectIds.length==0){
			alert("请先选择之后再进行操作");
		}else{
		//删除
		if(confirm('确定要删除吗？')){
			brandService.dele($scope.selectIds).success(
					function(response){
						if(response.success){
							$scope.reloadList();//刷新
						}else{
							alert(response.message);
						}						
					}
			);
		}					
		
	}	}
	
	$scope.searchEntity={};
	//条件查询 
	$scope.search=function(page,size){
		
		brandService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope .list=response.rows;//显示当前页数据 	
				$scope.paginationConf.totalItems=response.total;//更新总记录数 
			}		
		);	
		
	}
	
	
	});
	