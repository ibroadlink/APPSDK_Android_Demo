package cn.com.broadlink.blappsdkdemo.activity.h5;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.com.broadlink.blappsdkdemo.R;
import cn.com.broadlink.blappsdkdemo.activity.base.TitleActivity;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;

/**
 * 打开指定的url
 */
public class CommonH5Activity extends TitleActivity {
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private String mUrl;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_h5);
        setBackWhiteVisible();


        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mUrl = getIntent().getStringExtra(BLConstants.INTENT_URL);
        mTitle = getIntent().getStringExtra(BLConstants.INTENT_TITLE);

        setTitle(mTitle);
        
        mWebView.loadUrl(mUrl);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器 
                view.loadUrl(url);
                return true;
            }
        });
        
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                mProgressBar.postInvalidate();
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private String appendUrl(String url, HashMap<String, Object> param){
        StringBuffer sbUrl = new StringBuffer();

        Iterator<Map.Entry<String, Object>> iterator = param.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> item = iterator.next();
            sbUrl.append('&');
            sbUrl.append(item.getKey());
            sbUrl.append('=');
            sbUrl.append(String.valueOf(item.getValue()));
        }

        if (sbUrl.length() > 0) {
            sbUrl.replace(0, 1, "?");
            url += sbUrl.toString();
        }

        return url;
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mWebView.destroy();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}
