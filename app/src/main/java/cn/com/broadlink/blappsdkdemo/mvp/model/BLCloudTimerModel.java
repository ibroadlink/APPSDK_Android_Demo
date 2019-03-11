package cn.com.broadlink.blappsdkdemo.mvp.model;

import android.content.Context;

/**
 * File description
 *
 * @author YeJing
 * @data 2018/1/12
 */

public interface BLCloudTimerModel {
	 <T> T httpRequest(Context context, String url, String bodyStr, Class<T> clazz);
}
