package cn.com.broadlink.blappsdkdemo.activity.Family;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyListInterface;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.family.params.BLFamilyBaseInfo;


public class FamilyListActivity extends TitleActivity implements FamilyListInterface {

    private ListView mFamilyIdListView;
    private List<String> mFamilyIdList = new ArrayList<>();
    private List<String> mFamilyNameList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_list);
        setTitle(R.string.str_settings_my_place);
        setBackWhiteVisible();

        findView();
        setListener();

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mFamilyNameList);
        mFamilyIdListView.setAdapter(mAdapter);

        BLLocalFamilyManager.getInstance().setFamilyListInterface(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFamilyListFormCloud();
    }

    @Override
    public void queryFamilyBaseInfoList(List<BLFamilyBaseInfo> list) {
        dismissProgressDialog();

        if (list != null) {

            mFamilyIdList.clear();
            mFamilyNameList.clear();

            for(int i = 0 ; i < list.size(); i ++){
                BLFamilyBaseInfo baseInfo = list.get(i);
                mFamilyIdList.add(baseInfo.getFamilyInfo().getFamilyId());
                mFamilyNameList.add(baseInfo.getFamilyInfo().getFamilyName());
            }
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
                Intent intent = new Intent();
                intent.putExtra("INTENT_FAMILY_ID", mFamilyIdList.get(position));
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

}
