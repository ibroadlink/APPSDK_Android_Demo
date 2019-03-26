package cn.com.broadlink.blappsdkdemo.activity.family.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.base.BLBaseHttpAccessor;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.base.BLTrustManager;
import cn.com.broadlink.blappsdkdemo.activity.family.param.BLSUpdateFamilyInfoParams;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSEndpointInfo;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyCreateByQrResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfoQrResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfoResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyListResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyQrResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyUpdateResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSQueryEndpointListResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSQueryRoomListResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSRoomInfo;
import cn.com.broadlink.blappsdkdemo.activity.family.result.DataQrCode;
import cn.com.broadlink.blappsdkdemo.activity.family.constant.BLSFamilyConstants;
import cn.com.broadlink.blappsdkdemo.data.DataFamilyMemberList;
import cn.com.broadlink.blappsdkdemo.data.FamilyMember;
import cn.com.broadlink.blappsdkdemo.data.ParamDeleteFamilyMember;

public class BLSFamilyManager {
    private int httpTimeout = 10 * 1000;
    private String userid = null;
    private String loginsession = null;
    private String licenseid = null;
    private String serverHost = null;

    private static BLSFamilyManager instance = null;
    private BLSFamilyManager() {

    }
    public static BLSFamilyManager getInstance() {
        synchronized (BLSFamilyManager.class) {
            if (instance == null) {
                instance = new BLSFamilyManager();
            }
        }
        return instance;
    }
    
    public BLSFamilyManager init(String licenseid){
        this.setLicenseid(licenseid);
        this.setServerHost(String.format("https://%s%s", licenseid, BLSFamilyConstants.BASE_DOMAIN));
        return this;
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

    public BLSFamilyUpdateResult updateFamilyInfo(String familyId, BLSUpdateFamilyInfoParams updateParam) {

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
            result.getData().getFamilyInfo().setFamilyid(familyId);
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

    public BLSFamilyQrResult getFamilyQrCode(String familyId) {

        String url = generateRequestURL(BLSFamilyConstants.GET_FAMILY_INVITED_QRCODE);
        String params = "{}";

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSFamilyQrResult result = JSONObject.parseObject(ret, BLSFamilyQrResult.class);
            return result;
        }

        return null;
    }
    
    public BLSFamilyInfoQrResult getFamilyInfoByQrCode(DataQrCode qrCode) {

        String url = generateRequestURL(BLSFamilyConstants.SCAN_FAMILY_INVITED_QRCODE);
        String params = JSON.toJSONString(qrCode);

        Map<String, String> headMap = generateHead(null);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSFamilyInfoQrResult result = JSONObject.parseObject(ret, BLSFamilyInfoQrResult.class);
            return result;
        }

        return null;
    }

    public BLSFamilyCreateByQrResult joinFamilyByQrCode(DataQrCode qrCode) {

        String url = generateRequestURL(BLSFamilyConstants.JOIN_FAMILY_INVITED_QRCODE);
        String params = JSON.toJSONString(qrCode);

        Map<String, String> headMap = generateHead(null);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLSFamilyCreateByQrResult result = JSONObject.parseObject(ret, BLSFamilyCreateByQrResult.class);
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

    public ArrayList<FamilyMember> queryMemberList(String familyId, String owner) {

        String url = generateRequestURL(BLSFamilyConstants.GET_FAMILY_MEMBERS);
        String params = "{}";

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            DataFamilyMemberList baseDataResult = JSONObject.parseObject(ret, DataFamilyMemberList.class);
            
            if(baseDataResult != null && baseDataResult.succeed() && baseDataResult.data!=null){
                ArrayList<FamilyMember>  mFamilymember = (ArrayList<FamilyMember>) baseDataResult.data.familymember;
                for (FamilyMember familyMember : mFamilymember) {
                    if(familyMember.getUserid().equals(owner)){
                        mFamilymember.remove(familyMember);
                        mFamilymember.add(0, familyMember);
                        break;
                    }
                }
               return mFamilymember;
            }
        }
        return null;

    }

    public BLBaseResult delMemberList(String familyId, ParamDeleteFamilyMember familymember) {

        String url = generateRequestURL(BLSFamilyConstants.DEL_FAMILY_MEMBERS);
        String params = JSON.toJSONString(familymember);

        Map<String, String> head = new HashMap<>();
        head.put("familyId", familyId);
        Map<String, String> headMap = generateHead(head);

        String ret = BLBaseHttpAccessor.post(url, headMap, params.getBytes(), httpTimeout, new BLTrustManager());
        if (ret != null) {
            BLBaseResult baseDataResult = JSONObject.parseObject(ret, BLBaseResult.class);
           return baseDataResult;
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

        String url = generateRequestURL(BLSFamilyConstants.DEL_ENDPOINT);
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
