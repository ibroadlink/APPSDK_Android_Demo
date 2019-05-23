package cn.com.broadlink.blappsdkdemo.data.timer;

import cn.com.broadlink.base.BLBaseResult;

public class BLBaseTimerResultV2 extends BLBaseResult {
    public DataBean data;

    public static class DataBean extends BLBaseTimerResultV2{
        public String did;
        public int status;
        public int ver;
    }
}
