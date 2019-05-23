package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class RmTvCodeInfoResult implements Parcelable {

	private long irId;
	
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

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.irId);
		dest.writeString(this.brand);
		dest.writeString(this.id);
		dest.writeString(this.model);
		dest.writeString(this.name);
		dest.writeLong(this.num);
		dest.writeLong(this.type);
		dest.writeTypedList(this.functionList);
	}

	public RmTvCodeInfoResult() {}

	protected RmTvCodeInfoResult(Parcel in) {
		this.irId = in.readLong();
		this.brand = in.readString();
		this.id = in.readString();
		this.model = in.readString();
		this.name = in.readString();
		this.num = in.readLong();
		this.type = in.readLong();
		this.functionList = in.createTypedArrayList(RmTvCodeInfo.CREATOR);
	}

	public static final Parcelable.Creator<RmTvCodeInfoResult> CREATOR = new Parcelable.Creator<RmTvCodeInfoResult>() {
		@Override
		public RmTvCodeInfoResult createFromParcel(Parcel source) {return new RmTvCodeInfoResult(source);}

		@Override
		public RmTvCodeInfoResult[] newArray(int size) {return new RmTvCodeInfoResult[size];}
	};
}
