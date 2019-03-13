package cn.com.broadlink.blappsdkdemo.activity.check;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.com.broadlink.base.BLApiUrls;
import cn.com.broadlink.base.BLCommonTools;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.BLServiceCheckInfo;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.constants.controller.BLDeviceState;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class DeviceCheckActivity extends TitleActivity {

    private Button mCheckBtn;
    private TextView mServerView;
    private TextView mDidView;
    private TextView mPidView;
    private BLDNADevice mDNADevice;

    private ListView mServerListView;
    private List<BLServiceCheckInfo> mServers = new ArrayList<>();
    private DeviceCheckActivity.ServerAdapter mServerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_check_dev_list);

        setTitle(R.string.check_device);
        setBackWhiteVisible();

        mDNADevice = getIntent().getParcelableExtra(BLConstants.INTENT_DEVICE);

        findView();
        setListener();
    }

    private void findView() {
        mCheckBtn = (Button) findViewById(R.id.btn_start_check);
        mServerView = (TextView) findViewById(R.id.tv_device_remote_status);
        mDidView = (TextView) findViewById(R.id.tv_did);
        mPidView = (TextView) findViewById(R.id.tv_pid);

        mServerAdapter  = new DeviceCheckActivity.ServerAdapter(DeviceCheckActivity.this, mServers);
        mServerListView = (ListView) findViewById(R.id.server_list);
        mServerListView.setAdapter(mServerAdapter);

        mDidView.setText("Did: " + mDNADevice.getDid());
        mPidView.setText("Pid: " + mDNADevice.getPid());
    }

    private void setListener() {
        mCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new QueryDeviceServerConnectTask().execute();
                new networkCheckTask().execute();
            }
        });
    }

    private void paraseCheckResult(String result) {
        if (result == null) {
            return;
        }

        mServers.clear();

        try {
            JSONObject object = new JSONObject(result);

            Iterator<String> hosts = object.keys();
            while (hosts.hasNext()) {
                BLServiceCheckInfo info = new BLServiceCheckInfo();
                String host = hosts.next();

                JSONObject jsonObject = object.getJSONObject(host);
                JSONArray ipArray = jsonObject.getJSONArray("ip");
                String time = jsonObject.optString("time_total");

                info.setHost(host);
                info.setTime(time);

                List<String> iplist = new ArrayList<>();
                for (int i = 0; i < ipArray.length(); i++) {
                    iplist.add(ipArray.getString(i));
                }

                String[] ips = new String[iplist.size()];
                iplist.toArray(ips);
                info.setIps(ips);

                mServers.add(info);
            }

            mServerAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }
    }


    private class QueryDeviceServerConnectTask extends AsyncTask<Void, Void, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return BLLet.Controller.queryDeviceRemoteState(mDNADevice.getDid());
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == BLDeviceState.REMOTE) {
                mServerView.setText("Connect Service：online");
            } else {
                mServerView.setText("Connect Service：offline");
            }
        }
    }


    private class networkCheckTask extends AsyncTask<String, Void, String> {
        private BLProgressDialog mBLProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBLProgressDialog = BLProgressDialog.createDialog(DeviceCheckActivity.this, R.string.str_common_loading);
            mBLProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return BLApiUrls.checkDeviceHosts(mDNADevice.getType());
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mBLProgressDialog.dismiss();
            BLCommonTools.debug(result);
            paraseCheckResult(result);
        }
    }

    private class ServerAdapter extends ArrayAdapter<BLServiceCheckInfo> {
        public ServerAdapter(Context context, List<BLServiceCheckInfo> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DeviceCheckActivity.ServerAdapter.ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new DeviceCheckActivity.ServerAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_server, null);
                viewHolder.host = (TextView) convertView.findViewById(R.id.tv_host);
                viewHolder.ips = (TextView) convertView.findViewById(R.id.tv_address);
                viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (DeviceCheckActivity.ServerAdapter.ViewHolder) convertView.getTag();
            }

            BLServiceCheckInfo info = getItem(position);

            viewHolder.host.setText("Host: " + info.getHost());
            viewHolder.time.setText("Connect Time: " + info.getTime());

            StringBuffer sb = new StringBuffer();
            for(int i = 0;i<info.getIps().length;i++){
                sb.append(info.getIps()[i] + "  ");
            }
            viewHolder.ips.setText("IP: " + sb.toString());

            return convertView;
        }

        private class ViewHolder{
            TextView host;
            TextView ips;
            TextView time;
        }
    }

}
