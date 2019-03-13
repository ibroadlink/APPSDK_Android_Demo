package cn.com.broadlink.blappsdkdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.broadlink.blappsdkdemo.R;


public class BLListAlert {

    private static boolean mShowIconInfo = false;

    public interface OnItemClickLister {
        void onClick(int whichButton);
    }

    private BLListAlert() {}

    public static Dialog showAlert(Context context, String title, String[] items, final OnItemClickLister alertDo) {
        mShowIconInfo = false;
        return showAlert(context, title, items, null, alertDo, null);
    }
    
    public static Dialog showAlert(Context context, String title, String[] items, final OnItemClickLister alertDo, OnCancelListener cancelListener) {
        mShowIconInfo = false;
        return showAlert(context, title, items, null, alertDo, cancelListener);
    }

    public static Dialog showAlert(Context context, String title, String[] items, boolean showIconInfo, final OnItemClickLister alertDo) {
        mShowIconInfo = showIconInfo;
        return showAlert(context, title, items, null, alertDo, null);
    }

    public static Dialog showAlert(final Context context, final String title, final String[] items, String exit,
                                   final OnItemClickLister alertDo, OnCancelListener cancelListener) {
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.view_bl_dialog_listview, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        final ListView list = (ListView) layout.findViewById(R.id.content_list);
        AlertAdapter adapter = new AlertAdapter(context, title, items, exit);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!(title == null || title.equals("")) && position - 1 >= 0) {
                    alertDo.onClick(position - 1);
                    dlg.dismiss();
                    list.requestFocus();
                } else {
                    alertDo.onClick(position);
                    dlg.dismiss();
                    list.requestFocus();
                }
            }
        });

        TextView cancelText = (TextView) layout.findViewById(R.id.cancel_text);
        cancelText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        // set a large value put it in bottom
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        if (cancelListener != null) {
            dlg.setOnCancelListener(cancelListener);
        }
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

    private static class AlertAdapter extends BaseAdapter {
        public static final int TYPE_BUTTON = 0;

        public static final int TYPE_TITLE = 1;

        public static final int TYPE_EXIT = 2;

        private List<String> items;

        private int[] types;

        private boolean isTitle = false;

        private Context context;

        public AlertAdapter(Context context, String title, String[] items, String exit) {
            if (items == null || items.length == 0) {
                this.items = new ArrayList<String>();
            } else {
                this.items = new ArrayList<>(Arrays.asList(items));
            }
            this.types = new int[this.items.size() + 3];
            this.context = context;
            if (title != null && !title.equals("")) {
                types[0] = TYPE_TITLE;
                this.isTitle = true;
                this.items.add(0, title);
            }

            if (exit != null && !exit.equals("")) {
                types[this.items.size()] = TYPE_EXIT;
                this.items.add(exit);
            }

        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean isEnabled(int position) {
            if (position == 0 && isTitle) {
                return false;
            } else {
                return super.isEnabled(position);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String textString = (String) getItem(position);
            ViewHolder holder;
            int type = types[position];
            if (convertView == null || ((ViewHolder) convertView.getTag()).type != type) {
                holder = new ViewHolder();
//            if (type == TYPE_BUTTON) {
                convertView = View.inflate(context, R.layout.item_bl_dialog_listview, null);
//            } else if (type == TYPE_TITLE) {
//                convertView = View.inflate(context, R.layout.alert_dialog_menu_list_layout_title, null);
//            }

                holder.text = (TextView) convertView.findViewById(R.id.popup_text);
                holder.type = type;
                holder.info = (ImageView) convertView.findViewById(R.id.ico_info);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(textString);

            if(mShowIconInfo){
                holder.info.setVisibility(View.VISIBLE);
            }else{
                holder.info.setVisibility(View.GONE);
            }

            return convertView;
        }

        static class ViewHolder {
            TextView text;
            int type;
            ImageView info;
        }
    }

}
