package cn.com.broadlink.blappsdkdemo.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.sqlcipher.database.SQLiteDatabase;

import cn.com.broadlink.blappsdkdemo.db.data.BLDeviceInfo;
import cn.com.broadlink.blappsdkdemo.db.data.DNAKitDirInfo;

/**
 * 数据库处理类
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    /**数据库名称**/
    private static final String DATABASE_NAME = "appsdk_demo.db";

    /**数据库版本**/
    private static final int DATABASE_VERSION = 1;

    /**
     *
     * 构造函数
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, getKey(context));
    }

    /**
     * 初始化操作：建表
     *
     * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#(android.database.sqlite.SQLiteDatabase,
     *      com.j256.ormlite.support.ConnectionSource)
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
			TableUtils.createTableIfNotExists(connectionSource, BLDeviceInfo.class);
			TableUtils.createTableIfNotExists(connectionSource, DNAKitDirInfo.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

	public void cleanDB() {
		try {
			TableUtils.clearTable(connectionSource, BLDeviceInfo.class);
			TableUtils.clearTable(connectionSource, DNAKitDirInfo.class);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        cleanDB();
        onCreate(db, connectionSource);
	}

    private static String getKey(Context context){
    	return "test";
    }

}