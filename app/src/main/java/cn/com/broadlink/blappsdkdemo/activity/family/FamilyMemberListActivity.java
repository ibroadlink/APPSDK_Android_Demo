package cn.com.broadlink.blappsdkdemo.activity.family;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.activity.family.manager.BLSFamilyManager;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.FamilyMember;
import cn.com.broadlink.blappsdkdemo.data.ParamDeleteFamilyMember;
import cn.com.broadlink.blappsdkdemo.view.BLAlert;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;


public class FamilyMemberListActivity extends TitleActivity {

    private ListView mMemberListView;
    private List<FamilyMember> blsMemberInfos = new ArrayList<>();
    private MemberListAdapter mAdapter;
    private String mFamilyId = null;
    private String mOwnerId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_room_list);
        setTitle("Manage Members");
        setBackWhiteVisible();

        findView();
        
        setListener();

        mAdapter  = new MemberListAdapter(FamilyMemberListActivity.this, blsMemberInfos);
        mMemberListView.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            mFamilyId = getIntent().getStringExtra(BLConstants.INTENT_FAMILY_ID);
            mOwnerId = getIntent().getStringExtra(BLConstants.INTENT_NAME);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new QueryMemberListTask().execute(mFamilyId);
    }

    private void findView() {
        mMemberListView = (ListView) findViewById(R.id.room_listview);
    }

    private void setListener(){
        setRightButtonOnClickListener(R.drawable.btn_add_cycle_white, new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                BLCommonUtils.toActivity(FamilyMemberListActivity.this, FamilyQrShareActivity.class);
            }
        });

        mMemberListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                BLAlert.showDialog(FamilyMemberListActivity.this, "Confirm to delete this member?", new BLAlert.DialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        new DelMemberListTask().execute(position);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
                return true;
            }
        });
    }



    private class QueryMemberListTask extends AsyncTask<String, Void, ArrayList<FamilyMember>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(getResources().getString(R.string.loading));
        }

        @Override
        protected ArrayList<FamilyMember> doInBackground(String... strings) {
            String familyId = strings[0];

            return BLSFamilyManager.getInstance().queryMemberList(familyId, mOwnerId);
        }

        @Override
        protected void onPostExecute(ArrayList<FamilyMember> result) {
                super.onPostExecute(result);
                dismissProgressDialog();
                if(result != null){
                    blsMemberInfos.clear();
                    blsMemberInfos.addAll(result);

                    mAdapter.notifyDataSetChanged();
                }
        }
    }
    
    
    
    private class DelMemberListTask extends AsyncTask<Integer, Void, BLBaseResult> {
        int pos;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(getResources().getString(R.string.loading));
        }

        @Override
        protected BLBaseResult doInBackground(Integer... strings) {
            pos = strings[0];
            final ParamDeleteFamilyMember paramDeleteFamilyMember = new ParamDeleteFamilyMember();
            paramDeleteFamilyMember.getFamilymember().add(blsMemberInfos.get(pos).getUserid());
            return BLSFamilyManager.getInstance().delMemberList(mFamilyId, paramDeleteFamilyMember);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
                super.onPostExecute(result);
                dismissProgressDialog();
                if(result != null && result.succeed()){
                    blsMemberInfos.remove(pos);
                    mAdapter.notifyDataSetChanged();      
                }else{
                    BLCommonUtils.toastErr(result);
                }
        }
    }
    
    
    private class MemberListAdapter extends ArrayAdapter<FamilyMember> {
        public MemberListAdapter(Context context, List<FamilyMember> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_family_room, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.roomId = (TextView) convertView.findViewById(R.id.tv_mac);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            FamilyMember info = getItem(position);

            viewHolder.name.setText("userId: " + info.getUserid());
            viewHolder.roomId.setText("type: " + info.getType());

            return convertView;
        }

        private class ViewHolder{
            TextView name;
            TextView roomId;
        }
    }

}
