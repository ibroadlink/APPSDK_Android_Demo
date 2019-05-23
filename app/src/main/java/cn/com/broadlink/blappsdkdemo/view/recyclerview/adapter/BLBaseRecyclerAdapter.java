package cn.com.broadlink.blappsdkdemo.view.recyclerview.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
*基础的RecyclerView适配器，提供长按和短按点击监听器接口，使用时只需重载onBindViewHolder方法设置item界面即可
*@author JiangYaqiang
*createrd at 2018/8/14 10:24
*/
public abstract class BLBaseRecyclerAdapter<T> extends RecyclerView.Adapter<BLBaseViewHolder>{
    protected List<T> mBeans;
    private OnClickListener mClickListener;
    private OnLongClickListener mLongClickListener;

    private int mLayoutId;

    public interface OnClickListener {
       void onClick(int position, int viewType);
    }

    public interface OnLongClickListener {
       boolean onLongClick(int position, int viewType);
    }

    public BLBaseRecyclerAdapter(@NonNull List<T> beans) {
        this.mBeans = beans;
    }

    public BLBaseRecyclerAdapter(@NonNull List<T> beans, int layoutId) {
        this.mLayoutId = layoutId;
        this.mBeans = beans;
    }

    public T getItem(int position){
        return mBeans.get(position);
    }

    @Override
    public BLBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId(viewType), parent, false);
        return new BLBaseViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final BLBaseViewHolder holder, int position) {
        if(holder != null){
            final int adapterPosition = holder.getAdapterPosition();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mClickListener != null){
                        mClickListener.onClick(adapterPosition, getItemViewType(adapterPosition));
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mLongClickListener != null){
                        return mLongClickListener.onLongClick(adapterPosition, getItemViewType(adapterPosition));
                    }
                    return true;
                }
            });
        }
    }

    public void setOnItemClickListener(OnClickListener listener){
        this.mClickListener = listener;
    }
    
    public void setOnItemLongClickListener(OnLongClickListener listener){
        this.mLongClickListener = listener;
    }
    
    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    protected int layoutId(int viewType){
        return mLayoutId;
    }
    
}
