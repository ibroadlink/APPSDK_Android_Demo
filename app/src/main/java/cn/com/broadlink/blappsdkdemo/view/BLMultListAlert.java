package cn.com.broadlink.blappsdkdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseRecyclerAdapter;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter.BLBaseViewHolder;
import cn.com.broadlink.blappsdkdemo.view.recyclerview.divideritemdecoration.BLDividerUtil;


public class BLMultListAlert {

    public interface OnItemSelectedLister {
        void onClick(List<String> selectedList);
    }

    private BLMultListAlert() {}

    public static Dialog showAlert(Context context, String title, List<String> items, final OnItemSelectedLister alertDo) {
        return showAlert(context, title, items, null, alertDo, null);
    }

    public static Dialog showAlert(Context context, String title, List<String> items, final OnItemSelectedLister alertDo, OnCancelListener cancelListener) {
        return showAlert(context, title, items, null, alertDo, cancelListener);
    }

    public static Dialog showAlert(Context context, String title, List<String> items, boolean showIconInfo, final OnItemSelectedLister alertDo) {
        return showAlert(context, title, items, null, alertDo, null);
    }

    public static Dialog showAlert(final Context context, final String title, final List<String> items, String exit, final OnItemSelectedLister alertDo,
                                   OnCancelListener cancelListener) {
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.view_bl_mult_list_alert, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);

        TextView mTvCancel = (TextView) layout.findViewById(R.id.tv_cancel);
        TextView mTvOk = (TextView) layout.findViewById(R.id.tv_ok);
        TextView mTvTitle = (TextView) layout.findViewById(R.id.tv_title);
        RecyclerView mRlContent = (RecyclerView) layout.findViewById(R.id.rl_content);

        mTvTitle.setText(title);
        
        final MyAdapter myAdapter = new MyAdapter(items);
        mRlContent.setLayoutManager(new LinearLayoutManager(context));
        mRlContent.setAdapter(myAdapter);
        mRlContent.addItemDecoration(BLDividerUtil.getDefault(context, items));

        myAdapter.setOnItemClickListener(new BLBaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int viewType) {
                myAdapter.onCheckChanged(position);
            }
        });

        mTvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        
        mTvOk.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                dlg.dismiss();
                if(alertDo != null){
                    alertDo.onClick(myAdapter.getSelection());
                }
            }
        });

        // set a large value put it in bottom
        Window w = dlg.getWindow();
  
        WindowManager.LayoutParams lp = w.getAttributes();
        w.getDecorView().setPadding(0, 0, 0, 0);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        w.setAttributes(lp);
        
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        if (cancelListener != null) {
            dlg.setOnCancelListener(cancelListener);
        }
 
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }


    static class MyAdapter extends BLBaseRecyclerAdapter<String> {
        private List<String> selectedList = new ArrayList<>();

        public void onCheckChanged(int position) {
            if (selectedList.contains(getItem(position))) {
                selectedList.remove(getItem(position));
            } else {
                selectedList.add(getItem(position));
            }
            notifyDataSetChanged();
        }

        public List<String> getSelection() {
            return selectedList;
        }

        public MyAdapter(List<String> data) {
            super(data, R.layout.item_alert_mult_list);
        }

        @Override
        public void onBindViewHolder(BLBaseViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.setVisible(R.id.iv_select, selectedList.contains(getItem(position)) ? View.VISIBLE : View.INVISIBLE);
            holder.setText(R.id.tv_name, getItem(position));
        }
    }

}
