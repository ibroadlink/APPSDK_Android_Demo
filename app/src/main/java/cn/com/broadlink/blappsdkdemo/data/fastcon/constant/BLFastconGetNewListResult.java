package cn.com.broadlink.blappsdkdemo.data.fastcon.constant;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;

public class BLFastconGetNewListResult extends BLBaseResult {
    public DataBean data;
    
    public static class DataBean extends BLBaseFastconResult{
        public int index;
        public int total;
        public List<ItemBean> devlist = new ArrayList<>();
    }
    public static class ItemBean{
        public String pid;
        public String did;
    }
}
