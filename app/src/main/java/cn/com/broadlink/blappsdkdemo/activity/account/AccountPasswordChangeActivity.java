package cn.com.broadlink.blappsdkdemo.activity.account;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.view.BLPasswordEditView;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * 
 *  密码修改页面			
 */
public class AccountPasswordChangeActivity extends TitleActivity {

    private BLPasswordEditView mOldPasswordView, mNewPasswordView, mNewPasswordAgainView;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_password_change);
        setTitle(R.string.str_settings_safety_modify_password);
        setBackWhiteVisible();
        
        findView();
        
        setListener();
    }
    
    private void findView(){
        mOldPasswordView = (BLPasswordEditView) findViewById(R.id.old_password_view);
        mNewPasswordView = (BLPasswordEditView) findViewById(R.id.new_password_view);
        mNewPasswordAgainView = (BLPasswordEditView) findViewById(R.id.new_password_again_view);
    }
    
    private void setListener(){
        setRightButtonOnClickListener(R.string.str_common_next, getResources().getColor(R.color.bl_yellow_main_color), 
                new OnSingleClickListener() {
                    
                    @Override
                    public void doOnClick(View v) {
                        String oldPassword = mOldPasswordView.getEtPwd().getText().toString();
                        String newPassword1 = mNewPasswordView.getEtPwd().getText().toString();
                        String newPassword2 = mNewPasswordAgainView.getEtPwd().getText().toString();
                        
                        if(checkInputMessage(oldPassword, newPassword1, newPassword2)){
                            new RestPasswordTask().execute(oldPassword, newPassword1);
                        }
                    }
                });
    }
    
    private boolean checkInputMessage(String oldPassword, String newPassword1, String newPassword2){
        if(TextUtils.isEmpty(oldPassword)){
            BLCommonUtils.toastShow(AccountPasswordChangeActivity.this, R.string.str_input_old_password);
            return false;
        }
        
        
        if(TextUtils.isEmpty(newPassword1)){
            BLCommonUtils.toastShow(AccountPasswordChangeActivity.this, R.string.str_input_new_password);
            return false;
        }
        
        
        if(newPassword1.length() < 6 || newPassword1.length() > 20){
            BLCommonUtils.toastShow(AccountPasswordChangeActivity.this, R.string.str_account_hint_password_length_from_six_to_twenty);
            return false;
        }
        
    	if(BLCommonUtils.strContainCNChar(newPassword1)){
       	 	BLCommonUtils.toastShow(AccountPasswordChangeActivity.this, R.string.str_account_hint_no_chinese);
            return false;
        } 
        
        if(TextUtils.isEmpty(newPassword2)){
            BLCommonUtils.toastShow(AccountPasswordChangeActivity.this, R.string.str_input_new_password_again);
            return false;
        }
        
        if(!newPassword1.equals(newPassword2)){
            BLCommonUtils.toastShow(AccountPasswordChangeActivity.this, R.string.str_account_err_password_verify);
            return false;
        }
        return true;
    }
//

    //重置密码
    private class RestPasswordTask extends AsyncTask<String, Void, BLBaseResult> {
        private BLProgressDialog mBLProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBLProgressDialog = BLProgressDialog.createDialog(AccountPasswordChangeActivity.this,
                    R.string.str_account_signin);
            mBLProgressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            return BLAccount.modifyPassword(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            if(result != null && result.succeed()){
                mBLProgressDialog.toastShow(getString(R.string.str_settings_safety_modify_password_success), R.drawable.icon_ok);
                mBLProgressDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        back();
                    }
                });
            }else if(result != null){
                mBLProgressDialog.dismiss();
                BLCommonUtils.toastErr(result);
            }else{
                mBLProgressDialog.dismiss();
                BLCommonUtils.toastShow(AccountPasswordChangeActivity.this,R.string.str_err_network);
            }

        }
    }
}
