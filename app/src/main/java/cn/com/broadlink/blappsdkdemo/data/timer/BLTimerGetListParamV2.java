package cn.com.broadlink.blappsdkdemo.data.timer;

public class BLTimerGetListParamV2 extends BLBaseTimerParamV2{
    private String type;
    private int index;
    private int count;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
