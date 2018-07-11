package cn.com.broadlink.blappsdkdemo.activity.IRCode;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.BLIRCodeArea;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.result.controller.BLBaseBodyResult;

public class IRCodeAreaSelectActivity extends Activity {

    private ListView mAreaListView;
    private List<BLIRCodeArea> mAreas = new ArrayList<>();
    private IRCodeAreaSelectActivity.IRCodeAreaAdapter mAdapter;

    private int mDeviceType;
    private String mBrandID = null;
    private String mDownloadUrl = null;
    private String mScriptRandkey = null;
    private String mScriptName = null;
    private String mSubAreaId = null;
    private Boolean mIsLeaf = false;
    private String mProviderId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ircode_area_select);

        mDeviceType = getIntent().getIntExtra("DEVICE_TYPE", 3);

        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV_BOX) {
            mSubAreaId = "0";
        }

        findView();
        setListener();

        queryIRCodeArea();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void findView() {
        mAreaListView = (ListView) findViewById(R.id.ircode_area_list);
        mAdapter = new IRCodeAreaAdapter(IRCodeAreaSelectActivity.this, mAreas);
        mAreaListView.setAdapter(mAdapter);
    }

    private void setListener(){
        mAreaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final BLIRCodeArea area = mAreas.get(position);

                if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_AC || mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV) {
                    if (mBrandID == null) {
                        mBrandID = area.getAreaId();
                    } else {
                        mDownloadUrl = area.getDownloadUrl();
                        mScriptRandkey = area.getAreaId();
                        mScriptName = area.getAreaName();
                    }
                    queryIRCodeArea();
                } else {
                    if (!mIsLeaf) {

                        mSubAreaId = area.getAreaId();
                        mIsLeaf = area.isleaf();

                        if (mIsLeaf) {
                            //已经是最后一层叶子支点, 查询当地运营商ID
                            Log.d(BLConstants.BROADLINK_LOG_TAG, area.getAreaName() + " is leaf");
                            querySTBSubAreaProvider();
                        } else {
                            queryIRCodeArea();
                        }

                    } else {
                        if (mProviderId == null) {
                            mProviderId = area.getAreaId();
                        } else if (mDownloadUrl == null) {
                            mDownloadUrl = area.getDownloadUrl();
                            mScriptRandkey = area.getAreaId();
                            mScriptName = area.getAreaName();
                        }

                        querySTBIRCodeScriptDownloadUrl();
                    }
                }
            }
        });
    }

    private void queryIRCodeArea() {
        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_AC) {
            // 空调设备需要查询 Brand 和 Version
            if (mBrandID == null) {
                new QueryIRCodeBrandTask().execute(String.valueOf(mDeviceType));
            } else if (mDownloadUrl == null){
                new QueryIRCodeScriptDownloadUrlTask().execute(String.valueOf(mDeviceType), mBrandID);
            } else {
                goToNextActivity();
            }
        } else if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV) {
            // 电视设备只需要查询 Brand 即可，Version 都为 0
            if (mBrandID == null) {
                new QueryIRCodeBrandTask().execute(String.valueOf(mDeviceType));
            } else if(mDownloadUrl == null) {
                new QueryIRCodeScriptDownloadUrlTask().execute(String.valueOf(mDeviceType), mBrandID, "0");
            } else {
                goToNextActivity();
            }
        } else if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV_BOX) {
            new QueryIRCodeSubAreaTask().execute(mSubAreaId);
        }
    }

    private void querySTBSubAreaProvider() {
        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV_BOX) {
            new QueryIRCodeSubAreaProviderTask().execute(mSubAreaId);
        }
    }

    private void querySTBIRCodeScriptDownloadUrl() {
        if (mDeviceType == BLConstants.BL_IRCODE_DEVICE_TV_BOX) {
            if (mDownloadUrl == null) {
                new QuerySTBIRCodeScriptDownloadUrlTask().execute(mSubAreaId, mProviderId);
            } else {
                goToNextActivity();
            }
        }
    }

    private void goToNextActivity() {
        Intent intent = new Intent(IRCodeAreaSelectActivity.this, IRCodeRecognizeActivity.class);
        intent.putExtra("DownloadURL", mDownloadUrl);
        intent.putExtra("ScriptRandkey", mScriptRandkey);
        intent.putExtra("ScriptName", mScriptName);
        intent.putExtra("DeviceType", mDeviceType);

        startActivity(intent);
    }

    class QueryIRCodeBrandTask extends AsyncTask<String, Void, BLBaseBodyResult> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(IRCodeAreaSelectActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }
        @Override
        protected BLBaseBodyResult doInBackground(String... strings) {
            int type = Integer.parseInt(strings[0]);
            return BLLet.IRCode.requestIRCodeDeviceBrands(type);
        }

        @Override
        protected void onPostExecute(BLBaseBodyResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            progressDialog.dismiss();

            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jInfo = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray jBrands = jInfo.getJSONArray("brand");

                    int brandCount = jBrands.length();
                    mAreas.clear();
                    for (int i = 0; i < brandCount; i++) {
                        JSONObject jBrand = jBrands.optJSONObject(i);
                        BLIRCodeArea area = new BLIRCodeArea();
                        area.setAreaName(jBrand.optString("brand", null));
                        area.setAreaId(String.valueOf(jBrand.optInt("brandid")));
                        mAdapter.add(area);
                    }
                    mAdapter.notifyDataSetChanged();

                } catch (Exception e) {

                }
            } else {
                BLCommonUtils.toastShow(IRCodeAreaSelectActivity.this, "getIRCodeScriptBaseInfo failed! Error: " + String.valueOf(blBaseBodyResult.getStatus()) + " Msg:" + blBaseBodyResult.getMsg());
            }
        }
    }

    class QueryIRCodeSubAreaTask extends AsyncTask<String, Void, BLBaseBodyResult> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(IRCodeAreaSelectActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected BLBaseBodyResult doInBackground(String... strings) {
            int locateid = Integer.parseInt(strings[0]);
            return BLLet.IRCode.requestSubAreas(locateid);
        }

        @Override
        protected void onPostExecute(BLBaseBodyResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            progressDialog.dismiss();

            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jInfo = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray jSubareainfos = jInfo.getJSONArray("subareainfo");

                    int count = jSubareainfos.length();
                    mAreas.clear();
                    for (int i = 0; i < count; i++) {
                        JSONObject jSubInfo = jSubareainfos.optJSONObject(i);
                        BLIRCodeArea area = new BLIRCodeArea();
                        area.setAreaName(jSubInfo.optString("name", null));
                        area.setAreaId(String.valueOf(jSubInfo.optInt("locateid")));
                        int leaf = jSubInfo.getInt("isleaf");
                        area.setIsleaf(leaf > 0);
                        mAdapter.add(area);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        }
    }

    class QueryIRCodeSubAreaProviderTask extends AsyncTask<String, Void, BLBaseBodyResult> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(IRCodeAreaSelectActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected BLBaseBodyResult doInBackground(String... strings) {
            int locateid = Integer.parseInt(strings[0]);
            return BLLet.IRCode.requestSTBProvider(locateid);
        }

        @Override
        protected void onPostExecute(BLBaseBodyResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            progressDialog.dismiss();

            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jInfo = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray jProviderInfos = jInfo.getJSONArray("providerinfo");

                    int count = jProviderInfos.length();
                    mAreas.clear();
                    for (int i = 0; i < count; i++) {
                        JSONObject jProviderInfo = jProviderInfos.optJSONObject(i);
                        BLIRCodeArea area = new BLIRCodeArea();
                        area.setAreaName(jProviderInfo.optString("providername", null));
                        area.setAreaId(String.valueOf(jProviderInfo.optInt("providerid")));
                        mAdapter.add(area);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        }
    }

    class QueryIRCodeScriptDownloadUrlTask extends AsyncTask<String, Void, BLBaseBodyResult> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(IRCodeAreaSelectActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }
        @Override
        protected BLBaseBodyResult doInBackground(String... strings) {
            int type = Integer.parseInt(strings[0]);
            int brand = Integer.parseInt(strings[1]);
            return BLLet.IRCode.requestIRCodeScriptDownloadUrl(type, brand);
        }

        @Override
        protected void onPostExecute(BLBaseBodyResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            progressDialog.dismiss();

            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jResult = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray infos = jResult.optJSONArray("downloadinfo");

                    if (infos != null) {
                        int count = infos.length();
                        if (count > 1) {
                            mAreas.clear();
                            for (int i = 0; i < count; i++) {
                                JSONObject jInfo = infos.optJSONObject(i);
                                BLIRCodeArea area = new BLIRCodeArea();
                                area.setAreaName(jInfo.optString("name", null));
                                area.setAreaId(jInfo.optString("randkey", null));
                                area.setDownloadUrl(jInfo.optString("downloadurl", null));
                                mAdapter.add(area);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            JSONObject info = infos.getJSONObject(0);
                            if (info != null) {
                                mDownloadUrl = info.optString("downloadurl", null);
                                mScriptRandkey = info.optString("randkey", null);
                                mScriptName = info.optString("name", null);

                                goToNextActivity();
                            }
                        }
                    }

                } catch (Exception e) {

                }
            }
        }
    }

    class QuerySTBIRCodeScriptDownloadUrlTask extends AsyncTask<String, Void, BLBaseBodyResult> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(IRCodeAreaSelectActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected BLBaseBodyResult doInBackground(String... strings) {
            int locateid = Integer.parseInt(strings[0]);
            int providerid = Integer.parseInt(strings[1]);
            return BLLet.IRCode.requestSTBIRCodeScriptDownloadUrl(locateid, providerid, 0);
        }

        @Override
        protected void onPostExecute(BLBaseBodyResult blBaseBodyResult) {
            super.onPostExecute(blBaseBodyResult);
            progressDialog.dismiss();

            if (blBaseBodyResult.succeed()) {
                Log.d(BLConstants.BROADLINK_LOG_TAG, blBaseBodyResult.getResponseBody());
                try {
                    JSONObject jResult = new JSONObject(blBaseBodyResult.getResponseBody());
                    JSONArray infos = jResult.optJSONArray("downloadinfo");

                    if (infos != null) {
                        int count = infos.length();
                        if (count > 1) {
                            mAreas.clear();
                            for (int i = 0; i < count; i++) {
                                JSONObject jInfo = infos.optJSONObject(i);
                                BLIRCodeArea area = new BLIRCodeArea();
                                area.setAreaName(jInfo.optString("name", null));
                                area.setAreaId(jInfo.optString("randkey", null));
                                area.setDownloadUrl(jInfo.optString("downloadurl", null));
                                mAdapter.add(area);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            JSONObject info = infos.getJSONObject(0);
                            if (info != null) {
                                mDownloadUrl = info.optString("downloadurl", null);
                                mScriptRandkey = info.optString("randkey", null);
                                mScriptName = info.optString("name", null);

                                goToNextActivity();
                            }
                        }
                    }

                } catch (Exception e) {

                }
            }
        }
    }


    private class IRCodeAreaAdapter extends ArrayAdapter<BLIRCodeArea> {
        public IRCodeAreaAdapter(Context context, List<BLIRCodeArea> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            IRCodeAreaSelectActivity.IRCodeAreaAdapter.ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new IRCodeAreaSelectActivity.IRCodeAreaAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.adapter_device, null);
                viewHolder.brandName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.brandID = (TextView) convertView.findViewById(R.id.tv_mac);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (IRCodeAreaSelectActivity.IRCodeAreaAdapter.ViewHolder) convertView.getTag();
            }

            BLIRCodeArea area = getItem(position);

            viewHolder.brandName.setText(area.getAreaName());
            String brandid = area.getAreaId();
            viewHolder.brandID.setText(brandid);

            return convertView;
        }

        private class ViewHolder{
            TextView brandName;
            TextView brandID;
        }
    }
}
