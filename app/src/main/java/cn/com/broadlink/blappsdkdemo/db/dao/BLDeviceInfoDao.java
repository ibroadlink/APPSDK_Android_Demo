package cn.com.broadlink.blappsdkdemo.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import cn.com.broadlink.blappsdkdemo.db.DatabaseHelper;
import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;

public class BLDeviceInfoDao extends BaseDaoImpl<BLDeviceInfo, String> {

    /**
     * 构造函数
     * 
     * @param helper
     */
    public BLDeviceInfoDao(DatabaseHelper helper) throws SQLException {
        super(helper.getConnectionSource(), BLDeviceInfo.class);
    }

    /**
     * 构造函数
     * 
     * @param connectionSource
     * @param dataClass
     */
    public BLDeviceInfoDao(ConnectionSource connectionSource,
                           Class<BLDeviceInfo> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }


    public List<BLDeviceInfo> queryDevList() throws SQLException{
        QueryBuilder<BLDeviceInfo, String> queryBuilder = queryBuilder();
        return query(queryBuilder.prepare());
    }


    public void insertData(final List<BLDeviceInfo> list) throws SQLException {
        TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                for(int i = 0; i < list.size() ;i ++){
                    createOrUpdate(list.get(i));
                }
                return null;
            }
        });
    }
    
    /**
     * 删除设备
     * @param deviceInfo
     *          需要删除的设备信息
     * @throws SQLException
     */
    public int deleteDevice(BLDeviceInfo deviceInfo) throws SQLException {
        QueryBuilder<BLDeviceInfo, String> queryBuilder = queryBuilder();
        Where<BLDeviceInfo, String> where = queryBuilder.where();
        where.eq("pdid", deviceInfo.getDid());
        List<BLDeviceInfo> devList =  query(queryBuilder.prepare());

        for (BLDeviceInfo subDevInfo : devList){
            deleteById(subDevInfo.getDid());
        }

        return deleteById(deviceInfo.getDid());
    }
}
