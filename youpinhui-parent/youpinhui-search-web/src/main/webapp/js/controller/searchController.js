app.controller('searchController',function($scope,searchService){
	
	  $scope.enterEvent = function(e) {
	        var keycode = window.event?e.keyCode:e.which;
	        if(keycode==13){
	            $scope.search();
	        }
	    }
	
	$scope.search=function(){
		searchService.search($scope.searchMap).success(
				function(response){
					$scope.resultMap=response;
				}
		);
	}
	
	
});