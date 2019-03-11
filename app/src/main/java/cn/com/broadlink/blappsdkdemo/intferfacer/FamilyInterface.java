package cn.com.broadlink.blappsdkdemo.intferfacer;

import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfo;

public interface FamilyInterface {

    /**
     * 家庭信息发生变化
     * @param isChanged 家庭信息是否更新
     * @param familyId 家庭唯一ID
     * @param familyVersion 家庭版本
     */
    void familyInfoChanged(Boolean isChanged, String familyId, String familyVersion);

    /**
     * 获取家庭全部信息
     * @param allInfo 家庭全部信息
     */
    void familyAllInfo(BLSFamilyInfo allInfo);
}
