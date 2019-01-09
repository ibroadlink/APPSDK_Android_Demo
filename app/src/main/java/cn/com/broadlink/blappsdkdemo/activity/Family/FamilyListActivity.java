package cn.com.broadlink.blappsdkdemo.activity.Family;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.Device.MyDeviceListActivity;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSFamilyInfo;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.BLDeviceInfoDao;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyListInterface;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;




public class FamilyListActivity extends TitleActivity implements FamilyListInterface {

    private ListView mFamilyIdListView;

    private List<BLSFamilyInfo> blsFamilyInfos = new ArrayList<>();
    private FamilyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_list);
        setTitle(R.string.str_settings_my_place);
        setBackWhiteVisible();

        findView();
        setListener();

        mAdapter  = new FamilyListAdapter(FamilyListActivity.this, blsFamilyInfos);
        mFamilyIdListView.setAdapter(mAdapter);

        BLLocalFamilyManager.getInstance().setFamilyListInterface(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFamilyListFormCloud();
    }

    @Override
    public void queryFamilyBaseInfoList(List<BLSFamilyInfo> list) {
        dismissProgressDialog();

        if (list != null) {
            blsFamilyInfos.clear();
            blsFamilyInfos.addAll(list);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void findView() {
        mFamilyIdListView = (ListView) findViewById(R.id.listview);
    }

    private void setListener(){
        setRightButtonOnClickListener(R.drawable.btn_add_cycle_white, new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FamilyListActivity.this, FamilyAddActivity.class);
                startActivity(intent);
            }
        });

        mFamilyIdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BLSFamilyInfo info = blsFamilyInfos.get(position);
                String familyId = info.getFamilyid();

                Intent intent = new Intent();
                intent.putExtra("INTENT_FAMILY_ID", familyId);
                intent.setClass(FamilyListActivity.this, FamilyDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取家庭基本信息列表
     */
    private void getFamilyListFormCloud() {
        showProgressDialog(getResources().getString(R.string.loading));
        BLLocalFamilyManager.getInstance().queryAllFamilyBaseInfo();
    }

    private class FamilyListAdapter extends ArrayAdapter<BLSFamilyInfo> {
        public FamilyListAdapter(Context context, List<BLSFamilyInfo> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.adapter_device, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.familyId = (TextView) convertView.findViewById(R.id.tv_mac);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BLSFamilyInfo info = getItem(position);

            viewHolder.name.setText(info.getName());
            viewHolder.familyId.setText(info.getFamilyid());

            return convertView;
        }

        private class ViewHolder{
            TextView name;
            TextView familyId;
        }
    }

}
