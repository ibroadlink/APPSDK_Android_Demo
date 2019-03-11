package cn.com.broadlink.blappsdkdemo.common;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.List;

import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInftsValueInfo;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;

/**
 * 产品profile 工具类
 * Created by YeJin on 2016/8/30.
 */
public class BLProfileTools {

    private static String queryProfileStrByDid(String did){
        BLProfileStringResult result = BLLet.Controller.queryProfile(did);
        if(result != null &&  result.succeed()){
            return result.getProfile();
        }

        return null;
    }

    public static BLDevProfileInfo queryProfileInfoByDid(String did){
        String profileStr = queryProfileStrByDid(did);
        if(!TextUtils.isEmpty(profileStr)){
            return parseObject(profileStr);
        }

        return  null;
    }

    public static String queryProfileStrByPid(String pid){
        BLProfileStringResult result = BLLet.Controller.queryProfileByPid(pid);
        if(result != null &&  result.succeed()){
            return result.getProfile();
        }

        return null;
    }

    public static BLDevProfileInfo queryProfileInfoByPid(String pid){
        String profileStr = queryProfileStrByPid(pid);
        if(!TextUtils.isEmpty(profileStr)){
            return parseObject(profileStr);
        }

        return  null;
    }

    public static List<Integer> checkOutIn(BLDevProfileInfo curDevProfile, String itf){
        List<BLDevProfileInftsValueInfo> valueInfos = curDevProfile.getSuids().get(0).getIntfValue(itf);
        if(valueInfos !=null && !valueInfos.isEmpty()){
            return valueInfos.get(0).getIn();
        }
        return null;
    }
    
    public static BLDevProfileInfo parseObject(String profileStr){
        
 		if(TextUtils.isEmpty(profileStr)) return null;
        final BLDevProfileInfo blDevProfileInfo = JSON.parseObject(profileStr, BLDevProfileInfo.class);
        return blDevProfileInfo;
    }

}
