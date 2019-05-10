package vo;

import java.io.Serializable;
import java.util.List;

/**
 * 存饼状图
 */
public class Chart implements Serializable {
    //卖家的id
    private String sellerId;
    //卖家的名称
    private String nickName;
    //总销售金额
    private double totalMoney;
    //总订单数
    private int orderNum;

    //商品一级分类名称
    private String oneName;
    //订单分类属于在一级分类的数量
    private int oneNum;

    public Chart(String sellerId, double totalMoney, int orderNum, String oneName, int oneNum) {
        this.sellerId = sellerId;
        this.totalMoney = totalMoney;
        this.orderNum = orderNum;
        this.oneName = oneName;
        this.oneNum = oneNum;
    }

    public Chart() {
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getOneName() {
        return oneName;
    }

    public void setOneName(String oneName) {
        this.oneName = oneName;
    }

    public int getOneNum() {
        return oneNum;
    }

    public void setOneNum(int oneNum) {
        this.oneNum = oneNum;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "Chart{" +
                "sellerId='" + sellerId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", totalMoney=" + totalMoney +
                ", orderNum=" + orderNum +
                ", oneName='" + oneName + '\'' +
                ", oneNum=" + oneNum +
                '}';
    }
}
