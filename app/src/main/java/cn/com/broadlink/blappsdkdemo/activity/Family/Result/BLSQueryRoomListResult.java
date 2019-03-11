package cn.com.broadlink.blappsdkdemo.activity.family.result;

import java.util.List;

import cn.com.broadlink.base.BLBaseResult;

public class BLSQueryRoomListResult extends BLBaseResult {

    private BLSRoomListInfo data;

    public BLSRoomListInfo getData() {
        return data;
    }

    public void setData(BLSRoomListInfo data) {
        this.data = data;
    }

    public class BLSRoomListInfo {
        private List<BLSRoomInfo> roomList;

        public List<BLSRoomInfo> getRoomList() {
            return roomList;
        }

        public void setRoomList(List<BLSRoomInfo> roomList) {
            this.roomList = roomList;
        }
    }

}
