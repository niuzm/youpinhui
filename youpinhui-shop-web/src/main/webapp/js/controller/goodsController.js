 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,
		uploadService,goodsService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id=$location.search()['id'];
		if(id==null){
			return ;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;	
				//富文本框赋值
				editor.html($scope.entity.goodsDesc.introduction);//商品介绍
				
				//商品图片
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				
				//扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
			
				//规格选择
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
			
				//转换sku列表中的规格对象
				for (var i = 0; i < $scope.entity.itemList.length; i++) {
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}
	
	
	
	
	//保存 
	$scope.save=function(){		
		//把富文本编辑器的内容给实体
		$scope.entity.goodsDesc.introduction=editor.html();
		
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert(response.message);
					location.href="goods.html";

				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	
	/**
	 * 上传图片
	 */
	$scope.uploadFile=function(){	  
		uploadService.uploadFile().success(function(response) {        	
        	if(response.success){//如果上传成功，取出url
        		$scope.image_entity.url=response.message;//设置文件地址
        	}else{
        		alert(response.message);
        	}
        }).error(function() {           
        	     alert("上传发生错误");
        });        
    };    
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
    
    
    //将当前上传的图片entity存入图片列表
    $scope.add_image_entity=function(){
    	$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    
    //将当前图片entity从图片列表移除
    $scope.remove_image_entity=function(index){
    	$scope.entity.goodsDesc.itemImages.splice(index,1);
    }
    
    //查询一级商品分类列表
    $scope.selectItemCat1List=function(){
    	$scope.entity.goods.typeTemplateId=null;
    	itemCatService.findByParentId(0).success(
    		function(response){
    			$scope.itemCat1List=response;
    		}
    	);
    }
   
    //变量监控方法
    $scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
    	$scope.entity.goods.typeTemplateId=null;
    	$scope.itemCat3List=null;
    	 //查询二级商品分类列表 
    	    	itemCatService.findByParentId(newValue).success(
    	    		function(response){
    	    			$scope.itemCat2List=response;
    	    		}
    	    	);
    });
    
    //变量监控方法
    $scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
    	$scope.entity.goods.typeTemplateId=null;
    	 //查询三级商品分类列表 
    	    	itemCatService.findByParentId(newValue).success(
    	    		function(response){
    	    			$scope.itemCat3List=response;
    	    		}
    	    	);
    });
   
    //变量监控方法 
    $scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
    	
    	 // 读取模板id
    	itemCatService.findOne(newValue).success(
    		function(response){
    			$scope.entity.goods.typeTemplateId=response.typeId;
    		}
    	);
    });
    
    
    
    //变量监控方法 
    $scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
    	
    	 // 读取模板id后读取品牌列表
    	typeTemplateService.findOne(newValue).success(
    		function(response){
    			$scope.typeTemplate=response;
    			//品牌列表转换成json数据
    			$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
    			if($location.search()['id']==null){
    				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
    			}
    			//console.log($scope.entity.goodsDesc.customAttributeItems);
    		}
    	);
    	
    	// 模板列表
    	typeTemplateService.findSpecList(newValue).success(
    		function(response){
    			$scope.specList=response;
    		}
    	);
    	
    });
 
    
    $scope.updateSpecAttribute=function($event,name,value){
    	//查询是否是第一次添加
    	var obj=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
    	if(obj!=null){
    		if($event.target.checked){//勾选
    		obj.attributeValue.push(value);
    		}else{//取消勾选
    			obj.attributeValue.splice(obj.attributeValue.indexOf(obj),1);
    			//选项都取消的时候，删除本条
    			if(obj.attributeValue.length==0){
    				$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1);
    			}
    		}
    	}else{
    		$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
    	}
    }
    
    //创建SKU列表
    $scope.createItemList=function(){
    	//列表初始化
    	$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];
    	
    	var items=$scope.entity.goodsDesc.specificationItems;
    	
    	for(var i=0;i<items.length;i++){
    		$scope.entity.itemList=addCoulumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
    	}
    } 
    
    //添加属性
    addCoulumn=function(list,columnName,columnValues){
    	var newList=[];
    	
    	for(var i=0;i<list.length;i++){
    		var oldRow=list[i];
    		for(var j=0;j<columnValues.length;j++){
    			var newRow=JSON.parse(JSON.stringify(oldRow));//深克隆
    			newRow.spec[columnName]=columnValues[j];
    			newList.push(newRow);
    		}
    	}
    	
    	return newList;
    }
    $scope.status=['未审核','已审核','审核未通过','已关闭'];
    
    $scope.itemCatList=[];//商品分类列表
    //加载商品分类列表
    $scope.findItemCatList=function(){
    	itemCatService.findAll().success(
    		function(response){
    			for (var i = 0; i < response.length; i++) {
    				$scope.itemCatList[response[i].id]=response[i].name;
				}
    		}
    	);
    }
  

    
    $scope.checkAttributeValue=function(specName,optionName){
    	var items=$scope.entity.goodsDesc.specificationItems;
    	var obj=$scope.searchObjectByKey(items,'attributeName',specName);
    	if(obj!=null){
    		if(obj.attributeValue.indexOf(optionName)>=0){
    			return true;
    		}else{
    			return false;
    		}
    	}else{
    		return false;
    	}
    	return true;
    }
    
    
    
});	
