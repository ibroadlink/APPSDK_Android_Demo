package cn.com.broadlink.blappsdkdemo.intferfacer;

/**
 * Created by YeJin on 2016/5/10.
 */
public interface SPControlModel {

    void queryDevStatus(String did);

    void controlDevPwr(String did, int pwr);

    void taskDevSet(String did, String taskModel, String taskData);

}
