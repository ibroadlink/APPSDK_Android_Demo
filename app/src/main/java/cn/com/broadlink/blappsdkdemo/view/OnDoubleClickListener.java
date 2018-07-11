package cn.com.broadlink.blappsdkdemo.view;

import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YeJin on 2016/2/23.
 */
public abstract class OnDoubleClickListener implements View.OnClickListener {

    private boolean mEnable = false;

    private static final int mDelay = 700;

    @Override
    public void onClick(View v) {
        if (mEnable) {
            doDoubleOnClick(v);
        }else{
            mEnable = true;
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    mEnable = false;
                }
            }, mDelay);
        }
    }

    public abstract void doDoubleOnClick(View v);
}