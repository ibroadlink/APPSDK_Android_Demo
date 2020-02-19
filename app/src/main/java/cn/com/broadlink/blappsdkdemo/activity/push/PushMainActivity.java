package cn.com.broadlink.blappsdkdemo.activity.push;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLDevSrvConstans;
import cn.com.broadlink.blappsdkdemo.common.BLProfileTools;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.data.BLDevProfileInfo;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageConditionTimeInfo;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageConditionsInfo;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageDevPropertyInfo;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageDeviceExtend;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageDevicesInfo;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageInfo;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageSubscribeDevBaseInfo;
import cn.com.broadlink.blappsdkdemo.data.link.LinkageTriggerAttributeInfo;
import cn.com.broadlink.blappsdkdemo.data.link.QueryLinkageListResult;
import cn.com.broadlink.blappsdkdemo.data.push.NotificationTemplateResult;
import cn.com.broadlink.blappsdkdemo.intferfacer.BasePushListener;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.BLPushServicePresenter;
import cn.com.broadlink.blappsdkdemo.service.BLLocalDeviceManager;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

import static cn.com.broadlink.blappsdkdemo.data.link.LinkageInfo.SOURCE_NOTIFY;

/**
 * Desc
 *
 * @author JiangYaqiang
 * 2019/4/19 17:23
 */
public class PushMainActivity extends TitleActivity {

