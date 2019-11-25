package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
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
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
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
            final String fakeData = "{\"data\":{\"count\":6,\"deviceList\":[{\"mac\":\"ff:00:a2:2b:fe:42\",\"rssi\":-1," + "\"parentIndex\":65535},{\"mac\":\"01:c2:3b:78:e6:77\"," +
                    "\"rssi\":0," + "\"parentIndex\":0},{\"mac\":\"05:f0:2b:9d:18:34\",\"rssi\":0,\"parentIndex\":0}," + "{\"mac\":\"00:00:00:00:00:00\",\"rssi\":0," +
                    "\"parentIndex\":1}," + "{\"mac\":\"00" + ":00:00:00:00:00\",\"rssi\":0,\"parentIndex\":2}," + "{\"mac\":\"00:00:00:00:00:00\",\"rssi\":0,\"parentIndex\":1}]}," +
                    "\"status\":0,\"msg\":\"success\"}";
            final ResultGetFastconBridge resultGetFastconBridge = BLJSON.parseObject(fakeData, ResultGetFastconBridge.class);
            mNodeRoot = findNode(ROOT_NODE_FLAG, null, resultGetFastconBridge.data.deviceList);
            setupTreeAdapter();
        }else{
            queryFastconDevList();
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

    private void refreshView(String ret){
        final ResultGetFastconBridge resultGetFastconBridge = BLJSON.parseObject(ret, ResultGetFastconBridge.class);
        mNodeRoot = findNode(ROOT_NODE_FLAG, null, resultGetFastconBridge.data.deviceList);
        setupTreeAdapter();
    }

    private void setListener() {
        setRightButtonOnClickListener("Retry", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                queryFastconDevList();
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
     * 查询Bridge
     */
    private void queryFastconDevList() {
        new GetFastconBridgeDevsTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
    }

    /**
     * 查询Bridge
     **/
    private class GetFastconBridgeDevsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Get fastcon bridge devices...");
        }

        @Override
        protected String doInBackground(String... params) {
            return BLLet.Controller.dnaControl(mDNADevice.getDeviceId(), null, "{}",  BLDevCmdConstants.DEV_FASTCON_BRIDGE_DEVICES, null);
        }

        @Override
        protected void onPostExecute(String blBaseResult) {
            super.onPostExecute(blBaseResult);
            dismissProgressDialog();
            showResult(blBaseResult);
            refreshView(blBaseResult);
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

            public DeviceListBean() {
            }
        }
    }

}
