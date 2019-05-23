package cn.com.broadlink.blappsdkdemo.common;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.File;

import static android.graphics.BitmapFactory.decodeFile;

/**
 * 项目名称：BLEControlAppV4
 * 类名称：BLBitmapUtils
 * 类描述：   图片处理类
 * 创建人：YeJing
 * 创建时间：2015-3-23 上午9:19:37
 * 修改人：Administrator
 * 修改时间：2015-3-23 上午9:19:37
 * 修改备注：
 */
public class BLBitmapUtils {
	
	/**
	 * 将图片转为圆形
	 *
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toImageCircle(Bitmap bitmap) {
		final int color = 0xff424242;
		int pointX = bitmap.getWidth() / 2;
		int pointY = bitmap.getHeight() / 2;
		int pointR = pointX < pointY ? pointX : pointY;
		bitmap = Bitmap.createBitmap(bitmap, pointX - pointR, pointY - pointR, pointR * 2, pointR * 2);
		
		final Rect mSrcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final Rect mDestRect = mSrcRect;
		final RectF mSrcRectF = new RectF(mSrcRect);
		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		
		Bitmap output = Bitmap.createBitmap(pointR * 2, pointR * 2, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(mSrcRectF, pointR, pointR, paint);
		
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, mSrcRect, mDestRect, paint);
		
		return output;
	}
	
	/**
	 * Load bitmap file from sd card.
	 *
	 * @return The Bitmap object, the returned value may be null.
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (null == drawable) {
			return null;
		}
		
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		
		return drawableToBitmap(drawable, width, height);
	}
	
	
	/**
	 * Load bitmap file from sd card.
	 *
	 * @return The Bitmap object, the returned value may be null.
	 */
	public static Bitmap drawableToBitmap(Drawable drawable, int width,
                                          int height) {
		if (null == drawable || width <= 0 || height <= 0) {
			return null;
		}
		
		Config config = (drawable.getOpacity() != PixelFormat.OPAQUE) ? Config.ARGB_8888
				: Config.RGB_565;
		
		Bitmap bitmap = null;
		
		try {
			bitmap = Bitmap.createBitmap(width, height, config);
			if (null != bitmap) {
				Canvas canvas = new Canvas(bitmap);
				drawable.setBounds(0, 0, width, height);
				drawable.draw(canvas);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bitmap;
	}
	
	/**
	 * 修改图片的尺寸
	 *
	 * @param srcBitmap
	 * @param width     目标宽度
	 * @param hight     目标高度
	 * @return
	 */
	public static Bitmap changBitmapSize(Bitmap srcBitmap, int width, int hight) {
		Bitmap output = Bitmap.createBitmap(width, hight, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / (float) srcBitmap.getWidth(), (float) hight / (float) srcBitmap.getHeight());
		canvas.drawBitmap(srcBitmap, matrix, null);
		return output;
	}
	
	public static Bitmap getBitmapFromFile(File dst) {
		return getBitmapFromFile(dst, 0, 0);
	}
	
	/**
	 * 将图标文件显示
	 *
	 * @param dst
	 * @param width  显示的宽度
	 * @param height 显示的高度
	 * @return
	 */
	public static Bitmap getBitmapFromFile(File dst, int width, int height) {
		if (null != dst && dst.exists()) {
			BitmapFactory.Options options = null;
			if (width > 0 && height > 0) {
				options = new BitmapFactory.Options();
				// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
				options.inJustDecodeBounds = true;
				decodeFile(dst.getPath(), options);
				// 调用上面定义的方法计算inSampleSize值
				options.inSampleSize = computeSampleSize(options, width, height);
				// 使用获取到的inSampleSize值再次解析图片
				options.inJustDecodeBounds = false;
			}
			return decodeFile(dst.getPath(), options);
		}
		return null;
	}
	
	private static int computeSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio <= widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	
	public static Bitmap bitmap2Gray(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		Bitmap faceIconGreyBitmap = Bitmap
				.createBitmap(width, height, Config.ARGB_8888);
		
		Canvas canvas = new Canvas(faceIconGreyBitmap);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
				colorMatrix);
		paint.setColorFilter(colorMatrixFilter);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return faceIconGreyBitmap;
	}
	
	public static Bitmap getBitmapThumbnail(Bitmap bitmap, float size) {
		if (bitmap != null && size > 0) {
			Matrix matrix = new Matrix();
			
			int bitmapSize = getBitmapSize(bitmap) / 1024;
			float scaleVal = 0.9f;
			while (bitmapSize > size) {
				matrix.setScale(scaleVal, scaleVal);
				Bitmap bitmapTmp = bitmap;
				bitmap = bitmapTmp.createBitmap(bitmapTmp, 0, 0, bitmapTmp.getWidth(), bitmapTmp.getHeight(), matrix, true);
				bitmapTmp.recycle();
				bitmapSize = getBitmapSize(bitmap) / 1024;
				// System.gc();
			}
		}
		return bitmap;
	}
	
	/**
	 * 得到bitmap的大小，单位B
	 */
	private static int getBitmapSize(Bitmap bitmap) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
			return bitmap.getAllocationByteCount();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
			return bitmap.getByteCount();
		}
		// 在低版本中用一行的字节x高度
		return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
	}
}