    private EditText mEtResult;
    private EditText mEtDevice;
    private Button mBtReportToken;
    private Button mBtPushSwitch;
    private Button mBtLogout;
    private Button mBtGetTempList;
    private Button mBtGetLinkList;
    private RecyclerView mRvList;
    private BLPushServicePresenter mBLPushServicePresenter;
    private List<Object> mTemplateOrLinkList = new ArrayList<>();
    private MyAdapter mAdapter;
    private BLDNADevice mDeviceInfo;
    private String mCat = "108.222";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_push_main);
        setBackWhiteVisible();
        setTitle("Push Main");

        initData();

        findView();

        initView();

        setListener();
    }

    private void findView() {
        mEtResult = (EditText) findViewById(R.id.et_result);
        mEtDevice = (EditText) findViewById(R.id.et_device);
        mBtReportToken = (Button) findViewById(R.id.bt_report_token);
        mBtPushSwitch = (Button) findViewById(R.id.bt_push_switch);
        mBtLogout = (Button) findViewById(R.id.bt_logout);
        mBtGetTempList = (Button) findViewById(R.id.bt_get_temp_list);
        mBtGetLinkList = (Button) findViewById(R.id.bt_get_link_list);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
    }

    private void initData() {
        mBLPushServicePresenter = BLPushServicePresenter.getInstance(mActivity);
    }

    private void initView() {
        mAdapter = new MyAdapter();
        mRvList.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvList.addItemDecoration(BLDividerUtil.getDefault(mActivity, mTemplateOrLinkList));
        mRvList.setAdapter(mAdapter);
    }

    private void setListener() {
        
        mAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(final int position, int viewType) {

                final Object bean = mTemplateOrLinkList.get(position);
                // 模版
                if(bean instanceof NotificationTemplateResult.TemplatesBean){
                    instantiateTemplte((NotificationTemplateResult.TemplatesBean) bean);
                }else{ // 联动
                    BLAlert.showDialog(mActivity, "Confirm to delete this linkage?", new BLAlert.DialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            final String ruleid = ((LinkageInfo) bean).getRuleid();
                            mBLPushServicePresenter.delLink(ruleid, new BasePushListener() {
                                @Override
                                public void onCallBack(String result) {

                                    try {
                                        final BLBaseResult blBaseResult = JSON.parseObject(result, BLBaseResult.class);
                                        if(blBaseResult != null && blBaseResult.succeed()){
                                            doQueryLinkList();
                                            return;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    final StringBuffer stringBuffer = new StringBuffer();
                                    stringBuffer.append("Delete Linkage: " + ruleid).append("\n ");
                                    stringBuffer.append("Return:\n").append(result);
                                    showResult(stringBuffer.toString());
                                }
                            });
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    });
                }
            }
        });
        
        
        mBtReportToken.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                mBLPushServicePresenter.reportToken(new BasePushListener() {
                    @Override
                    public void onCallBack(String msg) {
                        final StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("Token: " + mBLPushServicePresenter.getToken()).append("\n ");
                        stringBuffer.append("Return:\n").append(msg);
                        showResult(stringBuffer.toString());
                    }
                });
            }
        });
        
        mBtPushSwitch.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                final String[] items = {"Enable", "Disable"};
                BLListAlert.showAlert(mActivity, null, items, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(final int whichButton) {
                        mBLPushServicePresenter.setPushEnable(whichButton == 0, new BasePushListener() {
                            @Override
                            public void onCallBack(String msg) {
                                final StringBuffer stringBuffer = new StringBuffer();
                                stringBuffer.append("Set : ").append(items[whichButton]).append("\n ");
                                stringBuffer.append("Return:\n").append(msg);
                                showResult(stringBuffer.toString());
                            }
                        });
                    }
                });
            }
        });

        mBtLogout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                mBLPushServicePresenter.logoutUser( new BasePushListener() {
                    @Override
                    public void onCallBack(String msg) {
                        final StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("Logout").append("\n ");
                        stringBuffer.append("Return:\n").append(msg);
                        showResult(stringBuffer.toString());
                    }
                });
            }
        });


        mBtGetTempList.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {

                if (mDeviceInfo == null) {
                    final List<BLDNADevice> devicesAddInSDK = BLLocalDeviceManager.getInstance().getDevicesAddInSDK();
                    if (devicesAddInSDK != null && !devicesAddInSDK.isEmpty()) {
                        final String[] dids = new String[devicesAddInSDK.size()];
                        for (int i = 0; i < devicesAddInSDK.size(); i++) {
                            dids[i] = devicesAddInSDK.get(i).getName() + "\n" + devicesAddInSDK.get(i).getDid();
                        }
                        BLListAlert.showAlert(mActivity, "Select device", dids, new BLListAlert.OnItemClickLister() {
                            @Override
                            public void onClick(int whichButton) {
                                mDeviceInfo = devicesAddInSDK.get(whichButton);
                                
                                BLDevProfileInfo profileInfo = BLProfileTools.queryProfileInfoByPid(mDeviceInfo.getPid());
                                if(profileInfo == null){
                                    BLToastUtils.show("No script found, download first.");
                                    return;
                                }
                                mCat = BLDevSrvConstans.getDevFirstCategory(profileInfo.getSrvs().get(0))+"."+BLDevSrvConstans.getDevCategory(profileInfo.getSrvs().get(0));
                                mEtDevice.setText("Category: " + mCat + "\n" + mDeviceInfo.toJSONString());
                                showCatDialog();
                            }
                        });
                    } else {
                        BLToastUtils.show("Devices added to sdk is empty, add one first!");
                    }
                } else {
                    showCatDialog();
                }
            }
        });

        mBtGetLinkList.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                doQueryLinkList();
            }
        });
    }

    private void showCatDialog() {
        BLAlert.showEditDilog(mActivity, "Input category", mCat, new BLAlert.BLEditDialogOnClickListener() {
            @Override
            public void onClink(String value) {
                mCat = value;
                doQueryTemplist(value);
            }

            @Override
            public void onCancel(String value) {
            }
        }, true);
    }

    private void doQueryLinkList() {
        mBLPushServicePresenter.queryLink( new BasePushListener() {
            @Override
            public void onCallBack(String msg) {
                try {
                    final QueryLinkageListResult result = JSON.parseObject(msg, QueryLinkageListResult.class);
                    if(result != null && result.isSuccess()){
                        mTemplateOrLinkList.clear();
                        if(result.getLinkages() != null){
                            mTemplateOrLinkList.addAll(result.getLinkages());
                        }
                        mAdapter.notifyDataSetChanged();
                        showResult("Get Linkage List Success");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showResult(msg);
            }
        });
    }

    private void doQueryTemplist(String value) {
        mBLPushServicePresenter.queryTempList(value, new BasePushListener() {
            @Override
            public void onCallBack(String msg) {
                try {
                    final NotificationTemplateResult result = JSON.parseObject(msg, NotificationTemplateResult.class);
                    if(result != null && result.isSuccess()){
                        mTemplateOrLinkList.clear();
                        if(result.getTemplates() != null){
                            mTemplateOrLinkList.addAll(result.getTemplates());
                        }
                        mAdapter.notifyDataSetChanged();
                        showResult("Get Template List Success");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showResult(msg);
            }
        });
    }

    private void showResult(Object result) {
        String showMsg = "Return null";
        if (result != null) {
            if (result instanceof String) {
                try {
                    final JSONObject jsonObject = JSON.parseObject((String) result);
                    showMsg = JSON.toJSONString(jsonObject, true);
                } catch (Exception e) {
                    showMsg = (String) result;
                }
            } else {
                showMsg = JSON.toJSONString(result, true);
            }
        }
        
        mEtResult.setText(showMsg);
    }

    //获取模板名称
    private String getTemplateName(NotificationTemplateResult.TemplatesBean templateData) {
        List<NotificationTemplateResult.TemplatesBean.TemplatenameBean> templatenameBeanList = templateData.getTemplatename();
        String templateName = "";
        for(NotificationTemplateResult.TemplatesBean.TemplatenameBean templatenameBean : templatenameBeanList){
            if(BLCommonUtils.getLanguage().equalsIgnoreCase(templatenameBean.getLanguage())){
                templateName = templatenameBean.getName();
            }
        }

        if(TextUtils.isEmpty(templateName)){
            templateName = templatenameBeanList.get(0).getName();
        }

        return templateName;
    }


    class MyAdapter extends BLBaseRecyclerAdapter<Object> {

        public MyAdapter() {
            super(mTemplateOrLinkList, R.layout.item_family_room);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            final Object bean = mTemplateOrLinkList.get(position);
            if(bean instanceof NotificationTemplateResult.TemplatesBean){
                holder.setText(R.id.tv_name, getTemplateName((NotificationTemplateResult.TemplatesBean) bean));
            }else{
                holder.setText(R.id.tv_name, JSON.toJSONString(bean, true));
            }
        }
    }

    //实例化模板
    private void instantiateTemplte(NotificationTemplateResult.TemplatesBean templatesBean) {
        LinkageInfo linkageInfo = new LinkageInfo();
        linkageInfo.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyInfo().getFamilyid());
        linkageInfo.setRuletype(LinkageInfo.RULE_TYPE_CHARACTERISTIC);
        linkageInfo.setRulename("RuleEcho");
        linkageInfo.setEnable(1);
        linkageInfo.setSource(SOURCE_NOTIFY + templatesBean.getTemplateid());
        
        List<String> moduleidList = new ArrayList<>();
        moduleidList.add(""); // 不加的话云端会返回“数据错误”
        linkageInfo.setModuleid(moduleidList);
       
        LinkageDevicesInfo linkageDevicesInfo = new LinkageDevicesInfo();
        linkageDevicesInfo.setLinkagetype("notify");
        linkageDevicesInfo.setDid(mDeviceInfo.getDid());
        
        LinkageDeviceExtend linkageDeviceExtend = new LinkageDeviceExtend();
        changeAction(true,templatesBean);
        linkageDeviceExtend.setAction(templatesBean.getAction());
        linkageDevicesInfo.setExtern(BLCommonUtils.Base64(JSON.toJSONString(linkageDeviceExtend).getBytes()));
        
        LinkageTriggerAttributeInfo attributeInfo = getAttributeInfo(templatesBean);
        linkageInfo.setCharacteristicinfo(JSON.toJSONString(attributeInfo));

        addSubscribe(linkageInfo,attributeInfo);
        linkageInfo.setLinkagedevices(linkageDevicesInfo);
      

        mBLPushServicePresenter.addLink(linkageInfo, new BasePushListener() {
            @Override
            public void onCallBack(String msg) {
                showResult(msg);
            }
        });
    }

    //修改推送参数
    public void changeAction(boolean b, NotificationTemplateResult.TemplatesBean templateData) {
        try {
            NotificationTemplateResult.TemplatesBean.ActionBean action = templateData.getAction().get(0);
            action.setLanguage(BLCommonUtils.getLanguage());
            action.setTemplatetype(templateData.getTemplatetype());

            //修改阿里推送
            NotificationTemplateResult.TemplatesBean.ActionBean.AlicloudBean alicloudBean = action.getAlicloud();
            alicloudBean.setEnable(b);
            alicloudBean.setDid(mDeviceInfo.getDid());
            List<String> aliKeyList = alicloudBean.getKeylist();
            org.json.JSONObject jsonObject = new org.json.JSONObject();
            if(aliKeyList != null && aliKeyList.size() > 0 && aliKeyList.contains("name")){
                jsonObject.put("name",mDeviceInfo.getName());
            }else{
                jsonObject.put("name","name-null");
            }
            jsonObject.put("location", "BedRoom");
            alicloudBean.setContent(BLCommonUtils.Base64(jsonObject.toString().getBytes()));


            //修改gcm推送
            NotificationTemplateResult.TemplatesBean.ActionBean.GcmBean gcmBean = action.getGcm();
            gcmBean.setEnable(b);
            gcmBean.setDid(mDeviceInfo.getDid());
            List<String> gcmKeyList = gcmBean.getKeylist();
            org.json.JSONObject gcmJsonObject = new org.json.JSONObject();
            if(gcmKeyList != null && gcmKeyList.size() > 0 && gcmKeyList.contains("name")){
                gcmJsonObject.put("name",mDeviceInfo.getName());
            }else{
                jsonObject.put("name","name-null");
            }
            gcmJsonObject.put("location", "BedRoom");
            gcmBean.setContent(BLCommonUtils.Base64(gcmJsonObject.toString().getBytes()));

            //修改ios推送
            NotificationTemplateResult.TemplatesBean.ActionBean.IosBean iosBean = action.getIos();
            iosBean.setEnable(b);
            iosBean.setDid(mDeviceInfo.getDid());
            List<String> iosKeyList = iosBean.getKeylist();
            org.json.JSONObject iosJsonObject = new org.json.JSONObject();
            if(iosKeyList != null && iosKeyList.size() > 0 && iosKeyList.contains("name")){
                iosJsonObject.put("name",mDeviceInfo.getName());
            }else{
                iosJsonObject.put("name","name-null");
            }
            iosJsonObject.put("location", "BedRoom");
            iosBean.setContent(BLCommonUtils.Base64(iosJsonObject.toString().getBytes()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private LinkageTriggerAttributeInfo getAttributeInfo(NotificationTemplateResult.TemplatesBean templateData) {
        LinkageTriggerAttributeInfo attributeInfo = new LinkageTriggerAttributeInfo();
        LinkageConditionsInfo conditionsinfo = getLinkageConditionsInfo(templateData);
        attributeInfo.setConditionsinfo(conditionsinfo);
        List<LinkageDevPropertyInfo> eventsList = getLinkageDevPropertyInfos(templateData);
        attributeInfo.setEvents(eventsList);
        return attributeInfo;
    }


    private LinkageConditionsInfo getLinkageConditionsInfo(NotificationTemplateResult.TemplatesBean templateData) {
        LinkageConditionsInfo conditionsinfo = new LinkageConditionsInfo();
        ArrayList<LinkageConditionTimeInfo> datetime = new ArrayList<>();
        List<NotificationTemplateResult.TemplatesBean.ConditionsinfoBean.DatetimeBean> datetimeBeans = templateData.getConditionsinfo().getDatetime();
        if(datetimeBeans != null){
            for(NotificationTemplateResult.TemplatesBean.ConditionsinfoBean.DatetimeBean datetimeBean : datetimeBeans){
                LinkageConditionTimeInfo timeInfo = new LinkageConditionTimeInfo();
                if(datetimeBean.getValidperiod().size() > 0){
                    timeInfo.setTimezone(datetimeBean.getTimezone());
                    timeInfo.setValidperiod(datetimeBean.getValidperiod());
                    if(!TextUtils.isEmpty(datetimeBean.getWeekdays())){
                        timeInfo.setWeekdays(datetimeBean.getWeekdays());
                    }else{
                        timeInfo.setWeekdays("1234567");
                    }
                    datetime.add(timeInfo);
                }
            }
        }
        conditionsinfo.setDatetime(datetime);
        List<LinkageDevPropertyInfo> propertyInfoList = new ArrayList<>();
        List<NotificationTemplateResult.TemplatesBean.ConditionsinfoBean.PropertyBean>  propertyBeans = templateData.getConditionsinfo().getProperty();
        if(propertyBeans != null){
            for(NotificationTemplateResult.TemplatesBean.ConditionsinfoBean.PropertyBean propertyBean : propertyBeans){
                LinkageDevPropertyInfo devPropertyInfo = new LinkageDevPropertyInfo();
                devPropertyInfo.setDev_name(mDeviceInfo.getName());
                devPropertyInfo.setIdev_did(mDeviceInfo.getDid());
                devPropertyInfo.setIkey(propertyBean.getIkey());
                devPropertyInfo.setRef_value(propertyBean.getRef_value());
                devPropertyInfo.setRef_value_name(propertyBean.getRef_value_name());
                devPropertyInfo.setTrend_type(propertyBean.getTrend_type());
                propertyInfoList.add(devPropertyInfo);
            }
        }

        conditionsinfo.setProperty(propertyInfoList);
        return conditionsinfo;
    }


    private List<LinkageDevPropertyInfo> getLinkageDevPropertyInfos(NotificationTemplateResult.TemplatesBean templateData) {
        List<LinkageDevPropertyInfo> eventsList = new ArrayList<>();
        List<NotificationTemplateResult.TemplatesBean.EventsBean> eventsBeans =  templateData.getEvents();
        if(eventsBeans != null){
            for(NotificationTemplateResult.TemplatesBean.EventsBean eventsBean : eventsBeans){
                LinkageDevPropertyInfo linkageDevPropertyInfo = new LinkageDevPropertyInfo();
                linkageDevPropertyInfo.setType(eventsBean.getType());
                linkageDevPropertyInfo.setTrend_type(eventsBean.getTrend_type());
                linkageDevPropertyInfo.setRef_value(eventsBean.getRef_value());
                linkageDevPropertyInfo.setRef_value_name(eventsBean.getRef_value_name());
                linkageDevPropertyInfo.setKeeptime(eventsBean.getKeeptime());
                linkageDevPropertyInfo.setIkey(eventsBean.getIkey());
                linkageDevPropertyInfo.setDev_name(mDeviceInfo.getName());
                linkageDevPropertyInfo.setIdev_did(mDeviceInfo.getDid());
                eventsList.add(linkageDevPropertyInfo);
            }
        }
        return eventsList;
    }


    private void addSubscribe(LinkageInfo linkageInfo, LinkageTriggerAttributeInfo attributeInfo) {
        //添加触发条件设备
        List<LinkageSubscribeDevBaseInfo> devList = new ArrayList<>();
        for (LinkageDevPropertyInfo propertyInfo : attributeInfo.getConditionsinfo().getProperty()) {
            BLDNADevice deviceInfo = BLLocalDeviceManager.getInstance().getCachedDevice(propertyInfo.getIdev_did());
            if(!TextUtils.isEmpty(deviceInfo.getpDid())){
                BLDNADevice pDevInfo = BLLocalDeviceManager.getInstance().getCachedDevice(deviceInfo.getpDid());
                linkageInfo.insertDevInfo(devList, deviceInfo, pDevInfo);
            }else{
                linkageInfo.insertDevInfo(devList, deviceInfo, null);
            }
        }

        //添加触发事件设备
        for (LinkageDevPropertyInfo propertyInfo : attributeInfo.getEvents()) {
            BLDNADevice deviceInfo = BLLocalDeviceManager.getInstance().getCachedDevice(propertyInfo.getIdev_did());
            if(!TextUtils.isEmpty(deviceInfo.getpDid())){
                BLDNADevice pDevInfo = BLLocalDeviceManager.getInstance().getCachedDevice(deviceInfo.getpDid());
                linkageInfo.insertDevInfo(devList, deviceInfo, pDevInfo);
            }else{
                linkageInfo.insertDevInfo(devList, deviceInfo, null);
            }
        }

        linkageInfo.setSubscribe(devList);
    }


}
