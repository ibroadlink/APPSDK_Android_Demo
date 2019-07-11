package cn.com.broadlink.blappsdkdemo.activity.ble.bean;

/**
 * 电表参数详情
 * 
 * @author JiangYaqiang
 * createrd at 2019/7/11 11:06
 */
public class MeterInfo extends BaseInfo {

    public static final int RELAY_STATE_OPEN = 1;
    public static final int RELAY_STATE_CLOSE = 0;
    
    private double balance;
    private String time;
    private int relayState;
    private double instantaneousActiveImportPower;
    private double activeEnergyImport;

    private int secretKeyVision;
    private int meterMode;
    private double positiveActivePower;
    private double positiveActiveEnergy;
    private double positiveActiveEnergyRate1;
    private double positiveActiveEnergyRate2;
    private double positiveActiveEnergyRate3;
    private double positiveActiveEnergyRate4;
    private double rate;

    public MeterInfo() {

    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getSecretKeyVision() {
        return secretKeyVision;
    }

    public void setSecretKeyVision(int secretKeyVision) {
        this.secretKeyVision = secretKeyVision;
    }

    public int getMeterMode() {
        return meterMode;
    }

    public void setMeterMode(int meterMode) {
        this.meterMode = meterMode;
    }

    public double getPositiveActivePower() {
        return positiveActivePower;
    }

    public void setPositiveActivePower(double positiveActivePower) {
        this.positiveActivePower = positiveActivePower;
    }

    public double getPositiveActiveEnergy() {
        return positiveActiveEnergy;
    }

    public void setPositiveActiveEnergy(double positiveActiveEnergy) {
        this.positiveActiveEnergy = positiveActiveEnergy;
    }

    public double getPositiveActiveEnergyRate1() {
        return positiveActiveEnergyRate1;
    }

    public void setPositiveActiveEnergyRate1(double positiveActiveEnergyRate1) {
        this.positiveActiveEnergyRate1 = positiveActiveEnergyRate1;
    }

    public double getPositiveActiveEnergyRate2() {
        return positiveActiveEnergyRate2;
    }

    public void setPositiveActiveEnergyRate2(double positiveActiveEnergyRate2) {
        this.positiveActiveEnergyRate2 = positiveActiveEnergyRate2;
    }

    public double getPositiveActiveEnergyRate3() {
        return positiveActiveEnergyRate3;
    }

    public void setPositiveActiveEnergyRate3(double positiveActiveEnergyRate3) {
        this.positiveActiveEnergyRate3 = positiveActiveEnergyRate3;
    }

    public double getPositiveActiveEnergyRate4() {
        return positiveActiveEnergyRate4;
    }

    public void setPositiveActiveEnergyRate4(double positiveActiveEnergyRate4) {
        this.positiveActiveEnergyRate4 = positiveActiveEnergyRate4;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRelayState() {
        return relayState;
    }

    public void setRelayState(int relayState) {
        this.relayState = relayState;
    }

    public double getInstantaneousActiveImportPower() {
        return instantaneousActiveImportPower;
    }

    public void setInstantaneousActiveImportPower(double instantaneousActiveImportPower) {
        this.instantaneousActiveImportPower = instantaneousActiveImportPower;
    }

    public double getActiveEnergyImport() {
        return activeEnergyImport;
    }

    public void setActiveEnergyImport(double activeEnergyImport) {
        this.activeEnergyImport = activeEnergyImport;
    }


}
