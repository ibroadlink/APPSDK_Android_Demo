package cn.com.broadlink.blappsdkdemo.activity.Account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.account.params.BLRegistParam;
import cn.com.broadlink.base.BLAppSdkErrCode;
import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.BLLoginResult;
import cn.com.broadlink.blappsdkdemo.BLApplcation;
import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.Device.DevListActivity;
import cn.com.broadlink.blappsdkdemo.common.BLCommonUtils;


/**
 * 手机注册
 * Created by YeJin on 2016/5/9.
 */
public class RegisterByPhoneActivity extends Activity{

    private EditText mCountryCodeView, mPhoneView, mVCodeView;
    private EditText mPasswordView, mNickNameView;

    private Button mGetVCodeBtn, mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_by_phone_layout);

        findView();

        setListener();
    }

    private void findView(){
        mCountryCodeView = (EditText) findViewById(R.id.country_code_view);
        mPhoneView = (EditText) findViewById(R.id.phone_view);
        mVCodeView = (EditText) findViewById(R.id.v_code_view);
        mPasswordView = (EditText) findViewById(R.id.password_view);
        mNickNameView = (EditText) findViewById(R.id.nickname_view);

        mGetVCodeBtn = (Button) findViewById(R.id.btn_get_v_code);
        mLoginBtn = (Button) findViewById(R.id.btn_register);
    }

    private void setListener(){
        mGetVCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = mCountryCodeView.getText().toString();
                String phone = mPhoneView.getText().toString();
                if(TextUtils.isEmpty(countryCode) || TextUtils.isEmpty(phone)){
                    BLCommonUtils.toastShow(RegisterByPhoneActivity.this, "请检查手机区号和手机号");
                }else{
                    new GetVCodeTask().execute(phone, countryCode);
                }
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = mCountryCodeView.getText().toString();
                String phone = mPhoneView.getText().toString();
                String vCode = mVCodeView.getText().toString();
                String password = mPasswordView.getText().toString();
                String nickName = mNickNameView.getText().toString();

                if(TextUtils.isEmpty(countryCode) || TextUtils.isEmpty(phone)
                        || TextUtils.isEmpty(vCode) || TextUtils.isEmpty(password)
                        || TextUtils.isEmpty(nickName)){
                    BLCommonUtils.toastShow(RegisterByPhoneActivity.this, "请检查信息是否填写完整");
                }else{
                    new RegistCodeTask().execute(countryCode, phone, vCode, password, nickName);
                }
            }
        });
    }

    //获取验证码
    private class RegistCodeTask extends AsyncTask<String, Void, BLLoginResult>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterByPhoneActivity.this);
            progressDialog.setMessage("注册中...");
            progressDialog.show();
        }

        @Override
        protected BLLoginResult doInBackground(String... params) {
            String countryCode = params[0];
            String phone = params[1];
            String vCode = params[2];
            String password = params[3];
            String mickName = params[4];

            BLRegistParam registParam = new BLRegistParam();
            registParam.setPhoneOrEmail(phone);
            registParam.setPassword(password);
            registParam.setNickname(mickName);
            registParam.setCountrycode(countryCode);
            registParam.setCode(vCode);

            /**图标地址*/
            registParam.setIconpath("http://musicdata.baidu.com/data2/pic/115360807/115360807.jpg");

            File iconFile = null;
            /**本地图标使用**/
//            iconFile = new File("本地图标路径");

            return BLAccount.regist(registParam, iconFile);
        }

        @Override
        protected void onPostExecute(BLLoginResult loginResult) {
            super.onPostExecute(loginResult);
            progressDialog.dismiss();
            if(loginResult != null && loginResult.getError() == BLAppSdkErrCode.SUCCESS){
                //保存登录信息
                BLApplcation.mBLUserInfoUnits.login(loginResult.getUserid(),
                        loginResult.getLoginsession(), loginResult.getNickname(),
                        loginResult.getIconpath(), loginResult.getLoginip(),
                        loginResult.getLogintime(), loginResult.getSex(), null, loginResult.getPhone(), loginResult.getEmail(), loginResult.getBirthday());

                BLCommonUtils.toastShow(RegisterByPhoneActivity.this, "注册成功");
                Intent intent = new Intent();
                intent.setClass(RegisterByPhoneActivity.this, DevListActivity.class);
                startActivity(intent);
                RegisterByPhoneActivity.this.finish();
            }else if(loginResult != null){
                BLCommonUtils.toastShow(RegisterByPhoneActivity.this, "ErrCode:" + loginResult.getError());
            }
        }
    }

    //获取验证码
    private class GetVCodeTask extends AsyncTask<String, Void, BLBaseResult>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterByPhoneActivity.this);
            progressDialog.setMessage("获取验证码...");
            progressDialog.show();
        }

        @Override
        protected BLBaseResult doInBackground(String... params) {
            return BLAccount.sendRegVCode(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(BLBaseResult baseResult) {
            super.onPostExecute(baseResult);
            progressDialog.dismiss();
            if(baseResult != null && baseResult.getError() == BLAppSdkErrCode.SUCCESS){
                BLCommonUtils.toastShow(RegisterByPhoneActivity.this, "获取验证码成功");
            }else if(baseResult != null){
                BLCommonUtils.toastShow(RegisterByPhoneActivity.this, "ErrCode:" + baseResult.getError());
            }
        }
    }
}
