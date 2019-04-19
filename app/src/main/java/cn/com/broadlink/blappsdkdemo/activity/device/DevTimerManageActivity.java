package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.timer.BLBaseTimerInfoV2;
import cn.com.broadlink.blappsdkdemo.data.timer.BLBaseTimerResultV2;
import cn.com.broadlink.blappsdkdemo.data.timer.BLCycleTimerInfoV2;
import cn.com.broadlink.blappsdkdemo.data.timer.BLTimerAddOrEditParamV2;
import cn.com.broadlink.blappsdkdemo.data.timer.BLTimerDelInfoV2;
import cn.com.broadlink.blappsdkdemo.data.timer.BLTimerDelParamV2;
import cn.com.broadlink.blappsdkdemo.data.timer.BLTimerGetListParamV2;
import cn.com.broadlink.blappsdkdemo.data.timer.BLTimerGetListResultV2;
import cn.com.broadlink.blappsdkdemo.data.timer.BLTimerInfoV2;
import cn.com.broadlink.blappsdkdemo.data.timer.constant.BLTimerConstants;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLStdData;

public class DevTimerManageActivity extends TitleActivity {

    private RecyclerView mRvContent;
    private List<BLBaseTimerInfoV2> mTimerList = new ArrayList<>();
    private BLDNADevice mDNADevice;
    private DnaParamAdapter mAdapter;
    private ImageView mIvRefresh;
    private String mSubDid;
    private String[] mTimerTypeArray = {"Common","Delay","Period","Cycle","Rand", "SunSet"};
    private List<String> mTimerTempList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        setBackWhiteVisible();
        setTitle("Timer Management");
        
        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        mSubDid = getIntent().getStringExtra(BLConstants.INTENT_ID);
        
        findView();

        initView();

        setListener();

