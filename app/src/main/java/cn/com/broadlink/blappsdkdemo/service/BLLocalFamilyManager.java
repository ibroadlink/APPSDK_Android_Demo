package cn.com.broadlink.blappsdkdemo.service;


import android.os.AsyncTask;


import java.util.ArrayList;
import java.util.List;


import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyListInterface;
import cn.com.broadlink.family.BLFamily;
import cn.com.broadlink.family.params.BLFamilyAllInfo;
import cn.com.broadlink.family.params.BLFamilyBaseInfo;
import cn.com.broadlink.family.params.BLFamilyRoomInfo;
import cn.com.broadlink.family.result.BLAllFamilyInfoResult;
import cn.com.broadlink.family.result.BLFamilyBaseInfoListResult;
import cn.com.broadlink.family.result.BLFamilyInfoResult;
import cn.com.broadlink.family.result.BLManageRoomResult;
import cn.com.broadlink.family.result.BLModuleControlResult;


public class BLLocalFamilyManager {

    private BLFamilyAllInfo currentFamilyAllInfo;
    private String currentFamilyId;
    private String currentFamilyVersion;

    private static BLLocalFamilyManager sharedInstance = null;
    private FamilyInterface familyInterface = null;
    private FamilyListInterface familyListInterface = null;

    public static BLLocalFamilyManager getInstance() {
        synchronized (BLLocalFamilyManager.class) {
            if (sharedInstance == null) {
                sharedInstance = new BLLocalFamilyManager();
            }
        }
        return sharedInstance;
    }

    public BLFamilyAllInfo getCurrentFamilyAllInfo() {
        return currentFamilyAllInfo;
    }

    public void setCurrentFamilyAllInfo(BLFamilyAllInfo allInfo) {
        currentFamilyAllInfo = allInfo;
        setCurrentFamilyId(currentFamilyAllInfo.getFamilyInfo().getFamilyId());
        setCurrentFamilyVersion(currentFamilyAllInfo.getFamilyInfo().getFamilyVersion());
    }

    public void setCurrentFamilyId(String familyId) {
        currentFamilyId = familyId;
        // 家庭选择之后，设置家庭ID到SDK，便于后续SDK数据处理
        BLFamily.setCurrentFamilyId(currentFamilyId);
    }

    public void setCurrentFamilyVersion(String version) {
        currentFamilyVersion = version;
    }

    public void setFamilyInterface(FamilyInterface familyInterface) {
        this.familyInterface = familyInterface;
    }

    public void setFamilyListInterface(FamilyListInterface familyListInterface) {
        this.familyListInterface = familyListInterface;
    }

    /**
     * 查询当前用户下所有家庭的基本信息
     */
    public void queryAllFamilyBaseInfo() {
        new FamilyBaseInfoTask().execute();
    }

    /**
     * 更新获取家庭详细信息
     * @param familyId 家庭唯一ID，若为空则使用当前家庭ID
     */
    public void queryFamilyAllInfo(String familyId) {
        if (familyId == null)
            familyId = currentFamilyId;
        new FamilyAllInfoTask().execute(familyId);
    }

    /**
     * 创建默认家庭
     * @param name 家庭名称
     */
    public void createDefaultFamily(String name) {
        new CreateDefaultFamilyTask().execute(name);
    }

    /**
     * 添加新的房间
     * @param name 房间名称
     */
    public void addRoomIntoFamily(String name) {
        String action = "add";
        new ManageRoomTask().execute(action, name, null);
    }

    /**
     * 删除指定房间
     * @param name 房间名称
     * @param roomId 房间ID
     */
    public void delRoomFromFamilt(String name, String roomId) {
        String action = "del";
        new ManageRoomTask().execute(action, name,roomId);
    }

    /**
     * 删除指定模块
     * @param moduleId 模块ID
     */
    public void delModuleFromFamily(String moduleId) {
        new DelModuleTask().execute(moduleId);
    }

    /**
     * 家庭基本信息列表获取
     */
    private class FamilyBaseInfoTask extends AsyncTask<String, Void, BLFamilyBaseInfoListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLFamilyBaseInfoListResult doInBackground(String... params) {
            return BLFamily.queryLoginUserFamilyBaseInfoList();
        }

