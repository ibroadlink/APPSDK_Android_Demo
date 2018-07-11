package cn.com.broadlink.blappsdkdemo.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.broadlink.lib.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.broadlink.lib.imageloader.core.DisplayImageOptions;
import com.broadlink.lib.imageloader.core.ImageLoader;
import com.broadlink.lib.imageloader.core.ImageLoaderConfiguration;
import com.broadlink.lib.imageloader.core.assist.FlushedInputStream;
import com.broadlink.lib.imageloader.core.assist.QueueProcessingType;
import com.broadlink.lib.imageloader.core.listener.ImageLoadingListener;
import com.broadlink.lib.imageloader.core.listener.ImageLoadingProgressListener;
import com.broadlink.lib.imageloader.download.BaseImageDownloader;
import com.broadlink.lib.imageloader.utils.DiskCacheUtils;
import com.broadlink.lib.imageloader.utils.MemoryCacheUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BLImageLoaderUtils {

    private static BLImageLoaderUtils mBitmapUtils;
    private static ImageLoader mImageLoader;

    private static DisplayImageOptions mOptions;

    private Context mContext;

    private BLImageLoaderUtils(Context context) {
        mImageLoader = ImageLoader.getInstance();

        mContext = context;
        // method.
        ImageLoaderConfiguration.Builder config = getConfig(context);
//         config.writeDebugLogs(); // Remove for release app
        mImageLoader.init(config.build());

        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @NonNull
    private ImageLoaderConfiguration.Builder getConfig(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(70 * 1024 * 1024); //
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.memoryCacheSize(3 * 1024 * 1024);//内存缓存
        return config;
    }

    public static BLImageLoaderUtils getInstence(Context context) {
        if (mBitmapUtils == null) {
            mBitmapUtils = new BLImageLoaderUtils(context);
        }
        return mBitmapUtils;
    }

    /***
     * 异步加载图片
     * @param uri
     *          图片地址
     * @param imageView
     *          显示的ImageView
     * @param listener
     *          回调
     */
    public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
        if (uri == null || !uri.equals("http://ihcv0.ibroadlink.com")) {
            mImageLoader.displayImage(uri, imageView, mOptions, listener);
        } else if (listener != null) {
            listener.onLoadingFailed(uri, imageView, null);
        }
    }


    /***
     * 异步加载图片
     * @param uri
     *          图片地址
     * @param imageView
     *          显示的ImageView
     * @param listener
     *          回调
     * @progressListener
     * 			下载进度
     */
    public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        mImageLoader.displayImage(uri, imageView, mOptions, listener, progressListener);
    }

    /**
     * 同步加载图片
     *
     * @param uri 图片地址
     * @return Bitmap
     */
    public Bitmap loadImageSync(String uri) {
        return mImageLoader.loadImageSync(uri, mOptions);
    }


    public void clearCache() {
        mImageLoader.clearMemoryCache();
        mImageLoader.clearDiskCache();
    }

    public void clearCache(String imagePath) {
        MemoryCacheUtils.removeFromCache(imagePath, mImageLoader.getMemoryCache());
        DiskCacheUtils.removeFromCache(imagePath, mImageLoader.getDiskCache());
    }

    public class AuthImageDownloader extends BaseImageDownloader {

        public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
        public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

        public AuthImageDownloader(Context context) {
            this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
        }

        public AuthImageDownloader(Context context, int connectTimeout, int readTimeout) {
            super(context, connectTimeout, readTimeout);
        }

        @Override
        protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {

            URL url = null;
            try {
                url = new URL(imageUri);
            } catch (MalformedURLException e) {
               e.printStackTrace();
            }
            HttpURLConnection http = null;

            if (Scheme.ofUri(imageUri) == Scheme.HTTPS) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url
                        .openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                http = https;
                http.connect();
            } else {
                http = (HttpURLConnection) url.openConnection();
            }

            http.setConnectTimeout(connectTimeout);
            http.setReadTimeout(readTimeout);
            return new FlushedInputStream(new BufferedInputStream(
                    http.getInputStream()));
        }

        // always verify the host - dont check for certificate
        final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        /**
         * Trust every server - dont check for any certificate
         */
        private  void trustAllHosts() {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] x509Certificates,
                        String s) throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] x509Certificates,
                        String s) throws java.security.cert.CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }};

            // Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection
                        .setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
