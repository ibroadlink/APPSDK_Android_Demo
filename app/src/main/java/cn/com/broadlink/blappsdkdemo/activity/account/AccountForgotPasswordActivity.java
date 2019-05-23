package cn.com.broadlink.blappsdkdemo.activity.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.view.BLProgressDialog;
import cn.com.broadlink.blappsdkdemo.view.InputTextView;
import cn.com.broadlink.blappsdkdemo.view.OnSingleClickListener;

/**
 * 
 * 项目名称：BLEControlAppV4    
 * <br>类名称：AccountForgotPasswordActivity	
 * <br>类描述： 忘记密码页面			
 * <br>创建人：YeJing 			
 * <br>创建时间：2015-5-19 上午11:44:08
 * <br>修改人：Administrator    		
 * <br>修改时间：2015-5-19 上午11:44:08
 * <br>修改备注：     		
 *
 */
public class AccountForgotPasswordActivity extends TitleActivity{

    private InputTextView mUserNameView;
    
    private Button mNextButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_forgot_password);
        setTitle(R.string.str_account_retrieve_password);
        setBackWhiteVisible();

        findView();
        
        setListener();

        initView();
    }
    
    private void findView(){
        mUserNameView = (InputTextView) findViewById(R.id.username_view);
        
        mNextButton = (Button) findViewById(R.id.btn_next);
    }

    private void initView(){
        mUserNameView.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mUserNameView.setTextHint(R.string.str_account_hint_account);
    }
    
    private void setListener(){
        mUserNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userName = mUserNameView.getTextString();
                mNextButton.setEnabled(s.length() > 0 && (BLCommonUtils.isEmail(userName) || BLCommonUtils.isPhone(userName)));
            }
        });

        mNextButton.setOnClickListener(new OnSingleClickListener() {
            
            @Override
            public void doOnClick(View v) {
                String userName = mUserNameView.getTextString();
                if(checkUserName(userName)){
                    new RequestVerifyCodeTask().execute(userName);
                }
            }
        });
    }

    /**获取发送验证码线程**/
    class RequestVerifyCodeTask extends AsyncTask<String, Void, BLBaseResult> {
        BLProgressDialog blProgressDialog;
        String userName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            blProgressDialog = BLProgressDialog.createDialog(AccountForgotPasswordActivity.this, null);
            blProgressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            userName = params[0];
            return BLAccount.sendRetrieveVCode(userName);
        }

        @Override
        protected void onPostExecute(BLBaseResult result) {
            super.onPostExecute(result);
            blProgressDialog.dismiss();
            if(result != null && result.getError() == BLAppSdkErrCode.SUCCESS){
                Intent intent = new Intent();
                intent.setClass(AccountForgotPasswordActivity.this, AccountForgotPasswordResetActivity.class);
                intent.putExtra(BLConstants.INTENT_NAME, userName);
                startActivity(intent);
            }else if (result != null){
                BLCommonUtils.toastShow(AccountForgotPasswordActivity.this, result.getMsg());
            }else{
                BLCommonUtils.toastShow(AccountForgotPasswordActivity.this,R.string.str_err_network);
            }

        }
    }
    
    //检测用户名称格式是否正确，如果出现不在0-9的字符，则判断为邮箱格式
    private boolean checkUserName(String userName){
        //检测是否为空
        if(TextUtils.isEmpty(userName)){
            BLCommonUtils.toastShow(AccountForgotPasswordActivity.this, R.string.str_account_hint_account);
            return false;
        }
        
        if(!TextUtils.isDigitsOnly(userName) && !BLCommonUtils.isEmail(userName)){
            BLCommonUtils.toastShow(AccountForgotPasswordActivity.this, R.string.str_account_err_incalid_email);
            return false;
        }
        
        return true;
    }
}
