package cn.com.broadlink.blappsdkdemo.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import cn.com.broadlink.blappsdkdemo.db.DatabaseHelper;
import cn.com.broadlink.blappsdkdemo.db.data.DNAKitDirInfo;

/**
 * File description
 *
 * @author YeJing
 * @data 2018/1/11
 */

public class DNAKitDirInfoDao extends BaseDaoImpl<DNAKitDirInfo, Long> {

	/**
	 * 构造函数
	 *
	 * @param helper
	 */
    public DNAKitDirInfoDao(DatabaseHelper helper) throws SQLException {
		super(helper.getConnectionSource(), DNAKitDirInfo.class);
	}

	/**
	 * 构造函数
	 *
	 * @param connectionSource
	 * @param dataClass
	 */
    public DNAKitDirInfoDao(ConnectionSource connectionSource,
                            Class< DNAKitDirInfo > dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	private int deleteProtocoCategoryData(String protocolStr) throws SQLException {
//		String protocolStr = toProtocolStr(protocolList);
		DeleteBuilder<DNAKitDirInfo, Long> deleteBuilder = deleteBuilder();
		Where<DNAKitDirInfo, Long> where = deleteBuilder.where();
		if(protocolStr == null){
			where.isNull("protocols");
		}else{
			where.eq("protocols", protocolStr);
		}
		return deleteBuilder.delete();
	}

	public List<DNAKitDirInfo> queryList(List<String> protocolList) throws SQLException {
		String protocolStr = toProtocolStr(protocolList);
		QueryBuilder<DNAKitDirInfo, Long> queryBuilder = queryBuilder();
		Where<DNAKitDirInfo, Long> where = queryBuilder.where();
		if(protocolStr == null){
			where.isNull("protocols");
		}else{
			where.eq("protocols", protocolStr);
		}
		return query(queryBuilder.orderBy("rank", true).prepare());
	}

	public void createList(final List<DNAKitDirInfo> list, final List<String> protocols) throws SQLException {
		if (list.isEmpty()) return;
		TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				String protocolStr = toProtocolStr(protocols);

				deleteProtocoCategoryData(protocolStr);

				for (DNAKitDirInfo brInfo : list) {
					brInfo.setProtocols(protocolStr);
					createOrUpdate(brInfo);
				}
				return null;
			}
		});
	}

	private String toProtocolStr(List<String> protocolList){
		if(protocolList != null){
			StringBuffer protocolStr = new StringBuffer();
			for (String protocol : protocolList) {
				protocolStr.append(protocol);
				protocolStr.append("|");
			}

			return protocolStr.toString();
		}

		return null;
	}
}
