package cn.com.broadlink.blappsdkdemo.activity.device;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    private EditText mTvResult;
    private TreeView mRvList;
    
    private BLDNADevice mDNADevice;
    private List<DefaultTreeNode<FastconBrigeBean.DeviceListBean>> mSubDeviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_fastcon_bridge);
        setBackWhiteVisible();
        setTitle("Fastcon Bridge");

        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_PARCELABLE);

        initData();
        
        findView();

        setListener();

        queryFastconDevList();
    }

    private void initData() {

    }
    
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

    private void findView() {
        mTvResult = (EditText) findViewById(R.id.et_result);
        mRvList = (TreeView) findViewById(R.id.rv_list);
    }

    private void initView(String ret) {
        final String fakeData = "{\"data\":{\"count\":6,\"deviceList\":[{\"mac\":\"ff:00:a2:2b:fe:42\",\"rssi\":-1," + "\"parentIndex\":65535},{\"mac\":\"01:c2:3b:78:e6:77\"," +
                "\"rssi\":0," + "\"parentIndex\":0},{\"mac\":\"05:f0:2b:9d:18:34\",\"rssi\":0,\"parentIndex\":0}," + "{\"mac\":\"00:00:00:00:00:00\",\"rssi\":0," +
                "\"parentIndex\":1}," + "{\"mac\":\"00" + ":00:00:00:00:00\",\"rssi\":0,\"parentIndex\":2}," + "{\"mac\":\"00:00:00:00:00:00\",\"rssi\":0,\"parentIndex\":1}]}," +
                "\"status\":0,\"msg\":\"success\"}";
        final ResultGetFastconBridge resultGetFastconBridge = BLJSON.parseObject(ret, ResultGetFastconBridge.class);

        final DefaultTreeNode<FastconBrigeBean.DeviceListBean> root = findNode(0xffff, null, resultGetFastconBridge.data.deviceList);
        if(root == null){
            return;
        }
        
        mRvList.setRoot(root);
        mRvList.setDefaultAnimation(true);
        mRvList.setTreeAdapter(new TreeAdapter<FastconBrigeBean.DeviceListBean>(mActivity, root, R.layout.item_fastcon_bridge){

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
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.tv_name.setText(BLJSON.toJSONString(node.getElement(), true));
                holder.tv_title.setText(String.valueOf(node.getDepth()));

                int depth = node.getDepth();
                setPadding(holder.tv_title, depth, -1);
                return convertView;
            }


            @Override
            public void toggle(Object... objects) {
                
            }

            class ViewHolder {
                TextView tv_name;
                TextView tv_title;
            }
        });
    }


    private void setListener() {

        setRightButtonOnClickListener("Retry", getResources().getColor(R.color.bl_yellow_main_color), new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                // retry
                queryFastconDevList();
            }
        });
    }


    private void queryFastconDevList() {
        new GetFastconBridgeDevsTask().executeOnExecutor(BLApplication.FULL_TASK_EXECUTOR);
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
            initView(blBaseResult);
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
