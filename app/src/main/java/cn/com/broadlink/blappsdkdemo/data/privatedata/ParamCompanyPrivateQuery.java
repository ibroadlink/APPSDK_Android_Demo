package cn.com.broadlink.blappsdkdemo.data.privatedata;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2020/1/17 9:50
 */
public class ParamCompanyPrivateQuery {
    public List<String> mtagList;

    public ParamCompanyPrivateQuery() {
        mtagList = new ArrayList<>();
        mtagList.add("device_info_v1");
    }

    public ParamCompanyPrivateQuery(String did) {
        mtagList = new ArrayList<>();
        mtagList.add(String.format("device_info_v1_%s", did));
    }
    
}
