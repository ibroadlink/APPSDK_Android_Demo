package cn.com.broadlink.blappsdkdemo.activity.family.result;

import java.util.List;

import cn.com.broadlink.base.BLBaseResult;

public class BLSQueryEndpointListResult extends BLBaseResult {

    private BLSEndpointListInfo data;

    public BLSEndpointListInfo getData() {
        return data;
    }

    public void setData(BLSEndpointListInfo data) {
        this.data = data;
    }

    public class BLSEndpointListInfo {
        private List<BLSEndpointInfo> endpoints;

        public List<BLSEndpointInfo> getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(List<BLSEndpointInfo> endpoints) {
            this.endpoints = endpoints;
        }
    }

}
