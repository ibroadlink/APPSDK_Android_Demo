package cn.com.broadlink.blappsdkdemo.data.timer;


import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;

public class BLTimerGetListResultV2 extends BLBaseResult {

    public DataBean data;

    public static class DataBean extends BLBaseTimerResultV2{
        public int index;
        public int total;
        public List<BLTimerInfoV2> timerlist = new ArrayList<>();
        public List<BLTimerInfoV2> delaylist = new ArrayList<>();
        public List<BLTimerInfoV2> periodlist= new ArrayList<>();
        public List<BLCycleTimerInfoV2> cyclelist= new ArrayList<>();
        public List<BLCycleTimerInfoV2> randomlist= new ArrayList<>();
    }
}
