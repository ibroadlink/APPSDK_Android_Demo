package cn.com.broadlink.blappsdkdemo.mvp.model;

import android.content.Context;

import java.util.List;

import cn.com.broadlink.blappsdkdemo.data.BatchQueryProductResult;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitDirResult;
import cn.com.broadlink.blappsdkdemo.data.GetDNAKitProductListResult;
import cn.com.broadlink.blappsdkdemo.data.GetProductBrandListResult;
import cn.com.broadlink.blappsdkdemo.data.ProductInfoResult;


/**
 * File description
 *
 * @author YeJing
 * @data 2018/1/10
 */

public interface BLProductModel {

	/**
	 * 产品信息请求
	 * @param url
	 * 			请求地址
	 * @param bodyStr
	 * 			请求body内容
	 * @param clazz<T>
	 * 			返回数据类型
	 * @return
	 * 			返回数据
	 */
	<T> T httpRequest(Context context, String url, String bodyStr, Class<T> clazz, boolean toastShowError);

	/**
	 * 查询产品目录列表
	 * @param  protocol
	 * 			需要查询哪些协议的产品目录，可以为null,
	 * 			null表示查询所有支持的产品目录
	 * @return
	 * 		目录列表
	 */
	GetDNAKitDirResult queryProductCategoryList(Context context, List<String> protocol);

	/**
	 * 查询目录下的产品列表
	 * @param categoryId
	 * 		查询目录下的产品列表
	 * @param protocol
	 * 		需要查询哪些协议的产品，可以为null
	 * @return
	 * 		产品列表
	 */
	GetDNAKitProductListResult queryProductList(Context context, String categoryId, List<String> protocol);

	/**
	 * 查询品牌下的产品列表
	 * @param categoryid
	 * 			查询目录下的产品列表
	 * @param brandName
	 *			品牌名称
	 * @param protocol
	 * 			需要查询哪些协议的产品，可以为null
	 * @return
	 * 			产品列表
	 */
	GetDNAKitProductListResult queryProductList(Context context, String categoryid, String brandName, List<String> protocol);

	/**
	 * 查询分类下的品牌列表
	 * @param categoryid
	 * 		查询目录下的产品列表
	 * @param protocol
	 * 		需要查询哪些协议的产品，可以为null
	 * @return
	 * 		品牌列表
	 */
	GetProductBrandListResult queryBrandList(Context context, String categoryid, List<String> protocol);

	/**
	 * 查询产品详情
	 * @param pid
	 * 			产品PID
	 * @return
	 * 			产品详情
	 */
	ProductInfoResult queryProducInfo(Context context, String pid);

	/**
	 * 查询产品详情
	 * @param context
	 * @param pid
	 * 			产品PID
	 * @param toastShowError
	 * 			显示错误信息
	 * @return
	 */
	ProductInfoResult queryProducInfo(Context context, String pid, boolean toastShowError);

	/**
	 * 通过二维码查询产品详情
	 * @param productQrCode
	 * 			产品二维码
	 * @return
	 * 			产品详情
	 */
	ProductInfoResult queryQrProducInfo(Context context, String productQrCode);

	/**
	 * 批量查询检查产品是否更新
	 *
	 * @param pidList
	 * 			产品列表
	 */
	void batchCheckDevListProductDetailVersion(Context context, List<String> pidList);

	void batchCheckDevListProductDetail(Context context, List<String> pidList, CheckListener listener);

	public abstract class CheckListener{
		public abstract void onResult(BatchQueryProductResult result);
	}
}
