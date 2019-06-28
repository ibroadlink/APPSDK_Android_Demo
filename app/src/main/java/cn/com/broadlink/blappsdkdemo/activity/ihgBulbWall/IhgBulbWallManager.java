package cn.com.broadlink.blappsdkdemo.activity.ihgBulbWall;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.BLMultDidUtils;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/6/27 10:18
 */
public class IhgBulbWallManager {

    private Activity mActivity;
    private BLProgressDialog mProgressDialog;
    private short mMsgId = 0;
    
    public IhgBulbWallManager(Activity mActivity) {
        this.mActivity = mActivity;
        mProgressDialog = BLProgressDialog.createDialog(mActivity);
    }

    public void getAllStatus(BLDNADevice device, IhgBulbCallBack ihgBulbCallBack){
        final ArrayList<String> params = new ArrayList<>();
        params.add(IhgBulbWallConstants.ITF.SHOW_PARAM);
        params.add(IhgBulbWallConstants.ITF.MACLIST);
        params.add(IhgBulbWallConstants.ITF.RGBLIST);
        
        final ArrayList<String> vals = new ArrayList<>();
        vals.add("");
        vals.add("");
        vals.add("");
        
        exeControlTask(device, "get", params, vals, "Get Status...",ihgBulbCallBack);
    }
    
