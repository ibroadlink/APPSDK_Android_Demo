package cn.com.broadlink.blappsdkdemo.utils.http;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.Looper;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.net.SocketException;
import java.security.KeyStore;
import java.util.Properties;

import cn.com.broadlink.blappsdkdemo.common.BLLog;


/**
 * 与服务端通信的抽象类
 */
public abstract class HttpBaseAccessor {

    private static final String TAG = HttpBaseAccessor.class.getName();

    public static final int METHOD_POST = 1;

    public static final int METHOD_GET = 2;

    public static final int METHOD_POST_MULTIPART = 3;

    /**
     * Context
     */
    protected Context mContext;

    /**
     * Handler
     */
    protected Handler mHandler;

    /**
     * 是否已停止
     */
    protected boolean mStoped = false;

    private static HttpClient mHttpClient;

    protected int mMethod;

    /**
     * 请求
     */
    protected HttpRequestBase mHttpRequest;

    /**
     * 构造函数
     */
    public HttpBaseAccessor(Context context, int method) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        mMethod = method;
    }

    public boolean checkNetworkState() throws SocketException {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Activity.CONNECTIVITY_SERVICE);

        State mobileState =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        State wifiState =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if (!mobileState.equals(State.CONNECTED)
                && !mobileState.equals(State.CONNECTING) && !wifiState.equals(State.CONNECTED)
                && !wifiState.equals(State.CONNECTING)) {
            return false;
        }

        return true;
    }

    /**
     * 中断通信
     */
    public void stop() {
        mStoped = true;
        mHttpRequest.abort();
    }

    protected synchronized HttpClient getHttpClient() {
        if (null == mHttpClient) {
            try {
                final Properties properties = loadProperties();

                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                HttpProtocolParams.setUseExpectContinue(params, true);

                ConnManagerParams.setTimeout(params,
                        Integer.parseInt(properties.getProperty("poolTimeout", "1000")));
                ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {

                    @Override
                    public int getMaxForRoute(HttpRoute route) {
                        return Integer.parseInt(properties.getProperty("maxConnectionsPerRoute", "64"));
                    }
                });
                ConnManagerParams.setMaxTotalConnections(params,
                        Integer.parseInt(properties.getProperty("maxTotalConnections", "128")));
                HttpConnectionParams.setConnectionTimeout(params,
                        Integer.parseInt(properties.getProperty("connectTimeout", "15000")));
                HttpConnectionParams.setSoTimeout(params,
                        Integer.parseInt(properties.getProperty("soTimeout", "15000")));

                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
                
                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schReg.register(new Scheme("https", sf, 443));

                ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
                mHttpClient = new DefaultHttpClient(conMgr, params);
            } catch (Exception e) {
                mHttpClient = new DefaultHttpClient();
            }
        }

        return mHttpClient;

    
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try {
            int id =
                    mContext.getResources().getIdentifier("access", "raw",
                            mContext.getPackageName());
            props.load(mContext.getResources().openRawResource(id));
        } catch (Exception e) {
            BLLog.e(TAG, e.getMessage(), e);
        }
        return props;
    }
    
}