        @Override
        protected void onPostExecute(BLFamilyBaseInfoListResult blFamilyBaseInfoListResult) {
            super.onPostExecute(blFamilyBaseInfoListResult);

            if (blFamilyBaseInfoListResult != null) {
                try {

                    int status = blFamilyBaseInfoListResult.getStatus();
                    String msg = blFamilyBaseInfoListResult.getMsg();
                    if (status == 0) {
                        List<BLFamilyBaseInfo> infoList = blFamilyBaseInfoListResult.getInfoList();
                        if (familyListInterface != null) {
                            familyListInterface.queryFamilyBaseInfoList(infoList);
                        }
                    } else {
                        BLCommonTools.debug(msg);
                    }
                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }
            }
        }
    }

    /**
     * 家庭详细信息获取
     */
    private class FamilyAllInfoTask extends AsyncTask<String, Void, BLAllFamilyInfoResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLAllFamilyInfoResult doInBackground(String... strings) {
            return BLFamily.queryAllFamilyInfos(strings);
        }

        @Override
        protected void onPostExecute(BLAllFamilyInfoResult blAllFamilyInfoResult) {
            super.onPostExecute(blAllFamilyInfoResult);

            if (blAllFamilyInfoResult.succeed()) {
                //We just query only one family
                BLFamilyAllInfo allInfo = blAllFamilyInfoResult.getAllInfos().get(0);
                setCurrentFamilyAllInfo(allInfo);
                if (familyInterface != null) {
                    familyInterface.familyAllInfo(allInfo);
                }
            } else {
                BLCommonTools.debug(blAllFamilyInfoResult.getMsg());
            }

        }
    }

    /**
     * 创建默认家庭
     */
    private class CreateDefaultFamilyTask extends AsyncTask<String, Void, BLFamilyInfoResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLFamilyInfoResult doInBackground(String... strings) {
            String name = strings[0];

            return BLFamily.createDefaultFamily(name, null, null, null);
        }

        @Override
        protected void onPostExecute(BLFamilyInfoResult result) {
            super.onPostExecute(result);

            if (familyInterface != null) {
                familyInterface.familyInfoChanged(result.succeed(), result.getFamilyInfo().getFamilyId(), result.getFamilyInfo().getFamilyVersion());
            }
        }
    }

    /**
     * 删除指定ID的家庭
     */
    private class DelFamilyTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLBaseResult doInBackground(String... strings) {
            String familyId = strings[0];
            String familyVersion = strings[1];

            return BLFamily.delFamily(familyId, familyVersion);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);

            if (familyInterface != null) {
                familyInterface.familyInfoChanged(result.succeed(), null, null);
            }
        }
    }

    /**
     * 家庭房间操作 - add / del / modify
     */
    private class ManageRoomTask extends AsyncTask<String, Void, BLManageRoomResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLManageRoomResult doInBackground(String... params) {

            String action = params[0];
            String name = params[1];
            String roomId = null;
            if (params.length >= 3 && params[2] != null) {
                roomId = params[2];
            }

            final BLFamilyRoomInfo roomInfo = new BLFamilyRoomInfo();
            roomInfo.setName(name);
            roomInfo.setAction(action);
            roomInfo.setRoomId(roomId);
            roomInfo.setFamilyId(currentFamilyId);

            List<BLFamilyRoomInfo> roomInfos = new ArrayList<BLFamilyRoomInfo>(){{
                add(roomInfo);
            }};

            BLManageRoomResult result = BLFamily.manageFamilyRooms(currentFamilyId, currentFamilyVersion, roomInfos);;
            return result;
        }

        @Override
        protected void onPostExecute(BLManageRoomResult result) {
            super.onPostExecute(result);

            if (result.succeed()) {
                setCurrentFamilyId(result.getFamilyId());
                setCurrentFamilyVersion(result.getFamilyVersion());
            } else {
                BLCommonTools.error(result.getMsg());
            }

            if (familyInterface != null) {
                familyInterface.familyInfoChanged(result.succeed(), result.getFamilyId(), result.getFamilyVersion());
            }
        }
    }

    private class DelModuleTask extends AsyncTask<String, Void, BLModuleControlResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLModuleControlResult doInBackground(String... strings) {
            String moduleId = strings[0];

            return BLFamily.delModuleFromFamily(moduleId, currentFamilyId, currentFamilyVersion);
        }

        @Override
        protected void onPostExecute(BLModuleControlResult result) {
            super.onPostExecute(result);

            if (familyInterface != null) {
                familyInterface.familyInfoChanged(result.succeed(), currentFamilyId, result.getVersion());
            }

        }
    }
}
