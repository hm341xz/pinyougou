// 定义服务层:
app.service("chartService",function($http){
	this.findChart = function(){
		return $http.get("../chart/findChart.do");
	};
	

	

});