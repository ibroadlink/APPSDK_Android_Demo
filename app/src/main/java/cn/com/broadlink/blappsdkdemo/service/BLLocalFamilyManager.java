package cn.com.broadlink.blappsdkdemo.service;


import android.os.AsyncTask;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyListInterface;
import cn.com.broadlink.blappsdkdemo.intferfacer.SimpleCallback;
import cn.com.broadlink.blsfamily.BLSFamily;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.family.BLSFamilyAddData;
import cn.com.broadlink.blsfamily.bean.family.BLSFamilyAddOrUpdateParams;
import cn.com.broadlink.blsfamily.bean.family.BLSFamilyInfo;
import cn.com.broadlink.blsfamily.bean.family.BLSFamilyInfoData;
import cn.com.broadlink.blsfamily.bean.family.BLSFamilyListData;
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
    
    public void modifyFamily(String familyId, BLSFamilyAddOrUpdateParams params){
        if(familyId == null){
            familyId = currentFamilyId;
        }
        new FamilyModifyTask(familyId, params).execute();
    }

    /**
     * 家庭信息列表获取
     */
    private class FamilyListTask extends AsyncTask<String, Void, BLSBaseDataResult<BLSFamilyListData> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLSBaseDataResult<BLSFamilyListData>  doInBackground(String... params) {
            return BLSFamily.Family.getList();
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSFamilyListData>  result) {
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
    private class FamilyAllInfoTask extends AsyncTask<String, Void, BLSBaseDataResult<BLSFamilyInfoData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLSBaseDataResult<BLSFamilyInfoData> doInBackground(String... strings) {
            String familyId = strings[0];
            return BLSFamily.Family.getInfo(familyId);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSFamilyInfoData> result) {
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
    private class CreateFamilyTask extends AsyncTask<String, Void, BLSBaseDataResult<BLSFamilyAddData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLSBaseDataResult<BLSFamilyAddData> doInBackground(String... strings) {

            BLSFamilyAddOrUpdateParams params = new BLSFamilyAddOrUpdateParams();
            params.setName(strings[0]);
            if(strings.length>=2){
                params.setCountryCode(strings[1]);
            }
            if(strings.length>=3){
                params.setProvinceCode(strings[2]);
            }
            return BLSFamily.Family.add(params);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSFamilyAddData> result) {
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
     * 删除家庭
     */
    private class FamilyDelTask extends AsyncTask<String, Void, BLBaseResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLBaseResult doInBackground(String... strings) {
            String familyId = strings[0];
            return BLSFamily.Family.delete(familyId);
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
        BLSFamilyAddOrUpdateParams blFamilyInfo;
        String familyId;

        public FamilyModifyTask(String familyId, BLSFamilyAddOrUpdateParams blFamilyInfo) {
            this.blFamilyInfo = blFamilyInfo;
            this.familyId = familyId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLBaseResult doInBackground(Void... strings) {
            return BLSFamily.Family.update(familyId, blFamilyInfo);
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
