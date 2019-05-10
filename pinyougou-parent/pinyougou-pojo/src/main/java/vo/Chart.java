package vo;

import java.io.Serializable;
import java.util.List;

/**
 * 存饼状图
 */
public class Chart implements Serializable {
    //卖家的id
    private String sellerId;
    //总销售金额
    private double totalMoney;





    public Chart(String sellerId, double totalMoney) {
        this.sellerId = sellerId;
        this.totalMoney = totalMoney;

    }

    public Chart() {
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

    @Override
    public String toString() {
        return "Chart{" +
                "sellerId='" + sellerId + '\'' +
                ", totalMoney=" + totalMoney +
                '}';
    }
}
