package cn.com.broadlink.blappsdkdemo.utils.http;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.blappsdkdemo.common.BLLog;
import cn.com.broadlink.blappsdkdemo.common.DataParseUtils;
import cn.com.broadlink.blappsdkdemo.data.BaseHeadParam;

/**
 * 从服务端读取JSON内容
 */
public class HttpAccessor extends HttpBaseAccessor {

    private static final String TAG = HttpAccessor.class.getName();

    private static final int LOAD_BUFF_SIZE = 8192;

    private boolean mEnableJsonLog = true;

    private HttpMultipartEntity.ProgressListener mProgressListener;
    
    private long mFileMaxLength;
    /**
     * 构造函数
     */
    public HttpAccessor(Context context, int method) {
        super(context, method);
    }

    /**
     * 连接服务端开始通信
     * 
     * @param url
     *            请求URL
     * @param param
     *            参数
     * @param returnType
     *            返回类型
     * 
     * @return 数据结果
     */
    public <T> T execute(String url, Object param, Class<T> returnType) {
        try {
            return access(url, null, param, returnType);
        } catch (Exception e) {
            onException(e);
        }
        return null;
    }
    
    /**
     * 连接服务端开始通信
     * 
     * @param url
     *            请求URL
     * @param headParam
     *            Header参数
     * @param param
     *            参数
     * @param returnType
     *            返回类型
     * 
     * @return 数据结果
     */
    public <T> T execute(String url, Object headParam, Object param, Class<T> returnType) {
        try {
            return access(url, headParam, param, returnType);
        } catch (Exception e) {
            onException(e);
        }
        return null;
    }

