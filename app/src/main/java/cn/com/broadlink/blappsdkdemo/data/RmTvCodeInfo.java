package cn.com.broadlink.blappsdkdemo.data;

import java.io.Serializable;

public class RmTvCodeInfo implements Serializable {
	
	private static final long serialVersionUID = -4112756562744530258L;

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
}
