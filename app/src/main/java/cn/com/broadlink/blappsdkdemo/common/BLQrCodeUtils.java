package cn.com.broadlink.blappsdkdemo.common;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <pre>
 * 二维码生成帮助类
 * @author YeJing
 * @data 2018/11/19
 * </pre>
 */
public class BLQrCodeUtils {

	/**
	 * 生成二维码图片
	 *
	 * @param contents
	 *          需要生成的字符串内容
	 * @return Bitmap
	 *
	 * @throws WriterException
	 */
	public static Bitmap qrCodeBitmap(String contents) throws WriterException {
		final int WHITE = 0xFFFFFFFF; //可以指定其他颜色，让二维码变成彩色效果
		final int BLACK = 0xFF000000;

		HashMap<EncodeHintType, String> hints = null;
		String encoding = guessAppropriateEncoding(contents);
		if (encoding != null) {
			hints = new HashMap<EncodeHintType, String>(2);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = writer.encode(contents, BarcodeFormat.QR_CODE, 400, 400,hints);
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		// All are 0, or black, by default
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

	public static Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		// DecodeHintType 和EncodeHintType
		Hashtable<DecodeHintType, String> hints = new Hashtable<>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
		Bitmap scanBitmap = BLBitmapUtils.getBitmapFromFile(new File(path), 300, 300);
		if(scanBitmap == null) return null;
		int[] intArray = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
		scanBitmap.getPixels(intArray, 0, scanBitmap.getWidth(), 0, 0, scanBitmap.getWidth(), scanBitmap.getHeight());
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(), scanBitmap.getHeight(), intArray);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		Reader reader = new MultiFormatReader();
		try {
			return reader.decode(bitmap1, hints);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		} 
		return null;
	}

	public static Result scanningImage(Bitmap scanBitmap) {
		// DecodeHintType 和EncodeHintType
		Hashtable<DecodeHintType, String> hints = new Hashtable<>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
		if(scanBitmap == null) return null;
		int[] intArray = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
		scanBitmap.getPixels(intArray, 0, scanBitmap.getWidth(), 0, 0, scanBitmap.getWidth(), scanBitmap.getHeight());
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(), scanBitmap.getHeight(), intArray);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		Reader reader = new MultiFormatReader();
		try {
			return reader.decode(bitmap1, hints);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}


}
