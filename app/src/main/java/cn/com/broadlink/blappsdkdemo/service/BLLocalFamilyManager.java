package cn.com.broadlink.blappsdkdemo.service;


import android.os.AsyncTask;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.activity.Family.BLSFamilyHTTP;
import cn.com.broadlink.blappsdkdemo.activity.Family.Params.BLSUpdateFamilyInfoParams;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSFamilyInfo;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSFamilyInfoResult;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSFamilyUpdateResult;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSFamilyListResult;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyListInterface;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.sdk.BLLet;


public class BLLocalFamilyManager {

    private BLSFamilyInfo currentFamilyInfo;
    private String currentFamilyId;

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

    public BLSFamilyInfo getCurrentFamilyInfo() {
        return currentFamilyInfo;
    }

    public void setCurrentFamilyInfo(BLSFamilyInfo currentFamilyInfo) {
        this.currentFamilyInfo = currentFamilyInfo;
    }

    public void setCurrentFamilyId(String familyId) {
        currentFamilyId = familyId;
        // 家庭选择之后，设置家庭ID到SDK，便于后续SDK数据处理
        BLLet.Controller.setCurrentFamilyId(currentFamilyId);
        BLIRCode.setFamilyId(currentFamilyId);
    }

    public String getCurrentFamilyId() {
        return currentFamilyId;
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
        new FamilyListTask().execute();
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
     * 家庭信息列表获取
     */
    private class FamilyListTask extends AsyncTask<String, Void, BLSFamilyListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLSFamilyListResult doInBackground(String... params) {
            return BLSFamilyHTTP.getInstance().queryFamilyList();
        }

        @Override
        protected void onPostExecute(BLSFamilyListResult result) {
            super.onPostExecute(result);

            if (result != null) {
                try {

                    int status = result.getStatus();
                    String msg = result.getMsg();
                    if (status == 0) {
                        if (result.getData() != null) {
                            if (familyListInterface != null) {
                                familyListInterface.queryFamilyBaseInfoList(result.getData().getFamilyList());
                            }
                            return;
                        }
                    } else {
                        BLCommonTools.debug(msg);
                    }

                    if (familyListInterface != null) {
                        familyListInterface.queryFamilyBaseInfoList(null);
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
    private class FamilyAllInfoTask extends AsyncTask<String, Void, BLSFamilyInfoResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLSFamilyInfoResult doInBackground(String... strings) {
            String familyId = strings[0];
            return BLSFamilyHTTP.getInstance().queryFamilyInfo(familyId);
        }

        @Override
        protected void onPostExecute(BLSFamilyInfoResult result) {
            super.onPostExecute(result);

            if (result.succeed() && result.getData() != null) {
                //We just query only one family
                BLSFamilyInfo allInfo = result.getData().getFamilyInfo();
                setCurrentFamilyInfo(allInfo);
                if (familyInterface != null) {
                    familyInterface.familyAllInfo(allInfo);
                }
            } else {
                BLCommonTools.debug(result.getMsg());
            }
        }
    }

    /**
     * 创建默认家庭
     */
    private class CreateDefaultFamilyTask extends AsyncTask<String, Void, BLSFamilyUpdateResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLSFamilyUpdateResult doInBackground(String... strings) {
            String name = strings[0];

            BLSUpdateFamilyInfoParams params = new BLSUpdateFamilyInfoParams();
            params.setName(name);

            return BLSFamilyHTTP.getInstance().AddFamily(params);
        }

        @Override
        protected void onPostExecute(BLSFamilyUpdateResult result) {
            super.onPostExecute(result);

            if (familyInterface != null) {
                familyInterface.familyInfoChanged(result.succeed(), null, null);
            }
        }
    }
}
