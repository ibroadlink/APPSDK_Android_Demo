package cn.com.broadlink.blappsdkdemo.utils.http;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.account.AccountMainActivity;
import cn.com.broadlink.blappsdkdemo.data.BaseResult;


/**
 * 项目名称：BLEControlAppV4 <br>
 * 类名称：BLHttpHPostAccessor <br>
 * 类描述： 如果返回不成功会自动提示错误信息 <br>
 * 创建人：YeJing <br>
 * 创建时间：2015-4-9 下午5:51:54 <br>
 * 修改人：Administrator <br>
 * 修改时间：2015-4-9 下午5:51:54 <br>
 * 修改备注：
 */
public class BLHttpPostAccessor extends HttpAccessor {

    // 是否显示错误信息
    private boolean mToastError = true;

    public BLHttpPostAccessor(Context context) {
        super(context, METHOD_POST);
    }

    @Override
    public <T> T execute(String url, Object headParam, Object param, Class<T> returnType) {
        final T result = super.execute(url, headParam, param, returnType);
        if (result != null) {
            if (result instanceof BaseResult) {
                final BaseResult resultDto = (BaseResult) result;

                if (resultDto != null && resultDto.getError() != BLHttpErrCode.SUCCESS) {
                    checkNeedLogout(resultDto.getError());
                }
                return result;
            } else if (result instanceof BLBaseResult) {
                final BLBaseResult resultDto = (BLBaseResult) result;

                if (resultDto != null && !resultDto.succeed()) {
                    checkNeedLogout(resultDto.getError());
                }
                return result;

            }
        }

        if (result == null && mToastError) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // 提示错误信息
                    Toast.makeText(mContext, R.string.str_err_network, Toast.LENGTH_SHORT).show();
                }
            });
        }
        return result;
    }

    private void checkNeedLogout(int error) {
        if (error == BLHttpErrCode.SESSION_INVALID || error == BLHttpErrCode.ACCOUNT_LOGIN_INVALID || error == BLHttpErrCode.LOGIN_AGAIN || error == BLHttpErrCode.UNLOGIN_IN) {
            BLApplication.mBLUserInfoUnits.loginOut();
            Intent intent = new Intent(mContext, AccountMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }


    @Override
    protected void onException(Exception e) {
        super.onException(e);

        if (!mToastError) return;

//        final int msgId;
//        if (e instanceof SocketException
//                || e instanceof InterruptedIOException || e instanceof UnknownHostException
//                || e instanceof UnknownServiceException) {
//            // 网络错误
//            msgId = R.string.str_err_network;
//        } else {
//            // 系统错误
//            msgId = R.string.err_system;
//        }

        if (mContext != null) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // 提示错误
                    Toast.makeText(mContext, R.string.str_err_network, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 设置是否显示错误信息
     *
     * @param toastError
     */
    public void setToastError(boolean toastError) {
        this.mToastError = toastError;
    }

}
