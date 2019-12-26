package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
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
    private TextView mTvBrief;
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
        mTvBrief = (TextView) findViewById(R.id.tv_brief);
    }

    private void initView() {
        
        // 模拟测试
        if(IS_FAKE_DATE){
            final String fakeData = "{\n" + "\t\"data\": {\n" + "\t\t\"count\": 37,\n" + "\t\t\"deviceList\": [{\n" + "\t\t\t\"mac\": \"c8:f7:42:fb:e2:ef\",\n" + "\t\t\t\"rssi\": 0,\n" + "\t\t\t\"parentIndex\": 65535\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:01\",\n" + "\t\t\t\"rssi\": -25,\n" + "\t\t\t\"parentIndex\": 14\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3c:ff\",\n" + "\t\t\t\"rssi\": -76,\n" + "\t\t\t\"parentIndex\": 20\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:34\",\n" + "\t\t\t\"rssi\": -78,\n" + "\t\t\t\"parentIndex\": 2\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"78:0f:77:e6:a7:c9\",\n" + "\t\t\t\"rssi\": -76,\n" + "\t\t\t\"parentIndex\": 3\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:26\",\n" + "\t\t\t\"rssi\": -64,\n" + "\t\t\t\"parentIndex\": 2\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:2e\",\n" + "\t\t\t\"rssi\": -76,\n" + "\t\t\t\"parentIndex\": 1\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:36\",\n" + "\t\t\t\"rssi\": -78,\n" + "\t\t\t\"parentIndex\": 1\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"34:ea:34:18:9d:2d\",\n" + "\t\t\t\"rssi\": -38,\n" + "\t\t\t\"parentIndex\": 7\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:3f\",\n" + "\t\t\t\"rssi\": -85,\n" + "\t\t\t\"parentIndex\": 2\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3c:d6\",\n" + "\t\t\t\"rssi\": -80,\n" + "\t\t\t\"parentIndex\": 20\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"78:0f:77:e6:a3:10\",\n" + "\t\t\t\"rssi\": -78,\n" + "\t\t\t\"parentIndex\": 1\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3c:e5\",\n" + "\t\t\t\"rssi\": -80,\n" + "\t\t\t\"parentIndex\": 7\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:4f\",\n" + "\t\t\t\"rssi\": -74,\n" + "\t\t\t\"parentIndex\": 3\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:0a\",\n" + "\t\t\t\"rssi\": 0,\n" + "\t\t\t\"parentIndex\": 0\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:2a\",\n" + "\t\t\t\"rssi\": -70,\n" + "\t\t\t\"parentIndex\": 12\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3c:bf\",\n" + "\t\t\t\"rssi\": -80,\n" + "\t\t\t\"parentIndex\": 15\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:4c\",\n" + "\t\t\t\"rssi\": -70,\n" + "\t\t\t\"parentIndex\": 15\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:1e\",\n" + "\t\t\t\"rssi\": -29,\n" + "\t\t\t\"parentIndex\": 10\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:3b\",\n" + "\t\t\t\"rssi\": -76,\n" + "\t\t\t\"parentIndex\": 15\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"78:0f:77:e6:a5:b4\",\n" + "\t\t\t\"rssi\": -69,\n" + "\t\t\t\"parentIndex\": 0\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3c:e9\",\n" + "\t\t\t\"rssi\": -72,\n" + "\t\t\t\"parentIndex\": 31\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"78:0f:77:e6:a7:2c\",\n" + "\t\t\t\"rssi\": -59,\n" + "\t\t\t\"parentIndex\": 20\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:0f\",\n" + "\t\t\t\"rssi\": -13,\n" + "\t\t\t\"parentIndex\": 22\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3c:e8\",\n" + "\t\t\t\"rssi\": -79,\n" + "\t\t\t\"parentIndex\": 7\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"78:0f:77:e6:a7:e0\",\n" + "\t\t\t\"rssi\": -55,\n" + "\t\t\t\"parentIndex\": 14\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:43\",\n" + "\t\t\t\"rssi\": 0,\n" + "\t\t\t\"parentIndex\": 0\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:39\",\n" + "\t\t\t\"rssi\": -56,\n" + "\t\t\t\"parentIndex\": 0\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:28\",\n" + "\t\t\t\"rssi\": -91,\n" + "\t\t\t\"parentIndex\": 30\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3c:f3\",\n" + "\t\t\t\"rssi\": -20,\n" + "\t\t\t\"parentIndex\": 32\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:46\",\n" + "\t\t\t\"rssi\": -60,\n" + "\t\t\t\"parentIndex\": 0\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:1f\",\n" + "\t\t\t\"rssi\": -75,\n" + "\t\t\t\"parentIndex\": 20\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:51\",\n" + "\t\t\t\"rssi\": -63,\n" + "\t\t\t\"parentIndex\": 28\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"24:df:a7:ac:3d:31\",\n" + "\t\t\t\"rssi\": -78,\n" + "\t\t\t\"parentIndex\": 30\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"78:0f:77:e6:a7:90\",\n" + "\t\t\t\"rssi\": 0,\n" + "\t\t\t\"parentIndex\": 0\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"78:0f:77:e6:a7:19\",\n" + "\t\t\t\"rssi\": 0,\n" + "\t\t\t\"parentIndex\": 0\n" + "\t\t}, {\n" + "\t\t\t\"mac\": \"78:0f:77:e6:a6:68\",\n" + "\t\t\t\"rssi\": 0,\n" + "\t\t\t\"parentIndex\": 0\n" + "\t\t}]\n" + "\t},\n" + "\t\"status\": 0,\n" + "\t\"msg\": \"success\"\n" + "}";
            
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
                final DefaultTreeNode<FastconBrigeBean.DeviceListBean> node = mNodesList.get(position);
                ViewHolder holder;

                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);
                    holder = new ViewHolder();
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
                    holder.iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
                    holder.bt_path = (Button) convertView.findViewById(R.id.bt_path);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.tv_name.setText(BLJSON.toJSONString(node.getElement(), true));
                holder.tv_count.setText(String.format("%d/%d", node.getDepth(), node.getSize()));
                
                holder.bt_path.setVisibility(node.isRoot() ? View.GONE : View.VISIBLE);

                holder.bt_path.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void doOnClick(View v) {
                        BLAlert.showAlert(mActivity, "Path", node.getPath(), new OnSingleClickListener() {
                            @Override
                            public void doOnClick(View v) {
                                //do nothing
                            }
                        });
                    }
                });

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
                Button bt_path;
            }
        };
        mVTree.setTreeAdapter(mAdapter);
        mVTree.performItemClick(mAdapter.getView(0, null, null), 0, 0);
        mTvBrief.setText(getTreeBriefInfo());
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
            
            if(TextUtils.isEmpty(item.getMac())){
                item.setMac(BLCommonUtils.parseMacFromDid(item.getEndpointId()));
            }
            
            if(mac.equalsIgnoreCase(item.getMac())){
                return item;
            }
        }
        return null;
    }
    
    private String getTreeBriefInfo(){
        final ArrayList<DefaultTreeNode> allNodesB = TreeUtils.getAllNodesB(mNodeRoot);
        if(allNodesB == null){
            return null;
        }
        
        int deepth = 0;
        for (DefaultTreeNode item : allNodesB) {
            deepth = Math.max(item.getDepth(), deepth);
        }
        return String.format("Count %d, Deepth %d", allNodesB.size(), deepth);
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

            @Override
            public String toString() {
                return TextUtils.isEmpty(name) ? mac : name;
            }

            public DeviceListBean() {
            }
        }
    }

}
