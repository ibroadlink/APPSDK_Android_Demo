package cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
*通用的RecyclerViewHolder， 目前只提供显示文字和图片功能，可扩展对其他view的操作
*@author JiangYaqiang
*createrd at 2018/8/14 10:25
*/
public class BLBaseViewHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> mViews;

    public BLBaseViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

//    /**
//     * 获取item对应的ViewDataBinding对象
//     *
//     * @param <T>
//     * @return
//     */
//    public <T extends ViewDataBinding> T getBinding() {
//        return DataBindingUtil.getBinding(this.itemView);
//    }

    /**
     * 根据View Id 获取对应的View
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T get(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = this.itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    //******** 提供对View、TextView、ImageView的常用设置方法 ******//

    public BLBaseViewHolder setText(int viewId, String text) {
        TextView tv = get(viewId);
        tv.setText(text);
        return this;
    }

    public BLBaseViewHolder setText(int viewId, int textRes) {
        TextView tv = get(viewId);
        tv.setText(textRes);
        return this;
    }

    public BLBaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = get(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public BLBaseViewHolder setTextSize(int viewId, int size) {
        TextView view = get(viewId);
        view.setTextSize(size);
        return this;
    }

    public BLBaseViewHolder setImageResource(int viewId, int resId) {
        ImageView view = get(viewId);
        view.setImageResource(resId);
        return this;
    }

    public BLBaseViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = get(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }


    public BLBaseViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = get(viewId);
        view.setImageDrawable(drawable);
        return this;
    }


    public BLBaseViewHolder setBackgroundColor(int viewId, int color) {
        View view = get(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public BLBaseViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = get(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BLBaseViewHolder setVisible(int viewId, boolean visible) {
        View view = get(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BLBaseViewHolder setVisible(int viewId, int visible) {
        View view = get(viewId);
        view.setVisibility(visible);
        return this;
    }
}
