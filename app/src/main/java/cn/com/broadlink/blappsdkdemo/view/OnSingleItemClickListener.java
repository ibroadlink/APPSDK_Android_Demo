package cn.com.broadlink.blappsdkdemo.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.Timer;
import java.util.TimerTask;

public abstract class OnSingleItemClickListener implements OnItemClickListener {

    private boolean mEnable = true;
    private static final int mDelay = 500;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mEnable) {
            mEnable = false;
            doOnClick(parent, view, position, id);
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    mEnable = true;
                }
            }, mDelay);
        }
    }

    public abstract void doOnClick(AdapterView<?> parent, View view, int position, long id);
}
