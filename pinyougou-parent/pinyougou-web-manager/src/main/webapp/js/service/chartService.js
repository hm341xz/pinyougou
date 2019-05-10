// 定义服务层:
app.service("chartService",function($http){

	this.findChart = function(){
		return $http.get("../chart/findChart.do");
	};
	
	this.particulars = function (sellerId) {
		//alert("1111111111")
		return $http.get("../chart/particularsById.do?sellerId="+sellerId);
    }
	

});