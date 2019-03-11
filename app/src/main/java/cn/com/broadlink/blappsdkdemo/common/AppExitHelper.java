package cn.com.broadlink.blappsdkdemo.common;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;

/**
 * <pre>
 * APP 退出
 * @author YeJing
 * @data 2018/8/29
 * </pre>
 */
public class AppExitHelper {
	//双击返回按钮退出APP
	private static boolean mCanExit;

	private static Timer mExitTimer;

	public static void exit(){
		if (mCanExit) {
			((BLApplication) BLAppUtils.getApp()).appFinish();
		} else {
			mCanExit = true;
			BLToastUtils.show(R.string.common_general_click_again_to_exit);

			mExitTimer = new Timer();
			mExitTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					mCanExit = false;
				}
			}, 1000);
		}
	}
}
