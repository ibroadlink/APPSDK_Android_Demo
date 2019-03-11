package cn.com.broadlink.blappsdkdemo.db.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import cn.com.broadlink.blappsdkdemo.db.dao.BLDeviceInfoDao;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;


@DatabaseTable(tableName = "deviceInfoTable", daoClass = BLDeviceInfoDao.class)
public class BLDeviceInfo implements Serializable {



    private static final long serialVersionUID = -6716919754718380845L;

    // 设备mac地址
    @DatabaseField(id = true)
    private String did;

	//父设备ID 父设备ID为空表示此设备为主设备
	@DatabaseField
	private String pdid;

    @DatabaseField
    private String mac;
    
    // 设备密钥
    @DatabaseField
    private long password;

    // 设备类型
    @DatabaseField
    private int deviceType;

    // 产品ID
    @DatabaseField
    private String pid;

    // 设备名称
    @DatabaseField
    private String name;

    // 设备是否加锁
    @DatabaseField
    private boolean lock;

    // AES密钥
    @DatabaseField
    private String key;

    // 终端ID
    @DatabaseField
    private int terminalId;

    @DatabaseField
    private String extend;


	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public long getPassword() {
		return password;
	}

	public void setPassword(long password) {
		this.password = password;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(int terminalId) {
		this.terminalId = terminalId;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}


	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPdid() {
		return pdid;
	}

	public void setPdid(String pdid) {
		this.pdid = pdid;
	}


	public BLDeviceInfo() {
		super();
	}

	public BLDeviceInfo(BLDNADevice probeDevInfo) {
		super();
		this.setDid(probeDevInfo.getDid());
		this.setPid(probeDevInfo.getPid());
		this.setMac(probeDevInfo.getMac());
		this.setName(probeDevInfo.getName());
		this.setDeviceType(probeDevInfo.getType());
		this.setTerminalId(probeDevInfo.getId());
		this.setKey(probeDevInfo.getKey());
		this.setLock(probeDevInfo.isLock());
		this.setPassword(probeDevInfo.getPassword());
		this.setPdid(probeDevInfo.getpDid());
		this.setExtend(probeDevInfo.getExtend());
	}

	public BLDNADevice cloneBLDNADevice(){
		BLDNADevice probeDevInfo = new BLDNADevice();
		probeDevInfo.setDid(getDid());
		probeDevInfo.setMac(getMac());
		probeDevInfo.setId(getTerminalId());
		probeDevInfo.setKey(getKey());
		probeDevInfo.setLock(isLock());
		probeDevInfo.setPid(getPid());
		probeDevInfo.setpDid(getPdid());
		probeDevInfo.setName(getName());
		probeDevInfo.setPassword(getPassword());
		probeDevInfo.setType(getDeviceType());
		probeDevInfo.setExtend(getExtend());

		return probeDevInfo;
	}
}
