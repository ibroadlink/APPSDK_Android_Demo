package cn.com.broadlink.blappsdkdemo.data.timer;

import java.util.ArrayList;
import java.util.List;

public class BLTimerAddOrEditParamV2 extends BLBaseTimerParamV2{
    private List<BLBaseTimerInfoV2> timerlist = new ArrayList<>();

    public List<BLBaseTimerInfoV2> getTimerlist() {
        return timerlist;
    }

    public void setTimerlist(List<BLBaseTimerInfoV2> timerlist) {
        this.timerlist = timerlist;
    }

}
