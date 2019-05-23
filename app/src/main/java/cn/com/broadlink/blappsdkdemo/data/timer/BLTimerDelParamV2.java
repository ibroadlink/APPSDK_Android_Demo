package cn.com.broadlink.blappsdkdemo.data.timer;

import java.util.ArrayList;
import java.util.List;

public class BLTimerDelParamV2 extends BLBaseTimerParamV2 {
    private List<BLTimerDelInfoV2> timerlist = new ArrayList<>();

    public List<BLTimerDelInfoV2> getTimerlist() {
        return timerlist;
    }

    public void setTimerlist(List<BLTimerDelInfoV2> timerlist) {
        this.timerlist = timerlist;
    }
}
