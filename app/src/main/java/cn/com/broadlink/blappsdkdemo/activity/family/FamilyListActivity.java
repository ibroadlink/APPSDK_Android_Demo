package cn.com.broadlink.blappsdkdemo.activity.family;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.family.result.BLSFamilyInfo;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyListInterface;
import cn.com.broadlink.blappsdkdemo.mvp.presenter.CountryContentProvider;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.BLListAlert;
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

                BLListAlert.showAlert(FamilyListActivity.this, null, new String[]{"Create", "Scan"}, new BLListAlert.OnItemClickLister() {
                    @Override
                    public void onClick(int whichButton) {
                       switch (whichButton){
                           case 0:
                               Intent intent = new Intent();
                               intent.setClass(FamilyListActivity.this, FamilyAddActivity.class);
                               startActivity(intent);
                               break;
                           case 1:
                               BLCommonUtils.toActivity(FamilyListActivity.this, FamilyScanQrActivity.class);
                               break;

                       }
                    }
                });
            }
        });

        mFamilyIdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BLSFamilyInfo info = blsFamilyInfos.get(position);
                String familyId = info.getFamilyid();

                Intent intent = new Intent();
                intent.putExtra(BLConstants.INTENT_FAMILY_ID, familyId);
                intent.setClass(FamilyListActivity.this, FamilyDetailActivity.class);
                startActivity(intent);
            }
        });

        mFamilyIdListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                BLAlert.showDialog(FamilyListActivity.this, "Confirm to delete this family?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        BLLocalFamilyManager.getInstance().deleteFamily(blsFamilyInfos.get(i).getFamilyid());
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                return true;
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
        CountryContentProvider contentProvider;
        public FamilyListAdapter(Context context, List<BLSFamilyInfo> objects) {
            super(context, 0, objects);
            contentProvider = CountryContentProvider.getInstance();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_family, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.familyId = (TextView) convertView.findViewById(R.id.tv_mac);
                viewHolder.createUser = (TextView) convertView.findViewById(R.id.tv_create_user);
                viewHolder.locate = (TextView) convertView.findViewById(R.id.tv_locate);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BLSFamilyInfo info = getItem(position);

            viewHolder.name.setText(info.getName());
            viewHolder.familyId.setText("FamilyId: " + info.getFamilyid());
            viewHolder.createUser.setText("Master: " + info.getMaster());
            viewHolder.locate.setText(TextUtils.isEmpty(info.getCountryCode()) ? "Location: null" : String.format("Location: %s",
                    contentProvider.findContryInfoByCode(info.getCountryCode(), null, null)));
            return convertView;
        }

        private class ViewHolder{
            TextView name;
            TextView familyId;
            TextView createUser;
            TextView locate;
        }
    }

}
