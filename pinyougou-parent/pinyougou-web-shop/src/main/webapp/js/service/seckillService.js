//服务层
app.service('seckillService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.applySeckill=function(seckill,selectItemIds){
		return $http.post('../seckill/applySeckill.do?selectItemIds='+selectItemIds,seckill);
	}

    this.findItemById = function (id) {
		return $http.get('../seckill/findItemById.do?goodsId='+id);
    }

});
