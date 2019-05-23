package cn.com.broadlink.blappsdkdemo.common;

import android.content.Context;
import android.widget.Toast;

/**
 * 作者：EchoJ on 2019/2/14 17:30 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：
 */
public final class BLToastUtils {
    private static Toast mToast;

    public static void show(int msgId) {
        show(BLAppUtils.getApp(), msgId);
    }

    public static void show(String msg) {
        show(BLAppUtils.getApp(), msg);
    }

    public static void show(Context context, int msgId) {
        show(context, context.getString(msgId));
    }

    public static void show(Context context, String msg) {
        cancel();

        mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}

