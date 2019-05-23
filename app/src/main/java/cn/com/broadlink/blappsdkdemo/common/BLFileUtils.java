package cn.com.broadlink.blappsdkdemo.common;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * 项目名称：BLEControlAppV4
 * 类名称：BLFileUtils
 * 类描述：  文件处理工具类
 * 创建人：YeJing
 * 创建时间：2015-3-23 上午9:19:50
 * 修改人：Administrator
 * 修改时间：2015-3-23 上午9:19:50
 * 修改备注：
 */
public class BLFileUtils {

	/**
	 * 复制文件夹
	 *
	 * @param sourceDir 源文件
	 * @param targetDir 目标文件
	 */
	public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}
	
	/**
	 * 复制文件
	 *
	 * @param srcFile  源文件
	 * @param destFile 目标文件
	 */
	public static final boolean copyFile(File srcFile, File destFile) {
		if (!srcFile.exists()) {
			return false;
		}
		
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			destFile.getParentFile().mkdirs();
			destFile.createNewFile();
			
			bis = new BufferedInputStream(new FileInputStream(srcFile));
			bos = new BufferedOutputStream(new FileOutputStream(destFile));
			
			int size;
			byte[] temp = new byte[1024];
			while ((size = bis.read(temp, 0, temp.length)) != -1) {
				bos.write(temp, 0, size);
			}
			
			bos.flush();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bos = null;
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bis = null;
			}
		}
		return false;
	}

	/**
	 * 保存字符串到文件
	 *
	 * @param value
	 * @param fileName
	 */
	public static final void saveStringToFile(String value, String fileName) {
		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			fw.write(value);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写文件
	 * @param fileName
	 * @param content
	 * @param isAppend true表示以追加形式写文件
	 * @return
	 */
	public static int writeFile(String fileName, String content, Boolean isAppend ) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter writer = new FileWriter(fileName, isAppend);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	/**
	 * 从文件读取字符串
	 *
	 * @param fileName
	 * @return
	 */
	public static final String getStringByFile(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuffer sbString = new StringBuffer();
				String str = null;
				while ((str = reader.readLine()) != null) {
					sbString.append(str);
				}
				reader.close();
				return sbString.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 删除文件夹及下面的子文件
	 *
	 * @param deletedFile
	 */
	public static void deleteFile(File deletedFile) {
		if (deletedFile.exists()) { // 判断文件是否存在
			if (deletedFile.isFile()) { // 判断是否是文件
				deletedFile.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (deletedFile.isDirectory()) { // 否则如果它是一个目录
				File files[] = deletedFile.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
		}
	}
	
	
	/**
	 * 根据URL获取到下载的文件名称
	 *
	 * @param url 下载路径
	 * @return 下载文件名称
	 */
	public static String getFileNameByUrl(String url) {
		int postion = url.lastIndexOf("/");
		return url.substring(postion + 1);
	}
	
	/**
	 * 将assert 目录下的文件拷贝到sdka
	 *
	 * @param context    上下文
	 * @param assestPath assest文件夹
	 * @param trgPath    目标路径
	 * @return 拷贝成功
	 */
	public static boolean copyAssertToSDCard(Context context, String assestPath, String trgPath) {
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			File file = new File(trgPath);
			
			if (!file.exists()) {
				file.createNewFile();
				
				is = context.getResources().getAssets().open(assestPath);
				fos = new FileOutputStream(trgPath);
				
				byte[] buffer = new byte[1024];
				int count = 0;
				
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭输出流
			// 关闭输入流
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
			
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is = null;
			}
		}
		
		return false;
	}
	
	/**
	 * 获取Assests文件下的文件内容
	 *
	 * @param fileName
	 * @return String    DOM对象
	 */
	public static String readAssetsFile(Context context, String fileName) {
		return readAssetsFile(context, fileName, "utf-8");
	}
	
	/**
	 * 读取asset目录下文件。
	 *
	 * @return content
	 */
	public static String readAssetsFile(Context mContext, String file, String code) {
		int len = 0;
		byte[] buf = null;
		String result = "";
		try {
			InputStream in = mContext.getAssets().open(file);
			len = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);
			
			result = new String(buf, code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 读取文本文件内容
	 *
	 * @param path 文件路径
	 * @return
	 */
	public static String readTextFileContent(String path) {
		BufferedReader br = null;
		try {
			
			File file = new File(path);
			if (!file.exists()) {
				return null;
			}
			
			br = new BufferedReader(new FileReader(file));
			StringBuffer result = new StringBuffer();
			String line = null;
			
			while ((line = br.readLine()) != null) {
				result.append(line).append("\r\n");
			}
			
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
		
		return null;
	}
	
}