    /**
     * 连接服务端开始通信
     * 
     * @param url
     *            请求URL
     * @param headParam
     *            Header参数
     * @param param
     *            参数(可以是String字符串)
     * @param returnType
     *            返回类型(String.class 直接返回String字符串)
     * 
     * @return 数据结果
     */
    protected <T> T access(String url, Object headParam, Object param, Class<T> returnType) throws Exception {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        try {
            if (mMethod == METHOD_POST || mMethod == METHOD_POST_MULTIPART) {
                mHttpRequest = new HttpPost();
            } else {
                mHttpRequest = new HttpGet();
            }

            addIHCCommonHttpHeadParam();

            addHeaderParam(headParam);

            url = addBodyParam(param, url);
            mHttpRequest.setURI(new URI(url));

            if (mEnableJsonLog){
                BLLog.d(TAG,"url: " + url);
                BLLog.d(TAG,"header: " + JSON.toJSONString(headParam));
                BLLog.d(TAG,"param: " + JSON.toJSONString(param));
            }
            
            HttpClient httpClient = getHttpClient();
            HttpResponse response = httpClient.execute(mHttpRequest);

            if (mStoped)
                return null;

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                bis = new BufferedInputStream(response.getEntity().getContent());
                baos = new ByteArrayOutputStream();

                int size;
                byte[] temp = new byte[LOAD_BUFF_SIZE];
                while ((size = bis.read(temp, 0, temp.length)) != -1 && !mStoped) {
                    baos.write(temp, 0, size);
                }

                if (mStoped)
                    return null;

                String json = baos.toString();

                if (mEnableJsonLog){
                    BLLog.d(TAG,"return: " + json);
                }

                if (json != null && json.length() > 0) {
                	if(returnType != null && returnType.isAssignableFrom(String.class))
                		return (T) json;
                	
                	if(returnType != null && returnType.isAssignableFrom(byte[].class))
                		return (T) baos.toByteArray();
                	
                    T result = null;

                    if (returnType != null)
                        result = JSON.parseObject(json, returnType);

                    if (mStoped)
                        return null;
                    else
                        return result;
                }

            } else {
                throw new SocketException("Status Code : "
                        + response.getStatusLine().getStatusCode());
            }

        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    BLLog.e(TAG, e.getMessage(), e);
                }
                bis = null;
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    BLLog.e(TAG, e.getMessage(), e);
                }
                baos = null;
            }

            mHttpRequest.abort();
        }

        return null;
    }
    
    /**
     * 设置Http 请求头信息
     *
     * @param   headParam    
     *              Header参数
     *
     */
    private void addHeaderParam(Object headParam) throws Exception {
        if(headParam != null){
            if(headParam instanceof Map){
                Map<String, Object> headMap = (HashMap) headParam;
                for (String key : headMap.keySet()) {
                    mHttpRequest.setHeader(key, String.valueOf(headMap.get(key)));
                }
            }else{
                List<Field> headfields = DataParseUtils.getFields(headParam.getClass(), Object.class);
                for (Field field : headfields) {
                    field.setAccessible(true);
                    if (field.get(headParam) != null) {
                        mHttpRequest.setHeader(field.getName(), String.valueOf(field
                                .get(headParam)));
                    }
                }
            }
        }
    }

    private void addIHCCommonHttpHeadParam() throws Exception {
        BaseHeadParam baseHeadParam = new BaseHeadParam();
        List<Field> headfields = DataParseUtils.getFields(baseHeadParam.getClass(), Object.class);
        for (Field field : headfields) {
            field.setAccessible(true);
            if (field.get(baseHeadParam) != null) {
                mHttpRequest.setHeader(field.getName(), String.valueOf(field
                        .get(baseHeadParam)));
            }
        }
    }
    
    /***
     * 设置http Entity
     * 
     * @param   param    
     * 
     * @param   url   
     *          请求的URL，Http Get请求将参数拼接到url上面
     * 
     * @return String url
     *             http URL
     *
     */
    private String addBodyParam(Object param, String url) throws Exception {
        if (param != null) {
            if(param instanceof String){
                ((HttpPost) mHttpRequest).setEntity(new StringEntity((String) param, HTTP.UTF_8));
            }else if(param instanceof byte[]){
                ((HttpPost) mHttpRequest).setEntity(new ByteArrayEntity((byte[]) param));
            }else{
                List<Field> fields = DataParseUtils.getFields(param.getClass(), Object.class);

                switch (mMethod) {
                case METHOD_POST:
                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (field.get(param) != null) {
                            params.add(new BasicNameValuePair(field.getName(), String.valueOf(field
                                    .get(param))));
                        }
                    }

                    UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(params, HTTP.UTF_8);

                    ((HttpPost) mHttpRequest).setEntity(formEntiry);

                    break;

                case METHOD_POST_MULTIPART:
                    HttpMultipartEntity multipartEntity = new HttpMultipartEntity(new HttpMultipartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            if(mProgressListener != null)
                                mProgressListener.transferred((long) ((num / (float) mFileMaxLength) * 100));
                        }
                    });

                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (field.get(param) != null) {
                            if (field.getType().equals(File.class)) {
                                multipartEntity.addPart(field.getName(),
                                        new FileBody((File) field.get(param)));
                            }else if (field.getType().equals(byte[].class)) {
                                multipartEntity.addPart(
                                        field.getName(),
                                        new ByteArrayBody((byte[]) field.get(param), HTTP.UTF_8));
                            } else {
                                multipartEntity.addPart(
                                        field.getName(),
                                        new StringBody(String.valueOf(field.get(param)), Charset.forName(HTTP.UTF_8)));
                            }
                        }
                    }

                    mFileMaxLength = multipartEntity.getContentLength();
                    ((HttpPost) mHttpRequest).setEntity(multipartEntity);
                
                    break;

                case METHOD_GET:
                    StringBuilder sbUrl = new StringBuilder();

                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (field.get(param) != null) {
                            sbUrl.append('&');
                            sbUrl.append(field.getName());
                            sbUrl.append('=');
                            sbUrl.append(String.valueOf(field.get(param)));
                        }
                    }

                    if (sbUrl.length() > 0) {
                        sbUrl.replace(0, 1, "?");
                        url += sbUrl.toString();
                    }

                    break;
                }
            }
        }
        return url;
    }

    protected void onException(Exception e) {
        BLLog.e(TAG, e.getMessage(), e);
    }


    public void enableJsonLog(boolean enable) {
        mEnableJsonLog = enable;
    }
    
    public void setUpLoadProgressListener(HttpMultipartEntity.ProgressListener progressListener){
    	mProgressListener = progressListener;
    }
}