package cn.com.broadlink.blappsdkdemo.activity.Family;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.TitleActivity;
import cn.com.broadlink.blappsdkdemo.intferfacer.FamilyInterface;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;
import cn.com.broadlink.family.params.BLFamilyAllInfo;
import cn.com.broadlink.family.params.BLFamilyRoomInfo;


public class FamilyRoomListActivity extends TitleActivity implements FamilyInterface {

    private ListView mRoomListView;
    private List<String> blFamilyRoomNameList = new ArrayList<>();
    private List<BLFamilyRoomInfo> blFamilyRoomInfoList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_room_list);
        setTitle(R.string.str_settings_place_room);
        setBackWhiteVisible();

        findView();
        setListener();

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, blFamilyRoomNameList);
        mRoomListView.setAdapter(mAdapter);

        BLLocalFamilyManager.getInstance().setFamilyInterface(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFamilyRooms();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void familyInfoChanged(Boolean isChanged, String familyId, String familyVersion) {
        if (isChanged) {
            showProgressDialog(getResources().getString(R.string.loading));
            BLLocalFamilyManager.getInstance().queryFamilyAllInfo(null);
        }
    }

    @Override
    public void familyAllInfo(BLFamilyAllInfo allInfo) {
        dismissProgressDialog();
        if (allInfo != null) {
            //家庭信息发生改变，则刷新房间列表
            updateFamilyRooms();
            mAdapter.notifyDataSetChanged();
        }
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
                        BLLocalFamilyManager.getInstance().addRoomIntoFamily("TestRoom");
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

                BLFamilyRoomInfo roomInfo = blFamilyRoomInfoList.get(position);
                final String name = roomInfo.getName();
                final String roomId = roomInfo.getRoomId();

                AlertDialog.Builder dialog = new AlertDialog.Builder(FamilyRoomListActivity.this);
                dialog.setTitle("Message");
                dialog.setMessage("Delete Room " + name);
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BLLocalFamilyManager.getInstance().delRoomFromFamilt(name, roomId);
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
        blFamilyRoomInfoList.clear();
        blFamilyRoomNameList.clear();

        blFamilyRoomInfoList = BLLocalFamilyManager.getInstance().getCurrentFamilyAllInfo().getRoomInfos();
        for (BLFamilyRoomInfo info : blFamilyRoomInfoList) {
            String name = info.getName();
            if (name != null)
                blFamilyRoomNameList.add(name);
        }
    }

}
