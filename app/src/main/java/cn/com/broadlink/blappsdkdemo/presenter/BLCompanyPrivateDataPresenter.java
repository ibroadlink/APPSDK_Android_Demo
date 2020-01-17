package cn.com.broadlink.blappsdkdemo.presenter;

import android.content.Context;

import java.util.ArrayList;

import cn.com.broadlink.base.BLBaseResult;
import cn.com.broadlink.base.fastjson.BLJSON;
import cn.com.broadlink.blappsdkdemo.BLApplication;
import cn.com.broadlink.blappsdkdemo.common.BLConstants;
import cn.com.broadlink.blappsdkdemo.data.privatedata.CompanyPrivateDataHeader;
import cn.com.broadlink.blappsdkdemo.data.privatedata.ParamCompanyPrivateAdd;
import cn.com.broadlink.blappsdkdemo.data.privatedata.ParamCompanyPrivateQuery;
import cn.com.broadlink.blappsdkdemo.data.privatedata.ResultCompanyDeviceQuery;
import cn.com.broadlink.blappsdkdemo.service.BLLocalFamilyManager;
import cn.com.broadlink.blappsdkdemo.utils.http.BLHttpPostAccessor;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 *
 * 云端红外码接口系统
 * File description
 *
 * @author YeJing
 * @data 2017/10/9
 */

public class BLCompanyPrivateDataPresenter {
	
	private Context mContext;

	public BLCompanyPrivateDataPresenter(Context context){
		this.mContext = context;
	}

	/**
	 * 增加
	 * 
	 * @param device 
	 * @return
	 */
	public BLBaseResult add(BLDNADevice device){

		final ParamCompanyPrivateAdd dataCompanyPrivate = new ParamCompanyPrivateAdd(device);
        final ArrayList<ParamCompanyPrivateAdd> paramCompanyPrivateAdds = new ArrayList<>();
        paramCompanyPrivateAdds.add(dataCompanyPrivate);
        
        String json = BLJSON.toJSONString(paramCompanyPrivateAdds);
		CompanyPrivateDataHeader baseHeadParam = new CompanyPrivateDataHeader();
		baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
		baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyId());

		BLHttpPostAccessor httpPostAccessor = new BLHttpPostAccessor(mContext);
		
		return httpPostAccessor.execute(BLConstants.PRIVATE_DATA_URL.ADD(),baseHeadParam,json, BLBaseResult.class);
	}


	/**
	 * 更新
	 * 
	 * @param device 
	 * @return
	 */
	public BLBaseResult update(BLDNADevice device){

        final ParamCompanyPrivateAdd dataCompanyPrivate = new ParamCompanyPrivateAdd(device);
        final ArrayList<ParamCompanyPrivateAdd> paramCompanyPrivateAdds = new ArrayList<>();
        paramCompanyPrivateAdds.add(dataCompanyPrivate);

        String json = BLJSON.toJSONString(paramCompanyPrivateAdds);
		CompanyPrivateDataHeader baseHeadParam = new CompanyPrivateDataHeader();
		baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
		baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyId());

		BLHttpPostAccessor httpPostAccessor = new BLHttpPostAccessor(mContext);

		return httpPostAccessor.execute(BLConstants.PRIVATE_DATA_URL.MDF(),baseHeadParam,json, BLBaseResult.class);
	}


	/**
	 * 查询
	 * 
	 * @return 
	 */
	public ResultCompanyDeviceQuery query(){

		final ParamCompanyPrivateQuery dataCompanyPrivate = new ParamCompanyPrivateQuery();

		String json = BLJSON.toJSONString(dataCompanyPrivate);
		CompanyPrivateDataHeader baseHeadParam = new CompanyPrivateDataHeader();
		baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
		baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyId());

		BLHttpPostAccessor httpPostAccessor = new BLHttpPostAccessor(mContext);

		return httpPostAccessor.execute(BLConstants.PRIVATE_DATA_URL.QUE(),baseHeadParam,json, ResultCompanyDeviceQuery.class);
	}


	/**
	 * 删除整个数据
	 * 
	 * @return 
	 */
	public ResultCompanyDeviceQuery delete(String did){

		final ParamCompanyPrivateQuery dataCompanyPrivate = new ParamCompanyPrivateQuery(did);

		String json = BLJSON.toJSONString(dataCompanyPrivate);
		CompanyPrivateDataHeader baseHeadParam = new CompanyPrivateDataHeader();
		baseHeadParam.setUserid(BLApplication.mBLUserInfoUnits.getUserid());
		baseHeadParam.setFamilyid(BLLocalFamilyManager.getInstance().getCurrentFamilyId());

		BLHttpPostAccessor httpPostAccessor = new BLHttpPostAccessor(mContext);

		return httpPostAccessor.execute(BLConstants.PRIVATE_DATA_URL.DEL(),baseHeadParam,json, ResultCompanyDeviceQuery.class);
	}
}
