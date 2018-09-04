var app=angular.module("youpinhui",[]);//建立模块

//定义过滤器
app.filter("trustHtml",['$sce',function($sce){
	return function(data){//传入被过滤的内容
		return $sce.trustAsHtml(data);//返回过滤后的内容
	}
}]);
	