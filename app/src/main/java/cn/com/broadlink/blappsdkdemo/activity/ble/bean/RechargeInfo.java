package cn.com.broadlink.blappsdkdemo.activity.ble.bean;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/7/9 10:03
 */
public class RechargeInfo extends BaseInfo {
    private double balance;
    private double rechargeValue;

    public RechargeInfo() {
    }

    public double getRechargeValue() {
        return rechargeValue;
    }

    public void setRechargeValue(double rechargeValue) {
        this.rechargeValue = rechargeValue;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
