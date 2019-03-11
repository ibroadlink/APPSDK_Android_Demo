package cn.com.broadlink.blappsdkdemo.data;

import java.util.List;

import cn.com.broadlink.base.BLBaseResult;

/**
 * 获取家庭成员
 */
public class DataFamilyMemberList extends BLBaseResult {

    public BLSRoomListInfo data;

    public class BLSRoomListInfo {
        public List<FamilyMember> familymember;
    }
}
