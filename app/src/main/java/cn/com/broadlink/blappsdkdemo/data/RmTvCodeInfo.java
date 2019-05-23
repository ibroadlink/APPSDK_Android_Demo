package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

public class RmTvCodeInfo implements Parcelable {
	
	private byte[] code;
	
	private String function;
	
	private String id;
	
	private String subIRId;
	
	private String name;
	
	private long order;
	
	private long value;
	
	private long index;

	private int type;

	private String backgroud;

	private String extend;

	private int channelId;

	public byte[] getCode() {
		return code;
	}

	public void setCode(byte[] code) {
		this.code = code;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubIRId() {
		return subIRId;
	}

	public void setSubIRId(String subIRId) {
		this.subIRId = subIRId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getOrder() {
		return order;
	}

	public void setOrder(long order) {
		this.order = order;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getBackgroud() {
		return backgroud;
	}

	public void setBackgroud(String backgroud) {
		this.backgroud = backgroud;
	}

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByteArray(this.code);
		dest.writeString(this.function);
		dest.writeString(this.id);
		dest.writeString(this.subIRId);
		dest.writeString(this.name);
		dest.writeLong(this.order);
		dest.writeLong(this.value);
		dest.writeLong(this.index);
		dest.writeInt(this.type);
		dest.writeString(this.backgroud);
		dest.writeString(this.extend);
		dest.writeInt(this.channelId);
	}

	public RmTvCodeInfo() {}

	protected RmTvCodeInfo(Parcel in) {
		this.code = in.createByteArray();
		this.function = in.readString();
		this.id = in.readString();
		this.subIRId = in.readString();
		this.name = in.readString();
		this.order = in.readLong();
		this.value = in.readLong();
		this.index = in.readLong();
		this.type = in.readInt();
		this.backgroud = in.readString();
		this.extend = in.readString();
		this.channelId = in.readInt();
	}

	public static final Parcelable.Creator<RmTvCodeInfo> CREATOR = new Parcelable.Creator<RmTvCodeInfo>() {
		@Override
		public RmTvCodeInfo createFromParcel(Parcel source) {return new RmTvCodeInfo(source);}

		@Override
		public RmTvCodeInfo[] newArray(int size) {return new RmTvCodeInfo[size];}
	};
}
