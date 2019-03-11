package cn.com.broadlink.blappsdkdemo.intferfacer;

import java.util.List;

import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfo;

public interface FamilyListInterface {
    void queryFamilyBaseInfoList(List<BLSFamilyInfo> list);
}
