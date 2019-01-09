package cn.com.broadlink.blappsdkdemo.activity.Family;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSQueryRoomListResult;
import cn.com.broadlink.blappsdkdemo.activity.Family.Result.BLSRoomInfo;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;


public class FamilyRoomListActivity extends TitleActivity {

    private ListView mRoomListView;
    private List<BLSRoomInfo> blsRoomInfos = new ArrayList<>();
    private RoomListAdapter mAdapter;
    private String mFamilyId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_room_list);
        setTitle(R.string.str_settings_place_room);
        setBackWhiteVisible();

        findView();
        setListener();

        mAdapter  = new RoomListAdapter(FamilyRoomListActivity.this, blsRoomInfos);
        mRoomListView.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            mFamilyId = getIntent().getStringExtra("INTENT_FAMILY_ID");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFamilyRooms();
    }

    private void findView() {
        mRoomListView = (ListView) findViewById(R.id.room_listview);
    }

    private void setListener(){
        setRightButtonOnClickListener(R.drawable.btn_add_cycle_white, new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FamilyRoomListActivity.this);
                dialog.setTitle("Message");
                dialog.setMessage("Add Room TestRoom ?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addRoomIntoFamily("TestRoom");
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });

        mRoomListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                BLSRoomInfo roomInfo = blsRoomInfos.get(position);
                final String name = roomInfo.getName();
                final String roomId = roomInfo.getRoomid();

                AlertDialog.Builder dialog = new AlertDialog.Builder(FamilyRoomListActivity.this);
                dialog.setTitle("Message");
                dialog.setMessage("Delete Room " + name);
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delRoomFromFamily(roomId);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();

                return true;
            }
        });
    }

    private void updateFamilyRooms() {
        new QueryRoomListTask().execute(mFamilyId);
    }

    /**
     * 添加新的房间
     * @param name 房间名称
     */
    public void addRoomIntoFamily(String name) {
        String action = "add";
        new ManageRoomTask().execute(mFamilyId, action, name);
    }

    /**
     * 删除指定房间
     * @param roomId 房间ID
     */
    public void delRoomFromFamily(String roomId) {
        String action = "del";
        new ManageRoomTask().execute(mFamilyId, action, roomId);
    }

    private class QueryRoomListTask extends AsyncTask<String, Void, BLSQueryRoomListResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLSQueryRoomListResult doInBackground(String... strings) {
            String familyId = strings[0];

            return BLSFamilyHTTP.getInstance().queryRoomList(familyId);
        }

        @Override
        protected void onPostExecute(BLSQueryRoomListResult result) {
            super.onPostExecute(result);

            if (result != null && result.succeed() && result.getData() != null) {
                blsRoomInfos.clear();

                if (result.getData().getRoomList() != null) {
                    blsRoomInfos.addAll(result.getData().getRoomList());
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    }


    private class ManageRoomTask extends AsyncTask<String, Void, BLSQueryRoomListResult> {

        private String familyId = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected BLSQueryRoomListResult doInBackground(String... strings) {
            BLSRoomInfo info = new BLSRoomInfo();

            familyId = strings[0];
            String action = strings[1];

            info.setAction(action);
            if (action.equalsIgnoreCase("add")) {
                String name = strings[2];
                info.setName(name);
            } else if (action.equalsIgnoreCase("del")) {
                String roomId = strings[2];
                info.setRoomid(roomId);
            }

            List<BLSRoomInfo> roomInfos = new ArrayList<>();
            roomInfos.add(info);

            return BLSFamilyHTTP.getInstance().manageRooms(familyId, roomInfos);
        }

        @Override
        protected void onPostExecute(BLSQueryRoomListResult result) {
            super.onPostExecute(result);

            if (result != null && result.succeed() && result.getData() != null) {
                new QueryRoomListTask().execute(familyId);
            }
        }
    }

    private class RoomListAdapter extends ArrayAdapter<BLSRoomInfo> {
        public RoomListAdapter(Context context, List<BLSRoomInfo> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.adapter_device, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.roomId = (TextView) convertView.findViewById(R.id.tv_mac);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BLSRoomInfo info = getItem(position);

            viewHolder.name.setText(info.getName());
            viewHolder.roomId.setText(info.getRoomid());

            return convertView;
        }

        private class ViewHolder{
            TextView name;
            TextView roomId;
        }
    }


}
