package cn.com.broadlink.blappsdkdemo.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RmTvCodeInfoResult implements Serializable {

	private long irId;
	
	private static final long serialVersionUID = 463551991019422811L;

	private String brand;
	
	private String id;
	
	private String model;
	
	private String name;
	
	private long num;
	
	private long type;
	
	private List<RmTvCodeInfo> functionList = new ArrayList<RmTvCodeInfo>();

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public List<RmTvCodeInfo> getFunctionList() {
		return functionList;
	}

	public void setFunctionList(List<RmTvCodeInfo> functionList) {
		this.functionList = functionList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public long getType() {
		return type;
	}

	public void setType(long type) {
		this.type = type;
	}

	public long getIrId() {
		return irId;
	}

	public void setIrId(long irId) {
		this.irId = irId;
	}
	
}
