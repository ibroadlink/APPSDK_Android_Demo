package cn.com.broadlink.blappsdkdemo.utils.http;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.account.AccountMainActivity;
import cn.com.broadlink.blappsdkdemo.data.BaseResult;


/**
 * 
 * 项目名称：BLEControlAppV4    
 * <br>类名称：BLHttpPostFileAccessor	
 * <br>类描述：MULTIPART方式上传文件 			
 * <br>创建人：YeJing 			
 * <br>创建时间：2015-4-21 下午7:08:37
 * <br>修改人：Administrator    		
 * <br>修改时间：2015-4-21 下午7:08:37
 * <br>修改备注：     		
 *
 */
public class BLHttpPostFileAccessor extends HttpAccessor {

    // 是否显示错误信息
    private boolean mToastError = true;

    public BLHttpPostFileAccessor(Context context) {
        super(context, METHOD_POST_MULTIPART);
    }

    @Override
    public <T> T execute(String url, Object headParam, Object param, Class<T> returnType) {
        T result = super.execute(url, headParam, param, returnType);
        if (result != null && result instanceof BaseResult) {
            final BaseResult resultDto = (BaseResult) result;
            if (resultDto != null && resultDto.getError() != BLHttpErrCode.SUCCESS) {

                if (mToastError) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // 提示错误信息
                            Toast.makeText(mContext, resultDto.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                checkNeedLogout(resultDto.getError());

                return null;

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

    /**
     * 设置是否显示错误信息
     * 
     * @param toastError
     */
    public void setToastError(boolean toastError) {
        this.mToastError = toastError;
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

}