    public IhgBulbInfo parseStatus(String result){
        final IhgBulbInfo ihgBulbInfo = new IhgBulbInfo();
        if(!TextUtils.isEmpty(result)){
            try {
                final JSONObject jsonObject = JSON.parseObject(result);
                final JSONObject data = jsonObject.getJSONObject("data");
                final JSONArray vals = data.getJSONArray("vals");
                
                final JSONArray paramArray = vals.getJSONArray(0);
                final JSONObject paramJson = paramArray.getJSONObject(0).getJSONObject("val");

                ihgBulbInfo.showParam.loop = paramJson.getString(IhgBulbWallConstants.ITF.LOOP);
                ihgBulbInfo.showParam.times = paramJson.getIntValue(IhgBulbWallConstants.ITF.TIMES);
                ihgBulbInfo.showParam.interval = paramJson.getIntValue(IhgBulbWallConstants.ITF.INTERVAL);
                ihgBulbInfo.showParam.brightness = paramJson.getIntValue(IhgBulbWallConstants.ITF.BRIGHTNESS);
                
                final JSONArray macArray = vals.getJSONArray(1);
                final JSONArray macJson = macArray.getJSONObject(0).getJSONArray("val");
                for (Object item : macJson) {
                    ihgBulbInfo.maclist.add((String) item);
                }

                final JSONArray rgbArray = vals.getJSONArray(2);
                final JSONArray rgbJson = rgbArray.getJSONObject(0).getJSONArray("val");
                for (Object item : rgbJson) {
                    ihgBulbInfo.rgblist.add((int) item);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        
        return ihgBulbInfo;
    }
    
    public void setLoopType(BLDNADevice device, String type, IhgBulbCallBack ihgBulbCallBack){
        final ArrayList<String> params = new ArrayList<>();
        params.add(IhgBulbWallConstants.ITF.SHOW_PARAM);
        final ArrayList<String> vals = new ArrayList<>();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(IhgBulbWallConstants.ITF.LOOP, type);
        vals.add(jsonObject.toString());

        exeControlTask(device, "set", params, vals, "Setup Loop Type...",ihgBulbCallBack);
    }
    
    public void setLoopCount(BLDNADevice device, int count, IhgBulbCallBack ihgBulbCallBack){
        final ArrayList<String> params = new ArrayList<>();
        params.add(IhgBulbWallConstants.ITF.SHOW_PARAM);
        final ArrayList<String> vals = new ArrayList<>();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(IhgBulbWallConstants.ITF.TIMES, count);
        vals.add(jsonObject.toString());

        exeControlTask(device, "set", params, vals, "Setup Loop Count...",ihgBulbCallBack);
    }
    
    public void setLoopInterval(BLDNADevice device, int interval, IhgBulbCallBack ihgBulbCallBack){
        final ArrayList<String> params = new ArrayList<>();
        params.add(IhgBulbWallConstants.ITF.SHOW_PARAM);
        final ArrayList<String> vals = new ArrayList<>();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(IhgBulbWallConstants.ITF.INTERVAL, interval);
        vals.add(jsonObject.toString());

        exeControlTask(device, "set", params, vals, "Setup Loop Interval...",ihgBulbCallBack);
    }
    
    
    public void setupBrightness(BLDNADevice device, int opt, IhgBulbCallBack ihgBulbCallBack){
        final ArrayList<String> params = new ArrayList<>();
        params.add(IhgBulbWallConstants.ITF.SHOW_PARAM);
        final ArrayList<String> vals = new ArrayList<>();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(IhgBulbWallConstants.ITF.BRIGHTNESS, opt);
        vals.add(jsonObject.toString());

        exeControlTask(device, "set", params, vals, "Setup Brightness...",ihgBulbCallBack);
    }


    public void setupScene(BLDNADevice device, int opt, IhgBulbCallBack ihgBulbCallBack){
        setupScene(device, opt, null, null, ihgBulbCallBack);
    }
    
    public void setupScene(BLDNADevice device, ArrayList<String> macList, ArrayList<Integer> rgbList, IhgBulbCallBack ihgBulbCallBack){
        setupScene(device, -1, macList, rgbList, ihgBulbCallBack);
    }
    
    public void setupScene(BLDNADevice device, int opt, ArrayList<String> macList, ArrayList<Integer> rgbList, IhgBulbCallBack ihgBulbCallBack){
        final ArrayList<String> params = new ArrayList<>();
        final ArrayList<String> vals = new ArrayList<>();
        
        if(opt>=0){
            params.add(IhgBulbWallConstants.ITF.ACT);
            vals.add(String.valueOf(opt));
        }
        
        if(macList != null){
            params.add(IhgBulbWallConstants.ITF.MACLIST);
            vals.add(JSON.toJSONString(macList));
        }
        
        if(rgbList != null){
            params.add(IhgBulbWallConstants.ITF.RGBLIST);
            vals.add(JSON.toJSONString(rgbList));
        }

        exeControlTask(device, "set", params, vals, "Setup Scene...",ihgBulbCallBack);
    }
    
    public void setupCat(BLDNADevice device, int opt, ArrayList<String> macList, ArrayList<Integer> rgbList, IhgBulbCallBack ihgBulbCallBack){
        final ArrayList<String> params = new ArrayList<>();
        final ArrayList<String> vals = new ArrayList<>();

        if(opt>=0){
            params.add(IhgBulbWallConstants.ITF.CATEGORY);
            vals.add(String.valueOf(opt));
        }

        if(macList != null){
            params.add(IhgBulbWallConstants.ITF.MACLIST);
            vals.add(JSON.toJSONString(macList));
        }

        if(rgbList != null){
            params.add(IhgBulbWallConstants.ITF.RGBLIST);
            vals.add(JSON.toJSONString(rgbList));
        }

        exeControlTask(device, "set", params, vals, "Setup Bulbs...",ihgBulbCallBack);
    }

    
    public void setupBulbCnt(BLDNADevice device, int cnt,  IhgBulbCallBack ihgBulbCallBack){
        final ArrayList<String> params = new ArrayList<>();
        final ArrayList<String> vals = new ArrayList<>();

        params.add(IhgBulbWallConstants.ITF.MACLIST);
        final ArrayList<String> macList = new ArrayList<>();
        for (int i = 0; i < cnt; i++) {
            macList.add("");
        }
        vals.add(JSON.toJSONString(macList));

        params.add(IhgBulbWallConstants.ITF.RGBLIST);
        final ArrayList<Integer> rgbList = new ArrayList<>();
        for (int i = 0; i < cnt; i++) {
            rgbList.add(0);
        }
        vals.add(JSON.toJSONString(rgbList));

        exeControlTask(device, "set", params, vals, "Setup Bulb Count...",ihgBulbCallBack);
    }
    
    
    private void showProgressDialog(String msg) {
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mActivity != null && !mActivity.isFinishing()) {
            mProgressDialog.dismiss();
        }
    }
    
    public void exeControlTask(BLDNADevice device, String setOrGet, ArrayList<String> paramList, ArrayList<String> valList, String msg, IhgBulbCallBack ihgBulbCallBack){
        new ExeTask(device, setOrGet, paramList, valList, msg, ihgBulbCallBack).executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }
    
    private String doControl(final BLDNADevice device, String setOrGet, ArrayList<String> paramList, ArrayList<String> valList) {

        if (paramList == null) {
            paramList = new ArrayList<>();
        }

        if (valList == null) {
            valList = new ArrayList<>();
        }

        BLStdControlParam stdControlParam = new BLStdControlParam();
        stdControlParam.setAct(setOrGet);
        stdControlParam.setDid(device.getDid());
        stdControlParam.getParams().addAll(paramList);

        for (int i = 0; i < paramList.size(); i++) {
            ArrayList<BLStdData.Value> dnaVals = new ArrayList<>();
            BLStdData.Value value = new BLStdData.Value();
            if (i < valList.size() && !TextUtils.isEmpty(valList.get(i))) {
                try {
                    final int intVal = Integer.parseInt(valList.get(i));
                    value.setVal(intVal);
                } catch (NumberFormatException e) {
                    try { // 如果输入是jsonStr则当作object，而不是string来处理
                        final JSONObject jsonObject = JSON.parseObject(valList.get(i));
                        value.setVal(jsonObject);
                    } catch (java.lang.ClassCastException e1) {
                        final JSONArray objects = JSON.parseArray(valList.get(i));
                        value.setVal(objects);
                    } catch (com.alibaba.fastjson.JSONException e1) {
                        value.setVal(valList.get(i));
                    }
                }
            } else {
                value.setVal("0");
            }
            dnaVals.add(value);
            stdControlParam.getVals().add(dnaVals);
        }

        stdControlParam.getParams().add(IhgBulbWallConstants.ITF.MSGID);
        ArrayList<BLStdData.Value> dnaVals1 = new ArrayList<>();
        BLStdData.Value value1 = new BLStdData.Value();
        value1.setVal(mMsgId);
        dnaVals1.add(value1);
        stdControlParam.getVals().add(dnaVals1);

        stdControlParam.getParams().add(IhgBulbWallConstants.ITF.COUNTER);
        ArrayList<BLStdData.Value> dnaVals2 = new ArrayList<>();
        BLStdData.Value value2 = new BLStdData.Value();
        value2.setVal(1);
        dnaVals2.add(value2);
        stdControlParam.getVals().add(dnaVals2);

        stdControlParam.getParams().add(IhgBulbWallConstants.ITF.SEQUENCE);
        ArrayList<BLStdData.Value> dnaVals3 = new ArrayList<>();
        BLStdData.Value value3 = new BLStdData.Value();
        value3.setVal(0);
        dnaVals3.add(value3);
        stdControlParam.getVals().add(dnaVals3);

        final String dataStr = ctrlParamObjectToStr(stdControlParam);
        final byte[] bytes = BLMultDidUtils.dnaControlData(device, stdControlParam);
        BLLog.d("ECHO_DATA", bytes == null ? "null" : BLCommonTools.bytes2HexString(bytes));

        return BLMultDidUtils.dnaControl(device, dataStr, "dev_ctrl", null);
    }

    // 参数转化
    private String ctrlParamObjectToStr(BLStdControlParam stdControlParam) {
        JSONObject jData = new JSONObject();

        try {
            jData.put("prop", stdControlParam.getProp());
            jData.put("act", stdControlParam.getAct());
            jData.put("did", stdControlParam.getDid());
            if (stdControlParam.getSrv() != null) {
                jData.put("srv", stdControlParam.getSrv());
            }

            if (stdControlParam.getPassword() != null) {
                jData.put("password", stdControlParam.getPassword());
            }

            JSONArray jParams = new JSONArray();
            Iterator var4 = stdControlParam.getParams().iterator();

            while (var4.hasNext()) {
                String param = (String) var4.next();
                jParams.add(param);
            }

            jData.put("params", jParams);
            JSONArray jVals = new JSONArray();
            Iterator var13 = stdControlParam.getVals().iterator();

            while (var13.hasNext()) {
                ArrayList<BLStdData.Value> listVal = (ArrayList) var13.next();
                JSONArray jArrayVal = new JSONArray();
                Iterator var8 = listVal.iterator();

                while (var8.hasNext()) {
                    BLStdData.Value val = (BLStdData.Value) var8.next();
                    JSONObject jVal = new JSONObject();
                    jVal.put("val", val.getVal());
                    jVal.put("idx", val.getIdx());
                    jArrayVal.add(jVal);
                }

                jVals.add(jArrayVal);
            }

            jData.put("vals", jVals);
        } catch (JSONException var11) {
            BLCommonTools.handleError(var11);
        }

        return jData.toString();
    }

    public interface IhgBulbCallBack {
        void onResult(String result);
    }

    
    class ExeTask extends AsyncTask<Void, Void, String> {
        BLDNADevice device;
        String setOrGet;
        ArrayList<String> paramList;
        ArrayList<String> valList;
        String msg;
        IhgBulbCallBack ihgBulbCallBack;

        public ExeTask(BLDNADevice device, String setOrGet, ArrayList<String> paramList, ArrayList<String> valList, String msg, IhgBulbCallBack ihgBulbCallBack) {
            this.device = device;
            this.setOrGet = setOrGet;
            this.paramList = paramList;
            this.valList = valList;
            this.msg = msg;
            this.ihgBulbCallBack = ihgBulbCallBack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(TextUtils.isEmpty(msg) ? "Loading..." : msg);
        }

        @Override
        protected String doInBackground(Void... params) {
            return doControl(device, setOrGet, paramList, valList);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if(ihgBulbCallBack != null){
                ihgBulbCallBack.onResult(result);
            }
        }
    }

}
