// 定义控制器:
app.controller("orderController",function($scope,$controller,$http,orderService,sellerService){
	// AngularJS中的继承:伪继承
	$controller('baseController',{$scope:$scope});
	
	/*搜所*/
    $scope.search = function (currentPage,itemsPerPage) {

        orderService.search(currentPage,itemsPerPage).success(
        	function (response) {
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;
                //$scope.sellerName=
            }
		)
    }

    $scope.sellerList = [];
    // 显示分类:
    $scope.findSellerList = function(){
        //查询所有商品分类
        sellerService.findAll().success(function(response){//respopnse List<ItemCat>
            for(var i=0;i<response.length;i++){
                $scope.sellerList[response[i].sellerId] = response[i].nickName;
            }
        });
    }
});
