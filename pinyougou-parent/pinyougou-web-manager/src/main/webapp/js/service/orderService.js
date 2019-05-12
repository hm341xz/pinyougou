// 定义服务层:
app.service("orderService",function($http){
	this.search = function(currentPage,itemsPerPage){

		return $http.get("../order/search.do?currentPage="+currentPage+"&itemsPerPage="+itemsPerPage);
	}
	

});