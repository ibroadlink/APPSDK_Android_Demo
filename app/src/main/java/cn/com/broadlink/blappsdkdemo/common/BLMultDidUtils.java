package cn.com.broadlink.blappsdkdemo.common;

import cn.com.broadlink.base.BLConfigParam;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.result.controller.BLPassthroughResult;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;
import cn.com.broadlink.sdk.result.controller.BLSubdevResult;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/5/8 14:49
 */
public class BLMultDidUtils {
    
    public static BLStdControlResult dnaControl(String did, String sDid, BLStdControlParam stdControlParam) {
        final String[] cachedIdentifiers = getCachedIdentifier(did, sDid);
        return BLLet.Controller.dnaControl(cachedIdentifiers[0], cachedIdentifiers[1], stdControlParam);
    }
    public static BLStdControlResult dnaControl(BLDNADevice device, BLStdControlParam stdControlParam) {
        final String[] cachedIdentifiers = getCachedIdentifier(device);
        return BLLet.Controller.dnaControl(cachedIdentifiers[0], cachedIdentifiers[1], stdControlParam);
    }

    
    public static String dnaControl(String did, String sDid, String dataStr, String cmd, BLConfigParam configParam) {
        final String[] cachedIdentifiers = getCachedIdentifier(did, sDid);
        return BLLet.Controller.dnaControl(cachedIdentifiers[0], cachedIdentifiers[1], dataStr, cmd, configParam);
    }
    public static String dnaControl(BLDNADevice device, String dataStr, String cmd, BLConfigParam configParam) {
        final String[] cachedIdentifiers = getCachedIdentifier(device);
        return BLLet.Controller.dnaControl(cachedIdentifiers[0], cachedIdentifiers[1], dataStr, cmd, configParam);
    }

    
    public static BLStdControlResult dnaControl(String did, String sDid, BLStdControlParam stdControlParam, BLConfigParam configParam) {
        final String[] cachedIdentifiers = getCachedIdentifier(did, sDid);
        return BLLet.Controller.dnaControl(cachedIdentifiers[0], cachedIdentifiers[1], stdControlParam, configParam);
    }
    public static BLStdControlResult dnaControl(BLDNADevice device, BLStdControlParam stdControlParam, BLConfigParam configParam) {
        final String[] cachedIdentifiers = getCachedIdentifier(device);
        return BLLet.Controller.dnaControl(cachedIdentifiers[0], cachedIdentifiers[1], stdControlParam, configParam);
    }

    
    public static BLPassthroughResult dnaPassthrough(String did, String sDid, byte[] data) {
        final String[] cachedIdentifiers = getCachedIdentifier(did, sDid);
        return BLLet.Controller.dnaPassthrough(cachedIdentifiers[0], cachedIdentifiers[1], data);
    }
    public static BLPassthroughResult dnaPassthrough(BLDNADevice device, byte[] data) {
        final String[] cachedIdentifiers = getCachedIdentifier(device);
        return BLLet.Controller.dnaPassthrough(cachedIdentifiers[0], cachedIdentifiers[1], data);
    }

    public static BLSubdevResult subDevDel(String did, String sDid) {
        return  BLLet.Controller.subDevDel(getCachedIdentifier(did), sDid);
    }
    public static BLSubdevResult subDevDel(BLDNADevice subDev) {
        if(subDev==null) return null;
        return  subDevDel(subDev.getpDid(), subDev.getDid());
    }

    public static String[] getCachedIdentifier(String did, String sDid){
        final BLDNADevice dev = BLLocalDeviceManager.getInstance().getCachedDevice(did);
        final BLDNADevice sDev = sDid == null ? null : BLLocalDeviceManager.getInstance().getCachedDevice(sDid);
        return new String[]{dev == null ? did : dev.getIdentifier(), sDev == null ? sDid : sDev.getIdentifier()};
    }
    
    public static String getCachedIdentifier(String did){
        final BLDNADevice dev = BLLocalDeviceManager.getInstance().getCachedDevice(did);
        return dev == null ? did : dev.getIdentifier();
    }

    public static String[] getCachedIdentifier(BLDNADevice device){
        final String[] didOrSubDid = BLCommonUtils.parseDidOrSubDid(device);
        return getCachedIdentifier(didOrSubDid[0], didOrSubDid[1]);
    }
}