        mTimerTempList.add("10_10_10_%d_%d_*_%d"); // 单次定时
        mTimerTempList.add("10_10_10_%d_%d_*_%d"); // 延时定时
        mTimerTempList.add("10_10_10_*_*_*_*"); // 周期定时
        mTimerTempList.add("18_18_18_%d_%d_*_%d"); // 循环定时
        mTimerTempList.add("10_10_10_%d_%d_*_%d"); // 放到定时
        mTimerTempList.add("U-0_30_0_*_*_0,1,3,5_*"); // 日出日落
    }

    private void setListener() {
        setRightButtonOnClickListener("Add", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLListAlert.showAlert(mActivity, null, mTimerTypeArray, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(final int whichButton) {

                        String timeStr = mTimerTempList.get(0);
                     switch (whichButton){
                         case 0:
                         case 1:
                             final Date date = new Date();
                             timeStr = String.format(mTimerTempList.get(whichButton), date.getDate(),date.getMonth(), date.getYear()+1900);
                             showAddTimerDialog(mTimerTypeArray[whichButton], timeStr);
                             break;
                             
                         case 2:
                         case 5:
                             timeStr = mTimerTempList.get(whichButton);
                             showAddTimerDialog(mTimerTypeArray[whichButton], timeStr);
                             break;
                             
                         case 3:
                         case 4:
                             showCycleTimerSetDialog(mTimerTypeArray[whichButton]);
                             break;
                     }
                    }
                });
            }
        });

        mAdapter.setOnItemLongClickListener(new BLBaseRecyclerAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position, int viewType) {
                showDelTimerDialog(position);
                return true;
            }
        });

        mIvRefresh.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                new GetTimerListTask().execute();
            }
        });
    }

    private void initView() {
        mAdapter = new DnaParamAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvContent.setAdapter(mAdapter);
        mRvContent.addItemDecoration(BLDividerUtil.getDefault(mActivity, mTimerList));
    }

    private void findView() {
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mIvRefresh = findViewById(R.id.iv_refresh);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetTimerListTask().execute();
    }

    private void showCycleTimerSetDialog(final String type){
        final LinearLayout dialog = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_cycle_timer, null);
        final InputTextView etCmd1 = dialog.findViewById(R.id.et_cmd1);
        final InputTextView etCmd1Duration = dialog.findViewById(R.id.et_cmd1_duration);
        final InputTextView etCmd2 = dialog.findViewById(R.id.et_cmd2);
        final InputTextView etCmd2Duration = dialog.findViewById(R.id.et_cmd2_duration);
        final InputTextView etStartTime = dialog.findViewById(R.id.et_start_time);
        final InputTextView etEndTime = dialog.findViewById(R.id.et_end_time);

        final Date date = new Date();
        String timeStartStr = String.format(mTimerTempList.get(0), date.getDate(),date.getMonth(), date.getYear()+1900);
        String timeEndStr = String.format(mTimerTempList.get(3), date.getDate(),date.getMonth(), date.getYear()+1900);
        String cmd1Str = "{ \"pwr\":1}";
        String cmd2Str = "{ \"pwr\":0}";
        int cmd1duration = 5000;
        int cmd2duration = 2000;

        etCmd1.setText(cmd1Str);
        etCmd2.setText(cmd2Str);
        etCmd1Duration.setText(String.valueOf(cmd1duration));
        etCmd2Duration.setText(String.valueOf(cmd2duration));
        etStartTime.setText(timeStartStr);
        etEndTime.setText(timeEndStr);

        BLAlert.showCustomViewDilog(mActivity, dialog, "Ok", "Cancel", new BLAlert.DialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                if(TextUtils.isEmpty(etCmd1.getTextString()) 
                        || TextUtils.isEmpty(etCmd2.getTextString())
                        || TextUtils.isEmpty(etCmd1Duration.getTextString())
                        || TextUtils.isEmpty(etCmd2Duration.getTextString())
                        || TextUtils.isEmpty(etStartTime.getTextString())
                        || TextUtils.isEmpty(etEndTime.getTextString())){
                    BLToastUtils.show("Should not be null");
                }else{
                    final BLCycleTimerInfoV2 blTimerInfoV2 = new BLCycleTimerInfoV2();
                    blTimerInfoV2.setType(type);

                    final JSONObject cmdJsonObj = JSON.parseObject(etCmd1.getTextString());
                    final BLStdData blStdData = js2StdCmd(cmdJsonObj);
                    blTimerInfoV2.setCmd1(blStdData);
                    
                    final JSONObject cmdJsonObj2 = JSON.parseObject(etCmd2.getTextString());
                    final BLStdData blStdData2 = js2StdCmd(cmdJsonObj2);
                    blTimerInfoV2.setCmd2(blStdData2);
                    
                    
                    blTimerInfoV2.setTime1(etStartTime.getTextString());
                    blTimerInfoV2.setTime2(etEndTime.getTextString());
                    
                    blTimerInfoV2.setTime1(etCmd1Duration.getTextString());
                    blTimerInfoV2.setTime2(etCmd2Duration.getTextString());
                    
                    new AddTimerTask().execute(blTimerInfoV2);
                }
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }

    private void showAddTimerDialog(final String type, String timeStr){
        final LinearLayout dialog = (LinearLayout) getLayoutInflater().inflate(R.layout.view_dialog_dna_std_param, null);
        final EditText etParam = dialog.findViewById(R.id.et_param);
        final EditText etVal = dialog.findViewById(R.id.et_val);

        etParam.setHint("Time: S_M_H_D_M_W_Y");
        etParam.setText(timeStr);
        etVal.setText("{ \"pwr\":1}");

        BLAlert.showCustomViewDilog(mActivity, dialog, "Ok", "Cancel", new BLAlert.DialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                if(TextUtils.isEmpty(etParam.getText()) || TextUtils.isEmpty(etVal.getText())){
                    BLToastUtils.show("Should not be null");
                }else{
                    final BLTimerInfoV2 blTimerInfoV2 = new BLTimerInfoV2();
                    blTimerInfoV2.setType(type);

                    final JSONObject cmdJsonObj = JSON.parseObject(etVal.getText().toString());
                    final BLStdData blStdData = js2StdCmd(cmdJsonObj);
                    
                    blTimerInfoV2.setCmd(blStdData);
                    blTimerInfoV2.setTime(etParam.getText().toString());
                    new AddTimerTask().execute(blTimerInfoV2);
                }
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }

    private BLStdData js2StdCmd(JSONObject cmdJsonObj) {
        final BLStdData blStdData = new BLStdData();
        blStdData.setAct(null);
        final Set<Map.Entry<String, Object>> entries = cmdJsonObj.entrySet();
        final Iterator<Map.Entry<String, Object>> iterator = entries.iterator();

        while (iterator.hasNext()){
            final Map.Entry<String, Object> objectEntry = iterator.next();
            blStdData.getParams().add(objectEntry.getKey());
            final BLStdData.Value valueStd = new BLStdData.Value();
            valueStd.setVal(objectEntry.getValue());
            final ArrayList<BLStdData.Value> values = new ArrayList<>();
            values.add(valueStd);
            blStdData.getVals().add(values);
        }
        return blStdData;
    }


    private void showDelTimerDialog(final int pos){

       BLAlert.showDialog(mActivity, "Confirm to delete this timer?", new BLAlert.DialogOnClickListener() {
           @Override
           public void onPositiveClick() {
               new DelTimerTask().execute(pos);
           }

           @Override
           public void onNegativeClick() {

           }
       });
    }


    class DnaParamAdapter extends BLBaseRecyclerAdapter<BLBaseTimerInfoV2> {

        public DnaParamAdapter() {
            super(mTimerList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setText(R.id.tv_name, mBeans.get(position).getName());
            holder.setText(R.id.tv_mac, JSON.toJSONString(mBeans.get(position), true));
        }
    }

    private String doControl(Object blTimerGetListParamV2) {
        final String result = BLLet.Controller.dnaControl(mDNADevice.getDid(), null, JSON.toJSONString(blTimerGetListParamV2), BLTimerConstants.DNA_TIMER_ITF, null);
        return result;
    }

    class GetTimerListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Querying Timer List...");
        }

        @Override
        protected String doInBackground(Void... params) {
            
            final BLTimerGetListParamV2 blTimerGetListParamV2 = new BLTimerGetListParamV2();
            
            blTimerGetListParamV2.setDid(getDid());
            blTimerGetListParamV2.setCount(10);
            blTimerGetListParamV2.setIndex(0);
            blTimerGetListParamV2.setAct(BLTimerConstants.ACT.GET_LIST);
            blTimerGetListParamV2.setType(BLTimerConstants.TYPE.COMM);

            return doControl(blTimerGetListParamV2);
        }

        @Override
        protected void onPostExecute(String ret) {
            super.onPostExecute(ret);
            dismissProgressDialog();
            
            final BLTimerGetListResultV2 result = JSON.parseObject(ret, BLTimerGetListResultV2.class);
            if(result != null && result.succeed()){
                mTimerList.clear();
                mTimerList.addAll(result.data.timerlist);
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }

    }



    class AddTimerTask extends AsyncTask<BLBaseTimerInfoV2, Void, String> {
        BLBaseTimerInfoV2 blTimerInfoV2;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Adding...");
        }

        @Override
        protected String doInBackground(BLBaseTimerInfoV2... params) {

            blTimerInfoV2 = params[0];
            
            final BLTimerAddOrEditParamV2 blTimerGetListParamV2 = new BLTimerAddOrEditParamV2();
            blTimerGetListParamV2.setDid(getDid());
            blTimerGetListParamV2.setAct(BLTimerConstants.ACT.ADD);
            blTimerGetListParamV2.getTimerlist().add(params[0]);
            
            return doControl(blTimerGetListParamV2);
        }

        @Override
        protected void onPostExecute(String ret) {
            super.onPostExecute(ret);
            dismissProgressDialog();

            final BLBaseTimerResultV2 result = JSON.parseObject(ret, BLBaseTimerResultV2.class);
            if(result != null && result.succeed() && result.data.status==0){
                mTimerList.add(blTimerInfoV2);
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }
    }

    private String getDid() {
        return TextUtils.isEmpty(mSubDid) ? mDNADevice.getDid() : mSubDid;
    }

    class DelTimerTask extends AsyncTask<Integer, Void, String> {
        int pos;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Deleting...");
        }

        @Override
        protected String doInBackground(Integer... params) {
            pos = params[0];
            
            final BLTimerDelParamV2 blTimerGetListParamV2 = new BLTimerDelParamV2();
            blTimerGetListParamV2.setDid(getDid());
            blTimerGetListParamV2.setAct(BLTimerConstants.ACT.DEL);

            final BLBaseTimerInfoV2 blTimerInfoV2 = mTimerList.get(params[0]);
            final BLTimerDelInfoV2 blTimerDelInfoV2 = new BLTimerDelInfoV2();
            blTimerDelInfoV2.setType(blTimerInfoV2.getType());
            blTimerDelInfoV2.setId(blTimerInfoV2.getId());
            blTimerGetListParamV2.getTimerlist().add(blTimerDelInfoV2);

            return doControl(blTimerGetListParamV2);
        }

        @Override
        protected void onPostExecute(String ret) {
            super.onPostExecute(ret);
            dismissProgressDialog();

            final BLBaseTimerResultV2 result = JSON.parseObject(ret, BLBaseTimerResultV2.class);
            if(result != null && result.succeed() && result.data.status==0){
                mTimerList.remove(pos);
                mAdapter.notifyDataSetChanged();
            } else {
                BLCommonUtils.toastErr(result);
            }
        }
    }


}
