package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.base.fastjson.JSONObject;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.common.BLToastUtils;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.blsfamily.BLSFamily;
import cn.com.broadlink.blsfamily.bean.BLSBaseDataResult;
import cn.com.broadlink.blsfamily.bean.endpoint.BLSEndpointInfo;
import cn.com.broadlink.blsfamily.bean.endpoint.BLSEndpointListData;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.constants.controller.BLDevCmdConstants;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import moe.leer.tree2view.TreeUtils;
import moe.leer.tree2view.TreeView;
import moe.leer.tree2view.adapter.TreeAdapter;
import moe.leer.tree2view.module.DefaultTreeNode;

public class DevFastconBridgeActivity extends TitleActivity {
    private static final int ROOT_NODE_FLAG = 0xffff;
    private static final boolean IS_FAKE_DATE = false;
    private EditText mTvResult;
    private TreeView mVTree;
    private BLDNADevice mDNADevice;
    private DefaultTreeNode<FastconBrigeBean.DeviceListBean> mNodeRoot = null;
    private TreeAdapter<FastconBrigeBean.DeviceListBean> mAdapter;
    private List<BLSEndpointInfo> mEndpointInfos = new ArrayList<>();
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_fastcon_bridge);
        setBackWhiteVisible();
        setTitle("Fastcon Bridge");

        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);
        
        findView();

        initView();

        setListener();

    }
    
    private void findView() {
        mTvResult = (EditText) findViewById(R.id.et_result);
        mVTree = (TreeView) findViewById(R.id.v_list);
    }

    private void initView() {
        
        // 模拟测试
        if(IS_FAKE_DATE){
            final String fakeData = "{\"data\":{\"count\":33,\"deviceList\":[{\"mac\":\"c8:f7:42:fb:e2:ef\",\"parentIndex\":65535,\"rssi\":0},{\"mac\":\"78:0f:77:e6:a6:68\"," +
                    "\"parentIndex\":0,\"rssi\":0},{\"mac\":\"78:0f:77:e6:a7:90\",\"parentIndex\":0,\"rssi\":0},{\"mac\":\"24:df:a7:ac:3d:28\",\"parentIndex\":24,\"rssi\":-85}," +
                    "{\"mac\":\"24:df:a7:ac:3d:51\",\"parentIndex\":26,\"rssi\":-92},{\"mac\":\"24:df:a7:ac:3c:f3\",\"parentIndex\":26,\"rssi\":-82}," +
                    "{\"mac\":\"24:df:a7:ac:3d:36\",\"parentIndex\":28,\"rssi\":-79},{\"mac\":\"34:ea:34:18:9d:2d\",\"name\":\"未命名设备9d2d\",\"parentIndex\":8,\"rssi\":-43}," +
                    "{\"mac\":\"24:df:a7:ac:3c:e5\",\"parentIndex\":6,\"rssi\":-79},{\"mac\":\"78:0f:77:e6:a3:10\",\"parentIndex\":8,\"rssi\":-67}," +
                    "{\"mac\":\"24:df:a7:ac:3d:4c\",\"parentIndex\":2,\"rssi\":-63},{\"mac\":\"24:df:a7:ac:3c:e8\",\"parentIndex\":6,\"rssi\":-82}," +
                    "{\"mac\":\"78:0f:77:e6:a7:2c\",\"parentIndex\":0,\"rssi\":0},{\"mac\":\"24:df:a7:ac:3d:3f\",\"parentIndex\":12,\"rssi\":-62},{\"mac\":\"78:0f:77:e6:a6:60\"," +
                    "\"parentIndex\":13,\"rssi\":-68},{\"mac\":\"24:df:a7:ac:3d:0f\",\"parentIndex\":13,\"rssi\":-62},{\"mac\":\"24:df:a7:ac:3c:ff\",\"parentIndex\":28," +
                    "\"rssi\":-80},{\"mac\":\"24:df:a7:ac:3d:1f\",\"parentIndex\":30,\"rssi\":-84},{\"mac\":\"24:df:a7:ac:3c:e9\",\"parentIndex\":17,\"rssi\":-68}," +
                    "{\"mac\":\"24:df:a7:ac:3d:1e\",\"parentIndex\":21,\"rssi\":-68},{\"mac\":\"24:df:a7:ac:3d:08\",\"parentIndex\":12,\"rssi\":-65}," +
                    "{\"mac\":\"24:df:a7:ac:3c:d6\",\"parentIndex\":26,\"rssi\":-85},{\"mac\":\"24:df:a7:ac:3d:4f\",\"parentIndex\":16,\"rssi\":-87}," +
                    "{\"mac\":\"24:df:a7:ac:3d:34\",\"parentIndex\":22,\"rssi\":-83},{\"mac\":\"24:df:a7:ac:3d:43\",\"parentIndex\":0,\"rssi\":0},{\"mac\":\"24:df:a7:ac:3d:39\"," +
                    "\"parentIndex\":30,\"rssi\":-32},{\"mac\":\"24:df:a7:ac:3d:46\",\"parentIndex\":25,\"rssi\":-68},{\"mac\":\"24:df:a7:ac:3d:01\",\"parentIndex\":0," +
                    "\"rssi\":0},{\"mac\":\"24:df:a7:ac:3d:0a\",\"parentIndex\":27,\"rssi\":-27},{\"mac\":\"24:df:a7:ac:3d:31\",\"parentIndex\":16,\"rssi\":-80}," +
                    "{\"mac\":\"24:df:a7:ac:3d:2e\",\"parentIndex\":27,\"rssi\":-81},{\"mac\":\"78:0f:77:e6:a7:67\",\"parentIndex\":0,\"rssi\":0},{\"mac\":\"78:0f:77:e6:a8:12\"," +
                    "\"parentIndex\":13,\"rssi\":-76}]}," +
                    "\"status\":0,\"msg\":\"success\"}";
            
//            final String fakeData = "{\"data\":{\"count\":6,\"deviceList\":[{\"mac\":\"ff:00:a2:2b:fe:42\",\"rssi\":-1," + "\"parentIndex\":65535},{\"mac\":\"01:c2:3b:78:e6:77\"," +
//                    "\"rssi\":0," + "\"parentIndex\":0},{\"mac\":\"05:f0:2b:9d:18:34\",\"rssi\":0,\"parentIndex\":0}," + "{\"mac\":\"00:00:00:00:00:00\",\"rssi\":0," +
//                    "\"parentIndex\":1}," + "{\"mac\":\"00" + ":00:00:00:00:00\",\"rssi\":0,\"parentIndex\":2}," + "{\"mac\":\"00:00:00:00:00:00\",\"rssi\":0,\"parentIndex\":1}]}," +
//                    "\"status\":0,\"msg\":\"success\"}";
            
            final ResultGetFastconBridge resultGetFastconBridge = BLJSON.parseObject(fakeData, ResultGetFastconBridge.class);
            refreshView(resultGetFastconBridge.data.deviceList);
            
            
        }else{
            queryFamilyDevList();
        }
    }

    private void setupTreeAdapter() {
        if (mNodeRoot == null) {
            return;
        }
        
        mVTree.setRoot(mNodeRoot);
        mVTree.setDefaultAnimation(false);
        mAdapter = new TreeAdapter<FastconBrigeBean.DeviceListBean>(mActivity, mNodeRoot, R.layout.item_fastcon_bridge) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (mNodesList == null) {
                    mNodesList = TreeUtils.getVisibleNodesD(super.mRoot);
                    notifyDataSetChanged();
                }
                DefaultTreeNode<FastconBrigeBean.DeviceListBean> node = mNodesList.get(position);
                ViewHolder holder;

                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);
                    holder = new ViewHolder();
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
                    holder.iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.tv_name.setText(BLJSON.toJSONString(node.getElement(), true));
                holder.tv_count.setText(String.format("%d/%d", node.getDepth(), node.getSize()));

                int depth = node.getDepth();
                setPadding(holder.iv_title, depth, -1);
                toggle(node, holder);
                return convertView;
            }

            @Override
            public void toggle(Object... objects) {
                DefaultTreeNode node = null;
                ViewHolder holder = null;
                try {
                    node = (DefaultTreeNode) objects[0];
                    holder = (ViewHolder) objects[1];
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }

                if (node.isExpandable() && node.getSize()!=0) {
                    if (!node.isExpanded()) {
                        //set right arrowIcon
                        holder.iv_title.setImageResource(R.drawable.icon_tri_right);
                    } else {
                        //set down arrowIcon
                        holder.iv_title.setImageResource(R.drawable.icon_tri_down);
                    }
                } else {
                    holder.iv_title.setImageResource(R.drawable.icon_point);
                }
            }

            class ViewHolder {
                TextView tv_name;
                TextView tv_count;
                ImageView iv_title;
            }
        };
        mVTree.setTreeAdapter(mAdapter);
        mVTree.performItemClick(mAdapter.getView(0, null, null), 0, 0);
    }

    private void refreshView(List<FastconBrigeBean.DeviceListBean> ret){
        if(ret == null ){
            return;
        }
        
        mNodeRoot = findNode(ROOT_NODE_FLAG, null, ret);
        setupTreeAdapter();
    }

    private void setListener() {
        setRightButtonOnClickListener("Retry", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                queryFamilyDevList();
            }
        });
    }
 
    private void showResult(Object result) {
        if (result == null) {
            mTvResult.setText("Return null");
        } else {
            if (result instanceof String) {
                mTvResult.setText((String) result);
            } else {
                mTvResult.setText(JSON.toJSONString(result, true));
            }
        }
    }
    
    private BLSEndpointInfo getEndpointByMac(String mac){
        if (TextUtils.isEmpty(mac) || mEndpointInfos == null) {
            return null;
        }

        for (BLSEndpointInfo item : mEndpointInfos) {
            if(item.getMac().equalsIgnoreCase(mac)){
                return item;
            }
        }
        return null;
    }

    /**
     * 递归，整理出树形结构
     */
    private DefaultTreeNode<FastconBrigeBean.DeviceListBean> findNode(int index, DefaultTreeNode<FastconBrigeBean.DeviceListBean> item, List<FastconBrigeBean.DeviceListBean> list){
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).parentIndex == index){
                final DefaultTreeNode node = new DefaultTreeNode(list.get(i));
                if(item == null){
                    item = node;
                }else{
                    item.addChild(node);
                }
                findNode(i, node, list);
            }
        }
        return item;
    }


    /**
     * 查询家庭下设备列表
     */
    private void queryFamilyDevList() {
        new QueryEndpointListTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR, BLLocalFamilyManager.getInstance().getCurrentFamilyId());
    }

    /**
     * 查询Bridge
     */
    private void queryFastconDevList() {
        new GetFastconBridgeDevsTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }

    /**
     * 查询Bridge
     **/
    private class GetFastconBridgeDevsTask extends AsyncTask<String, Void, String> {
        List<FastconBrigeBean.DeviceListBean> ret = null;
                
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Get fastcon bridge devices...");
        }
        
        private String getData(int index, List<FastconBrigeBean.DeviceListBean> ret){
            final JSONObject paramObj = new JSONObject();
            paramObj.put("index", index);
            final String result = BLLet.Controller.dnaControl(mDNADevice.getDeviceId(), null, paramObj.toString(), BLDevCmdConstants.DEV_FASTCON_BRIDGE_DEVICES, null);
            final ResultGetFastconBridge resultGetFastconBridge = BLJSON.parseObject(result, ResultGetFastconBridge.class);
            if(ret == null){
                ret = new ArrayList<>();
            }
            
            if(resultGetFastconBridge == null || !resultGetFastconBridge.succeed() || resultGetFastconBridge.data.deviceList == null){
                return result;
            }
            
            ret.addAll(resultGetFastconBridge.data.deviceList);

            // 从家庭设备列表获得name字段
            for (FastconBrigeBean.DeviceListBean deviceListBean : ret) {
                final BLSEndpointInfo endpoint = getEndpointByMac(deviceListBean.mac);
                if(endpoint != null){
                    deviceListBean.name = endpoint.getFriendlyName();
                }
            }
            
            if(resultGetFastconBridge.data.deviceList.size()+index < resultGetFastconBridge.data.count){
                getData(index + resultGetFastconBridge.data.deviceList.size(), ret);
            }
            this.ret = ret;
            return BLJSON.toJSONString(ret);
        }

        @Override
        protected String doInBackground(String... params) {
            return getData(0, ret);
        }

        @Override
        protected void onPostExecute(String blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
            refreshView(ret);
        }
    }


    private class QueryEndpointListTask extends AsyncTask<String, Void, BLSBaseDataResult<BLSEndpointListData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Query Family Endpoint List...");
        }

        @Override
        protected BLSBaseDataResult<BLSEndpointListData> doInBackground(String... strings) {
            String familyId = strings[0];

            return BLSFamily.Endpoint.getList(familyId);
        }

        @Override
        protected void onPostExecute(BLSBaseDataResult<BLSEndpointListData>  result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if (result != null && result.succeed() && result.getData() != null) {
                mEndpointInfos.clear();

                if (result.getData().getEndpoints() != null) {
                    mEndpointInfos.addAll(result.getData().getEndpoints());
                }else{
                    BLToastUtils.show("家庭设备列表为空");
                }
                queryFastconDevList();
            }else{
                BLCommonUtils.toastErr(result);
            }
        }
    }
    
    public static class ResultGetFastconBridge extends BLBaseResult{
        public FastconBrigeBean data;

        public ResultGetFastconBridge() {
        }
    }
    
    public static final class FastconBrigeBean{

        public int count;
        public List<DeviceListBean> deviceList = new ArrayList<>();

        public FastconBrigeBean() {
        }

        public static final class DeviceListBean {
            public String mac;
            public int parentIndex;
            public int rssi;
            public String name;

            public DeviceListBean() {
            }
        }
    }

}
