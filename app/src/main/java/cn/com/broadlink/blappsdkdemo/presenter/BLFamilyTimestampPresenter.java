package cn.com.broadlink.blappsdkdemo.presenter;

import android.content.Context;

import cn.com.broadlink.base.BLApiUrls;
import cn.com.broadlink.blappsdkdemo.data.RequestTimestampResult;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpErrCode;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpGetAccessor;


/**
 * 获取Timestamp
 * Created by YeJin on 2016/9/9.
 */
public class BLFamilyTimestampPresenter {

    private static final int TIMESTAMP_INDATE = 2 * 60 * 60 * 1000;

    private static RequestTimestampResult mCacheTimestamp;

    private static long mCacheTime;

    public static RequestTimestampResult getTimestamp(Context context){
        return getServerTimestamp(context,true);
    }

    public  static RequestTimestampResult getServerTimestamp(Context context, boolean isToastError){
        if(mCacheTimestamp == null || System.currentTimeMillis() - mCacheTime >= TIMESTAMP_INDATE){
            BLHttpGetAccessor getAccessor = new BLHttpGetAccessor(context);
            getAccessor.setToastError(isToastError);
            RequestTimestampResult timestampResult = getAccessor.execute(BLApiUrls.Family.URL_KEY_ADN_TIMESTRATRAMP(), null, RequestTimestampResult.class);
            if(timestampResult != null && timestampResult.getError() == BLHttpErrCode.SUCCESS){
                mCacheTime = System.currentTimeMillis();
                mCacheTimestamp = timestampResult;
            }
        }

        return  mCacheTimestamp;
    }
}
