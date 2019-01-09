package cn.com.broadlink.blappsdkdemo.activity.Family;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.base.BLBaseHttpAccessor;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.base.BLTrustManager;
import cn.com.broadlink.blappsdkdemo.activity.Family.Params.BLSUpdateFamilyInfoParams;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSEndpointInfo;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSFamilyInfoResult;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSFamilyUpdateResult;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSFamilyListResult;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSQueryEndpointListResult;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSQueryRoomListResult;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSRoomInfo;

public class BLSFamilyHTTP {
    private int httpTimeout = 10 * 1000;
    private String userid = null;
    private String loginsession = null;
    private String licenseid = null;
    private String serverHost = null;

    private static BLSFamilyHTTP instance = null;
    private BLSFamilyHTTP() {

    }
    public static BLSFamilyHTTP getInstance() {
        synchronized (BLSFamilyHTTP.class) {
            if (instance == null) {
                instance = new BLSFamilyHTTP();
            }
        }
        return instance;
    }

    private Map<String, String> generateHead(Map<String, String> head) {

        Map<String, String> mapHead = new HashMap<>();
        mapHead.put("userid", userid);
        mapHead.put("loginsession", loginsession);
        mapHead.put("licenseid", licenseid);
        mapHead.put("language", BLCommonTools.getLanguage());

        long nowTimeInvterval = System.currentTimeMillis();
        String msgId = userid + "-" + String.valueOf(nowTimeInvterval);
        mapHead.put("messageId", msgId);

        if (head != null && !head.isEmpty()) {
            for (String k : head.keySet()) {
                mapHead.put(k, head.get(k));
            }
        }

        return mapHead;
    }

    private String generateRequestURL(String requestPath) {
        return serverHost + requestPath;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLoginsession() {
        return loginsession;
    }

    public void setLoginsession(String loginsession) {
        this.loginsession = loginsession;
    }

    public String getLicenseid() {
        return licenseid;
    }

    public void setLicenseid(String licenseid) {
        this.licenseid = licenseid;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public BLSFamilyUpdateResult AddFamily(BLSUpdateFamilyInfoParams addParam) {

        String params = JSONObject.toJSONString(addParam);
        BLCommonTools.debug("AddFamily Params: " + params);

        String url = generateRequestURL(BLSFamilyConstants.ADD_FAMILY);
        Map<String, String> headMap = generateHead(null);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSFamilyUpdateResult result = JSONObject.parseObject(ret, BLSFamilyUpdateResult.class);
            return result;
        }

        return null;
    }

    public BLSFamilyUpdateResult UpdateFamilyInfo(String familyId, BLSUpdateFamilyInfoParams updateParam) {

        String params = JSONObject.toJSONString(updateParam);
        BLCommonTools.debug("UpdateFamilyInfo Params: " + params);

        String url = generateRequestURL(BLSFamilyConstants.MODIFY_FAMILY);
        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);

        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSFamilyUpdateResult result = JSONObject.parseObject(ret, BLSFamilyUpdateResult.class);
            return result;
        }

        return null;
    }

    public BLSFamilyListResult queryFamilyList() {
        String url = generateRequestURL(BLSFamilyConstants.GET_FAMILY_LIST);

        String params = "{}";
        Map<String, String> headMap = generateHead(null);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSFamilyListResult result = JSONObject.parseObject(ret, BLSFamilyListResult.class);
            return result;
        }

        return null;
    }

    public BLSFamilyInfoResult queryFamilyInfo(String familyId) {

        String url = generateRequestURL(BLSFamilyConstants.GET_FAMILY_BASE_INFO);
        String params = "{}";

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSFamilyInfoResult result = JSONObject.parseObject(ret, BLSFamilyInfoResult.class);
            return result;
        }

        return null;

    }

    public BLBaseResult delFamily(String familyId) {

        String url = generateRequestURL(BLSFamilyConstants.DEL_FAMILY);
        String params = "{}";

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSFamilyUpdateResult result = JSONObject.parseObject(ret, BLSFamilyUpdateResult.class);
            return result;
        }

        return null;
    }

    public BLSQueryRoomListResult queryRoomList(String familyId) {

        String url = generateRequestURL(BLSFamilyConstants.QUERY_ROOM_LIST);
        String params = "{}";

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSQueryRoomListResult result = JSONObject.parseObject(ret, BLSQueryRoomListResult.class);
            return result;
        }

        return null;

    }

    public BLSQueryRoomListResult manageRooms(String familyId, List<BLSRoomInfo> roomInfos) {

        String url = generateRequestURL(BLSFamilyConstants.MANAGE_ROOM);

        JSONObject jParams = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jParams.put("manageinfo", jsonArray);

        for (BLSRoomInfo info : roomInfos) {
            String infoStr = JSONObject.toJSONString(info);
            jsonArray.add(JSONObject.parse(infoStr));
        }

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, jParams.toJSONString().getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSQueryRoomListResult result = JSONObject.parseObject(ret, BLSQueryRoomListResult.class);
            return result;
        }

        return null;

    }

    public BLSQueryEndpointListResult queryEndpointList(String familyId) {

        String url = generateRequestURL(BLSFamilyConstants.QUERY_ENDPOINT_LIST);
        String params = "{}";

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSQueryEndpointListResult result = JSONObject.parseObject(ret, BLSQueryEndpointListResult.class);
            return result;
        }

        return null;

    }

    public BLBaseResult addEndpoint(String familyId, List<BLSEndpointInfo> infos) {
        String url = generateRequestURL(BLSFamilyConstants.ADD_ENDPOINT);

        JSONObject jParams = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jParams.put("endpoints", jsonArray);
        for (BLSEndpointInfo info : infos) {
            String infoStr = JSONObject.toJSONString(info);
            jsonArray.add(JSONObject.parse(infoStr));
        }

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);


        String ret = BLBaseHttpAccessor.post(url, headMap, jParams.toJSONString().getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSQueryEndpointListResult result = JSONObject.parseObject(ret, BLSQueryEndpointListResult.class);
            return result;
        }

        return null;
    }

    public BLBaseResult delEndpoint(String familyId, String endpointId) {

        String url = generateRequestURL(BLSFamilyConstants.QUERY_ENDPOINT_LIST);
        JSONObject jParams = new JSONObject();
        JSONObject jBody = new JSONObject();
        jBody.put("endpointId", endpointId);
        jParams.put("endpoint", jBody);

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, jParams.toJSONString().getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSQueryEndpointListResult result = JSONObject.parseObject(ret, BLSQueryEndpointListResult.class);
            return result;
        }

        return null;


    }


}
