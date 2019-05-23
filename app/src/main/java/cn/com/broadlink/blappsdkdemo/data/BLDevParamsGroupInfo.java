package cn.com.broadlink.blappsdkdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * File description
 *
 * @author YeJing
 * @data 2018/1/17
 */

public class BLDevParamsGroupInfo implements Parcelable {
	private String name;

	private List<String> predefinedcategory = new ArrayList<>();

	private List<String> params = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPredefinedcategory() {
		return predefinedcategory;
	}

	public void setPredefinedcategory(List<String> predefinedcategory) {
		this.predefinedcategory = predefinedcategory;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<BLDevParamsGroupInfo> CREATOR = new Creator<BLDevParamsGroupInfo>() {
		@Override
		public BLDevParamsGroupInfo createFromParcel(Parcel in) {
			return new BLDevParamsGroupInfo(in);
		}

		@Override
		public BLDevParamsGroupInfo[] newArray(int size) {
			return new BLDevParamsGroupInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeStringList(this.predefinedcategory);
		dest.writeStringList(this.params);
	}

	public BLDevParamsGroupInfo() {
	}

	protected BLDevParamsGroupInfo(Parcel in) {
		this.name = in.readString();
		this.predefinedcategory = in.createStringArrayList();
		this.params = in.createStringArrayList();
	}

}
