package cn.com.broadlink.blappsdkdemo.presenter;


import cn.com.broadlink.base.BLBaseResult;

/**
 * Created by YeJin on 2016/5/10.
 */
public interface SPControlListener {
    void deviceStatusShow(int pwr);

    /**开始控制*/
    void controlStart();

    /**结束控制*/
    void controlEnd();

    /**控制成功*/
    void controlSuccess(int pwr);

    /**控制失败*/
    void controlFail(BLBaseResult result);

    /**任务设置成功*/
    void taskSuccess();

    /**任务设置失败*/
    void taskFaile(String msg);
}
