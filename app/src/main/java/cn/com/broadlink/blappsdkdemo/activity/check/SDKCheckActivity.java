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
import cn.com.broadlink.blappsdkdemo.data.BLServiceCheckInfo;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;

public class SDKCheckActivity extends TitleActivity {

    private Button mCheckBtn;
    private ListView mServerListView;
    private List<BLServiceCheckInfo> mServers = new ArrayList<>();
    private ServerAdapter mServerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdkcheck);

        setTitle(R.string.check_sdk);
        setBackWhiteVisible();

        findView();
        setListener();
    }

    private void findView() {
        mCheckBtn = (Button) findViewById(R.id.btn_start_check);

        mServerAdapter  = new ServerAdapter(SDKCheckActivity.this, mServers);
        mServerListView = (ListView) findViewById(R.id.server_list);
        mServerListView.setAdapter(mServerAdapter);
    }

    private void setListener() {

        mCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                //String time = jsonObject.optString("time_total");

                info.setHost(host);
                //info.setTime(time);

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

    private class networkCheckTask extends AsyncTask<String, Void, String> {
        private BLProgressDialog mBLProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBLProgressDialog = BLProgressDialog.createDialog(SDKCheckActivity.this, R.string.str_common_loading);
            mBLProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return BLApiUrls.checkApiUrlHosts();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mBLProgressDialog.dismiss();
            BLCommonTools.debug(result);
            paraseCheckResult(result);
        }
    }

    //设备列表适配器
    private class ServerAdapter extends ArrayAdapter<BLServiceCheckInfo> {
        public ServerAdapter(Context context, List<BLServiceCheckInfo> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SDKCheckActivity.ServerAdapter.ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new SDKCheckActivity.ServerAdapter.ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_server, null);
                viewHolder.host = (TextView) convertView.findViewById(R.id.tv_host);
                viewHolder.ips = (TextView) convertView.findViewById(R.id.tv_address);
                viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SDKCheckActivity.ServerAdapter.ViewHolder) convertView.getTag();
            }

            BLServiceCheckInfo info = getItem(position);

            viewHolder.host.setText("Host: " + info.getHost());
            //viewHolder.time.setText("Connect Time: " + info.getTime());

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
