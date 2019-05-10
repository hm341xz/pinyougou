// 定义控制器:
app.controller("chartController",function($scope,$controller,$http,chartService){
	// AngularJS中的继承:伪继承
	$controller('baseController',{$scope:$scope});


	$scope.findChart = function () {
		//向后台发送请求
        chartService.findChart().success(function (response) {
            //定义数组
            var sellerIdList=[];
            var totalMoneyList=[];
            var dataList=[];
            //遍历返回的结果
            for(var i=0;i<response.length;i++){
                sellerIdList.push(response[i].sellerId);
                totalMoneyList.push(response[i].totalMoney);
            }
            for(var a=0;a<sellerIdList.length;a++){
                dataList.push({
                    name : sellerIdList[a],
                    value : totalMoneyList[a]
                });
            }
            hanshu(sellerIdList,dataList);

        })
    }

    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('main'));
    // 指定图表的配置项和数据

    function hanshu(sellerIdList,dataList) {
        var option = {
            title : {
                text: '销售饼状图',
                subtext: '品优购',
                x:'center'
            },
            tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: sellerIdList
            },
            series : [
                {
                    name: '销售金额',
                    type: 'pie',
                    radius : '70%',
                    center: ['50%', '60%'],
                    /*-------------------------------------*/

                    data : dataList,

                    /*------------------------------------*/
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    }


});
