package cn.com.broadlink.blappsdkdemo.service;


import android.os.AsyncTask;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.activity.family.param.BLSUpdateFamilyInfoParams;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfo;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfoResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyListResult;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyUpdateResult;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyListInterface;
import cn.com.broadlink.blappsdkdemo.intferfacer.SimpleCallback;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.sdk.BLLet;


public class BLLocalFamilyManager {

    private BLSFamilyInfo currentFamilyInfo;
    private String currentFamilyId;

    private static BLLocalFamilyManager sharedInstance = null;
    private FamilyInterface familyInterface = null;
    private FamilyListInterface familyListInterface = null;
    private SimpleCallback familyAddInterface = null;

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
        BLApplication.mBLUserInfoUnits.cacheFamilyInfo(currentFamilyInfo);
        this.currentFamilyInfo = currentFamilyInfo;
        if(currentFamilyInfo != null){
            this.setCurrentFamilyId(currentFamilyInfo.getFamilyid());
        }else{
            this.setCurrentFamilyId(null);
        }
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
    public void setFamilyAddInterface(SimpleCallback familyAddInterface) {
        this.familyAddInterface = familyAddInterface;
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
    
    public void deleteFamily(String familyId){
        new FamilyDelTask().execute(familyId);
    }
    
    public void addFamily(String... strings){
        new CreateFamilyTask().execute(strings);
    }
    
    public void modifyFamily(String familyId, BLSUpdateFamilyInfoParams params){
        if(familyId == null){
            familyId = currentFamilyId;
        }
        new FamilyModifyTask(familyId, params).execute();
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
            return BLSFamilyManager.getInstance().queryFamilyList();
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
            return BLSFamilyManager.getInstance().queryFamilyInfo(familyId);
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
                BLCommonUtils.toastErr(result);
                if (familyInterface != null) {
                    familyInterface.familyAllInfo(null);
                }
            }
        }
    }

    /**
     * 创建默认家庭
     */
    private class CreateFamilyTask extends AsyncTask<String, Void, BLSFamilyUpdateResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLSFamilyUpdateResult doInBackground(String... strings) {
       
            BLSUpdateFamilyInfoParams params = new BLSUpdateFamilyInfoParams();
            params.setName(strings[0]);
            if(strings.length>=2){
                params.setCountryCode(strings[1]);
            }
            if(strings.length>=3){
                params.setProvinceCode(strings[2]);
            }
            return BLSFamilyManager.getInstance().AddFamily(params);
        }

        @Override
        protected void onPostExecute(BLSFamilyUpdateResult result) {
            super.onPostExecute(result);

            if (result != null && result.succeed()) {
                if(familyAddInterface != null){
                    familyAddInterface.onResult(true);
                }
            } else {
                BLCommonUtils.toastErr(result);
                if(familyAddInterface != null){
                    familyAddInterface.onResult(false);
                }
            }
        }
    }

    /**
     * 家庭详细信息获取
     */
    private class FamilyDelTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLBaseResult doInBackground(String... strings) {
            String familyId = strings[0];
            return BLSFamilyManager.getInstance().delFamily(familyId);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);

            if (result != null && result.succeed()) {
                queryAllFamilyBaseInfo();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }
    }
    
    /**
     * 修改家庭信息
     */
    private class FamilyModifyTask extends AsyncTask<Void, Void, BLBaseResult> {
        BLSUpdateFamilyInfoParams blFamilyInfo;
        String familyId;

        public FamilyModifyTask(String familyId, BLSUpdateFamilyInfoParams blFamilyInfo) {
            this.blFamilyInfo = blFamilyInfo;
            this.familyId = familyId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLBaseResult doInBackground(Void... strings) {
            return BLSFamilyManager.getInstance().updateFamilyInfo(familyId, blFamilyInfo);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);

            if (result != null && result.succeed()) {
                queryFamilyAllInfo(familyId);
            } else {
                BLCommonUtils.toastErr(result);
            }
        }
    }
}
